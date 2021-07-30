/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 处理实例
 * JiangHL 2011-5-10
 */
package dyna.data.service.ins;

import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.FoundationObjectImpl;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.foundation.BOMView;
import dyna.common.bean.data.foundation.ViewObject;
import dyna.common.bean.data.structure.BOMStructure;
import dyna.common.bean.model.cls.ClassObject;
import dyna.common.bean.model.code.CodeItem;
import dyna.common.bean.model.code.CodeObject;
import dyna.common.bean.xml.UpperKeyMap;
import dyna.common.dto.DSSFileInfo;
import dyna.common.dto.Folder;
import dyna.common.dto.Session;
import dyna.common.dto.aas.Group;
import dyna.common.dto.aas.URIG;
import dyna.common.dto.aas.User;
import dyna.common.dto.cfm.ClassficationFeature;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.lf.LifecyclePhaseInfo;
import dyna.common.dto.template.bom.BOMTemplateInfo;
import dyna.common.dto.template.relation.RelationTemplateInfo;
import dyna.common.dtomapper.DynaObjectMapper;
import dyna.common.dtomapper.FoundationObjectMapper;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.sqlbuilder.plmdynamic.SqlParamData;
import dyna.common.sqlbuilder.plmdynamic.insert.DynamicInsertParamData;
import dyna.common.sqlbuilder.plmdynamic.update.DynamicUpdateParamData;
import dyna.common.systemenum.*;
import dyna.common.util.DateFormat;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.common.exception.DynaDataExceptionAll;
import dyna.data.common.exception.DynaDataExceptionSQL;
import dyna.data.common.exception.DynaDataNoAuthorityException;
import dyna.data.common.util.DSCommonUtil;
import dyna.data.context.DataServerContext;
import dyna.data.service.DSAbstractServiceStub;
import dyna.data.service.acl.AclService;
import dyna.data.service.sdm.FieldValueEqualsFilter;
import dyna.data.service.sdm.SystemDataService;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * 处理实例
 *
 * @author JiangHL
 */
public class DSInstanceUpdateStub extends DSAbstractServiceStub<InstanceServiceImpl>
{
	@Autowired
	private FoundationObjectMapper                foundationObjectMapper;
	/**
	 * @param context
	 * @param service
	 * @throws DynaDataException
	 */
	public DSInstanceUpdateStub(DataServerContext context, InstanceServiceImpl service) throws DynaDataException
	{
		super(context, service);
	}

	/**
	 * 保存实例
	 *
	 * @param foundationObject
	 * @param isCheckAcl
	 * @param sessionId
	 * @param isImport
	 * @param isCheckCheckout
	 *            此参数用于，是否需要检查库数据必须是检出状态才能保存，目前此参数不启用（按false处理）
	 * @param isCheckUpdateTime
	 *            修改时是否判断更新时间
	 * @throws DynaDataException
	 */
	protected FoundationObject save(FoundationObject foundationObject, boolean isCheckAcl, String sessionId, String fixTranId, boolean isImport, boolean isCheckCheckout,
			boolean isCheckUpdateTime) throws ServiceRequestException
	{
		// 保存时有以下几种情况：
		// 1、bomview、view 如果经处于检出状态，不需要检入，否则需要自动检出检入
		// 2、私人数据，需要自动检出检入
		// 3、库数据（不包括bomview、view），保存检出后的数据
		// 4、其他（数据导入、ec eca ect），直接保存

		String className = foundationObject.getObjectGuid().getClassName();
		ClassObject classObject = DataServer.getClassModelService().getClassObject(className);
		boolean isView = (foundationObject instanceof ViewObject) || (foundationObject instanceof BOMView);

		// 抛出异常时使用的参数
		String exceptionParameter = foundationObject.getId();
		if (isView)
		{
			exceptionParameter = StringUtils.convertNULLtoString(foundationObject.getName());
		}
		// 更新对象时特殊处理：EC,ECA,ECT 不用是检出状态
		boolean isCheckout = foundationObject.isCheckOut();
		boolean isCommit = foundationObject.isCommited();
		boolean isNeedAutoCheckout = false;

		// 普通FO时，如果isCheckCheckout为true同时为库数据时，需要检查是否检查，否则不检查 --此处暂时不对isCheckCheckout检查 2012年1月9日
		if (isCheckCheckout && isCommit && !isView && !isImport)
		{

			Map<String, Object> paras = new HashMap<>();
			paras.put("GUID", foundationObject.getGuid());
			paras.put("tablename", DataServer.getDSCommonService().getTableName(foundationObject.getObjectGuid().getClassGuid()));
			String checkoutuser;
			try
			{
				checkoutuser = this.dynaObjectMapper.getCheckoutUser(paras);
			}
			catch (Exception e)
			{
				throw new DynaDataExceptionAll("query error : the query data is error .", null, DataExceptionEnum.UNKNOWN, exceptionParameter);
			}

			if (null == checkoutuser)
			{
				throw new DynaDataExceptionAll("save error : the commited data is not in checkout status .", null, DataExceptionEnum.DS_NON_CHECKOUT, exceptionParameter);
			}
		}

		// 需要做检出检入操作
		if (!isImport && ((isView && !isCheckout) || !isCommit))
		{
			isNeedAutoCheckout = true;
		}

		if (isNeedAutoCheckout)
		{
			if ((ISCHECKACL && isCheckAcl) && !DataServer.getAclService().hasAuthority(foundationObject.getObjectGuid(), AuthorityEnum.CHECKOUT, sessionId))
			{
				throw new DynaDataExceptionAll("CHECKOUT", null, DataExceptionEnum.DS_NO_CHECKOUT_AUTH, exceptionParameter);
			}
		}

		this.dealWithIDNameAlterId(foundationObject, sessionId, fixTranId);

		boolean maFoundtionIsChanged = false;
		Session session = DataServer.getDSCommonService().getSession(sessionId);

		Map<String, List<SqlParamData>> paramOfTableMap = new HashMap<>();
		if (classObject.getFieldList() != null)
		{
			for (ClassField field : classObject.getFieldList())
			{
				if (field.getName().equalsIgnoreCase(ViewObject.ISPRECISE))
				{
					continue;
				}
				if (field.getName().equalsIgnoreCase(SystemClassFieldEnum.CREATETIME.getName()) || field.getName().equalsIgnoreCase(SystemClassFieldEnum.UPDATETIME.getName())
						|| field.getName().equalsIgnoreCase(SystemClassFieldEnum.CREATEUSER.getName())
						|| field.getName().equalsIgnoreCase(SystemClassFieldEnum.UPDATEUSER.getName()))
				{
					continue;
				}

				FieldTypeEnum fieldType = field.getType();
				if (!foundationObject.isChanged(field.getName()) && !fieldType.equals(FieldTypeEnum.OBJECT))
				{
					continue;
				}
				else if (!foundationObject.isChanged(field.getName()) && fieldType.equals(FieldTypeEnum.OBJECT))
				{
					if (!foundationObject.isChanged(field.getName() + "$MASTER"))
					{
						continue;
					}
				}

				maFoundtionIsChanged = true;

				String tableName = DataServer.getDSCommonService().getTableName(className, field.getName());
				if (!paramOfTableMap.containsKey(tableName))
				{
					paramOfTableMap.put(tableName, new ArrayList<>());
				}

				paramOfTableMap.get(tableName)
						.add(new SqlParamData(field.getColumnName(), foundationObject.get(field.getName()), DSCommonUtil.getJavaTypeOfField(field.getType())));
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
						paramOfTableMap.get(tableName).add(new SqlParamData(field.getColumnName() + "$CLASS", foundationObject.get(tempFieldName + "$CLASS"),
								DSCommonUtil.getJavaTypeOfField(field.getType())));
						paramOfTableMap.get(tableName).add(new SqlParamData(field.getColumnName() + "$MASTER", foundationObject.get(tempFieldName + "$MASTER"),
								DSCommonUtil.getJavaTypeOfField(field.getType())));
					}
				}
			}
		}

		try
		{
			DataServer.getTransactionManager().startTransaction(fixTranId);

			String guid = foundationObject.getObjectGuid().getGuid();
			if (isNeedAutoCheckout)
			{
				// 自动检出检入，不能将修改时间作为检出检入的条件，因为自动检出时修改时间已经改变，自动检入将永远无法实现
				this.stubService.getCheckoutStub().checkOut(guid, //
						className, //
						session.getUserGuid(), //
						true, // 自动检出
						foundationObject.getUpdateTime(), //
						Integer.parseInt(classObject.getIterationLimit()), //
						session);
				// 如果是私人数据，需要和文件关联
				if (!isCommit)
				{
					// create bi_file
					Map<String, Object> fileMap = new HashMap<>();
					fileMap.put("SOURCEGUID", guid);
					fileMap.put("TABLENAME", DataServer.getDSCommonService().getTableName(classObject.getName()));
					fileMap.put("CURRENTTIME", new Date());
					this.dynaObjectMapper.createBiFile(fileMap);
				}
			}

			String sysTableName = DataServer.getDSCommonService().getRealBaseTableName(className) + "_0";
			// 修改入库位置
			if (foundationObject.isChanged(SystemClassFieldEnum.COMMITFOLDER.getName()))
			{
				maFoundtionIsChanged = true;

				String fromFolderGuid = (String) foundationObject.getOriginalValue(SystemClassFieldEnum.COMMITFOLDER.getName());
				// 只检查数据正确性,不实际更新数据
				String locationLib = DataServer.getFolderService().moveToFolder(foundationObject.getObjectGuid(), fromFolderGuid, foundationObject.getCommitFolderGuid(),
						isCheckAcl, isCheckAcl, true, true, sessionId, fixTranId);
				if (StringUtils.isGuid(locationLib))
				{
					foundationObject.put(SystemClassFieldEnum.LOCATIONLIB.getName(), locationLib);
				}
			}

			Date sysDate = new Date();
			if (maFoundtionIsChanged)
			{
				List<SqlParamData> updateParamList = paramOfTableMap.get(sysTableName);
				if (updateParamList == null)
				{
					updateParamList = new ArrayList<>();
				}
				List<SqlParamData> whereParamList = new ArrayList<>();
				whereParamList.add(new SqlParamData("GUID", guid, DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.STRING)));
				if (isCheckUpdateTime)
				{
					// 条件
					Date updatetime = foundationObject.getUpdateTime();
					whereParamList.add(new SqlParamData("updatetime", updatetime, DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.DATETIME)));

					// 更新字段
					updateParamList.add(new SqlParamData("updatetime", sysDate, DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.DATETIME)));
					updateParamList.add(new SqlParamData("updateuser", session.getUserGuid(), DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.STRING)));
				}

				if (isCheckout && !isView && isCheckUpdateTime)
				{
					// 条件
					whereParamList.add(new SqlParamData("CHECKOUTUSER", session.getUserGuid(), DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.STRING)));
				}

				DynamicUpdateParamData data = new DynamicUpdateParamData();
				data.setTableName(sysTableName);
				data.setWhereParamList(whereParamList);
				data.setUpdateParamList(updateParamList);

				if (this.dynaObjectMapper.updateDynamic(data) == 0)
				{
					throw new DynaDataExceptionAll("save failed. guid = " + guid, null, DataExceptionEnum.DS_SAVE_FOUNDATION_REVISION, exceptionParameter);
				}

				if (isCheckUpdateTime)
				{
					foundationObject.setUpdateUserGuid(DataServer.getDSCommonService().getSession(sessionId).getUserGuid());
					foundationObject.put(SystemClassFieldEnum.UPDATETIME.getName(), sysDate);
				}

				paramOfTableMap.remove(sysTableName);
				if (!SetUtils.isNullMap(paramOfTableMap))
				{
					for (String tableName : paramOfTableMap.keySet())
					{
						List<SqlParamData> updateParamList_ = paramOfTableMap.get(tableName);
						List<SqlParamData> whereParamList_ = new ArrayList<>();
						whereParamList_.add(new SqlParamData("FOUNDATIONFK", guid, DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.STRING)));

						data = new DynamicUpdateParamData();
						data.setTableName(tableName);
						data.setWhereParamList(whereParamList_);
						data.setUpdateParamList(updateParamList_);

						int cnt = this.dynaObjectMapper.updateDynamic(data);
						if (cnt == 0)
						{
							List<SqlParamData> insertParamList = paramOfTableMap.get(tableName);
							insertParamList.add(new SqlParamData("FOUNDATIONFK", guid, DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.STRING)));

							DynamicInsertParamData insertParamData = new DynamicInsertParamData();
							insertParamData.setTableName(tableName);
							insertParamData.setInsertParamList(insertParamList);

							this.dynaObjectMapper.insertDynamic(insertParamData);
						}
					}
				}
			}
			// update ma_foundtion 需要判断是否需要修改ma_foundation表**************************************

			// 修改特殊字段
			this.updateSpecialField(foundationObject, foundationObject.getObjectGuid().getGuid(), sessionId);

			this.updateValueOfMaster(foundationObject.getObjectGuid(), exceptionParameter, foundationObject.getUnique());

			if (isNeedAutoCheckout)
			{
				Map<String, Object> checkinMap = new HashMap<>();
				checkinMap.put("table", DataServer.getDSCommonService().getTableName(className));
				checkinMap.put("GUID", foundationObject.getObjectGuid().getGuid());
				checkinMap.put("UPDATEUSER", session.getUserGuid());
				checkinMap.put("CURRENTTIME", new Date());
				this.dynaObjectMapper.checkin(checkinMap);

			}

			// 更新分类信息
			this.updateClassification(foundationObject, sessionId);

			FoundationObject simpleObj = this.dynaObjectMapper.lockForCheckout(guid,sysTableName);
			foundationObject.put(SystemClassFieldEnum.UPDATETIME.getName(), simpleObj.getUpdateTime());
			FoundationObjectImpl retObject = (FoundationObjectImpl) foundationObject.getClass().getConstructor().newInstance();
			retObject.sync(foundationObject);

			DataServer.getTransactionManager().commitTransaction();

			return retObject;
		}
		catch (SQLException e)
		{
			DataServer.getTransactionManager().rollbackTransaction();
			throw new DynaDataExceptionSQL("save() Id =" + foundationObject.getId(), e, DataExceptionEnum.DS_SAVE_FOUNDATION, exceptionParameter);
		}
		catch (Exception e)
		{
			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof DynaDataExceptionSQL)
			{
				throw (DynaDataExceptionSQL) e;
			}
			else if (e instanceof DynaDataExceptionAll)
			{
				throw (DynaDataExceptionAll) e;
			}
			throw new DynaDataExceptionAll("save() Id =" + foundationObject.getId(), e, DataExceptionEnum.DS_SAVE_FOUNDATION, exceptionParameter);
		}
	}

	/**
	 * 保存分类数据。
	 *
	 * @param foundationObject
	 *            分类所属实例
	 * @param sessionId
	 *
	 * @throws SQLException
	 */
	private void updateClassification(FoundationObject foundationObject, String sessionId) throws Exception
	{
		String objectGuid = foundationObject.getObjectGuid().getGuid();

		List<FoundationObject> classificationList = foundationObject.restoreAllClassification(false);
		if (classificationList != null && classificationList.size() > 0)
		{
			for (FoundationObject clf : classificationList)
			{
				String groupName = clf.getClassificationGroupName();
				CodeObject codeObject = DataServer.getCodeModelService().getCodeObject(groupName);
				if (codeObject == null)
				{
					continue;
				}
				if (codeObject.isHasFields())
				{
					// 当分类item为空时，从数据库中删除该item的数据
					if (!StringUtils.isGuid(clf.getObjectGuid().getGuid()) && StringUtils.isNullString(clf.getClassificationGuid()))
					{
						this.deleteClassificationItem(foundationObject.getObjectGuid().getGuid(), (String) clf.getOriginalValue(SystemClassFieldEnum.CLASSIFICATION.getName()));
						foundationObject.clearClasssification(groupName);
					}
					else
					{
						// 要保存的分类item在数据库中不存在，则追加，否则更新。
						if (!StringUtils.isGuid(clf.getObjectGuid().getGuid()))
						{
							this.insertClassificationItem(objectGuid, clf, sessionId);
						}
						else
						{
							this.updateClassificationItem(objectGuid, clf, sessionId);
						}

						FoundationObjectImpl retObject = (FoundationObjectImpl) clf.getClass().getConstructor().newInstance();
						retObject.sync(clf);
						foundationObject.addClassification(retObject, false);
					}
				}
			}
		}
	}

	/**
	 * 删除实例的指定item分类数据
	 *
	 * @param fkguid
	 * @param clguid
	 * @throws SQLException
	 */
	private void deleteClassificationItem(String fkguid, String clguid) throws ServiceRequestException
	{
		try
		{
			CodeItem codeItemObject = DataServer.getCodeModelService().getCodeItemByGuid(clguid);
			CodeObject codeObject = DataServer.getCodeModelService().getCodeObjectByGuid(codeItemObject.getCodeGuid());
			if (StringUtils.isNullString(codeObject.getRevisionTableName()) || StringUtils.isNullString(codeObject.getIterationTableName()))
			{
				return;
			}

			String whereStr = "foundationfk = '" + fkguid + "' and CLASSIFICATIONITEMGUID='" + clguid + "'";
			this.classificationModelMapper.deleteClassificationData(codeObject.getRevisionTableName(),whereStr);

			whereStr = "foundationfk = '" + fkguid + "' and CLASSIFICATIONITEMGUID='" + clguid + "'";
			this.classificationModelMapper.deleteClassificationData(codeObject.getIterationTableName(), whereStr);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException("", null, e);
		}
	}

	/**
	 * 往revision表和iteration表中更新分类item数据。
	 *
	 * @param foundationtGuid
	 * @param classification
	 * @throws SQLException
	 */
	private void updateClassificationItem(String foundationtGuid, FoundationObject classification, String sessionId) throws ServiceRequestException
	{
		String groupName = classification.getClassificationGroupName();
		String itemName = classification.getClassificationName();

		CodeItem codeItem = DataServer.getCodeModelService().getCodeItem(groupName, itemName);
		if (codeItem == null)
		{
			return;
		}
		CodeObject codeObject = DataServer.getCodeModelService().getCodeObject(groupName);
		List<ClassField> fields = codeItem.getFieldList();
		if (StringUtils.isNullString(codeObject.getRevisionTableName()) || StringUtils.isNullString(codeObject.getIterationTableName()))
		{
			return;
		}

		boolean isNewClassificationItem = false;
		if (classification.isChanged(SystemClassFieldEnum.CLASSIFICATION.getName()))
		{
			isNewClassificationItem = true;
		}

		List<SqlParamData> updateParamList = new ArrayList<>();
		if (fields != null)
		{
			for (ClassField field : fields)
			{
				if (!isNewClassificationItem && !classification.isChanged(field.getName()))
				{
					continue;
				}

				ClassField clfField = codeItem.getField(field.getName());
				String columnName = clfField == null ? null : clfField.getColumnName();
				if (StringUtils.isNullString(columnName))
				{
					continue;
				}

				List<SqlParamData> updateParamList_ = this.getUpdateFieldParam(classification, sessionId, columnName, field);
				if (!SetUtils.isNullList(updateParamList_))
				{
					updateParamList.addAll(updateParamList_);
				}
			}
		}
		updateParamList.add(new SqlParamData("foundationfk", foundationtGuid, String.class));
		updateParamList.add(new SqlParamData("classificationitemguid", classification.getClassificationGuid(), String.class));

		List<SqlParamData> whereParamList = new ArrayList<>();
		whereParamList.add(new SqlParamData("GUID", classification.getObjectGuid().getGuid(), String.class));

		DynamicUpdateParamData updateParamData = new DynamicUpdateParamData();
		updateParamData.setWhereParamList(whereParamList);
		updateParamData.setUpdateParamList(updateParamList);
		updateParamData.setTableName(codeObject.getRevisionTableName());

		try
		{
			this.dynaObjectMapper.updateDynamic(updateParamData);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException("", null, e);
		}
	}

	/**
	 * 往revision表和iteration表中追加分类item数据，并保存实例与分类item的关系。
	 *
	 * @param foundationtGuid
	 * @param classification
	 * @throws SQLException
	 */
	private void insertClassificationItem(String foundationtGuid, FoundationObject classification, String sessionId) throws ServiceRequestException
	{
		String groupName = classification.getClassificationGroupName();
		String itemName = classification.getClassificationName();

		CodeItem codeItem = DataServer.getCodeModelService().getCodeItem(groupName, itemName);
		if (codeItem == null)
		{
			return;
		}

		CodeObject codeObject = DataServer.getCodeModelService().getCodeObject(groupName);
		List<ClassField> fields = codeItem.getFieldList();
		if (StringUtils.isNullString(codeObject.getRevisionTableName()) || StringUtils.isNullString(codeObject.getIterationTableName()))
		{
			return;
		}

		List<SqlParamData> insertSqlParamList = new ArrayList<>();
		boolean hasFieldValue = false;
		if (fields != null)
		{
			for (ClassField field : fields)
			{
				ClassField clfField = codeItem.getField(field.getName());
				String columnName = clfField == null ? null : clfField.getColumnName();
				if (StringUtils.isNullString(columnName))
				{
					continue;
				}
				if (((FoundationObjectImpl) classification).containsKey(clfField.getName().toUpperCase()))
				{
					hasFieldValue = true;
				}
				List<SqlParamData> insertSqlParams = this.getUpdateFieldParam(classification, sessionId, columnName, field);
				if (!SetUtils.isNullList(insertSqlParams))
				{
					insertSqlParamList.addAll(insertSqlParams);
				}
			}
			if (hasFieldValue)
			{
				insertSqlParamList.add(new SqlParamData("FOUNDATIONFK", foundationtGuid, String.class));
				insertSqlParamList.add(new SqlParamData("CLASSIFICATIONITEMGUID", classification.getClassificationGuid(), String.class));

				DynamicInsertParamData insertParamData = new DynamicInsertParamData();
				insertParamData.setTableName(codeObject.getRevisionTableName());
				insertParamData.setInsertParamList(insertSqlParamList);

				try
				{
					if (!SetUtils.isNullList(insertSqlParamList))
					{
						//TODO
//						String founcationGuid = (String) this.sqlSessionFactory.insert(mapper_DYNAOBJECT + ".insertDynamic", insertParamData);
//						classification.getObjectGuid().setGuid(founcationGuid);
//						classification.setGuid(founcationGuid);
					}
				}
				catch (Exception e)
				{
					throw new ServiceRequestException("", null, e);
				}
			}

		}
	}

	/**
	 * 取得更新字段sql
	 *
	 * @param foundationObject
	 * @param columnName
	 * @param field
	 * @return
	 */
	private List<SqlParamData> getUpdateFieldParam(FoundationObject foundationObject, String sessionId, String columnName, ClassField field) throws ServiceRequestException
	{
		List<SqlParamData> sqlParamDataList = new ArrayList<>();

		FieldTypeEnum fieldType = field.getType();
		Object fieldValue = foundationObject.get(field.getName());
		if (SystemClassFieldEnum.GUID.getName().equals(columnName))
		{
			return null;
		}
		if (columnName.endsWith("$"))
		{
			columnName = columnName.substring(0, columnName.length() - 1);
		}

		if (fieldType.equals(FieldTypeEnum.INTEGER))
		{
			sqlParamDataList.add(new SqlParamData(columnName, fieldValue, Integer.class));
		}
		else if (fieldType.equals(FieldTypeEnum.FLOAT))
		{
			sqlParamDataList.add(new SqlParamData(columnName, fieldValue, Float.class));
		}
		else if (fieldType.equals(FieldTypeEnum.DATE) && null != fieldValue && !fieldValue.equals(""))
		{
			sqlParamDataList.add(new SqlParamData(columnName, fieldValue, Date.class));
		}
		else if (fieldType.equals(FieldTypeEnum.DATETIME) && null != fieldValue)
		{
			sqlParamDataList.add(new SqlParamData(columnName, fieldValue, Date.class));
		}
		else if (fieldType.equals(FieldTypeEnum.OBJECT))
		{
			String masterVal = StringUtils.convertNULLtoString(foundationObject.get(field.getName() + "$MASTER"));
			String classVal = StringUtils.convertNULLtoString(foundationObject.get(field.getName() + "$CLASS"));
			String objectVal = StringUtils.convertNULLtoString(foundationObject.get(field.getName()));
			sqlParamDataList.add(new SqlParamData(columnName, objectVal, String.class));

			ClassObject classObj = DataServer.getClassModelService().getClassObject(field.getTypeValue());
			if (!classObj.hasInterface(ModelInterfaceEnum.IUser) && !classObj.hasInterface(ModelInterfaceEnum.IGroup) && !classObj.hasInterface(ModelInterfaceEnum.IPMRole)
					&& !classObj.hasInterface(ModelInterfaceEnum.IPMCalendar))
			{
				sqlParamDataList.add(new SqlParamData(columnName + "$MASTER", masterVal, String.class));
				sqlParamDataList.add(new SqlParamData(columnName + "$CLASS", classVal, String.class));
			}
		}
		else
		{
			sqlParamDataList.add(new SqlParamData(columnName, fieldValue, String.class));
		}

		return sqlParamDataList;
	}

	/**
	 * 修改ID、NAME、ALTERID。
	 * 编码规则部分转移到应用层做。
	 *
	 * @param foundationObject
	 * @param sessionId
	 */
	private void dealWithIDNameAlterId(FoundationObject foundationObject, String sessionId, String fixTranId) throws ServiceRequestException
	{
		ObjectGuid objectGuid = foundationObject.getObjectGuid();
		String masterGuid = objectGuid.getMasterGuid();

		String updateguid = foundationObject.getObjectGuid().getGuid();
		try
		{
			DataServer.getTransactionManager().startTransaction(fixTranId);

			if (foundationObject.isChanged(SystemClassFieldEnum.ID.getName()))
			{
				String masterId = foundationObject.getId();

				// 同时更新crt、wip 、ecp revision
				String updateStr = "UPDATE " + DataServer.getDSCommonService().getTableName(objectGuid.getClassName()) + " SET md_id='" + StringUtils.translateSpecialChar(masterId)
						+ "' WHERE guid='" + updateguid + "'";
				this.dynaObjectMapper.updateAll(updateStr);
			}
			if (foundationObject.isChanged(SystemClassFieldEnum.NAME.getName()))
			{
				String masterName = foundationObject.getName();
				// 同时更新crt、wip revision
				String updateStr = "UPDATE " + DataServer.getDSCommonService().getTableName(objectGuid.getClassName()) + " SET md_name='"
						+ StringUtils.translateSpecialChar(masterName) + "'" + " WHERE guid='" + updateguid + "'";
				this.dynaObjectMapper.updateAll(updateStr);
			}
			if (foundationObject.isChanged(SystemClassFieldEnum.ALTERID.getName()))
			{
				String masterAlterid = foundationObject.getAlterId();

				String updateStr = "UPDATE " + DataServer.getDSCommonService().getTableName(objectGuid.getClassName()) + " SET alterid='"
						+ StringUtils.translateSpecialChar(masterAlterid) + "' WHERE guid='" + updateguid + "'";
				this.dynaObjectMapper.updateAll(updateStr);
			}

			DataServer.getTransactionManager().commitTransaction();
		}
		catch (Exception e)
		{
			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof DynaDataExceptionSQL)
			{
				throw (DynaDataExceptionSQL) e;
			}
			else if (e instanceof DynaDataExceptionAll)
			{
				throw (DynaDataExceptionAll) e;
			}
			throw new DynaDataExceptionAll("saveMasterData other exception masterGuid =" + masterGuid, e, DataExceptionEnum.DS_SAVE_FOUNDATION_PARAM);
		}
	}

	/**
	 * 创建实例
	 *
	 * @param foundationObject
	 * @param originalFoundationGuid
	 *            原始版本guid
	 * @param isCheckAcl
	 * @param sessionId
	 * @return
	 * @throws DynaDataException
	 */
	protected FoundationObject create(FoundationObject foundationObject, String originalFoundationGuid, boolean isCheckAcl, String sessionId, String fixTranId, boolean isImport)
			throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		Session session = DataServer.getDSCommonService().getSession(sessionId);
		String userGuid = session.getUserGuid();
		String className = foundationObject.getObjectGuid().getClassName();
		String classGuid = foundationObject.getObjectGuid().getClassGuid();
		String foundationGuid;
		String masterGuid = foundationObject.getObjectGuid().getMasterGuid();
		ClassObject classObject = DataServer.getClassModelService().getClassObject(className);
		boolean isView = classObject.hasInterface(ModelInterfaceEnum.IViewObject);
		boolean isBOMView = classObject.hasInterface(ModelInterfaceEnum.IBOMView);
		boolean isCreateMaster = StringUtils.isNullString(masterGuid);

		// 抛出异常时使用的参数
		String exceptionParameter = foundationObject.getId();
		if (isView || isBOMView)
		{
			exceptionParameter = StringUtils.convertNULLtoString(foundationObject.getName());
		}
		String id = foundationObject.getId();
		String uniques = foundationObject.getUnique();

		if (foundationObject.getStatus() == null)
		{
			foundationObject.setStatus(SystemStatusEnum.WIP);
		}

		String groupGuid = session.getLoginGroupGuid();
		Group group = sds.get(Group.class, groupGuid);

		// 如果是view、bomview，同時end1已经入库，那么需要根据end1所在的库去判断view、bomview的创建权限--2012年3月5日 zhw
		String end1Guid;
		// end1是否生效
		try
		{
			if (isView || isBOMView)
			{
				end1Guid = StringUtils.convertNULLtoString(foundationObject.get("END1"));
				// 在ec中，变更bom的时候生成的解决对象，这时候是没有end1的，此时把bomview先放到工作库中
				if (!StringUtils.isGuid(end1Guid))
				{
					foundationObject.setLocationlib(group.getLibraryGuid());
				}
				else
				{
					FoundationObject end1Foundation = this.stubService.getSystemFieldInfo(end1Guid, (String) foundationObject.get("END1$CLASS"), false, sessionId);
					if (end1Foundation.isCommited())
					{
						String end1LocationLib = end1Foundation.getLocationlib();
						foundationObject.setLocationlib(end1LocationLib);
					}
					else
					{
						foundationObject.setLocationlib(group.getLibraryGuid());
					}
				}
			}
			else
			{
				if (!StringUtils.isGuid(foundationObject.getCommitFolderGuid()))
				{
					foundationObject.setLocationlib(group.getLibraryGuid());
				}
				else
				{
					try
					{
						String locationLib = this.getLibraryByFolder(foundationObject.getCommitFolderGuid());
						foundationObject.setLocationlib(locationLib);
					}
					catch (SQLException e)
					{
						e.printStackTrace();
					}
				}
			}

			DataServer.getTransactionManager().startTransaction(fixTranId);

			// check auth
			if ((ISCHECKACL && isCheckAcl) && isCreateMaster)
			{
				// create
				AclService aclService = DataServer.getAclService();
				String authority = aclService.getCreateAuthority(foundationObject, null, sessionId, true, false);
				if (authority.equalsIgnoreCase("2"))
				{
					throw new DynaDataExceptionAll("CREATE", null, DataExceptionEnum.DS_NO_CREATE_AUTH, exceptionParameter);
				}
			}
			if ((ISCHECKACL && isCheckAcl) && !isCreateMaster)
			{
				// revision
				AclService aclService = DataServer.getAclService();
				if (!aclService.hasAuthority(originalFoundationGuid, className, AuthorityEnum.REVISE, sessionId))
				{
					throw new DynaDataExceptionAll("REVISE", null, DataExceptionEnum.DS_NO_REVISE_AUTH, exceptionParameter);
				}
			}
			if (StringUtils.isNullString(foundationObject.getUnique()))
			{
				foundationObject.setUnique(StringUtils.generateRandomUID(32));
			}
			if (isCreateMaster)
			{
				// insert master
				List<SqlParamData> insertParamList = new ArrayList<>();
				insertParamList.add(new SqlParamData("CLASSGUID", classGuid, DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.STRING)));
				insertParamList.add(new SqlParamData("MD_ID", foundationObject.getId(), DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.STRING)));
				insertParamList.add(new SqlParamData("UNIQUES", foundationObject.getUnique(), DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.STRING)));

				DynamicInsertParamData insertData = new DynamicInsertParamData();
				insertData.setTableName(DataServer.getDSCommonService().getMasterTableName(classGuid));
				insertData.setInsertParamList(insertParamList);

				//TODO
//				masterGuid = (String) this.dynaObjectMapper.insertDynamic(insertData);
			}

			foundationObject.put(SystemClassFieldEnum.MASTERFK.getName(), masterGuid);
			if (!StringUtils.isGuid(foundationObject.getCommitFolderGuid()))
			{
				UpperKeyMap filter = new UpperKeyMap();
				filter.put(Folder.LIBRARY_USER, group.getLibraryGuid());
				filter.put(Folder.CLASSIFICATION, FolderTypeEnum.LIBRARY.toString());
				Folder library = DataServer.getSystemDataService().findInCache(Folder.class, new FieldValueEqualsFilter<>(filter));
				Folder rootFolder = DataServer.getSystemDataService().findInCache(Folder.class, new FieldValueEqualsFilter<>(Folder.PARENT_GUID, library.getGuid()));
				foundationObject.setCommitFolderGuid(rootFolder.getGuid());
			}
			ObjectGuid ecFlagObjectGuid = foundationObject.getECFlag();
			if (ecFlagObjectGuid != null)
			{
				foundationObject.setECFlag(ecFlagObjectGuid);
			}
			foundationObject.setCreateUserGuid(userGuid);
			foundationObject.setUpdateUserGuid(userGuid);

			Integer revisionIdSequence = (Integer) this.dynaObjectMapper.getRevisionIdSequence(DataServer.getDSCommonService().getTableName(className),masterGuid);
			foundationObject.put(SystemClassFieldEnum.REVISIONIDSEQUENCE.getName(), new BigDecimal(revisionIdSequence));
			foundationObject.put(SystemClassFieldEnum.LATESTREVISION.getName(), "mw");
			foundationObject.put(SystemClassFieldEnum.ITERATIONID.getName(), new BigDecimal(1));
			foundationObject.put(SystemClassFieldEnum.ISCHECKOUT.getName(), "N");
			foundationObject.put(SystemClassFieldEnum.ISEXPORTTOERP.getName(), "0");
			foundationObject.put(SystemClassFieldEnum.CREATETIME.getName(), DateFormat.getSysDate());
			foundationObject.put(SystemClassFieldEnum.UPDATETIME.getName(), DateFormat.getSysDate());
			if ("RLS".equals(foundationObject.getStatus().getId()))
			{
				if (foundationObject.getReleaseTime() == null)
				{
					foundationObject.put(SystemClassFieldEnum.RELEASETIME.getName(), DateFormat.getSysDate());
				}
			}

			Map<String, List<SqlParamData>> insertParamMap = new HashMap<>();
			if (classObject.getFieldList() != null)
			{
				for (ClassField field : classObject.getFieldList())
				{
					String tableName = DataServer.getDSCommonService().getTableName(className, field.getName());
					if (!insertParamMap.containsKey(tableName))
					{
						insertParamMap.put(tableName, new ArrayList<>());
					}

					if (isCreateMaster && SystemStatusEnum.RELEASE == foundationObject.getStatus() && "LATESTREVISION$".equalsIgnoreCase(field.getName()))
					{
						foundationObject.put("LATESTREVISION$", "mr");
					}

					insertParamMap.get(tableName)
							.add(new SqlParamData(field.getColumnName(), foundationObject.get(field.getName()), DSCommonUtil.getJavaTypeOfField(field.getType())));
					if (field.getType() == FieldTypeEnum.OBJECT)
					{
						ObjectFieldTypeEnum objectFieldTypeOfField = DataServer.getDSCommonService().getObjectFieldTypeOfField(field, session.getBizModelName());
						if (objectFieldTypeOfField == ObjectFieldTypeEnum.OBJECT)
						{
							String tempFieldName = field.getName();
							String tempColumnName = field.getColumnName();
							if (field.getName().endsWith("$"))
							{
								tempFieldName = tempFieldName.substring(0, tempFieldName.length() - 1);
							}
							if (tempColumnName.endsWith("$"))
							{
								tempColumnName = tempColumnName.substring(0, tempColumnName.length() - 1);
							}
							insertParamMap.get(tableName).add(
									new SqlParamData(tempColumnName + "$CLASS", foundationObject.get(tempFieldName + "$CLASS"), DSCommonUtil.getJavaTypeOfField(field.getType())));
							insertParamMap.get(tableName).add(new SqlParamData(tempColumnName + "$MASTER", foundationObject.get(tempFieldName + "$MASTER"),
									DSCommonUtil.getJavaTypeOfField(field.getType())));
						}
					}
				}
			}

			String sysTableName = DataServer.getDSCommonService().getTableName(className);

			DynamicInsertParamData insertData = new DynamicInsertParamData();
			insertData.setTableName(sysTableName);
			insertData.setInsertParamList(insertParamMap.get(sysTableName));
			//TODO
//			foundationGuid = (String) this.dynaObjectMapper.insertDynamic(insertData);
			foundationGuid = null;

			insertParamMap.remove(sysTableName);
			if (!SetUtils.isNullMap(insertParamMap))
			{
				for (String tableName : insertParamMap.keySet())
				{
					List<SqlParamData> insertParamList = insertParamMap.get(tableName);
					insertParamList.add(new SqlParamData("FOUNDATIONFK", foundationGuid, DSCommonUtil.getJavaTypeOfField(FieldTypeEnum.STRING)));

					insertData = new DynamicInsertParamData();
					insertData.setTableName(tableName);
					insertData.setInsertParamList(insertParamList);
					this.dynaObjectMapper.insertDynamic(insertData);
				}
			}

			// 只检查数据正确性,不实际更新数据
			DataServer.getFolderService().moveToFolder(foundationObject.getObjectGuid(), null, foundationObject.getCommitFolderGuid(), isCheckAcl, isCheckAcl, false, true,
					sessionId, fixTranId);

			// 计算出最新版本 wip、rls 、master的最新版
			if (!isCreateMaster)
			{
				this.calculateInstanceLatesttestVal(classGuid, masterGuid);
			}

			foundationObject.getObjectGuid().setGuid(foundationGuid);
			foundationObject.setGuid(foundationGuid);
			this.updateClassification(foundationObject, sessionId);

			if (isCheckAcl && (isBOMView || isView))
			{
				ObjectGuid end1ObjectGuid;
				if (isBOMView)
				{
					end1ObjectGuid = ((BOMView) foundationObject).getEnd1ObjectGuid();
				}
				else
				{
					end1ObjectGuid = ((ViewObject) foundationObject).getEnd1ObjectGuid();
				}
				AclService aclService = DataServer.getAclService();
				if (!aclService.hasAuthority(end1ObjectGuid, AuthorityEnum.LINK, sessionId))
				{
					throw new DynaDataExceptionAll("LINK", null, DataExceptionEnum.DS_NO_LINK_AUTH, exceptionParameter);
				}
			}

			FoundationObject simpleObj = (FoundationObject) this.dynaObjectMapper.lockForCheckout(foundationGuid,sysTableName);
			foundationObject.put(SystemClassFieldEnum.UPDATETIME.getName(), simpleObj.getUpdateTime());

			FoundationObjectImpl retObject = (FoundationObjectImpl) foundationObject.getClass().getConstructor().newInstance();
			retObject.sync(foundationObject);

			DataServer.getTransactionManager().commitTransaction();

			return retObject;
		}
		catch (SQLException e)
		{
			DataServer.getTransactionManager().rollbackTransaction();

			String masterTable = DataServer.getDSCommonService().getMasterTableName(classGuid);

			String uniques_ = null;
			if ((e.getMessage() != null && e.getMessage().toUpperCase().contains("SUX_CI_" + masterTable.toUpperCase()))
					|| (e.getCause().getMessage() != null && e.getCause().getMessage().toUpperCase().contains("SUX_CI_" + masterTable.toUpperCase())))
			{
				uniques_ = id;
			}
			else if ((e.getMessage() != null && e.getMessage().toUpperCase().contains("SUX_CN_" + masterTable.toUpperCase()))
					|| (e.getCause().getMessage() != null && e.getCause().getMessage().toUpperCase().contains("SUX_CN_" + masterTable.toUpperCase())))
			{
				uniques_ = id;
			}
			else if ((e.getMessage() != null && e.getMessage().toUpperCase().contains("SUX_CU_" + masterTable.toUpperCase()))
					|| (e.getCause().getMessage() != null && e.getCause().getMessage().toUpperCase().contains("SUX_CU_" + masterTable.toUpperCase())))
			{
				uniques_ = StringUtils.isNullString(uniques) ? id : uniques;
			}
			if (uniques_ != null)
			{
				throw new DynaDataExceptionAll("ID_DS_UNIQUE_VIOLATE.uniques=" + uniques_, null, DataExceptionEnum.DS_UNIQUE_VIOLATE, exceptionParameter, uniques_);
			}
			throw new DynaDataExceptionSQL("SQLException Create() Id =" + foundationObject.getId(), e, DataExceptionEnum.DS_CREATE_FOUNDATION, exceptionParameter);
		}
		catch (Exception e)
		{
			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof DynaDataExceptionSQL)
			{
				throw (DynaDataExceptionSQL) e;
			}
			else if (e instanceof DynaDataExceptionAll)
			{
				throw (DynaDataExceptionAll) e;
			}
			throw new DynaDataExceptionAll("Exception Create() Id =" + foundationObject.getId(), e, DataExceptionEnum.DS_CREATE_FOUNDATION, exceptionParameter);
		}
	}

	/**
	 * 删除实例
	 *
	 * @param foundationObject
	 * @param isCheckAcl
	 * @param sessionId
	 * @throws DynaDataException
	 */
	@SuppressWarnings("unchecked")
	protected void delete(FoundationObject foundationObject, boolean isCheckAcl, String sessionId, String fixTranId) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			DataServer.getTransactionManager().startTransaction(fixTranId);

			Map<String, Object> deleteMap = new HashMap<>();
			String guid = foundationObject.getObjectGuid().getGuid();

			boolean isShortCut = foundationObject.isShortcut();
			String className = foundationObject.getObjectGuid().getClassName();
			ClassObject classObject = DataServer.getClassModelService().getClassObject(className);
			boolean isView = classObject.hasInterface(ModelInterfaceEnum.IViewObject);
			boolean isBOMView = classObject.hasInterface(ModelInterfaceEnum.IBOMView);

			// 抛出异常时使用的参数
			String exceptionParameter = foundationObject.getId();
			if (isView || isBOMView)
			{
				exceptionParameter = StringUtils.convertNULLtoString(foundationObject.getName());
			}

			AclService aclService = DataServer.getAclService();
			if ((ISCHECKACL && isCheckAcl))
			{
				String delRefAuth;
				// 如果是快捷方式，判断删除关联关系权限
				if (isShortCut)
				{
					String folderGuid = (String) foundationObject.get("SHORTCUTFOLDERGUID");
					if (folderGuid == null)
					{
						throw new DynaDataExceptionAll("DELETE REF ERROR. ID=" + foundationObject.getId(), null, DataExceptionEnum.DS_FOLDER_IS_NULL, exceptionParameter);
					}
					delRefAuth = aclService.getDelAuthority(foundationObject.getObjectGuid(), StringUtils.convertNULLtoString(folderGuid), sessionId, true);
					if (delRefAuth.equalsIgnoreCase("2"))
					{
						throw new DynaDataExceptionAll("DELETE NO REF AUTH. ID=" + foundationObject.getId(), null, DataExceptionEnum.DS_NO_DEL_PREFERENCE_AUTHORITY,
								exceptionParameter);
					}
				}
				else
				{
					delRefAuth = aclService.getDelAuthority(foundationObject.getObjectGuid(), null, sessionId, false);
					if (delRefAuth.equalsIgnoreCase("2"))
					{
						throw new DynaDataExceptionAll("DELETE NO REF AUTH. ID=" + foundationObject.getId(), null, DataExceptionEnum.DS_NO_DELETE_AUTH, exceptionParameter);
					}
				}
			}

			// 如果数据已经检出抛出异常
			FoundationObject tempFoundationObject = this.stubService.getSystemFieldInfo(guid, foundationObject.getObjectGuid().getClassGuid(), false, sessionId);
			if (tempFoundationObject.isCheckOut())
			{
				throw new DynaDataExceptionAll("DELETE error the data is checkout. ID=" + foundationObject.getId(), null, DataExceptionEnum.DS_DELETE_FOUNDATION_CHECKOUT,
						exceptionParameter);
			}

			// 删除BOM和关系
			if (isBOMView || isView)
			{
				String structureClassGuid = null;
				String templateId = (String) foundationObject.get("TEMPLATEID");
				if (isBOMView)
				{
					BOMTemplateInfo template = sds.findInCache(BOMTemplateInfo.class, new FieldValueEqualsFilter<BOMTemplateInfo>(BOMTemplateInfo.ID, templateId));
					if (template != null)
					{
						structureClassGuid = template.getStructureClassGuid();
					}
				}
				else
				{
					RelationTemplateInfo template = sds.findInCache(RelationTemplateInfo.class,
							new FieldValueEqualsFilter<RelationTemplateInfo>(RelationTemplateInfo.ID, templateId));
					if (template != null)
					{
						structureClassGuid = template.getStructureClassGuid();
					}
				}

				if (StringUtils.isGuid(structureClassGuid))
				{
					this.dynaObjectMapper.deleteAllStructure(DataServer.getDSCommonService().getTableName(structureClassGuid),guid);
				}
			}

			// 删除所有分类数据
			this.deleteClassification(foundationObject.getObjectGuid().getClassGuid(), guid);

			String tableName = DataServer.getDSCommonService().getTableName(className);

			deleteMap.put("SELECT", "COUNT(1) coun");
			deleteMap.put("FROM", tableName);
			deleteMap.put("WHERE", "masterfk = (SELECT masterfk FROM  " + tableName + " WHERE guid = '" + guid + "')");
			List<Map<String, String>> list = this.dynaObjectMapper.selectAutoHalf(deleteMap);

			Map<String, String> deleteValueMap = list.get(0);
			int coun = Integer.parseInt(String.valueOf(deleteValueMap.get("COUN")));
			deleteMap.clear();
			// 如果删除的revision不是最后一个版本，删除revision
			if (coun > 1)
			{
				deleteMap.put("GUID", guid);
				deleteMap.put("tablename", tableName);
				deleteMap.put("CURRENTTIME", new Date());
				// 如果当前操作者是上一次修改用户，不判断更新时间
				if (!(foundationObject.getUpdateUserGuid().equalsIgnoreCase(DataServer.getDSCommonService().getSession(sessionId).getUserGuid())))
				{
					SimpleDateFormat SDFYMDHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					deleteMap.put("UPDATETIME", SDFYMDHMS.format(foundationObject.getUpdateTime()));
				}
				if (this.dynaObjectMapper.deleteRevision(deleteMap) == 0)
				{
					throw new DynaDataExceptionAll("delete foundationObject guid =" + guid, null, DataExceptionEnum.DS_DELETE_FOUNDATION_DATA_LOST, exceptionParameter);
				}

				// 重新计算出最新版本 wip、rls 、master的最新版
				this.calculateInstanceLatesttestVal(foundationObject.getObjectGuid().getClassGuid(), foundationObject.getObjectGuid().getMasterGuid());

				updateValueOfMaster(foundationObject.getObjectGuid(), exceptionParameter, foundationObject.getUnique());

				if (classObject.hasInterface(ModelInterfaceEnum.IManufacturingRule))
				{
					DataServer.getConfigManagerService().deleteConfigTable(foundationObject.getObjectGuid().getMasterGuid(), false, ModelInterfaceEnum.IManufacturingRule,
							sessionId);
				}
				if (classObject.hasInterface(ModelInterfaceEnum.IOption))
				{
					DataServer.getConfigManagerService().deleteConfigTable(foundationObject.getObjectGuid().getMasterGuid(), false, ModelInterfaceEnum.IOption, sessionId);
				}
			}
			else if (coun == 1)
			{
				// 如果是数据导入删除实例，需要特殊处理
				deleteMap.clear();
				deleteMap.put("GUID", guid);
				deleteMap.put("tablename", tableName);
				deleteMap.put("mastertablename", DataServer.getDSCommonService().getMasterTableName(className));
				deleteMap.put("CURRENTTIME", new Date());
				if (!(foundationObject.getUpdateUserGuid().equalsIgnoreCase(DataServer.getDSCommonService().getSession(sessionId).getUserGuid())))
				{
					SimpleDateFormat SDFYMDHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					deleteMap.put("UPDATETIME", SDFYMDHMS.format(foundationObject.getUpdateTime()));
				}
				if (this.dynaObjectMapper.deleteMaster(deleteMap) == 0)
				{
					throw new DynaDataExceptionAll("delete foundationObject guid =" + foundationObject.getObjectGuid().getGuid(), null,
							DataExceptionEnum.DS_DELETE_FOUNDATION_DATA_LOST, exceptionParameter);
				}

				this.dynaObjectMapper.deleteAutoHalf(DataServer.getDSCommonService().getTableName(className),"guid = '" + guid + "'");

				if (classObject.hasInterface(ModelInterfaceEnum.IManufacturingRule))
				{
					DataServer.getConfigManagerService().deleteConfigTable(foundationObject.getObjectGuid().getMasterGuid(), true, ModelInterfaceEnum.IManufacturingRule,
							sessionId);
				}
				if (classObject.hasInterface(ModelInterfaceEnum.IOption))
				{
					DataServer.getConfigManagerService().deleteConfigTable(foundationObject.getObjectGuid().getMasterGuid(), true, ModelInterfaceEnum.IOption, sessionId);
				}
			}
			else
			{
				throw new DynaDataExceptionAll("delete foundationObject guid =" + foundationObject.getObjectGuid().getGuid(), null, DataExceptionEnum.DS_DELETE_FOUNDATION_NO_DATA,
						exceptionParameter);
			}

			DataServer.getTransactionManager().commitTransaction();
		}
		catch (Exception e)
		{
			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof DynaDataExceptionSQL)
			{
				throw (DynaDataExceptionSQL) e;
			}
			else if (e instanceof DynaDataExceptionAll)
			{
				throw (DynaDataExceptionAll) e;
			}
			throw new DynaDataExceptionAll("delete foundationObject guid =" + foundationObject.getObjectGuid(), e, DataExceptionEnum.DS_DELETE_FOUNDATION,
					StringUtils.convertNULLtoString(foundationObject.getName()));
		}
	}

	/**
	 * 删除指定实例版本的所有分类数据
	 *
	 * @param foundationObjectGuid
	 * @throws SQLException
	 */
	private void deleteClassification(String classGuid, String foundationObjectGuid) throws ServiceRequestException
	{
		try
		{
			List<ClassficationFeature> classficationFeatureList = DataServer.getClassificationFeatureService().listClassficationFeatureBySubClass(classGuid);
			if (!SetUtils.isNullList(classficationFeatureList))
			{
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
					if (StringUtils.isNullString(codeObject.getRevisionTableName()) || StringUtils.isNullString(codeObject.getIterationTableName()))
					{
						return;
					}
					if (codeObject.isHasFields())
					{
						this.classificationModelMapper.deleteClassificationData(codeObject.getRevisionTableName(),"FOUNDATIONFK = '" + foundationObjectGuid + "'");
						this.classificationModelMapper.deleteClassificationData(codeObject.getIterationTableName(),"FOUNDATIONFK = '" + foundationObjectGuid + "'");
					}
				}
			}
		}
		catch (Exception e)
		{
			throw new ServiceRequestException("", null, e);
		}
	}

	/**
	 * @param objectGuid
	 * @param (!ISCHECKACL
	 *            ||isCheckAcl)
	 * @param colunmValue
	 * @param authority
	 * @param sessionId
	 * @param column
	 * @return
	 * @throws DynaDataNoAuthorityException
	 */
	private String checkFieldAuthority(ObjectGuid objectGuid, boolean isCheckAcl, String colunmValue, AuthorityEnum authority, String sessionId, String column,
			DataExceptionEnum dataExceptionEnum) throws ServiceRequestException
	{
		AclService aclService = DataServer.getAclService();
		if ((ISCHECKACL && isCheckAcl) && colunmValue != null && !aclService.hasAuthority(objectGuid, authority, sessionId))
		{
			throw new DynaDataExceptionAll("save NoAuthority,guid = " + objectGuid.getGuid(), null, dataExceptionEnum);
		}
		else if (colunmValue != null)
		{
			return column + "='" + colunmValue + "',";
		}
		else
		{
			return "";
		}
	}

	/**
	 * 移交检出
	 * 如果是item：移交检出时需把当前实例和源实例 的检出人改变，同时修改检出位置（判断权限后）
	 * 如果是BOMView:只需要把当前实例的检出人修改即可（判断权限后）
	 *
	 * @param foundationObject
	 * @param toUserGuid
	 * @param isCheckAcl
	 * @param isOwnerOnly
	 * @param sessionId
	 * @throws DynaDataException
	 */
	protected FoundationObject transferCheckout(FoundationObject foundationObject, String toUserGuid, boolean isCheckAcl, boolean isOwnerOnly, String sessionId, String fixTranId)
			throws ServiceRequestException
	{
		boolean isView = (foundationObject instanceof ViewObject) || (foundationObject instanceof BOMView);

		// 抛出异常时使用的参数
		String exceptionParameter = foundationObject.getId();
		if (isView)
		{
			exceptionParameter = StringUtils.convertNULLtoString(foundationObject.getName());
		}

		// 如果没有检出不能移交
		if (foundationObject.getCheckedOutUserGuid() == null)
		{
			throw new DynaDataExceptionAll("transferCheckout error. id = " + foundationObject.getId(), null, DataExceptionEnum.DS_TRANS_CHECKOUT_DATA_LOST, exceptionParameter);
		}

		// 不是自己检出的，不能移交
		if (isOwnerOnly && !DataServer.getDSCommonService().getSession(sessionId).getUserGuid().equals(foundationObject.getCheckedOutUserGuid()))
		{
			throw new DynaDataExceptionAll("transferCheckout error. id = " + foundationObject.getId(), null, DataExceptionEnum.DS_TRANS_CHECKOUT_ERROR_1, exceptionParameter);
		}

		// 对方没有检出权限，不能移交
		if (!this.isAdmin(toUserGuid))
		{
			AclService aclService = DataServer.getAclService();
			String chkOutAuth = aclService.getTransferCheckoutAuthority(toUserGuid, foundationObject.getObjectGuid().getGuid(), foundationObject.getObjectGuid().getClassName());
			if ("2".equals(chkOutAuth))
			{
				throw new DynaDataExceptionAll("transferCheckout error. id = " + foundationObject.getId(), null, DataExceptionEnum.DS_TRANS_CHECKOUT_NO_AUTH_2, exceptionParameter);
			}
			else
			{
				if (!DataServer.getAclService().hasAuthority(foundationObject, AuthorityEnum.READ, sessionId))
				{
					throw new DynaDataExceptionAll("transferCheckout error. id = " + foundationObject.getId(), null, DataExceptionEnum.DS_TRANS_SEL_NO_AUTH_2, exceptionParameter);
				}
			}
		}

		// 移交
		try
		{
			DataServer.getTransactionManager().startTransaction(fixTranId);
			// 查找出此实例对应的checkout实例
			String className = foundationObject.getObjectGuid().getClassName();
			// update revision
			Map<String, Object> transferMap = new HashMap<>();
			// BOMview直接移交
			String transferGuid = "'" + foundationObject.getObjectGuid().getGuid() + "'";

			Date currentTime = new Date();
			transferMap.put("table", DataServer.getDSCommonService().getTableName(className));
			transferMap.put("GUID", transferGuid);
			transferMap.put("UPDATEUSER", DataServer.getDSCommonService().getSession(sessionId).getUserGuid());
			transferMap.put("UPDATETIME", foundationObject.getUpdateTime());
			transferMap.put("TOUSERGUID", toUserGuid);
			transferMap.put("CURRENTTIME", currentTime);
			if (isOwnerOnly)
			{
				transferMap.put("CHECKOUTUSER", DataServer.getDSCommonService().getSession(sessionId).getUserGuid());
			}

			if (this.dynaObjectMapper.transferCheckout(transferMap) == 0)
			{
				if (isOwnerOnly)
				{
					throw new DynaDataExceptionAll("doTransferCheckout() id =" + foundationObject.getId(), null, DataExceptionEnum.DS_TRANS_CHECKOUT_IS_OWNER_ONLY,
							exceptionParameter);
				}
				throw new DynaDataExceptionAll("doTransferCheckout() id =" + foundationObject.getId(), null, DataExceptionEnum.DS_TRANS_CHECKOUT_DATA_LOST, exceptionParameter);
			}

			FoundationObject simpleObj = (FoundationObject) this.dynaObjectMapper.lockForCheckout(transferGuid,(String)transferMap.get("table"));
			foundationObject.put(SystemClassFieldEnum.CHECKOUTUSER.getName(), toUserGuid);
			foundationObject.put(SystemClassFieldEnum.UPDATEUSER.getName(), DataServer.getDSCommonService().getSession(sessionId).getUserGuid());
			foundationObject.put(SystemClassFieldEnum.UPDATETIME.getName(), currentTime);
			
			
			foundationObject.put(SystemClassFieldEnum.UPDATETIME.getName(), simpleObj.getUpdateTime());
			foundationObject.put(SystemClassFieldEnum.CHECKOUTTIME.getName(), simpleObj.getCheckedOutTime());

			DataServer.getTransactionManager().commitTransaction();

			FoundationObjectImpl retObject = (FoundationObjectImpl) foundationObject.getClass().getConstructor().newInstance();
			retObject.sync(foundationObject);

			return retObject;
		}
		catch (Exception e)
		{
			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof DynaDataExceptionSQL)
			{
				throw (DynaDataExceptionSQL) e;
			}
			else if (e instanceof DynaDataExceptionAll)
			{
				throw (DynaDataExceptionAll) e;
			}
			throw new DynaDataExceptionAll("doTransferCheckout() id =" + foundationObject.getId(), e, DataExceptionEnum.DS_TRANS_CHECKOUT, exceptionParameter);
		}

	}

	private boolean isAdmin(String userGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		User user = sds.get(User.class, userGuid);
		if (user != null)
		{
			if (!user.isValid())
			{
				return false;
			}

			List<URIG> urigList = sds.listFromCache(URIG.class, new FieldValueEqualsFilter<>(URIG.USER_GUID, userGuid));
			if (!SetUtils.isNullList(urigList))
			{
				for (URIG urig : urigList)
				{
					String groupGuid = urig.getGroupGuid();
					Group group = sds.get(Group.class, groupGuid);
					if (group.isValid() && group.isAdminGroup())
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 保存文件属性
	 *
	 * @param guid
	 * @param fileGuid
	 * @param fileName
	 * @param fileType
	 * @throws DynaDataException
	 */
	protected void saveFile(String guid, String classGuid, String sessionId, String fileGuid, String fileName, String fileType, String md5) throws ServiceRequestException
	{
		Map<String, Object> fileMap = new HashMap<>();
		fileMap.put("table", DataServer.getDSCommonService().getTableName(classGuid));
		fileMap.put("GUID", guid);
		fileMap.put("FILEGUID", StringUtils.convertNULLtoString(fileGuid));
		fileMap.put("FILENAME", StringUtils.convertNULLtoString(fileName));
		fileMap.put("FILETYPE", StringUtils.convertNULLtoString(fileType));
		fileMap.put("MD5", StringUtils.convertNULLtoString(md5));
		try
		{
			if (this.dynaObjectMapper.saveFile(fileMap) == 0)
			{
				throw new DynaDataExceptionAll("saveFile error.", null, DataExceptionEnum.SAVE_FILE_ERROR);
			}
		}
		catch (Exception e)
		{
			throw new DynaDataExceptionSQL("saveFile error.", e, DataExceptionEnum.SAVE_FILE_ERROR);
		}
	}

	/**
	 * 保存系统字段
	 *
	 * @param objectGuid
	 * @param ownerUserGuid
	 * @param ownerGroupGuid
	 * @param revisionId
	 * @param updatetime
	 * @param fromLifeCyclePhase
	 * @param toLifeCyclePhase
	 * @param rlsUserGuid
	 * @param status
	 * @param isCheckAcl
	 * @param sessionId
	 * @throws DynaDataException
	 */
	protected void save(ObjectGuid objectGuid, //
			String ownerUserGuid, //
			String ownerGroupGuid, //
			String revisionId, //
			Date updatetime, //
			LifecyclePhaseInfo fromLifeCyclePhase, //
			LifecyclePhaseInfo toLifeCyclePhase, //
			String rlsUserGuid, //
			SystemStatusEnum status, //
			boolean isCheckAcl, String sessionId, String fixTranId) throws ServiceRequestException
	{

		// 不允许ItemRevision上修改对象的ID、name、AlterID。
		// 在master上随时可修改对象的ID、name、AlterID（不需要检出操作），需根据权限判断（对master下所有的版本的权限做交集）。
		// Master的ID、name、AlterID修改后，对crt/wip状态的revision同时进行修改。新修订时，使用master的id\name\alterid。

		// 如果此数据处于检出状态那么修改检出的数据
		String updateStr;
		String guid = objectGuid.getGuid();
		try
		{
			DataServer.getTransactionManager().startTransaction(fixTranId);
			// 1,判断map中传进的数据值是否符合规范
			if ((ownerUserGuid != null || ownerGroupGuid != null || revisionId != null) && updatetime == null)
			{
				throw new DynaDataExceptionAll("param error. guid = " + guid, null, DataExceptionEnum.DS_SAVE_FOUNDATION_PARAM_INPUT);
			}
			// 2,判断是否有权限
			// ownerUser
			updateStr = this.checkFieldAuthority(objectGuid, (ISCHECKACL && isCheckAcl), ownerUserGuid, AuthorityEnum.CHANGOWNER, sessionId, "OWNERUSER",
					DataExceptionEnum.DS_NO_AUTH_CHANGOWNER);

			// ownerGroup
			updateStr += this.checkFieldAuthority(objectGuid, (ISCHECKACL && isCheckAcl), ownerGroupGuid, AuthorityEnum.CHANGOWNER, sessionId, "OWNERGROUP",
					DataExceptionEnum.DS_NO_AUTH_CHANGOWNER);

			// revisionId
			updateStr += this.checkFieldAuthority(null, false, revisionId, null, null, "REVISIONID", DataExceptionEnum.DS_NO_AUTH);

			Session session = DataServer.getDSCommonService().getSession(sessionId);
			Map<String, Object> param = new HashMap<>();
			param.put("GUID", guid);
			param.put("TABLENAME", DataServer.getDSCommonService().getTableName(objectGuid.getClassName()));
			if (updateStr.length() > 0)
			{
				updateStr = updateStr.substring(0, updateStr.length() - 1);
				// param.put("ISOWNERCHANGE", "Y");
				param.put("UPDATEUSER", session.getUserGuid());
				param.put("FIELDSQL", updateStr);
			}
			// 更新生命周期（在ec中会用到，不用判断权限）
			if (fromLifeCyclePhase != null && toLifeCyclePhase != null)
			{
				param.put("LIFECYCLEPHASE", toLifeCyclePhase.getGuid());
				param.put("LIFECYCLEPHASEFROM", fromLifeCyclePhase.getGuid());
			}

			// 更新status
			if (status != null)
			{
				param.put("STATUS", status.toString());
				// 更改为发布状态时需要发布人和发布时间更新
				if (status == SystemStatusEnum.RELEASE)
				{
					param.put("ISRELEASE", "Y");
				}
			}

			if (this.foundationObjectMapper.updateFoundationQuick(param) == 0)
			{
				throw new DynaDataExceptionAll("save failed. guid = " + guid, null, DataExceptionEnum.DS_SAVE_FOUNDATION);
			}

			DataServer.getTransactionManager().commitTransaction();
		}
		catch (Exception e)
		{
			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof DynaDataExceptionSQL)
			{
				throw (DynaDataExceptionSQL) e;
			}
			else if (e instanceof DynaDataExceptionAll)
			{
				throw (DynaDataExceptionAll) e;
			}
			throw new DynaDataExceptionAll("save() guid =" + guid, e, DataExceptionEnum.DS_SAVE_FOUNDATION_PARAM);
		}
	}

	protected void obsolete(ObjectGuid foundationObjectGuid, boolean isFromWF, boolean isCheckAcl, String sessionId, String fixTranId) throws ServiceRequestException
	{
		// 判断权限
		AclService aclService = DataServer.getAclService();
		if ((ISCHECKACL && isCheckAcl))
		{
			ObjectGuid objectGuid = new ObjectGuid();
			objectGuid.setGuid(foundationObjectGuid.getGuid());
			if (!aclService.hasAuthority(objectGuid, AuthorityEnum.OBSOLETE, sessionId))
			{
				throw new DynaDataExceptionSQL("obsolete error no obsolete authrity . ", null, DataExceptionEnum.DS_NO_OBSOLETE_AUTH);
			}
		}

		try
		{
			DataServer.getTransactionManager().startTransaction(fixTranId);
			// 设置废弃时间
			Map<String, Object> effectMap = new HashMap<>();
			effectMap.put("table", DataServer.getDSCommonService().getTableName(foundationObjectGuid.getClassName()));
			effectMap.put("GUID", foundationObjectGuid.getGuid());
			effectMap.put("UPDATEUSER", DataServer.getDSCommonService().getSession(sessionId).getUserGuid());
			effectMap.put("OBSOLETEUSER", DataServer.getDSCommonService().getSession(sessionId).getUserGuid());
			effectMap.put("CURRENTTIME", new Date());
			int updateCount = this.dynaObjectMapper.obsolete(effectMap);

			if (!isFromWF && updateCount == 0)
			{
				throw new DynaDataExceptionAll("obsolete error data lost.", null, DataExceptionEnum.DS_OBSOLETE_DATA_LOST);
			}

			// 删除临时表数据
			//TODO
//			this.dynaObjectMapper.deleteTempObsoleteTime(effectMap);
			// 计算出 wip、rls 、master的最新版 bomview和viewobject不用更新
			String className = foundationObjectGuid.getClassName();
			if (StringUtils.isNullString(className))
			{
				String classGuid = foundationObjectGuid.getClassGuid();
				ClassObject classObject = DataServer.getClassModelService().getClassObjectByGuid(classGuid);
				className = classObject.getName();
			}
			ClassObject classObject = DataServer.getClassModelService().getClassObject(className);
			if (!classObject.hasInterface(ModelInterfaceEnum.IBOMView) && !classObject.hasInterface(ModelInterfaceEnum.IViewObject))
			{
				String masterGuid = foundationObjectGuid.getMasterGuid();
				if (!StringUtils.isGuid(masterGuid))
				{
					// 查询出masterGuid
					Map<String, Object> paraMap = new HashMap<>();
					paraMap.put("GUID", foundationObjectGuid.getGuid());
					paraMap.put("tablename", DataServer.getDSCommonService().getTableName(foundationObjectGuid.getClassGuid()));
					masterGuid = (String) this.dynaObjectMapper.getMasterGuidByFoundationGuid(paraMap);
				}

				this.calculateInstanceLatesttestVal(foundationObjectGuid.getClassGuid(), masterGuid);
			}
			DataServer.getTransactionManager().commitTransaction();
		}
		catch (Exception e)
		{
			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof DynaDataExceptionSQL)
			{
				throw (DynaDataExceptionSQL) e;
			}
			else if (e instanceof DynaDataExceptionAll)
			{
				throw (DynaDataExceptionAll) e;
			}
			throw new DynaDataExceptionAll("obsolete guid =" + foundationObjectGuid.getGuid(), e, DataExceptionEnum.DS_OBSOLETE);
		}
	}

	protected void changeStatus(SystemStatusEnum from, SystemStatusEnum to, ObjectGuid foundationObject, boolean isCheckAcl, String sessionId) throws ServiceRequestException
	{
		if (from == null || to == null)
		{
			return;
		}

		this.checkAuthority(from, to, foundationObject.getGuid(), sessionId, isCheckAcl);

		Session session = DataServer.getDSCommonService().getSession(sessionId);

		SystemStatusChangeBean rule = new SystemStatusChangeBean(from, to).getStatusChangeRule();

		List<SqlParamData> whereParamList = new ArrayList<>();
		whereParamList.add(new SqlParamData("GUID", foundationObject.getGuid(), String.class));

		List<SqlParamData> updateParamList = new ArrayList<>();
		updateParamList.add(new SqlParamData("status", to.getId(), String.class));

		String[] clearFields = rule.getClearFields();
		if (clearFields != null && clearFields.length > 0)
		{
			for (String clearField : clearFields)
			{
				updateParamList.add(new SqlParamData(clearField, null, String.class));
			}
		}

		String[] setValFiels = rule.getSetValsFields();
		if (setValFiels != null && setValFiels.length > 0)
		{
			updateParamList.add(new SqlParamData(setValFiels[0], new Date(), Date.class));
			if (setValFiels.length > 1)
			{
				updateParamList.add(new SqlParamData(setValFiels[1], session.getUserGuid(), String.class));
			}
		}

		DynamicUpdateParamData data = new DynamicUpdateParamData();
		data.setTableName(DataServer.getDSCommonService().getTableName(foundationObject.getClassGuid()));
		data.setWhereParamList(whereParamList);
		data.setUpdateParamList(updateParamList);

		try
		{
			int cnt = this.dynaObjectMapper.updateDynamic(data);
			if (cnt == 0)
			{
				throw new DynaDataExceptionAll("changeStatus error.", null, DataExceptionEnum.DS_CHANGESTATUS);
			}

			// 计算出最新版本 wip、rls 、master的最新版 bomview和viewobject不用更新
			ObjectGuid objectGuid = foundationObject.getObjectGuid();
			String className = objectGuid.getClassName();
			ClassObject classObject = DataServer.getClassModelService().getClassObject(className);

			if (!classObject.hasInterface(ModelInterfaceEnum.IBOMView) && !classObject.hasInterface(ModelInterfaceEnum.IViewObject))
			{
				String masterGuid = foundationObject.getMasterGuid();
				if (!StringUtils.isGuid(masterGuid))
				{
					// 查询出masterGuid
					Map<String, Object> paraMap = new HashMap<>();
					paraMap.put("GUID", foundationObject.getGuid());
					paraMap.put("tablename", DataServer.getDSCommonService().getTableName(foundationObject.getClassGuid()));
					masterGuid = (String) this.dynaObjectMapper.getMasterGuidByFoundationGuid(paraMap);
				}

				this.calculateInstanceLatesttestVal(foundationObject.getClassGuid(), masterGuid);
			}

			if (classObject.hasInterface(ModelInterfaceEnum.IManufacturingRule) && from == SystemStatusEnum.PRE && to == SystemStatusEnum.RELEASE)
			{
				DataServer.getConfigManagerService().releaseConfigTable(objectGuid.getMasterGuid(), null, ModelInterfaceEnum.IManufacturingRule, sessionId);
			}
			if (classObject.hasInterface(ModelInterfaceEnum.IOption) && from == SystemStatusEnum.PRE && to == SystemStatusEnum.RELEASE)
			{
				DataServer.getConfigManagerService().releaseConfigTable(objectGuid.getMasterGuid(), null, ModelInterfaceEnum.IOption, sessionId);
			}
		}
		catch (Exception e)
		{
			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof DynaDataExceptionSQL)
			{
				throw (DynaDataExceptionSQL) e;
			}
			else if (e instanceof DynaDataExceptionAll)
			{
				throw (DynaDataExceptionAll) e;
			}
			throw new DynaDataExceptionAll("changeStatus error : guid ", e, DataExceptionEnum.DS_CHANGESTATUS);
		}
	}

	private void checkAuthority(SystemStatusEnum from, SystemStatusEnum to, String foundationObjectGuid, String sessionId, boolean isCheckAcl) throws ServiceRequestException
	{
		AclService aclService = DataServer.getAclService();
		AuthorityEnum authority;
		String errMsg;

		if (to.equals(SystemStatusEnum.RELEASE))
		{
			authority = AuthorityEnum.RELEASE;
			errMsg = "release error no release authrity . ";
		}
		else if (from.equals(SystemStatusEnum.RELEASE) && to.equals(SystemStatusEnum.OBSOLETE))
		{
			authority = AuthorityEnum.OBSOLETE;
			errMsg = "obsolete error no obsolete authrity . ";
		}
		else if (from.equals(SystemStatusEnum.OBSOLETE))
		{
			authority = AuthorityEnum.UNOBSOLETE;
			errMsg = "unobsolete error no unobsolete authrity . ";
		}
		else
		{
			return;
		}

		if ((ISCHECKACL && isCheckAcl))
		{
			ObjectGuid objectGuid = new ObjectGuid();
			objectGuid.setGuid(foundationObjectGuid);
			if (!aclService.hasAuthority(objectGuid, authority, sessionId))
			{
				throw new DynaDataExceptionSQL(errMsg, null, DataExceptionEnum.DS_NO_AUTH);
			}
		}
	}

	/**
	 * 取消废弃
	 *
	 * @param guid
	 * @param isCheckAcl
	 * @param sessionId
	 * @throws DynaDataException
	 */
	public void cancelObsolete(String guid, String classGuid, boolean isCheckAcl, String sessionId) throws ServiceRequestException
	{
		Session session = DataServer.getDSCommonService().getSession(sessionId);
		Map<String, Object> cancelObsoleteMap = new HashMap<>();
		cancelObsoleteMap.put("USERGUID", session.getUserGuid());
		cancelObsoleteMap.put("GUID", guid);
		cancelObsoleteMap.put("tablename", DataServer.getDSCommonService().getTableName(classGuid));
		cancelObsoleteMap.put("CURRENTTIME", new Date());
		try
		{
			if (this.dynaObjectMapper.resume(cancelObsoleteMap) == 0)
			{
				throw new DynaDataExceptionAll("cancelObsolete error.", null, DataExceptionEnum.DS_CANCELOBSOLETE);
			}
		}
		catch (Exception e)
		{
			throw new DynaDataExceptionSQL("cancelObsolete error. ", e, DataExceptionEnum.DS_CANCELOBSOLETE);
		}

	}

	/**
	 * 设置是否导入ERP
	 *
	 * @param guid
	 * @param isExportToERP
	 * @param isCheckAcl
	 * @param sessionId
	 * @throws DynaDataException
	 */
	public void setIsExportToERP(String guid, String className, boolean isExportToERP, boolean isCheckAcl, String sessionId) throws ServiceRequestException
	{
		Session session = DataServer.getDSCommonService().getSession(sessionId);

		String isExportToERPStr;
		if (isExportToERP)
		{
			isExportToERPStr = "1";
		}
		else
		{
			isExportToERPStr = "0";
		}

		List<SqlParamData> updateParamList = new ArrayList<>();
		updateParamList.add(new SqlParamData("ISEXPORTTOERP", isExportToERPStr, String.class));
		updateParamList.add(new SqlParamData("UPDATEUSER", session.getUserGuid(), String.class));
		updateParamList.add(new SqlParamData("UPDATETIME", new Date(), Date.class));

		List<SqlParamData> whereParamList = new ArrayList<>();
		whereParamList.add(new SqlParamData("guid", guid, String.class));

		DynamicUpdateParamData paramData = new DynamicUpdateParamData();
		paramData.setTableName(DataServer.getDSCommonService().getTableName(className));
		paramData.setUpdateParamList(updateParamList);
		paramData.setWhereParamList(whereParamList);

		try
		{
			if (this.dynaObjectMapper.updateDynamic(paramData) == 0)
			{
				throw new DynaDataExceptionAll("setIsExportToERP error.", null, DataExceptionEnum.DS_ISEXPORTTOERP);
			}
		}
		catch (Exception e)
		{
			throw new DynaDataExceptionSQL("setIsExportToERP error. ", e, DataExceptionEnum.DS_ISEXPORTTOERP);
		}

	}

	protected void saveOwner(String foundationGuid, String classGuid, String ownerUserGuid, String ownerGroupGuid, String sessionId) throws ServiceRequestException
	{
		Session session = DataServer.getDSCommonService().getSession(sessionId);
		String userGuid = session.getUserGuid();

		Map<String, Object> saveMap = new HashMap<>();
		saveMap.put("FOUNDATIONGUID", foundationGuid);
		saveMap.put("OWNERUSERGUID", ownerUserGuid);
		saveMap.put("OWNERGROUPGUID", ownerGroupGuid);
		saveMap.put("UPDATEUSERGUID", userGuid);
		saveMap.put("tablename", DataServer.getDSCommonService().getTableName(classGuid));
		saveMap.put("CURRENTTIME", new Date());
		try
		{
			if (this.dynaObjectMapper.saveOwner(saveMap) == 0)
			{
				throw new DynaDataExceptionAll("saveOwner failed. guid = " + foundationGuid, null, DataExceptionEnum.DS_MOFIFY_OWNER_ERROR_DELETE);
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
			throw new DynaDataExceptionAll("saveOwner guid =" + foundationGuid, e, DataExceptionEnum.DS_MOFIFY_OWNER_ERROR);
		}
	}

	@SuppressWarnings("unchecked")
	protected void deleteMaster(String masterGuid, String classGuid, boolean isCheckAcl, String sessionId, String fixTranId) throws ServiceRequestException
	{
		try
		{
			DataServer.getTransactionManager().startTransaction(fixTranId);
			// 取出master下的所有版本
			String tableName = DataServer.getDSCommonService().getTableName(classGuid);
			Map<String, Object> paraMap = new HashMap<>();
			paraMap.put("MASTERFK", masterGuid);
			paraMap.put("tablename", tableName);
			List<UpperKeyMap> revisionList = this.dynaObjectMapper.getAllRevisionByMaster(paraMap);
			if (!SetUtils.isNullList(revisionList))
			{
				for (Map<String, String> revisionMap : revisionList)
				{
					// 判断是否检出状态
					String isCheckout = revisionMap.get("ISCHECKOUT$");
					String revisionId = revisionMap.get("REVISIONID$");
					if ("Y".equals(isCheckout))
					{
						throw new DynaDataExceptionAll("deleteMaster exception,some data is in checkOut status. masterGuid =" + masterGuid, null,
								DataExceptionEnum.DS_DELETE_MASTER_ERROR_STATUS, revisionId);
					}
					// 判断是否PRE、RLS、OBS状态
					String status = revisionMap.get("STATUS$");
					if ("PRE".equals(status) || "RLS".equals(status) || "OBS".equals(status))
					{
						throw new DynaDataExceptionAll("deleteMaster exception,some data is in PRE、RLS、OBS status. masterGuid =" + masterGuid, null,
								DataExceptionEnum.DS_DELETE_MASTER_ERROR_STATUS, revisionId);
					}
					// 判断权限
					if (ISCHECKACL && isCheckAcl)
					{
						String guid = revisionMap.get("GUID$");
						ObjectGuid objectGuid = new ObjectGuid();
						objectGuid.setGuid(guid);

						AclService aclService = DataServer.getAclService();
						if (!aclService.hasAuthority(objectGuid, AuthorityEnum.DELETE, sessionId))
						{
							throw new DynaDataExceptionAll("deleteMaster exception , the user is not auth for some data. masterGuid =" + masterGuid, null,
									DataExceptionEnum.DS_DELETE_MASTER_ERROR_AUTH, revisionId);
						}
					}
				}

				// 删除master
				paraMap.clear();
				paraMap.put("MASTERGUID", masterGuid);
				paraMap.put("tablename", tableName);
				paraMap.put("mastertablename", DataServer.getDSCommonService().getMasterTableName(classGuid));
				if (this.dynaObjectMapper.deleteMasterByMasterGuid(paraMap) == 0)
				{
					throw new DynaDataExceptionAll("deleteMaster exception , some data is in WF or is deleted. masterGuid =" + masterGuid, null,
							DataExceptionEnum.DS_DELETE_MASTER_ERROR_WF_DELETED);
				}

				this.dynaObjectMapper.deleteRevisionByMaster(tableName,masterGuid);
			}

			ClassObject classObject = DataServer.getClassModelService().getClassObjectByGuid(classGuid);
			if (classObject.hasInterface(ModelInterfaceEnum.IManufacturingRule))
			{
				DataServer.getConfigManagerService().deleteConfigTable(masterGuid, true, ModelInterfaceEnum.IManufacturingRule, sessionId);
			}
			if (classObject.hasInterface(ModelInterfaceEnum.IOption))
			{
				DataServer.getConfigManagerService().deleteConfigTable(masterGuid, true, ModelInterfaceEnum.IManufacturingRule, sessionId);
			}

			DataServer.getTransactionManager().commitTransaction();
		}
		catch (Exception e)
		{
			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof DynaDataExceptionSQL)
			{
				throw (DynaDataExceptionSQL) e;
			}
			else if (e instanceof DynaDataExceptionAll)
			{
				throw (DynaDataExceptionAll) e;
			}
			throw new DynaDataExceptionAll("deleteMaster exception masterGuid =" + masterGuid, e, DataExceptionEnum.DS_DELETE_MASTER_ERROR);
		}
	}

	private void updateSpecialField(FoundationObject foundationObject, String sourceGuid, String sessionId) throws ServiceRequestException
	{
		boolean isView = (foundationObject instanceof ViewObject) || (foundationObject instanceof BOMView);

		// 抛出异常时使用的参数
		String exceptionParameter = foundationObject.getId();
		if (isView)
		{
			exceptionParameter = StringUtils.convertNULLtoString(foundationObject.getName());
		}

		String foundationUpdate = "";
		boolean maFoundtionIsChanged = false;
		// 增加对EC的判断
		if (foundationObject.isChanged(SystemClassFieldEnum.ECFLAG.getName()))
		{
			maFoundtionIsChanged = true;
			ObjectGuid ecFlagObjectGuid = foundationObject.getECFlag();
			if (ecFlagObjectGuid == null)
			{
				foundationUpdate += "ECFLAG = '',";
				foundationUpdate += "ECFLAG$CLASS = '',";
				foundationUpdate += "ECFLAG$MASTER = '',";
			}
			else
			{
				foundationUpdate += "ECFLAG = '" + ecFlagObjectGuid.getGuid() + "',";
				foundationUpdate += "ECFLAG$CLASS = '" + StringUtils.convertNULLtoString(ecFlagObjectGuid.getClassGuid()) + "',";
				foundationUpdate += "ECFLAG$MASTER = '" + StringUtils.convertNULLtoString(ecFlagObjectGuid.getMasterGuid()) + "',";
			}
		}

		// 增加对isExportToErp的判断
		if (foundationObject.isChanged(SystemClassFieldEnum.ISEXPORTTOERP.getName()))
		{
			maFoundtionIsChanged = true;
			if (foundationObject.isExportToERP())
			{
				foundationUpdate += "ISEXPORTTOERP = '1',";
			}
			else
			{
				foundationUpdate += "ISEXPORTTOERP = '0',";
			}
		}

		if (foundationObject.isChanged(SystemClassFieldEnum.UNIQUES.getName()))
		{
			maFoundtionIsChanged = true;
			foundationUpdate += "UNIQUES = '" + StringUtils.translateSpecialChar(StringUtils.convertNULLtoString(foundationObject.getUnique())) + "',";
		}

		if (foundationObject.isChanged(SystemClassFieldEnum.REPEAT.getName()))
		{
			maFoundtionIsChanged = true;
			foundationUpdate += "REPEATVALUE = '" + StringUtils.translateSpecialChar(StringUtils.convertNULLtoString(foundationObject.getRepeat())) + "',";
		}

		if (foundationObject.isChanged(SystemClassFieldEnum.MD5.getName()))
		{
			maFoundtionIsChanged = true;
			foundationUpdate += "MD5 = '" + StringUtils.convertNULLtoString(foundationObject.getMD5()) + "',";
		}

		if (maFoundtionIsChanged)
		{
			if (!StringUtils.isNullString(foundationUpdate))
			{
				foundationUpdate += " masterfk = '" + foundationObject.getObjectGuid().getMasterGuid() + "'";
			}
			else
			{
				foundationUpdate = "masterfk = '" + foundationObject.getObjectGuid().getMasterGuid() + "'";
			}
			Map<String, Object> updateMap = new HashMap<>();
			updateMap.put("GUID", sourceGuid);
			updateMap.put("UPDATESTATEMENT", foundationUpdate);
			updateMap.put("tablename", DataServer.getDSCommonService().getTableName(foundationObject.getObjectGuid().getClassGuid()));
			try
			{
				if (this.dynaObjectMapper.saveSystemSpecialFiled(updateMap) == 0)
				{
					throw new DynaDataExceptionAll("save failed. guid = " + sourceGuid, null, DataExceptionEnum.DS_SAVE_FOUNDATION_REVISION, exceptionParameter);
				}
			}
			catch (Exception e)
			{
				throw new DynaDataExceptionSQL("save() Id =" + foundationObject.getId(), e, DataExceptionEnum.DS_SAVE_FOUNDATION, exceptionParameter);
			}
		}
	}

	/**
	 * 不通过工作流，把实例直接发布。
	 *
	 * @param foundationObjectGuid
	 *            要发布的实例。
	 * @param sessionId
	 *            session。
	 * @throws DynaDataException
	 */
	protected void release(ObjectGuid foundationObjectGuid, String sessionId, String fixTranId) throws ServiceRequestException
	{
		String user = DataServer.getDSCommonService().getSession(sessionId).getUserGuid();

		try
		{
			DataServer.getTransactionManager().startTransaction(fixTranId);

			Map<String, Object> releaseMap = new HashMap<>();
			releaseMap.put("table", DataServer.getDSCommonService().getTableName(foundationObjectGuid.getClassName()));
			releaseMap.put("UPDATEUSER", user);
			releaseMap.put("GUID", foundationObjectGuid.getGuid());
			releaseMap.put("CURRENTTIME", new Date());
			int updateCount = this.dynaObjectMapper.release(releaseMap);
			if (updateCount == 0)
			{
				DataServer.getTransactionManager().rollbackTransaction();
				throw new DynaDataExceptionAll("release error data lost.", null, DataExceptionEnum.DS_RELEASE_DATA_LOST);
			}

			// 重新计算最新版本信息
			this.calculateInstanceLatesttestVal(foundationObjectGuid.getClassGuid(), foundationObjectGuid.getMasterGuid());

			ClassObject classObject = DataServer.getClassModelService().getClassObject(foundationObjectGuid.getClassName());
			if (classObject.hasInterface(ModelInterfaceEnum.IManufacturingRule))
			{
				DataServer.getConfigManagerService().releaseConfigTable(foundationObjectGuid.getMasterGuid(), null, ModelInterfaceEnum.IManufacturingRule, sessionId);
			}
			if (classObject.hasInterface(ModelInterfaceEnum.IOption))
			{
				DataServer.getConfigManagerService().releaseConfigTable(foundationObjectGuid.getMasterGuid(), null, ModelInterfaceEnum.IOption, sessionId);
			}

			DataServer.getTransactionManager().commitTransaction();
		}
		catch (Exception e)
		{
			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof DynaDataExceptionSQL)
			{
				throw (DynaDataExceptionSQL) e;
			}
			else if (e instanceof DynaDataExceptionAll)
			{
				throw (DynaDataExceptionAll) e;
			}
			throw new DynaDataExceptionAll("release error : guid ", e, DataExceptionEnum.DS_RELEASE);
		}
	}

	private String getLibraryByFolder(String folderGuid) throws SQLException
	{
		Folder folder = DataServer.getSystemDataService().get(Folder.class, folderGuid);
		return folder.getLibraryUser();
	}

	@SuppressWarnings("unchecked")
	public void copyBomOrRelation(String viewClassGuid, String origViewGuid, String destViewGuid, String structureClassGuid, String fotype, Map<String, Object> specialField,
			String sessionId, String fixTranId) throws ServiceRequestException
	{
		try
		{
			Session session = DataServer.getDSCommonService().getSession(sessionId);

			// 取得结构表的字段信息
			String tableName = DataServer.getDSCommonService().getTableName(structureClassGuid);

			StringBuilder columnBuilder = new StringBuilder();
			List<ClassField> fieldList = DataServer.getClassModelService().getClassObjectByGuid(structureClassGuid).getFieldList();
			for (ClassField field : fieldList)
			{
				if (columnBuilder.length() > 0)
				{
					columnBuilder.append(",");
				}
				// 排除guid和viewfk
				if ("GUID".equalsIgnoreCase(field.getColumnName()) || "VIEWFK".equalsIgnoreCase(field.getColumnName()))
				{
					continue;
				}
				columnBuilder.append(field.getColumnName());
				ObjectFieldTypeEnum objectFieldTypeEnum = DataServer.getDSCommonService().getObjectFieldTypeOfField(field, session.getBizModelName());
				if (objectFieldTypeEnum == ObjectFieldTypeEnum.OBJECT)
				{
					columnBuilder.append(",").append(field.getColumnName()).append("$MASTER");
					columnBuilder.append(",").append(field.getColumnName()).append("$CLASS");
				}
			}

			// 把结构数据存储在临时表中
			Map<String, Object> param = new HashMap<>();
			param.put("BASETABLENAME", tableName);
			param.put("ORIGVIEWGUID", origViewGuid);
			List<String> end2ClassList = this.dynaObjectMapper.selectEnd2ClassOfStruc(tableName,origViewGuid);

			StringBuilder tableBuffer = new StringBuilder();
			for (String end2Class : end2ClassList)
			{
				if (tableBuffer.length() > 0)
				{
					tableBuffer.append(" union ");
				}
				tableBuffer.append("select a.masterfk, a.latestrevision, a.status from ").append(DataServer.getDSCommonService().getTableName(end2Class)).append(" a");
			}
			if (tableBuffer.length() == 0)
			{
				return;
			}

			DataServer.getTransactionManager().startTransaction(fixTranId);
			param.put("tablesql", tableBuffer.toString());
			param.put("DESTVIEWGUID", destViewGuid);
			String guid = (String) this.dynaObjectMapper.insertAllStructureInfo(param);

			// 从临时表中创建结构表数据
			param.clear();
			param.put("BASETABLENAME", tableName);
			param.put("REVISION_COLUMN", columnBuilder.toString());
			param.put("STRUCTUREGUID", guid);
			this.dynaObjectMapper.copyStructure(param);

			// 根据模板修正结构中end2的值
			// BOM不会因为view的状态而影响end2的值，因此只处理关系
			if ("5".equals(fotype))
			{
				param.clear();
				param.put("VIEWGUID", destViewGuid);
				param.put("VIEWTABLE", DataServer.getDSCommonService().getTableName(viewClassGuid));
				param.put("STRUCTABLE", tableName);

				this.dynaObjectMapper.updateEnd2ToNull(param);
			}

			if (specialField == null)
			{
				specialField = new HashMap<>();
			}
			specialField.put("createuser", session.getUserGuid());
			specialField.put("updateuser", session.getUserGuid());
			if ("3".equals(fotype))
			{
				specialField.put("isBOMModified", "N");
			}

			List<SqlParamData> whereParamList = new ArrayList<>();
			whereParamList.add(new SqlParamData("viewfk", destViewGuid, String.class));

			List<SqlParamData> updateParamList = new ArrayList<>();
			updateParamList.add(new SqlParamData("createtime", new Date(), Date.class));
			updateParamList.add(new SqlParamData("updatetime", new Date(), Date.class));
			updateParamList.add(new SqlParamData("createuser", session.getUserGuid(), String.class));
			updateParamList.add(new SqlParamData("updateuser", session.getUserGuid(), String.class));
			if ("3".equals(fotype))
			{
				updateParamList.add(new SqlParamData("isBOMModified", "N", String.class));
			}

			for (Entry<String, Object> entry : specialField.entrySet())
			{
				if ((columnBuilder.toString().toLowerCase()).contains(entry.getKey().toLowerCase()))
				{
					if (BOMStructure.BOMKEY.equalsIgnoreCase(entry.getKey()))
					{
						updateParamList.add(new SqlParamData(entry.getKey(), StringUtils.genericGuid(), String.class));
					}
					else
					{
						updateParamList.add(new SqlParamData(entry.getKey(), entry.getValue(), String.class));
					}
				}
			}

			DynamicUpdateParamData data = new DynamicUpdateParamData();
			data.setTableName(tableName);
			data.setWhereParamList(whereParamList);
			data.setUpdateParamList(updateParamList);

			this.dynaObjectMapper.updateDynamic(data);

			DataServer.getTransactionManager().commitTransaction();
		}
		catch (Exception e)
		{
			DataServer.getTransactionManager().rollbackTransaction();
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

	public void copyFileOnlyRecord(FoundationObject destObject, FoundationObject srcObject, String sessionId, String fixTranId) throws ServiceRequestException
	{
		try
		{
			SystemDataService sds = DataServer.getSystemDataService();

			DataServer.getTransactionManager().startTransaction(fixTranId);
			Map<String, Object> filter = new HashMap<>();
			filter.put(DSSFileInfo.REVISION_GUID, srcObject.getObjectGuid().getGuid());
			filter.put(DSSFileInfo.ITERATION_ID, srcObject.getIterationId());
			List<DSSFileInfo> fileList = sds.query(DSSFileInfo.class, filter, "selectForCopy");
			if (fileList != null)
			{
				for (DSSFileInfo info : fileList)
				{
					info.setGuid(null);
					info.setRevision(destObject.getObjectGuid().getGuid());
					info.setIterationId(destObject.getIterationId());
					sds.save(info);
				}
			}
			DSSFileInfo fileInfo = new DSSFileInfo();
			fileInfo.put("DESTREVISIONGUID", destObject.getObjectGuid().getGuid());
			fileInfo.put("TABLENAME", DataServer.getDSCommonService().getTableName(destObject.getObjectGuid().getClassGuid()));
			sds.save(fileInfo, "updatePrimaryFileAfterCopyOnly");
			DataServer.getTransactionManager().commitTransaction();
		}
		catch (Exception e)
		{
			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof DynaDataExceptionSQL)
			{
				throw (DynaDataExceptionSQL) e;
			}
			else if (e instanceof DynaDataExceptionAll)
			{
				throw (DynaDataExceptionAll) e;
			}
			throw new DynaDataExceptionAll("copyFileOnlyRecord exception. ", e, DataExceptionEnum.DS_COPY_BOM_OR_RELATION_ERROR);
		}
	}

	public void changePhase(String foundationGuid, String foundationClassGuid, String lifecyclePhaseGuid, boolean isCheckAuth, String fixTranId) throws ServiceRequestException
	{
		if (!StringUtils.isGuid(lifecyclePhaseGuid))
		{
			return;
		}

		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> filter = new HashMap<>();
		filter.put("FOUNDATIONGUID", foundationGuid);
		filter.put("LIFECYCLEPHASE", lifecyclePhaseGuid);
		filter.put("tablename", DataServer.getDSCommonService().getTableName(foundationClassGuid));
		try
		{
			DataServer.getTransactionManager().startTransaction(fixTranId);
			sds.update(FoundationObject.class, filter, "updateFoundationPhase");
			DataServer.getTransactionManager().commitTransaction();
		}
		catch (Exception e)
		{
			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof DynaDataExceptionSQL)
			{
				throw (DynaDataExceptionSQL) e;
			}
			else if (e instanceof DynaDataExceptionAll)
			{
				throw (DynaDataExceptionAll) e;
			}
			throw new DynaDataExceptionAll("changePhase exception. ", e, DataExceptionEnum.DS_UPDATE_DATA_EXCEPTION);
		}

	}

	// 删除对象版本后，异步删除关联数据
	public void deleteReferenceData(ObjectGuid objectGuid, String sessionId) throws ServiceRequestException
	{
		// 只有先删除了原始对象，其关联数据才能被删除

		try
		{
			Map<String, Object> paraMap = new HashMap<>();
			paraMap.put("GUID", objectGuid.getGuid());
			paraMap.put("tablename", DataServer.getDSCommonService().getTableName(objectGuid.getClassGuid()));
			FoundationObject foundationObject = (FoundationObjectImpl) this.foundationObjectMapper.getFoundationByGuid(paraMap);
			if (foundationObject != null)
			{
				return;
			}

			ClassObject classObject = DataServer.getClassModelService().getClassObjectByGuid(objectGuid.getClassGuid());
			String classGuidOrClassName = objectGuid.getClassGuid();
			if (!StringUtils.isGuid(classGuidOrClassName))
			{
				classGuidOrClassName = objectGuid.getClassName();
				classObject = DataServer.getClassModelService().getClassObject(objectGuid.getClassName());
			}
			if (classObject == null || StringUtils.isNullString(classGuidOrClassName))
			{
				return;
			}

			String basetableName = DataServer.getDSCommonService().getRealBaseTableName(classGuidOrClassName);

			paraMap.clear();
			paraMap.put("GUID", objectGuid.getGuid());
			paraMap.put("tablename", basetableName);
			// 取得所有的表索引
			List<Integer> tableIndexList = classObject.getTableIndexList();
			for (Integer index : tableIndexList)
			{
				// 删除非0表数据
				if (index != 0)
				{
					this.foundationObjectMapper.deleteRevisionByGuid(basetableName + "_" + index,objectGuid.getGuid());
				}
			}

			// 删除i表数据
			this.foundationObjectMapper.deleteRevisionByGuid(basetableName + "_I", objectGuid.getGuid());

			// 删除master表数据
			Integer result = this.foundationObjectMapper.hasRevision(basetableName,objectGuid.getMasterGuid());
			boolean isAllRevisionDeleled = result == 0;

			// 根据关联关系模板取得结构类,从结构类表中删除end2信息
			if (classObject.hasInterface(ModelInterfaceEnum.IViewObject))
			{
				this.deleteViewReferenceData(objectGuid, false);
			}
			else if (classObject.hasInterface(ModelInterfaceEnum.IBOMView))
			{
				this.deleteViewReferenceData(objectGuid, true);
			}
			else
			{
				// 删除BOM相关数据
				this.deleteReferenceDataByEnd1OrEnd2(objectGuid, true, isAllRevisionDeleled, sessionId);
				// 删除关联关系相关数据
				this.deleteReferenceDataByEnd1OrEnd2(objectGuid, false, isAllRevisionDeleled, sessionId);
			}

			// 删除master表数据
			if (isAllRevisionDeleled)
			{
				this.foundationObjectMapper.deleteByGuid(basetableName + "_mast",objectGuid.getMasterGuid());
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
			throw new DynaDataExceptionAll("changePhase exception. ", e, DataExceptionEnum.DS_UPDATE_DATA_EXCEPTION);
		}
	}

	/**
	 * 删除对象作为END1或者END2时，关联的BOM数据和关联关系数据
	 *
	 * @param objectGuid
	 *            要删除的对象
	 * @param isBOM
	 *            BOM或者关联关系
	 * @param isAllRevisionDeleled
	 *            所有版本数据都已被删除，此时对象作为END2时的所有结构数据都要被删除
	 * @throws SQLException
	 * @throws ServiceRequestException
	 */
	private void deleteReferenceDataByEnd1OrEnd2(ObjectGuid objectGuid, boolean isBOM, boolean isAllRevisionDeleled, String sessionId) throws SQLException, ServiceRequestException
	{
		List<String> viewClassGuidList = new ArrayList<>();
		List<String> structureClassGuidList = new ArrayList<>();
		if (isBOM)
		{
			List<BOMTemplateInfo> bomTemplateList = DataServer.getRelationService().listAllBOMTemplateInfo(true);
			if (!SetUtils.isNullList(bomTemplateList))
			{
				for (BOMTemplateInfo bomTemplate : bomTemplateList)
				{
					if (StringUtils.isGuid(bomTemplate.getViewClassGuid()) && !viewClassGuidList.contains(bomTemplate.getViewClassGuid()))
					{
						viewClassGuidList.add(bomTemplate.getViewClassGuid());
					}
					if (isAllRevisionDeleled)
					{
						if (StringUtils.isGuid(bomTemplate.getStructureClassGuid()) && !structureClassGuidList.contains(bomTemplate.getStructureClassGuid()))
						{
							structureClassGuidList.add(bomTemplate.getStructureClassGuid());
						}
					}
					else if (bomTemplate.getPrecise()==BomPreciseType.PRECISE)
					{
						structureClassGuidList.add(bomTemplate.getStructureClassGuid());
					}
				}
			}
		}
		else
		{
			List<RelationTemplateInfo> relationTemplateList = DataServer.getRelationService().listAllRelationTemplateInfo(true);
			if (!SetUtils.isNullList(relationTemplateList))
			{
				for (RelationTemplateInfo relationTemplate : relationTemplateList)
				{
					if (StringUtils.isGuid(relationTemplate.getViewClassGuid()) && !viewClassGuidList.contains(relationTemplate.getViewClassGuid()))
					{
						viewClassGuidList.add(relationTemplate.getViewClassGuid());
					}
					if (isAllRevisionDeleled)
					{
						if (StringUtils.isGuid(relationTemplate.getStructureClassGuid()) && !structureClassGuidList.contains(relationTemplate.getStructureClassGuid()))
						{
							structureClassGuidList.add(relationTemplate.getStructureClassGuid());
						}
					}
					else
					{
						if ("2".equals(relationTemplate.getEnd2Type()))
						{
							structureClassGuidList.add(relationTemplate.getStructureClassGuid());
						}
					}
				}
			}
		}

		// 删除作为END1时的VIEW
		if (!SetUtils.isNullList(viewClassGuidList))
		{
			for (String viewClassGuid : viewClassGuidList)
			{
				this.deleteViewReferenceDataByEnd1(objectGuid, viewClassGuid, sessionId);
			}
		}

		// 删除作为END2时的结构
		if (!SetUtils.isNullList(structureClassGuidList))
		{
			for (String structureClassGuid : structureClassGuidList)
			{
				this.deleteStrucReferenceDataByEnd2(objectGuid, structureClassGuid, isAllRevisionDeleled);
			}
		}
	}

	/**
	 * 对象作为BOMView或者ViewObject时，删除关联的数据
	 *
	 * @param viewObjectGuid
	 * @param isBOM
	 * @throws SQLException
	 * @throws ServiceRequestException
	 */
	private void deleteViewReferenceData(ObjectGuid viewObjectGuid, boolean isBOM) throws SQLException, ServiceRequestException
	{
		List<String> strucClassGuidList = new ArrayList<>();
		if (isBOM)
		{
			List<BOMTemplateInfo> bomTemplateList = DataServer.getRelationService().listAllBOMTemplateInfo(true);
			if (!SetUtils.isNullList(bomTemplateList))
			{
				for (BOMTemplateInfo templateInfo : bomTemplateList)
				{
					if (StringUtils.isGuid(templateInfo.getViewClassGuid()) && templateInfo.getViewClassGuid().equals(viewObjectGuid.getClassGuid())
							&& StringUtils.isGuid(templateInfo.getStructureClassGuid()) && !strucClassGuidList.contains(templateInfo.getStructureClassGuid()))
					{
						strucClassGuidList.add(templateInfo.getStructureClassGuid());
					}
				}
			}
		}
		else
		{
			List<RelationTemplateInfo> relationTemplateList = DataServer.getRelationService().listAllRelationTemplateInfo(true);
			if (!SetUtils.isNullList(relationTemplateList))
			{
				for (RelationTemplateInfo templateInfo : relationTemplateList)
				{
					if (StringUtils.isGuid(templateInfo.getViewClassGuid()) && templateInfo.getViewClassGuid().equals(viewObjectGuid.getClassGuid())
							&& StringUtils.isGuid(templateInfo.getStructureClassGuid()) && !strucClassGuidList.contains(templateInfo.getStructureClassGuid()))
					{
						strucClassGuidList.add(templateInfo.getStructureClassGuid());
					}
				}
			}
		}

		if (!SetUtils.isNullList(strucClassGuidList))
		{
			Map<String, String> paraMap = new HashMap<>();
			for (String strucClassGuid : strucClassGuidList)
			{
				// 删除所有结构数据
				String strucTable = DataServer.getDSCommonService().getTableName(strucClassGuid);
				if (!StringUtils.isNullString(strucTable))
				{
					// 根据view删除对应的所有struc
					this.foundationObjectMapper.deleteStrucByViewGuid(strucTable, viewObjectGuid.getGuid());
				}
			}
		}
	}

	/**
	 * 对象作为END2时，删除所有相关的结构数据
	 *
	 * @param end2ObjectGuid
	 * @param structureClassGuid
	 * @param isAllRevisionDeleled
	 * @throws ServiceRequestException
	 * @throws SQLException
	 */
	private void deleteStrucReferenceDataByEnd2(ObjectGuid end2ObjectGuid, String structureClassGuid, boolean isAllRevisionDeleled) throws ServiceRequestException, SQLException
	{
		String strucTable = DataServer.getDSCommonService().getTableName(structureClassGuid);
		if (!StringUtils.isNullString(strucTable))
		{
			Map<String, String> paraMap = new HashMap<>();
			paraMap.put("END2", end2ObjectGuid.getGuid());
			if (isAllRevisionDeleled)
			{
				paraMap.put("END2MASTER", end2ObjectGuid.getMasterGuid());
			}
			if (isAllRevisionDeleled)
			{
				this.foundationObjectMapper.deleteStrucByEnd2MASTER(strucTable, end2ObjectGuid.getMasterGuid());
			}
			else
			{
				this.foundationObjectMapper.deleteStrucByEnd2GUID(strucTable, end2ObjectGuid.getGuid());
			}
		}
	}

	/**
	 * 对象作为end1时删除关联的视图数据和结构数据
	 *
	 * @param end1ObjectGuid
	 * @param viewClassGuid
	 * @throws SQLException
	 * @throws ServiceRequestException
	 */
	private void deleteViewReferenceDataByEnd1(ObjectGuid end1ObjectGuid, String viewClassGuid, String sessionId) throws SQLException, ServiceRequestException
	{
		String viewTable = DataServer.getDSCommonService().getTableName(viewClassGuid);
		if (!StringUtils.isNullString(viewTable))
		{
			Map<String, String> paraMap = new HashMap<>();
			paraMap.put("end1", end1ObjectGuid.getGuid());
			paraMap.put("tablename", viewTable);
			List<FoundationObject> viewList = this.foundationObjectMapper.selectViewbyEnd1(viewTable, end1ObjectGuid.getGuid());
			if (!SetUtils.isNullList(viewList))
			{
				for (FoundationObject view : viewList)
				{
					paraMap.clear();
					paraMap.put("tablename", viewTable);
					paraMap.put("GUID", view.getObjectGuid().getGuid());
					this.foundationObjectMapper.deleteByGuid(viewTable, view.getObjectGuid().getGuid());

					this.deleteReferenceData(view.getObjectGuid(), sessionId);
				}
			}
		}
	}

	public void updateValueOfMaster(ObjectGuid objectGuid, String exceptionParameter, String uniques) throws ServiceRequestException
	{
		String classGuid = objectGuid.getClassGuid();
		ClassObject classObject = DataServer.getClassModelService().getClassObjectByGuid(classGuid);
		if (!classObject.isIdUnique() && !classObject.isCheckUnique())
		{
			return;
		}

		String baseTable = DataServer.getDSCommonService().getRealBaseTableName(objectGuid.getClassGuid());
		Map<String, Object> param = new HashMap<>();
		param.put("BASETABLE", baseTable);
		param.put("MASTERGUID", objectGuid.getMasterGuid());

		try
		{
			this.foundationObjectMapper.updateValOfMaster(param);
		}
		catch (Exception e)
		{
			String uniques_ = exceptionParameter;
			if ((e.getMessage() != null && e.getMessage().contains("IXCI_ITEM_MAST"))
					|| (e.getCause().getMessage() != null && e.getCause().getMessage().contains("IXCI_ITEM_MAST")))
			{
				uniques_ = exceptionParameter;
			}
			else if ((e.getMessage() != null && e.getMessage().contains("IXCU_ITEM_MAST"))
					|| (e.getCause().getMessage() != null && e.getCause().getMessage().contains("IXCU_ITEM_MAST")))
			{
				uniques_ = StringUtils.isNullString(uniques) ? exceptionParameter : uniques;
			}
			if (uniques_ != null)
			{
				throw new DynaDataExceptionAll("ID_DS_UNIQUE_VIOLATE.uniques=" + uniques_, null, DataExceptionEnum.DS_UNIQUE_VIOLATE, exceptionParameter, uniques_);
			}
			throw new DynaDataExceptionAll("SQLException updateValueOfMaster() Id =" + exceptionParameter, e, DataExceptionEnum.DS_UPDATE_DATA_EXCEPTION, exceptionParameter);
		}
	}

	/**
	 * 计算对象上的属性值
	 *
	 * @param classGuid
	 * @param masterGuid
	 * @throws SQLException
	 */
	public void calculateInstanceLatesttestVal(String classGuid, String masterGuid) throws ServiceRequestException
	{
		// 计算最新版本标记
		this.calculateLatestRevision(classGuid, masterGuid);
		// 计算下个版本发布时间
		this.calculateNextRLSTime(classGuid, masterGuid);
	}

	@SuppressWarnings("unchecked")
	protected void calculateLatestRevision(String classGuid, String masterGuid) throws ServiceRequestException
	{
		try
		{
			Map<String, Object> param = new HashMap<>();
			param.put("TABLENAME", DataServer.getDSCommonService().getTableName(classGuid));
			param.put("MASTERGUID", masterGuid);
			List<FoundationObject> dataList = this.foundationObjectMapper.selectAllRevisionShort(param);
			if (!SetUtils.isNullList(dataList))
			{
				int maxRevSequence = this.getMaxRevSequence(dataList);
				int maxRlsRevSequence = this.getMaxRLSRevSequence(dataList);
				int maxWipRevSequence = this.getMaxWIPRevSequence(dataList);

				param.put("MAXREVSEQ", maxRevSequence);
				param.put("MAXRLSREVSEQ", maxRlsRevSequence);
				param.put("MAXWIPREVSEQ", maxWipRevSequence);
				param.put("UPDATEREVSEQS", maxRevSequence + "," + maxRlsRevSequence + "," + maxWipRevSequence);
				this.foundationObjectMapper.updateLatestRev(param);
			}
		}
		catch (Exception e)
		{
			throw new DynaDataExceptionAll("SQLException uhasBOM() masterGuid =" + masterGuid, e, DataExceptionEnum.SDS_UPDATE, masterGuid);
		}
	}

	protected void calculateNextRLSTime(String classGuid, String masterGuid) throws ServiceRequestException
	{
		try
		{
			Map<String, Object> param = new HashMap<>();
			param.put("TABLENAME", DataServer.getDSCommonService().getTableName(classGuid));
			param.put("MASTERGUID", masterGuid);
			this.foundationObjectMapper.updateNextRlsTime(param);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException("", null, e);
		}
	}

	private int getMaxRevSequence(List<FoundationObject> dataList)
	{
		int sequence = -1;
		for (FoundationObject foundationObject : dataList)
		{
			if (((BigDecimal) foundationObject.get("REVISIONIDSEQUENCE$")).intValue() > sequence)
			{
				sequence = ((BigDecimal) foundationObject.get("REVISIONIDSEQUENCE$")).intValue();
			}
		}
		return sequence;
	}

	private int getMaxRLSRevSequence(List<FoundationObject> dataList)
	{
		int sequence = -1;
		for (FoundationObject foundationObject : dataList)
		{
			if (foundationObject.getStatus() == SystemStatusEnum.RELEASE && ((BigDecimal) foundationObject.get("REVISIONIDSEQUENCE$")).intValue() > sequence)
			{
				sequence = ((BigDecimal) foundationObject.get("REVISIONIDSEQUENCE$")).intValue();
			}
		}
		return sequence;
	}

	private int getMaxWIPRevSequence(List<FoundationObject> dataList)
	{
		int sequence = -1;
		for (FoundationObject foundationObject : dataList)
		{
			if (foundationObject.getStatus() == SystemStatusEnum.WIP && ((BigDecimal) foundationObject.get("REVISIONIDSEQUENCE$")).intValue() > sequence)
			{
				sequence = ((BigDecimal) foundationObject.get("REVISIONIDSEQUENCE$")).intValue();
			}
		}
		return sequence;
	}
}
