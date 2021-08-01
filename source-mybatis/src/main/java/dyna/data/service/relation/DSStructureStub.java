/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 处理普通关联关系
 * JiangHL 2011-5-10
 */
package dyna.data.service.relation;

import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.bean.data.*;
import dyna.common.bean.data.foundation.ViewObject;
import dyna.common.bean.data.structure.BOMStructure;
import dyna.common.bean.data.template.RelationTemplate;
import dyna.common.bean.model.cls.ClassObject;
import dyna.common.bean.xml.UpperKeyMap;
import dyna.common.dto.DataRule;
import dyna.common.dto.Session;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.template.bom.BOMTemplateEnd2;
import dyna.common.dto.template.bom.BOMTemplateInfo;
import dyna.common.dto.template.relation.RelationTemplateEnd2;
import dyna.common.dto.template.relation.RelationTemplateInfo;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.sqlbuilder.plmdynamic.SqlParamData;
import dyna.common.sqlbuilder.plmdynamic.insert.DynamicInsertParamData;
import dyna.common.sqlbuilder.plmdynamic.select.DynamicSelectParamData;
import dyna.common.sqlbuilder.plmdynamic.update.DynamicUpdateParamData;
import dyna.common.systemenum.*;
import dyna.common.util.DateFormat;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.context.DataServerContext;
import dyna.data.service.DSAbstractServiceStub;
import dyna.data.service.acl.AclService;
import dyna.data.sqlbuilder.ClassInstanceGetSqlBuilder;
import dyna.data.sqlbuilder.RelationFromQuerySqlBuilder;
import dyna.data.sqlbuilder.RelationToQuerySqlBuilder;
import dyna.dbcommon.exception.DynaDataExceptionAll;
import dyna.dbcommon.exception.DynaDataExceptionSQL;
import dyna.dbcommon.util.Constants;
import dyna.dbcommon.util.DSCommonUtil;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 处理普通关联关系
 *
 * @author JiangHL
 */
public class DSStructureStub extends DSAbstractServiceStub<RelationServiceImpl>
{
	private static final Pattern CHINESE_CHAR_PATTERN = Pattern.compile("[\u4e00-\u9fa5]");

	protected DSStructureStub(DataServerContext context, RelationServiceImpl service)
	{
		super(context, service);
	}

	/**
	 * 创建structure
	 *
	 * @param structureObject
	 * @param sessionId
	 * @param fixTranId
	 * @return
	 * @throws SQLException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws ServiceRequestException
	 */
	private StructureObject create(StructureObject structureObject, String sessionId, String fixTranId) throws IllegalArgumentException, SecurityException, InstantiationException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException, ServiceRequestException
	{
		String strcuctureObjectGuid = null;
		Session session = DataServer.getDSCommonService().getSession(sessionId);
		String userGuid = session.getUserGuid();

		String className = structureObject.getObjectGuid().getClassName();
		String systemTable = DataServer.getDSCommonService().getTableName(className);
		ObjectGuid viewObjectGuid = structureObject.getViewObjectGuid();
		ObjectGuid childObjectGuid = structureObject.getEnd2ObjectGuid();
		ObjectGuid objectGuid = structureObject.getObjectGuid();

		Map<String, List<SqlParamData>> tableDataMap = new HashMap<>();
		tableDataMap.put(systemTable, new ArrayList<>());
		tableDataMap.get(systemTable).add(new SqlParamData("VIEWFK", viewObjectGuid.getGuid(), DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.STRING)));
		tableDataMap.get(systemTable).add(new SqlParamData("VIEWCLASSGUID", viewObjectGuid.getClassGuid(), DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.STRING)));
		tableDataMap.get(systemTable).add(new SqlParamData("END2", childObjectGuid.getGuid(), DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.STRING)));
		tableDataMap.get(systemTable).add(new SqlParamData("END2$MASTER", childObjectGuid.getMasterGuid(), DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.STRING)));
		tableDataMap.get(systemTable).add(new SqlParamData("END2$CLASS", childObjectGuid.getClassGuid(), DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.STRING)));
		tableDataMap.get(systemTable).add(new SqlParamData("CREATETIME", DateFormat.getSysDate(), DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.DATETIME)));
		tableDataMap.get(systemTable).add(new SqlParamData("CREATEUSER", userGuid, DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.STRING)));
		tableDataMap.get(systemTable).add(new SqlParamData("UPDATETIME", DateFormat.getSysDate(), DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.DATETIME)));
		tableDataMap.get(systemTable).add(new SqlParamData("UPDATEUSER", userGuid, DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.STRING)));
		tableDataMap.get(systemTable).add(new SqlParamData("CLASSGUID", objectGuid.getClassGuid(), DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.STRING)));

		ClassObject classObject = DataServer.getClassModelService().getClassObject(className);
		if (classObject.getFieldList() != null)
		{
			for (ClassField field : classObject.getFieldList())
			{
				if (SystemClassFieldEnum.GUID.getName().equals(field.getName()))
				{
					continue;
				}
				if (field.isSystem())
				{
					continue;
				}

				Object fieldValue = structureObject.get(field.getName());
				FieldTypeEnum fieldType = field.getType();

				int fieldSize = 128;
				if ((fieldType == FieldTypeEnum.MULTICODE || fieldType == FieldTypeEnum.STRING) && fieldValue != null)
				{
					if (field.getFieldSize() != null)
					{
						fieldSize = Integer.parseInt(field.getFieldSize());
					}

					// 包含中文字符，按一个字符三个长度计，暂不考虑中文字符个数，只要包含，实际可用长度即为设置长度的1/3
					Matcher m = CHINESE_CHAR_PATTERN.matcher(fieldValue.toString());
					if (m.find())
					{
						fieldSize = fieldSize / 3;
					}
					if (fieldSize < fieldValue.toString().length())
					{
						throw new DynaDataExceptionAll("Value is too large.", null, DataExceptionEnum.DS_VALUE_TOO_LARGE, field.getName(), fieldValue.toString().length(),
								fieldSize);
					}
				}

				String tableName = DataServer.getDSCommonService().getTableName(className, field.getName());
				if (!tableDataMap.containsKey(tableName))
				{
					tableDataMap.put(tableName, new ArrayList<SqlParamData>());
				}

				tableDataMap.get(tableName).add(new SqlParamData(field.getColumnName(), structureObject.get(field.getName()), DSCommonUtil.getJavaTypeOfField(field.getType())));
				ObjectFieldTypeEnum objectFieldTypeOfField = DataServer.getDSCommonService().getObjectFieldTypeOfField(field, session.getBizModelName());
				if (objectFieldTypeOfField == ObjectFieldTypeEnum.OBJECT)
				{
					tableDataMap.get(tableName).add(
							new SqlParamData(field.getColumnName() + "$CLASS", structureObject.get(field.getName() + "$CLASS"), DSCommonUtil.getJavaTypeOfField(field.getType())));
					tableDataMap.get(tableName).add(new SqlParamData(field.getColumnName() + "$MASTER", structureObject.get(field.getName()) + "$MASTER",
							DSCommonUtil.getJavaTypeOfField(field.getType())));
				}
			}
		}

		try
		{
			// 字段存储在多张表中
			if (tableDataMap.get(systemTable) != null)
			{
				DynamicInsertParamData insertParamData = new DynamicInsertParamData();
				insertParamData.setTableName(systemTable);
				insertParamData.setInsertParamList(tableDataMap.get(systemTable));

				strcuctureObjectGuid = String.valueOf(this.dynaObjectMapper.insertDynamic(insertParamData));
				structureObject.getObjectGuid().setGuid(strcuctureObjectGuid);
				structureObject.setGuid(strcuctureObjectGuid);
				structureObject.put(ViewObject.END1, structureObject.getEnd1ObjectGuid().getGuid());
				structureObject.put(ViewObject.END1 + "$CLASS", structureObject.getEnd1ObjectGuid().getClassGuid());
				structureObject.put(ViewObject.END1 + "$MASTER", structureObject.getEnd1ObjectGuid().getMasterGuid());
			}
			tableDataMap.remove(systemTable);

			if (!SetUtils.isNullMap(tableDataMap))
			{
				for (String tableName : tableDataMap.keySet())
				{
					if (SetUtils.isNullList(tableDataMap.get(tableName)))
					{
						continue;
					}

					List<SqlParamData> insertParamList = new ArrayList<>(tableDataMap.get(tableName));
					insertParamList.add(0, new SqlParamData("FOUNDATIONFK", strcuctureObjectGuid, DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.STRING)));

					DynamicInsertParamData insertParamData = new DynamicInsertParamData();
					insertParamData.setTableName(tableName);
					insertParamData.setInsertParamList(insertParamList);

					this.dynaObjectMapper.insertDynamic(insertParamData);
				}
			}

			StructureObjectImpl retObject = (StructureObjectImpl) structureObject.getClass().getConstructor().newInstance();
			retObject.sync(structureObject);
			return retObject;
		}
		catch (Exception e)
		{
			throw new ServiceRequestException("", null, e);
		}
	}

	/**
	 * 创建structure
	 *
	 * @param structureObject
	 * @param isCheckAcl
	 * @param sessionId
	 * @return
	 * @throws ServiceRequestException
	 */
	protected StructureObject create(StructureObject structureObject, boolean isPrecise, boolean isCheckAcl, String sessionId, String fixTranId) throws ServiceRequestException
	{
		AclService aclService = DataServer.getAclService();

		// 变更839 第8条 判断添加end2权限
		if (isCheckAcl && !aclService.hasAuthority(structureObject.getViewObjectGuid(), AuthorityEnum.LINK, sessionId))
		{
			throw new DynaDataExceptionAll("create sttructure error, no link auth. bomview guid  = " + structureObject.getViewObjectGuid().getGuid(), null,
					DataExceptionEnum.DS_NO_LINK_AUTH);
		}
		try
		{
//			DataServer.getTransactionManager().startTransaction(fixTranId);
			String end2 = null;
			if (!isPrecise)
			{
				end2 = (String) structureObject.get("END2");
				structureObject.put("END2", null);
			}
			StructureObject retObject = this.create(structureObject, sessionId, fixTranId);
			if (!isPrecise)
			{
				retObject.put("END2", end2);
			}

//			DataServer.getTransactionManager().commitTransaction();
			return retObject;
		}
		catch (Exception e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof DynaDataExceptionSQL)
			{
				throw (DynaDataExceptionSQL) e;
			}
			else if (e instanceof DynaDataExceptionAll)
			{
				throw (DynaDataExceptionAll) e;
			}
			throw new DynaDataExceptionAll("CreateStructureObject() Id =" + structureObject.getSequence(), e, DataExceptionEnum.DS_CREATE_STRUCTURE,
					structureObject.getName());
		}
	}

	protected void deleteAllStructure(String className, ObjectGuid end1ObjectGuid, ObjectGuid viewObjectGuid, boolean isCheckAcl, String sessionId) throws ServiceRequestException
	{
		AclService aclService = DataServer.getAclService();
		if (isCheckAcl && !aclService.hasAuthority(viewObjectGuid, AuthorityEnum.UNLINK, sessionId))
		{
			throw new DynaDataExceptionAll("delete structure error, no unlink auth. bomview guid  = " + viewObjectGuid.getGuid(), null, DataExceptionEnum.DS_NO_UNLINK_AUTH);
		}

		try
		{
			this.dynaObjectMapper.deleteAllStruc( DataServer.getDSCommonService().getTableName(className), viewObjectGuid.getGuid());
		}
		catch (Exception e)
		{
			if (e instanceof DynaDataExceptionSQL)
			{
				throw (DynaDataExceptionSQL) e;
			}
			else if (e instanceof DynaDataExceptionAll)
			{
				throw (DynaDataExceptionAll) e;
			}
			throw new DynaDataExceptionAll("deleteStructureObject Id =" + viewObjectGuid.getObjectGuid(), e, DataExceptionEnum.DS_DELETE_STRCTURE);
		}
	}

	/**
	 * 删除structure
	 *
	 * @param structureObject
	 * @param isCheckAcl
	 * @param sessionId
	 * @throws ServiceRequestException
	 */
	protected void delete(StructureObject structureObject, boolean isCheckAcl, String sessionId) throws ServiceRequestException
	{
		AclService aclService = DataServer.getAclService();

		// 变更839 第8条 判断移除end2权限
		if (isCheckAcl && !aclService.hasAuthority(structureObject.getViewObjectGuid(), AuthorityEnum.UNLINK, sessionId))
		{
			throw new DynaDataExceptionAll("delete structure error, no unlink auth. bomview guid  = " + structureObject.getViewObjectGuid().getGuid(), null,
					DataExceptionEnum.DS_NO_UNLINK_AUTH);
		}

		try
		{
			String className = structureObject.getObjectGuid().getClassName();
			if (StringUtils.isNullString(className))
			{
				className = DataServer.getClassModelService().getClassObjectByGuid(structureObject.getObjectGuid().getClassGuid()).getName();
			}
			Map<String, Object> delMap = new HashMap<>();
			// delete structure
			delMap.put("table", DataServer.getDSCommonService().getTableName(className));
			delMap.put("GUID", structureObject.getObjectGuid().getGuid());
			delMap.put("CHECKWFDATEGUID", structureObject.getViewObjectGuid().getGuid());
			if (structureObject instanceof BOMStructure)
			{
				// delMap.put("CHECKOUTUSER", this.stubService(sessionId).getUserGuid());
			}

			String viewClassGuid = structureObject.getViewObjectGuid().getClassGuid();
			delMap.put("VAITETABLE", DataServer.getDSCommonService().getTableName(viewClassGuid));

			delMap.put("ISCHECKACL", isCheckAcl ? "Y" : "N");

			if (0 == this.dynaObjectMapper.delete(delMap))
			{
				throw new DynaDataExceptionAll("deleteStructureObject Id =" + structureObject.getObjectGuid(), null, DataExceptionEnum.DS_DELETE_STRCTURE_NODATA,
						structureObject.getName());
			}

		}
		catch (Exception e)
		{
			if (e instanceof DynaDataExceptionSQL)
			{
				throw (DynaDataExceptionSQL) e;
			}
			else if (e instanceof DynaDataExceptionAll)
			{
				throw (DynaDataExceptionAll) e;
			}
			throw new DynaDataExceptionAll("deleteStructureObject Id =" + structureObject.getObjectGuid(), e, DataExceptionEnum.DS_DELETE_STRCTURE, structureObject.getName());
		}
	}

	protected int getEnd2CountOfView(String viewGuid, String relationTemplateGuid) throws ServiceRequestException
	{
		RelationTemplateInfo relationTemplate = DataServer.getRelationService().getRelationTemplateInfo(relationTemplateGuid);
		Map<String, Object> param = new HashMap<>();
		param.put("SELECT", "COUNT(1) END2COUNT");
		param.put("FROM", DataServer.getDSCommonService().getTableName(relationTemplate.getStructureClassName()));
		param.put("WHERE", "VIEWFK = '" + viewGuid + "'");
		try
		{
			UpperKeyMap end2CountMap = (UpperKeyMap) this.dynaObjectMapper.selectAutoHalf(param);
			return ((BigDecimal) end2CountMap.get("END2COUNT")).intValue();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new DynaDataExceptionAll("query StructureObject  error", e, DataExceptionEnum.DS_QUERY_DATA_EXCEPTION);
		}
	}

	protected List<BOMStructure> listBOMStructure(ObjectGuid viewObjectGuid, String templateGuid, DataRule dataRule, boolean isCheckAcl, String sessionId,
			SearchCondition searchCondition) throws ServiceRequestException
	{
		BOMTemplateInfo bomTemplate = DataServer.getRelationService().getBOMTemplateInfo(templateGuid);
		RelationTemplate template = new RelationTemplate();
		template.getInfo().putAll(bomTemplate);
		template.setEnd2Type(bomTemplate.getPrecise() == BomPreciseType.PRECISE ? "2" : "1");

		List<BOMStructure> bomStructureList = new ArrayList<>();

		List<RelationTemplateEnd2> relationTemplateEnd2List = new ArrayList<>();
		List<BOMTemplateEnd2> bomTemplateEnd2List = DataServer.getRelationService().listBOMTemplateEnd2(templateGuid);
		if (!SetUtils.isNullList(bomTemplateEnd2List))
		{
			for (BOMTemplateEnd2 end2 : bomTemplateEnd2List)
			{
				RelationTemplateEnd2 rEnd2 = new RelationTemplateEnd2();
				rEnd2.putAll(end2);
				relationTemplateEnd2List.add(rEnd2);
			}
		}
		template.setRelationTemplateEnd2List(relationTemplateEnd2List);
		List<StructureObject> structureList = this.listObjectOfRelation(viewObjectGuid, template, dataRule, isCheckAcl, sessionId, searchCondition);
		if (!SetUtils.isNullList(structureList))
		{
			for (StructureObject structureObject : structureList)
			{
				bomStructureList.add(new BOMStructure(structureObject));
			}
		}
		return bomStructureList;
	}

	public List<StructureObject> listSimpleStructureOfRelation(String viewGuid, String viewClassNameOrGuid, String struClassGuid, String sessionId)
	{
		try
		{
			DynamicSelectParamData paramData = RelationFromQuerySqlBuilder.buildStructureInfoSearchParamData(sessionId, viewClassNameOrGuid, struClassGuid, viewGuid);
			List<FoundationObject> list = DataServer.getDSCommonService().executeQuery(paramData);
			if (SetUtils.isNullList(list))
			{
				return null;
			}
			List<StructureObject> structureObjectList = new ArrayList<>();
			for (FoundationObject fo : list)
			{
				StructureObjectImpl structureObject = new StructureObjectImpl();
				structureObject.putAll((FoundationObjectImpl) fo);
				structureObject.put("END2$MASTERFK", structureObject.get("END2$MASTER"));
				structureObject.put("END2$CLASSGUID", structureObject.get("END2$CLASS"));
				structureObject.put("END2$MASTERFK", structureObject.get("END2$MASTER"));
				structureObject.put("END2$CLASSGUID", structureObject.get("END2$CLASS"));
				structureObjectList.add(structureObject);
			}
			return structureObjectList;
		}
		catch (Exception e)
		{
			DynaLogger.error(e.getMessage(), e);
			if (e instanceof DynaDataExceptionSQL)
			{
				throw (DynaDataExceptionSQL) e;
			}
			else if (e instanceof DynaDataExceptionAll)
			{
				throw (DynaDataExceptionAll) e;
			}
			throw new DynaDataExceptionAll("listSimpleStructureOfRelation exception. ", e, DataExceptionEnum.DS_QUERY_DATA_EXCEPTION);
		}
	}
	
	/**
	 * 关系模板只需要取单层结构, 结构模板才需要取树形结构
	 *
	 * @param viewObjectGuid
	 * @param relationTemplate
	 * @param dataRule
	 * @param isCheckAcl
	 * @param sessionId
	 * @param searchCondition
	 * @param fixTranId
	 * @return
	 * @throws ServiceRequestException
	 */
	@SuppressWarnings("unchecked")
	public List<String> listEnd2ClassOfRelation(String viewGuid, String viewClassNameOrGuid, String struClassGuid, String sessionId)
			throws ServiceRequestException
	{
		try
		{
			
			String tableName = DataServer.getDSCommonService().getTableName(struClassGuid);
			List<String> end2ClassList = this.dynaObjectMapper.selectEnd2ClassOfStruc(tableName, viewGuid);
			return end2ClassList;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e instanceof DynaDataExceptionSQL)
			{
				throw (DynaDataExceptionSQL) e;
			}
			else if (e instanceof DynaDataExceptionAll)
			{
				throw (DynaDataExceptionAll) e;
			}
			throw new DynaDataExceptionAll("query StructureObject  error", e, DataExceptionEnum.DS_QUERY_DATA_EXCEPTION);
		}
	}

	
	
	/**
	 * 关系模板只需要取单层结构, 结构模板才需要取树形结构
	 *
	 * @param viewObjectGuid
	 * @param relationTemplate
	 * @param dataRule
	 * @param isCheckAcl
	 * @param sessionId
	 * @param searchCondition
	 * @param fixTranId
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<StructureObject> listSimpleStructureOfRelation(ObjectGuid end1ObjectGuid, String templateId, String viewClassNameOrGuid, String struClassGuid, String sessionId)
			throws ServiceRequestException
	{
		try
		{
			DynamicSelectParamData paramData = null;
			if (end1ObjectGuid.isMaster())
			{
				if (StringUtils.isNullString(end1ObjectGuid.getMasterGuid()))
				{
					throw new DynaDataExceptionAll("invoke error.", null, DataExceptionEnum.DS_KEY_VALUE_ERROR);
				}
				paramData = RelationFromQuerySqlBuilder.buildStructureInfoSearchParamDataByEnd1Master(sessionId, templateId, viewClassNameOrGuid, struClassGuid,
						end1ObjectGuid.getMasterGuid());
			}
			else
			{
				if (StringUtils.isNullString(end1ObjectGuid.getMasterGuid()))
				{
					throw new DynaDataExceptionAll("invoke error.", null, DataExceptionEnum.DS_KEY_VALUE_ERROR);
				}
				paramData = RelationFromQuerySqlBuilder.buildStructureInfoSearchParamDataByEnd1(sessionId, templateId, viewClassNameOrGuid, struClassGuid,
						end1ObjectGuid.getGuid());
			}
			List<FoundationObject> list = DataServer.getDSCommonService().executeQuery(paramData);
			if (SetUtils.isNullList(list))
			{
				return null;
			}
			List<StructureObject> structureObjectList = new ArrayList<>();
			for (FoundationObject fo : list)
			{
				StructureObjectImpl structureObject = new StructureObjectImpl();
				structureObject.putAll((FoundationObjectImpl) fo);
				structureObject.put("END2$MASTERFK", structureObject.get("END2$MASTER"));
				structureObject.put("END2$CLASSGUID", structureObject.get("END2$CLASS"));
				structureObject.put("END2$MASTERFK", structureObject.get("END2$MASTER"));
				structureObject.put("END2$CLASSGUID", structureObject.get("END2$CLASS"));
				structureObjectList.add(structureObject);
			}
			return structureObjectList;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e instanceof DynaDataExceptionSQL)
			{
				throw (DynaDataExceptionSQL) e;
			}
			else if (e instanceof DynaDataExceptionAll)
			{
				throw (DynaDataExceptionAll) e;
			}
			throw new DynaDataExceptionAll("query StructureObject  error", e, DataExceptionEnum.DS_QUERY_DATA_EXCEPTION);
		}

	}

	/**
	 * 关系模板只需要取单层结构, 结构模板才需要取树形结构
	 *
	 * @param viewObjectGuid
	 * @param relationTemplate
	 * @param dataRule
	 * @param isCheckAcl
	 * @param sessionId
	 * @param searchCondition
	 * @param fixTranId
	 * @return
	 * @throws ServiceRequestException
	 */
	@SuppressWarnings("unchecked")
	public List<StructureObject> listObjectOfRelation(ObjectGuid viewObjectGuid, RelationTemplate relationTemplate, DataRule dataRule, boolean isCheckAcl, String sessionId,
			SearchCondition searchCondition) throws ServiceRequestException
	{
		ObjectGuid objectGuid = new ObjectGuid();
		objectGuid.setClassGuid(relationTemplate.getStructureClassGuid());
		objectGuid.setClassName(relationTemplate.getStructureClassName());
		if (searchCondition == null)
		{
			searchCondition = SearchConditionFactory.createSearchCondition(objectGuid, null, true);
		}
		if (searchCondition.getObjectGuid() == null || (searchCondition.getObjectGuid().getClassGuid() == null && searchCondition.getObjectGuid().getClassName() == null))
		{
			searchCondition.getObjectGuid().setClassGuid(relationTemplate.getStructureClassGuid());
			searchCondition.getObjectGuid().setClassName(relationTemplate.getStructureClassName());
		}

		List<String> end2FieldList = this.getFoundationFieldListFromCondition(searchCondition);
		Map<String, ClassObject> end2TableMap = new HashMap<>();
		List<String> end2ClassList = listEnd2ClassOfRelation(viewObjectGuid.getGuid(), viewObjectGuid.getClassGuid(), relationTemplate.getStructureClassGuid(), sessionId);
		if (!SetUtils.isNullList(end2ClassList))
		{
			for (String end2ClassGuid : end2ClassList)
			{
				ClassObject end2ClassObject = DataServer.getClassModelService().getClassObjectByGuid(end2ClassGuid);
				if (end2ClassList!=null)
				{
					String end2ClassName = end2ClassObject.getName();
					String end2Table = DataServer.getDSCommonService().getTableName(end2ClassName);
					end2TableMap.put(end2Table, end2ClassObject);
				}
			}
			FoundationObject viewObject = DataServer.getInstanceService().getFoundationObject(viewObjectGuid, false, sessionId);
			List<StructureObject> structureObjectList = new ArrayList<>();
			for (String end2Table : end2TableMap.keySet())
			{
				DynamicSelectParamData paramData = RelationFromQuerySqlBuilder.buildInstanceSearchParamData(sessionId, viewObjectGuid, relationTemplate, searchCondition,
						end2TableMap.get(end2Table), end2FieldList, dataRule);
				List<FoundationObject> tempStructureObjectList = DataServer.getDSCommonService().executeQuery(paramData);
				if (tempStructureObjectList != null)
				{
					for (FoundationObject fo : tempStructureObjectList)
					{
						StructureObjectImpl structureObject = new StructureObjectImpl();
						structureObject.putAll((FoundationObjectImpl) fo);
						structureObject.put("END1", viewObject.get("END1"));
						structureObject.put("END1$MASTER", viewObject.get("END1$MASTER"));
						structureObject.put("END1$CLASS", viewObject.get("END1$CLASS"));
						structureObject.put("END2$MASTERFK", structureObject.get("END2$MASTER"));
						structureObject.put("END2$CLASSGUID", structureObject.get("END2$CLASS"));
						structureObject.put("END2$MASTERFK", structureObject.get("END2$MASTER"));
						structureObject.put("END2$CLASSGUID", structureObject.get("END2$CLASS"));
						structureObject.put("VIEWFK$MASTER", viewObjectGuid.getMasterGuid());
						structureObject.put("VIEWFK$CLASS", viewObjectGuid.getClassGuid());
						structureObject.put("VIEWFK$MASTERFK", viewObjectGuid.getMasterGuid());
						structureObject.put("VIEWFK$CLASSGUID", viewObjectGuid.getClassGuid());
						structureObject.setViewObjectGuid(viewObjectGuid);
						structureObjectList.add(structureObject);
					}
				}
			}

			if (SetUtils.isNullList(structureObjectList))
			{
				return structureObjectList;
			}

			List<StructureObject> results = (List<StructureObject>) this.restoreStructureObject(structureObjectList, isCheckAcl, sessionId);

			results.sort((o1, o2) -> {
				String sequence1 = StringUtils.flushLeft('0', 32, o1.getSequence());
				String sequence2 = StringUtils.flushLeft('0', 32, o2.getSequence());

				return sequence1.compareTo(sequence2);
			});

			return results;
		}
		return null;
	}

	private List<String> getFoundationFieldListFromCondition(SearchCondition searchCondition)
	{
		List<String> returnList = new ArrayList<>();

		List<String> allList = searchCondition.getResultFieldList();
		if (allList != null)
		{
			for (int i = allList.size() - 1; i > -1; i--)
			{
				String fieldName = allList.get(i);
				if (fieldName.startsWith(ViewObject.PREFIX_END2))
				{
					fieldName = fieldName.substring(ViewObject.PREFIX_END2.length());
					returnList.add(fieldName);
					allList.remove(i);
				}
			}
		}
		return returnList;
	}

	/**
	 * 根据structureguid获取structure
	 *
	 * @param StructureGuid
	 * @param searchCondition
	 * @param isCheckAcl
	 * @param sessionId
	 * @return
	 * @throws ServiceRequestException
	 */
	protected StructureObject getStructureObject(ObjectGuid objectGuid, boolean isCheckAcl, boolean isBOM, String sessionId) throws ServiceRequestException
	{
		try
		{
			// 取得结构对象
			DynamicSelectParamData paramData = ClassInstanceGetSqlBuilder.buildInstanceSearchParamData(objectGuid, sessionId);
			List<FoundationObject> list = DataServer.getDSCommonService().executeQuery(paramData);
			if (SetUtils.isNullList(list))
			{
				return null;
			}
			FoundationObject fo = list.get(0);

			String end2Guid = (String) fo.get("END2$");
			String end2Class = (String) fo.get("END2$CLASS");
			String end2Master = (String) fo.get("END2$MASTER");

			// 取得view
			ObjectGuid viewObjectGuid = new ObjectGuid((String) fo.get("VIEWCLASSGUID$"), null, (String) fo.get("VIEWFK$"), null);
			paramData = ClassInstanceGetSqlBuilder.buildInstanceSearchParamData(viewObjectGuid, sessionId);
			List<FoundationObject> viewList = DataServer.getDSCommonService().executeQuery(paramData);
			if (SetUtils.isNullList(viewList))
			{
				return null;
			}
			ViewObject viewObject = new ViewObject(viewList.get(0));
			ObjectGuid end2ObjectGuid = new ObjectGuid(end2Class, null, end2Guid, end2Master, null);

			StructureObjectImpl structureObject = (isBOM) ? new BOMStructure() : new StructureObjectImpl();
			structureObject.setEnd2ObjectGuid(end2ObjectGuid);
			structureObject.setViewObjectGuid(viewObjectGuid);
			structureObject.setEnd1ObjectGuid(viewObject.getEnd1ObjectGuid());
			structureObject.putAll((FoundationObjectImpl) fo);
			structureObject.put(ViewObject.TEMPLATE_ID, viewObject.getTemplateID());
			structureObject.put("END2$MASTERFK", structureObject.get("END2$MASTER"));
			structureObject.put("END2$CLASSGUID", structureObject.get("END2$CLASS"));
			return structureObject;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e instanceof DynaDataExceptionSQL)
			{
				throw (DynaDataExceptionSQL) e;
			}
			else if (e instanceof DynaDataExceptionAll)
			{
				throw (DynaDataExceptionAll) e;
			}
			throw new DynaDataExceptionAll("query StructureObject  error", e, DataExceptionEnum.DS_QUERY_DATA_EXCEPTION);
		}
	}

	/**
	 * 反向获取structure
	 *
	 * @param objectGuid
	 * @param isCheckAcl
	 * @param sessionId
	 * @param viewName
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<FoundationObject> listWhereUsed(ObjectGuid objectGuid, String templateName, String viewClassName, boolean isPrice, SearchCondition end1SearchCondition,
			SearchCondition struSearchCondition, boolean isViewHistory, boolean isCheckAcl, String sessionId) throws ServiceRequestException
	{
		try
		{
			AclService aclService = DataServer.getAclService();
			if (isCheckAcl && !aclService.hasAuthority(objectGuid, AuthorityEnum.READ, sessionId))
			{
				return null;
			}
			DataServer.getDSCommonService().decorateClass(end1SearchCondition);
			DataServer.getDSCommonService().decorateClass(struSearchCondition);
			DynamicSelectParamData paramData = RelationToQuerySqlBuilder.buildInstanceSearchParamData(sessionId, objectGuid, templateName, viewClassName, isPrice,
					end1SearchCondition, struSearchCondition, isViewHistory);
			List<FoundationObject> selectResultList = DataServer.getDSCommonService().executeQuery(paramData);
			if (!SetUtils.isNullList(selectResultList))
			{
				List<FoundationObject> list = new ArrayList<>();
				for (FoundationObject foundationObject : selectResultList)
				{
					list.add(foundationObject);
					foundationObject.getObjectGuid().setHasAuth(true);
					if (isCheckAcl)
					{
						String viewGuid = (String) foundationObject.get(StructureObject.STRUCTURE_DOLLAR_PREFIX + "VIEWFK$");

						ObjectGuid viewObjectGuid = new ObjectGuid();
						viewObjectGuid.setGuid(viewGuid);
						viewObjectGuid.setClassName(viewClassName);
						if (!aclService.hasAuthority(viewObjectGuid, AuthorityEnum.READ, sessionId)
								|| !aclService.hasAuthority(foundationObject.getObjectGuid(), AuthorityEnum.READ, sessionId))
						{
							foundationObject.getObjectGuid().setHasAuth(false);
						}
					}
				}
				return list;
			}
			return selectResultList;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (e instanceof DynaDataExceptionSQL)
			{
				throw (DynaDataExceptionSQL) e;
			}
			else if (e instanceof DynaDataExceptionAll)
			{
				throw (DynaDataExceptionAll) e;
			}
			throw new DynaDataExceptionAll("listWhereReferenced guid =" + objectGuid.getGuid(), e, DataExceptionEnum.DS_QUERY_DATA_EXCEPTION);
		}

	}

	/**
	 * 更新结构
	 *
	 * @param structureObject
	 * @param isCheckAcl
	 * @param isCheckConnect
	 * @param sessionId
	 * @throws ServiceRequestException
	 */
	protected StructureObject save(StructureObject structureObject, boolean isPrecise, boolean isCheckAcl, String sessionId, String fixTranId) throws ServiceRequestException
	{
		AclService aclService = DataServer.getAclService();

		// 变更839 第8条 判断编辑结构权限
		if (isCheckAcl && !aclService.hasAuthority(structureObject.getViewObjectGuid(), AuthorityEnum.EDITLINK, sessionId))
		{
			throw new DynaDataExceptionAll("save structure error, no editlink auth. bomview guid  = " + structureObject.getViewObjectGuid().getGuid(), null,
					DataExceptionEnum.DS_NO_EDITLINK_AUTH);
		}

		String structureGuid = structureObject.getObjectGuid().getGuid();
		String className = structureObject.getObjectGuid().getClassName();
		if (StringUtils.isNullString(className))
		{
			String classGuid = structureObject.getObjectGuid().getClassGuid();
			className = DataServer.getClassModelService().getClassObjectByGuid(classGuid).getName();
		}
		ClassObject classObject = DataServer.getClassModelService().getClassObject(className);
		Session session = DataServer.getDSCommonService().getSession(sessionId);
		String curUser = session.getUserGuid();

		Date sysDate = DateFormat.getSysDate();
		String sysTable = DataServer.getDSCommonService().getTableName(className);

		Map<String, List<SqlParamData>> tableParamMap = new HashMap<>();
		tableParamMap.put(sysTable, new ArrayList<>());
		tableParamMap.get(sysTable).add(new SqlParamData("UPDATETIME", sysDate, DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.DATETIME)));
		tableParamMap.get(sysTable).add(new SqlParamData("UPDATEUSER", curUser, DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.STRING)));

		String originalEnd2MasterGuid = (String) structureObject.getOriginalValue("END2$MASTERFK");
		String newEnd2MasterGuid = structureObject.getEnd2ObjectGuid().getMasterGuid();
		String originalEnd2Guid = (String) structureObject.getOriginalValue("END2$");
		String newEnd2Guid = structureObject.getEnd2ObjectGuid().getGuid();

		String end2Guid = null;
		String end2Master = null;
		String end2ClassGuid = null;
		if (!newEnd2MasterGuid.equals(originalEnd2MasterGuid))
		{
			end2Master = newEnd2MasterGuid;
			end2ClassGuid = structureObject.getEnd2ObjectGuid().getClassGuid();

			tableParamMap.get(sysTable).add(new SqlParamData("END2$MASTER", newEnd2MasterGuid, DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.STRING)));
			tableParamMap.get(sysTable)
					.add(new SqlParamData("END2$CLASS", structureObject.getEnd2ObjectGuid().getClassGuid(), DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.STRING)));
			if (isPrecise)
			{
				end2Guid = newEnd2Guid;
				tableParamMap.get(sysTable).add(new SqlParamData("END2", newEnd2Guid, DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.STRING)));
			}
		}
		else if (newEnd2Guid != null && !newEnd2Guid.equals(originalEnd2Guid))
		{
			end2Guid = newEnd2Guid;
			tableParamMap.get(sysTable).add(new SqlParamData("END2", newEnd2Guid, DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.STRING)));
		}
		else
		{
		}

		if (classObject.getFieldList() != null)
		{
			for (ClassField field : classObject.getFieldList())
			{
				if (field.getName().equals(SystemClassFieldEnum.UPDATETIME.getName()) || field.getName().equals(SystemClassFieldEnum.UPDATEUSER.getName()))
				{
					continue;
				}

				if (!structureObject.isChanged(field.getName()) || field.getName().equals(SystemClassFieldEnum.END2.getName()))
				{
					continue;
				}

				Object fieldValue = structureObject.get(field.getName());
				FieldTypeEnum fieldType = field.getType();

				int fieldSize = 128;
				if (field.getFieldSize() != null && (fieldType == FieldTypeEnum.MULTICODE || fieldType == FieldTypeEnum.STRING))
				{
					fieldSize = Integer.parseInt(field.getFieldSize());
				}

				if ((fieldType == FieldTypeEnum.MULTICODE || fieldType == FieldTypeEnum.STRING) && fieldValue != null)
				{
					int fieldLenght = 0;
					fieldLenght = fieldValue.toString().getBytes(StandardCharsets.UTF_8).length;
					if (fieldSize < fieldLenght)
					{
						throw new DynaDataExceptionAll("Value is too large.", null, DataExceptionEnum.DS_VALUE_TOO_LARGE, field.getName(), fieldValue.toString().length(),
								fieldSize);
					}
				}

				String tableName = DataServer.getDSCommonService().getTableName(className, field.getName());
				if (!tableParamMap.containsKey(tableName))
				{
					tableParamMap.put(tableName, new ArrayList<>());
				}
				tableParamMap.get(tableName).add(new SqlParamData(field.getColumnName(), fieldValue, DSCommonUtil.getJavaTypeOfField(fieldType)));

				ObjectFieldTypeEnum objectFieldTypeOfField = DataServer.getDSCommonService().getObjectFieldTypeOfField(field, session.getBizModelName());
				if (objectFieldTypeOfField == ObjectFieldTypeEnum.OBJECT)
				{
					if (fieldValue == null)
					{
						tableParamMap.get(tableName).add(new SqlParamData(field.getColumnName() + "$CLASS", null, DSCommonUtil.getJavaTypeOfField(fieldType)));
						tableParamMap.get(tableName).add(new SqlParamData(field.getColumnName() + "$MASTER", null, DSCommonUtil.getJavaTypeOfField(fieldType)));
					}
					else
					{
						String tempField = field.getName();
						if (field.isSystem())
						{
							tempField = tempField.substring(0, tempField.length() - 1);
						}
						tableParamMap.get(tableName)
								.add(new SqlParamData(field.getColumnName() + "$CLASS", structureObject.get(tempField + "$CLASS"), DSCommonUtil.getJavaTypeOfField(fieldType)));
						tableParamMap.get(tableName)
								.add(new SqlParamData(field.getColumnName() + "$MASTER", structureObject.get(tempField + "$MASTER"), DSCommonUtil.getJavaTypeOfField(fieldType)));
					}
				}
			}
		}
		try
		{
//			DataServer.getTransactionManager().startTransaction(fixTranId);

			List<SqlParamData> whereParamList = new ArrayList<>();
			whereParamList.add(new SqlParamData("GUID", structureGuid, DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.STRING)));

			DynamicUpdateParamData updateParamData = new DynamicUpdateParamData();
			updateParamData.setTableName(sysTable);
			updateParamData.setUpdateParamList(tableParamMap.get(sysTable));
			updateParamData.setWhereParamList(whereParamList);

			if (this.dynaObjectMapper.updateDynamic(updateParamData) == 0)
			{
				throw new DynaDataExceptionAll("save structureObject failed. guid = " + structureGuid, null, DataExceptionEnum.DS_SAVE_STRUCTURE_REVISION,
						structureObject.getName());
			}

			tableParamMap.remove(sysTable);
			if (!SetUtils.isNullMap(tableParamMap))
			{
				for (String tableName : tableParamMap.keySet())
				{
					whereParamList.clear();
					whereParamList.add(new SqlParamData("FOUNDATIONFK", structureGuid, DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.STRING)));

					updateParamData.setTableName(tableName);
					updateParamData.setUpdateParamList(tableParamMap.get(tableName));
					updateParamData.setWhereParamList(whereParamList);

					if (this.dynaObjectMapper.updateDynamic(updateParamData) == 0)
					{
						throw new DynaDataExceptionAll("save structureObject gen table failed. guid = " + structureGuid, null, DataExceptionEnum.DS_SAVE_STRUCTURE_REVISION_GEN,
								structureObject.getName());
					}
				}
			}

			structureObject.put(SystemClassFieldEnum.UPDATETIME.getName(), sysDate);
			structureObject.put(SystemClassFieldEnum.UPDATEUSER.getName(), curUser);

			if (end2Guid != null)
			{
				structureObject.put(SystemClassFieldEnum.END2.getName(), end2Guid);
			}
			if (end2Master != null)
			{
				structureObject.put(SystemClassFieldEnum.END2.getName() + "CLASS", end2ClassGuid);
				structureObject.put(SystemClassFieldEnum.END2.getName() + "MASTER", end2Master);
			}

//			DataServer.getTransactionManager().commitTransaction();

			StructureObjectImpl retObject = (StructureObjectImpl) structureObject.getClass().getConstructor().newInstance();
			retObject.sync(structureObject);

			return retObject;
		}
		catch (Exception e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof DynaDataExceptionSQL)
			{
				throw (DynaDataExceptionSQL) e;
			}
			else if (e instanceof DynaDataExceptionAll)
			{
				throw (DynaDataExceptionAll) e;
			}
			throw new DynaDataExceptionAll("save structureObject guid =" + structureGuid, e, DataExceptionEnum.DS_SAVE_STRUCTURE, structureObject.getName());
		}
	}

	public List<?> restoreStructureObject(List<? extends StructureObject> structureList, boolean isCheckAcl, String sessionId) throws ServiceRequestException
	{
		List<String> prefixList = Constants.getPrefixForListStructure();
		if (!SetUtils.isNullList(structureList))
		{
			for (StructureObject structure : structureList)
			{
				for (String prefix : prefixList)
				{
					FoundationObjectImpl fObject = new FoundationObjectImpl();
					List<String> findKeysList = new ArrayList<>();
					for (String key : ((StructureObjectImpl) structure).keySet())
					{
						if (key.startsWith(prefix))
						{
							String newFieldName = key.substring(prefix.length());
							fObject.put(newFieldName, structure.get(key));

							findKeysList.add(key);
						}
					}
					if (prefix.equals(ViewObject.PREFIX_END2))
					{
						structure.put(SystemClassFieldEnum.END2.getName(), fObject.getObjectGuid().getGuid());
						structure.put(SystemClassFieldEnum.END2.getName() + "NAME", fObject.get(SystemClassFieldEnum.NAME.getName()));
					}

					// 部分字段需要使用end2的值
					List<String> defaultFieldListForSc = Constants.getDefaultFieldListForStructure();
					for (String fieldName : defaultFieldListForSc)
					{
						structure.put(fieldName, fObject.get(fieldName));
					}

					structure.put(prefix, fObject);

					if (!SetUtils.isNullList(findKeysList))
					{
						for (String findKey : findKeysList)
						{
							((StructureObjectImpl) structure).remove(findKey);
						}
					}
				}
				((StructureObjectImpl) structure).getOriginalMap().putAll(((StructureObjectImpl) structure));
			}
		}

		AclService aclService = DataServer.getAclService();
		Map<String, Boolean> viewAuthMap = new HashMap<>();
		List<StructureObject> results = new ArrayList<>();
		if (!SetUtils.isNullList(structureList))
		{
			for (StructureObject structureObject : structureList)
			{
				StructureObjectImpl structure = (StructureObjectImpl) structureObject;
				boolean hasAuth = true;
				if (isCheckAcl)
				{
					String viewGuid = structure.getViewObjectGuid().getGuid();
					if (viewAuthMap.containsKey(viewGuid))
					{
						hasAuth = viewAuthMap.get(viewGuid);
					}
					else
					{
						hasAuth = aclService.hasAuthority(structure.getViewObjectGuid(), AuthorityEnum.READ, sessionId);
						viewAuthMap.put(viewGuid, hasAuth);
					}

					if (hasAuth)
					{
						hasAuth = aclService.hasAuthority(structure.getEnd2ObjectGuid(), AuthorityEnum.READ, sessionId);
					}
				}

				structure.getEnd2ObjectGuid().setHasAuth(hasAuth);

				results.add(structure);
			}
		}

		return results;
	}

	public void copyBomOrRelation(String viewClassGuid, String origViewGuid, String destViewGuid, String structureClassGuid, String fotype, Map<String, Object> specialField,
			String sessionId, String fixTranId) throws ServiceRequestException
	{
		List<StructureObject> list = this.listSimpleStructureOfRelation(origViewGuid, viewClassGuid, structureClassGuid, sessionId);
		if (SetUtils.isNullList(list))
		{
			return;
		}
		try
		{
			ClassObject classObject = DataServer.getClassModelService().getClassObjectByGuid(structureClassGuid);
//			DataServer.getTransactionManager().startTransaction(fixTranId);
			for (StructureObject fo : list)
			{
				StructureObjectImpl structureObject = new StructureObjectImpl();
				structureObject.putAll((StructureObjectImpl) fo);
				structureObject.setGuid(null);
				structureObject.put("CLASSNAME$", classObject.getName());
				structureObject.put("VIEWFK$CLASS", viewClassGuid);
				structureObject.put("VIEWFK$CLASSGUID", viewClassGuid);
				structureObject.put("VIEWCLASSGUID", viewClassGuid);
				structureObject.put("VIEWFK$", destViewGuid);
				if ("3".equals(fotype))
				{
					structureObject.put("isBOMModified", "N");
				}
				if (specialField != null)
				{
					for (String key : specialField.keySet())
					{
						if (BOMStructure.BOMKEY.equalsIgnoreCase(key))
						{
							structureObject.put(key, UUID.randomUUID().toString().replace("-", ""));
						}
						else
						{
							structureObject.put(key, specialField.get(key));
						}
					}
				}
				this.create(structureObject, sessionId, fixTranId);
			}
//			DataServer.getTransactionManager().commitTransaction();
		}
		catch (Exception e)
		{
			e.printStackTrace();
//			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof DynaDataExceptionSQL)
			{
				throw (DynaDataExceptionSQL) e;
			}
			else if (e instanceof DynaDataExceptionAll)
			{
				throw (DynaDataExceptionAll) e;
			}
			throw new DynaDataExceptionAll("copyBomOrRelation exception. ", e, DataExceptionEnum.DS_COPY_BOM_OR_RELATION_ERROR);
		}
	}

}
