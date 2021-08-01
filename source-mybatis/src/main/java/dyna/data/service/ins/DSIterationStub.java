/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 处理实例和文件夹的关联关系
 * JiangHL 2011-5-10
 */
package dyna.data.service.ins;

import dyna.common.SearchCondition;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.model.cls.ClassObject;
import dyna.common.bean.model.code.CodeItem;
import dyna.common.bean.model.code.CodeObject;
import dyna.common.dto.Session;
import dyna.common.dto.cfm.ClassficationFeature;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dtomapper.FoundationObjectMapper;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.sqlbuilder.plmdynamic.SqlParamData;
import dyna.common.sqlbuilder.plmdynamic.update.DynamicUpdateParamData;
import dyna.common.systemenum.*;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.context.DataServerContext;
import dyna.data.service.DSAbstractServiceStub;
import dyna.data.service.sdm.FieldValueEqualsFilter;
import dyna.data.sqlbuilder.DSIterationSqlBuilder;
import dyna.dbcommon.exception.DynaDataExceptionAll;
import dyna.dbcommon.exception.DynaDataExceptionSQL;
import dyna.dbcommon.util.DSCommonUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;
import java.util.*;

/**
 * 处理实例和文件夹的关联关系
 *
 * @author JiangHL
 */
public class DSIterationStub extends DSAbstractServiceStub<InstanceServiceImpl>
{

	@Autowired
	private FoundationObjectMapper              foundationObjectMapper;
	/**
	 * @param context
	 * @param service
	 * @throws DynaDataException
	 */
	public DSIterationStub(DataServerContext context, InstanceServiceImpl service)
	{
		super(context, service);
	}

	/**
	 * 获取iteration列表,只返回系统字段
	 *
	 * @param objectGuid
	 * @param iterationId
	 * @param isCheckAcl
	 * @param sessionId
	 * @return
	 * @throws DynaDataException
	 */
	@SuppressWarnings("unchecked")
	protected List<FoundationObject> listIteration(ObjectGuid objectGuid, Integer iterationId, SearchCondition searchCondition, boolean isCheckAcl, String sessionId)
			throws ServiceRequestException
	{
		if ((ISCHECKACL && isCheckAcl) && !DataServer.getAclService().hasAuthority(objectGuid, AuthorityEnum.READ, sessionId))
		{
			throw new DynaDataExceptionAll("READ", null, DataExceptionEnum.DS_NO_READ_AUTH);
		}

		String baseTable = DataServer.getDSCommonService().getRealBaseTableName(objectGuid.getClassName());

		String selectString = DSIterationSqlBuilder.getSelectSqlForInteration("I$", objectGuid.getClassName(), DataServer.getDSCommonService().getSession(sessionId));

		Map<String, Object> selectMap = new HashMap<>();
		selectMap.put("TABLE", baseTable);
		selectMap.put("ITERATIONID", iterationId == null ? "" : String.valueOf(iterationId));
		selectMap.put("FOUNDATIONFK", objectGuid.getGuid());
		selectMap.put("FIELDLIST", selectString);
		List<FoundationObject> selectResultList;
		try
		{
			selectResultList = this.foundationObjectMapper.selectIteration(selectMap);
		}
		catch (Exception e)
		{
			throw new DynaDataExceptionSQL("listIteration error. ", e, DataExceptionEnum.DS_LIST_ITERATION);
		}
		return selectResultList;
	}

	/**
	 * 或滚到某一个小版本
	 *
	 * @param objectGuid
	 *            revision.objectguid
	 * @param iterationId
	 *            小版本id
	 * @param isOwnerOnly
	 *            是否只有自己才能回滚
	 * @param sessionId
	 * @return
	 * @throws DynaDataException
	 * @throws SQLException
	 */
	protected void rollbackIteration(ObjectGuid objectGuid, Integer iterationId, boolean isOwnerOnly, String sessionId) throws DynaDataException, ServiceRequestException
	{
		try
		{
			String result = this.rollbackIteration(objectGuid.getGuid(), objectGuid.getClassGuid(), iterationId, isOwnerOnly, sessionId);

			if (result.equalsIgnoreCase("N"))
			{
				throw new DynaDataExceptionAll("rollbackRevision error.", null, DataExceptionEnum.ROLLBACK_OWNER_ONLY);
			}
			else if (result.equalsIgnoreCase("UNWIP"))
			{
				throw new DynaDataExceptionAll("rollbackRevision error.", null, DataExceptionEnum.DS_WIP_ONLY);
			}
			else if (result.equalsIgnoreCase("WFDATE"))
			{
				throw new DynaDataExceptionAll("rollbackRevision error.", null, DataExceptionEnum.DS_WF_DATA);
			}
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
			throw new DynaDataExceptionAll("rollbackRevision() guid=" + objectGuid.getGuid(), e, DataExceptionEnum.DS_CANCEL_CHECKOUT);
		}
	}

	@SuppressWarnings("unchecked")
	protected String rollbackIteration(String guid, String classGuid, Integer iterationId, boolean isOwnerOnly, String sessionId) throws ServiceRequestException
	{
		Session session = DataServer.getDSCommonService().getSession(sessionId);

		try
		{
			Map<String, Object> param = new HashMap<>();
			param.put("SELECT", "status, ischeckout");
			param.put("FROM", DataServer.getDSCommonService().getTableName(classGuid));
			param.put("WHERE", "guid = '" + guid + "'");
			Map<String, String> result = (Map<String, String>) this.dynaObjectMapper.selectAutoHalf(param);
			if (SetUtils.isNullMap(result))
			{
				throw new DynaDataExceptionAll("rollbackRevision error.", null, DataExceptionEnum.DS_CANCEL_CHECKOUT_DATA_LOST);
			}
			SystemStatusEnum status = SystemStatusEnum.getStatusEnum(result.get("STATUS"));
			if (status != SystemStatusEnum.WIP && status != SystemStatusEnum.ECP)
			{
				return "UNWIP";
			}

			ClassObject classObject = DataServer.getClassModelService().getClassObjectByGuid(classGuid);

			// I表所有回滚字段
			String iterationFields = DSIterationSqlBuilder.getSelectSqlForInteration("I$", classObject.getName(), DataServer.getDSCommonService().getSession(sessionId));

			// 取得I表数据
			param.clear();
			param.put("FIELDS", iterationFields);
			param.put("TABLENAME", DataServer.getDSCommonService().getRealBaseTableName(classGuid) + "_I I$");
			param.put("WHERESQL", "I$.FOUNDATIONFK = '" + guid + "' and I$.iterationid = " + iterationId);
			FoundationObject iterationFoundation = (FoundationObject) this.foundationObjectMapper.selectOneShortHalf(param);
			if (iterationFoundation == null)
			{
				return "N";
			}
			String systemTableName = DataServer.getDSCommonService().getTableName(classGuid);

			// 更新0表数据
			List<ClassField> updateFieldList = DSIterationSqlBuilder.listRollBackFieldForUpdate(classObject);
			Map<String, List<SqlParamData>> paramOfTableMap = new HashMap<>();
			for (ClassField field : updateFieldList)
			{

				String tableName = DataServer.getDSCommonService().getTableName(classObject.getName(), field.getName());
				if (!paramOfTableMap.containsKey(tableName))
				{
					paramOfTableMap.put(tableName, new ArrayList<>());
				}
				paramOfTableMap.get(tableName)
						.add(new SqlParamData(field.getColumnName(), iterationFoundation.get(field.getName()), DSCommonUtil.getJavaTypeOfField(field.getType())));
				if (field.getType() == FieldTypeEnum.OBJECT)
				{
					ObjectFieldTypeEnum objectFieldTypeOfField = DataServer.getDSCommonService().getObjectFieldTypeOfField(field, session.getBizModelName());
					if (objectFieldTypeOfField == ObjectFieldTypeEnum.OBJECT)
					{
						String tempFieldName = field.getName();
						if (field.isSystem())
						{
							tempFieldName = field.getName().substring(0, field.getName().length() - 1);
						}
						paramOfTableMap.get(tableName).add(new SqlParamData(field.getColumnName() + "$CLASS", iterationFoundation.get(tempFieldName + "$CLASS"),
								DSCommonUtil.getJavaTypeOfField(field.getType())));
						paramOfTableMap.get(tableName).add(new SqlParamData(field.getColumnName() + "$MASTER", iterationFoundation.get(tempFieldName + "$MASTER"),
								DSCommonUtil.getJavaTypeOfField(field.getType())));
					}
				}
			}
			List<SqlParamData> updateParamList = paramOfTableMap.get(systemTableName);
			List<SqlParamData> whereParamList = new ArrayList<>();
			whereParamList.add(new SqlParamData("GUID", guid, DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.STRING)));
			if (isOwnerOnly)
			{
				whereParamList.add(new SqlParamData("CHECKOUTUSER", session.getUserGuid(), DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.STRING)));
			}

			DynamicUpdateParamData data = new DynamicUpdateParamData();
			data.setTableName(systemTableName);
			data.setWhereParamList(whereParamList);
			data.setUpdateParamList(updateParamList);
			int dataCount = this.dynaObjectMapper.updateDynamic(data);
			if (dataCount == 0)
			{
				return "N";
			}
			paramOfTableMap.remove(systemTableName);
			if (!SetUtils.isNullMap(paramOfTableMap))
			{
				whereParamList = new ArrayList<>();
				whereParamList.add(new SqlParamData("FOUNDATIONFK", guid, DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.STRING)));
				for (String tableName : paramOfTableMap.keySet())
				{
					data.setTableName(tableName);
					data.setWhereParamList(whereParamList);
					data.setUpdateParamList(paramOfTableMap.get(tableName));
					this.dynaObjectMapper.updateDynamic(data);
				}
			}

			List<SqlParamData> masterUpdateParamList = new ArrayList<>();
			masterUpdateParamList.add(new SqlParamData("md_id", iterationFoundation.get("ID$"), DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.STRING)));
			if (StringUtils.isNullString((String) iterationFoundation.get("UNIQUES$")))
			{
				masterUpdateParamList.add(new SqlParamData("uniques", iterationFoundation.getObjectGuid().getMasterGuid(), DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.STRING)));
			}
			else
			{
				masterUpdateParamList.add(new SqlParamData("uniques", iterationFoundation.get("UNIQUES$"), DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.STRING)));
			}
			whereParamList = new ArrayList<>();
			whereParamList.add(new SqlParamData("GUID", iterationFoundation.getObjectGuid().getMasterGuid(), DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.STRING)));
			data.setTableName(DataServer.getDSCommonService().getRealBaseTableName(classObject.getName()) + "_mast");
			data.setWhereParamList(whereParamList);
			data.setUpdateParamList(masterUpdateParamList);

			this.dynaObjectMapper.updateDynamic(data);
			param.clear();
			param.put("TABLENAME", DataServer.getDSCommonService().getRealBaseTableName(classGuid));
			param.put("ITERATIONID", iterationId);
			param.put("GUID", guid);
			this.foundationObjectMapper.deleteOverIteration(param);
			return this.rollBackClfIterationData(guid, classGuid, iterationId, session);
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
			throw new DynaDataExceptionAll("rollbackRevision() guid=" + guid, e, DataExceptionEnum.DS_CANCEL_CHECKOUT);
		}
	}

	protected void createIterationData(String foundationGuid, Integer iterationId, String className, int iterationLimit, Session session)
			throws SQLException, ServiceRequestException
	{
		Map<String, String> relationTableMap = new HashMap<>();
		String selectSql = "f$.guid," + DSIterationSqlBuilder.getSelectSqlForFoundation("f$", relationTableMap, className, session);
		String columnsOfIterationTab = "foundationfk, " + DSIterationSqlBuilder.getInsertSqlForInteration(className, session);

		StringBuilder tableSql = new StringBuilder(DataServer.getDSCommonService().getTableName(className) + " f$");
		for (String tableName : relationTableMap.keySet())
		{
			String joinCond = relationTableMap.get(tableName);
			String join = " inner join ";
			if (joinCond.endsWith("(+)"))
			{
				join = " left join ";
				joinCond = joinCond.substring(0, joinCond.length() - 3);
			}
			else if (joinCond.startsWith("(+)"))
			{
				join = " right join ";
				joinCond = joinCond.substring(3);
			}
			tableSql.append(join).append(tableName).append(" on ").append(joinCond);
		}

		String whereSql = "f$.guid = '" + foundationGuid + "'";

		// 新增备份数据
		Map<String, Object> param = new HashMap<>();
		param.put("TABLETO", DataServer.getDSCommonService().getRealBaseTableName(className) + "_I");
		param.put("FIELDLIST", columnsOfIterationTab);
		param.put("FIELDVALUES", selectSql);
		param.put("TABLEFROM", tableSql.toString());
		param.put("WHERESQL", whereSql);
		this.dynaObjectMapper.insertSelectOne(param);

		// 取得系统中已存在的最大版序
		int maxIterationId = this.getMaxIteration(foundationGuid, className);

		if (iterationLimit > 0 && maxIterationId > iterationLimit)
		{
			// 已超过最大版序数量限制，删除最早的一个版序
			param.clear();
			param.put("TABLENAME", DataServer.getDSCommonService().getRealBaseTableName(className));
			param.put("GUID", foundationGuid);
			param.put("ITERATIONID", maxIterationId - iterationLimit);
			this.foundationObjectMapper.deleteOverflowIteration(param);
		}

		ClassObject classObject = DataServer.getClassModelService().getClassObject(className);
		if (!classObject.hasInterface(ModelInterfaceEnum.IBOMView) && !classObject.hasInterface(ModelInterfaceEnum.IViewObject))
		{
			this.createClassificationIterationData(foundationGuid, iterationId, classObject.getGuid(), maxIterationId, iterationLimit, session);
		}
	}

	/**
	 * 创建分类版序数据
	 *
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	private void createClassificationIterationData(String foundationGuid, int iterationId, String classGuid, int maxIterationId, int iterationLimit, Session session)
			throws SQLException, ServiceRequestException
	{
		// 取得指定类的所有映射分类
		List<String> allSupClassGuidList = DataServer.getClassModelService().listAllParentClassGuid(classGuid);
		if (!allSupClassGuidList.contains(classGuid))
		{
			allSupClassGuidList.add(classGuid);
		}

		Map<String, String> classificationMap = new HashMap<>();
		for (Iterator<String> it = allSupClassGuidList.iterator(); it.hasNext();)
		{
			String subClassGuid = it.next();
			List<ClassficationFeature> featureList = DataServer.getSystemDataService().listFromCache(ClassficationFeature.class,
					new FieldValueEqualsFilter<ClassficationFeature>(ClassficationFeature.CLASSGUID, subClassGuid));
			if (!SetUtils.isNullList(featureList))
			{
				featureList.forEach(feature -> {
					if (!StringUtils.isNullString(feature.getClassificationfk()))
					{
						CodeObject codeObject = DataServer.getCodeModelService().getCodeObjectByGuid(feature.getClassificationfk());
						if (codeObject != null && !StringUtils.isNullString(codeObject.getBaseTableName()))
						{
							if (codeObject.isHasFields())
							{
								classificationMap.put(codeObject.getGuid(), codeObject.getBaseTableName());
							}
						}
					}
				});
			}
		}
		if (!SetUtils.isNullMap(classificationMap))
		{
			for (String codeGuid : classificationMap.keySet())
			{
				String cfBaseTableName = classificationMap.get(codeGuid);

				// 取得当前对象映射的所有分类的数据
				Map<String, Object> param = new HashMap<>();
				param.put("CFTABLENAME", "CF_" + cfBaseTableName);
				param.put("FOUNDATIONFK", foundationGuid);
				List<FoundationObject> cfList = this.classificationModelMapper.selectItemData(param);
				if (!SetUtils.isNullList(cfList))
				{
					for (FoundationObject cf : cfList)
					{
						String classificationItemGuid = cf.getClassificationGuid();
						String iterationColumns = this.getIterationColumns(classificationItemGuid, session);

						param.clear();
						param.put("CFTABLENAME", "CF_" + cfBaseTableName);
						param.put("ITERATIONID", iterationId);
						param.put("ITERATIONCOLUMNS", iterationColumns);
						param.put("GUID", StringUtils.generateRandomUID(32).toUpperCase());
						param.put("TABLENAME", DataServer.getDSCommonService().getTableName(classGuid));
						param.put("CFGUID", cf.getObjectGuid().getGuid());
						this.classificationModelMapper.insertCFIterationData(param);

						if (iterationLimit > 0 && maxIterationId > iterationLimit)
						{
							// 删除该版序对应的分类数据
							param.clear();
							param.put("CFTABLENAME", "CF_" + cfBaseTableName);
							param.put("FOUNDATIONGUID", foundationGuid);
							param.put("CLASSIFICATIONITEMGUID", classificationItemGuid);
							param.put("MAXITERATIONID", maxIterationId);
							param.put("LIMITITERATION", iterationLimit);
							this.classificationModelMapper.deleteOverflowCFIterationData(param);
						}
					}
				}
			}
		}
	}

	/**
	 * 取得指定分类子项的所有回滚字段列表
	 *
	 * @param classificationItemGuid
	 * @param session
	 * @return
	 */
	private String getIterationColumns(String classificationItemGuid, Session session) throws ServiceRequestException
	{
		StringBuilder buffer = new StringBuilder();
		CodeItem classificationItem = DataServer.getCodeModelService().getCodeItemByGuid(classificationItemGuid);
		if (classificationItem != null)
		{
			List<ClassField> fieldList = classificationItem.getFieldList();
			if (!SetUtils.isNullList(fieldList))
			{
				for (ClassField classificationField : fieldList)
				{
					buffer.append(",");
					buffer.append(classificationField.getColumnName());

					ObjectFieldTypeEnum objectFieldTypeOfField = DataServer.getDSCommonService().getObjectFieldTypeOfField(classificationField, session.getBizModelName());
					if (objectFieldTypeOfField == ObjectFieldTypeEnum.OBJECT)
					{
						buffer.append(",").append(classificationField.getColumnName()).append("$CLASS");
						buffer.append(",").append(classificationField.getColumnName()).append("$MASTER");
					}
				}
			}
		}
		return buffer.toString();
	}

	/**
	 * 取得对象的最大版序
	 *
	 * @param foundationGuid
	 * @param className
	 * @return
	 * @throws SQLException
	 */
	private Integer getMaxIteration(String foundationGuid, String className) throws SQLException, ServiceRequestException
	{
		Map<String, Object> param = new HashMap<>();
		param.put("TABLENAME", DataServer.getDSCommonService().getRealBaseTableName(className) + "_i");
		param.put("FILTER", "foundationfk='" + foundationGuid + "'");
		return (int) (Integer) this.dynaObjectMapper.selectMaxIterationId(param);
	}

	/**
	 * 分类数据回滚
	 *
	 * @param foundationguid
	 * @param iterationId
	 * @return
	 * @throws SQLException
	 */
	private String rollBackClfIterationData(String foundationguid, String classGuid, Integer iterationId, Session session) throws SQLException, ServiceRequestException
	{
		ObjectGuid foundationObjectGuid = new ObjectGuid();
		foundationObjectGuid.setClassGuid(classGuid);
		foundationObjectGuid.setGuid(foundationguid);
		List<ClassficationFeature> classficationFeatureList = DataServer.getClassificationFeatureService().listClassficationFeatureBySubClass(classGuid);
		if (!SetUtils.isNullList(classficationFeatureList))
		{
			Map<String, Object> param = new HashMap<>();
			for (ClassficationFeature feature : classficationFeatureList)
			{
				if (StringUtils.isNullString(feature.getClassificationfk()))
				{
					continue;
				}
				CodeObject codeObject = DataServer.getCodeModelService().getCodeObjectByGuid(feature.getClassificationfk());
				if (codeObject == null)
				{
					continue;
				}
				if (codeObject.isHasFields())
				{
					String baseTableName = codeObject.getBaseTableName();
					if (StringUtils.isNullString(baseTableName))
					{
						continue;
					}
					Map<String, FieldTypeEnum> map = this.listAllColumns(feature.getClassificationfk(), session.getBizModelName());
					String iterationColumns = this.getIterationColumns(map);
					if (StringUtils.isNullString(iterationColumns))
					{
						continue;
					}
					param.put("BASETABLENAME", baseTableName);
					param.put("REVCOL", iterationColumns);
					param.put("ITERATIONID", iterationId);
					param.put("FOUNDATIONFK", foundationguid);
					param.put("ITEMTABLE", DataServer.getDSCommonService().getTableName(classGuid));
					FoundationObject cfiObject = (FoundationObject) this.foundationObjectMapper.selectCFRollbackRevisionData(param);
					if (cfiObject != null)
					{
						List<SqlParamData> updateParamList = new ArrayList<>();
						for (String col : map.keySet())
						{
							updateParamList.add(new SqlParamData(col, cfiObject.get(col), DSCommonUtil.getJavaTypeOfField(map.get(col))));
						}
						List<SqlParamData> whereParamList = new ArrayList<>();
						whereParamList.add(new SqlParamData("FOUNDATIONFK", foundationguid, String.class));

						DynamicUpdateParamData updateParamData = new DynamicUpdateParamData();
						updateParamData.setWhereParamList(whereParamList);
						updateParamData.setUpdateParamList(updateParamList);
						updateParamData.setTableName(codeObject.getRevisionTableName());
						this.dynaObjectMapper.updateDynamic(updateParamData);

						param.clear();
						param.put("BASETABLENAME", baseTableName);
						param.put("ITERATIONID", iterationId);
						param.put("FOUNDATIONFK", foundationguid);
						this.foundationObjectMapper.deleteCFOverIteration(param);
					}
				}
			}
		}
		return "Y";
	}

	private String getIterationColumns(Map<String, FieldTypeEnum> map) throws ServiceRequestException
	{
		StringBuilder columnBuilder = new StringBuilder();
		if (!SetUtils.isNullMap(map))
		{
			map.remove("ITERATIONID");
			map.remove("GUID");
			for (String columnName : map.keySet())
			{
				if (columnBuilder.length() > 0)
				{
					columnBuilder.append(",");
				}

				// 排除guid和iterationid
				if ("GUID".equalsIgnoreCase(columnName) || "ITERATIONID".equalsIgnoreCase(columnName))
				{
					continue;
				}

				columnBuilder.append(columnName);
			}
		}
		return columnBuilder.toString();
	}

	private Map<String, FieldTypeEnum> listAllColumns(String classificationGuid, String bmName) throws ServiceRequestException
	{
		CodeObject codeObject = DataServer.getCodeModelService().getCodeObjectByGuid(classificationGuid);
		List<CodeItem> allCodeItems = codeObject == null ? null : DataServer.getCodeModelService().listAllCodeItem(codeObject.getName());
		Map<String, FieldTypeEnum> columnMap = new HashMap<>();
		if (!SetUtils.isNullList(allCodeItems))
		{
			for (CodeItem codeItem : allCodeItems)
			{
				List<ClassField> fieldList = DataServer.getCodeModelService().listField(codeItem.getGuid());
				if (!SetUtils.isNullList(fieldList))
				{
					for (ClassField field : fieldList)
					{
						if (!columnMap.containsKey(field.getColumnName()))
						{
							ObjectFieldTypeEnum objectFieldTypeEnum = DataServer.getDSCommonService().getObjectFieldTypeOfField(field, bmName);
							columnMap.put(field.getColumnName(), field.getType());

							if (objectFieldTypeEnum == ObjectFieldTypeEnum.OBJECT)
							{
								columnMap.put(field.getColumnName() + "$MASTER", field.getType());
								columnMap.put(field.getColumnName() + "$CLASS", field.getType());
							}
						}
					}
				}
			}
		}
		return columnMap;
	}
}
