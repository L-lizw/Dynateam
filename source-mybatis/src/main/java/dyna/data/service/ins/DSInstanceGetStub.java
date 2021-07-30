package dyna.data.service.ins;

import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.FoundationObjectImpl;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.model.cls.ClassObject;
import dyna.common.bean.model.code.CodeItem;
import dyna.common.bean.model.code.CodeObject;
import dyna.common.dto.DataRule;
import dyna.common.dto.Folder;
import dyna.common.dto.Session;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.dto.model.code.CodeObjectInfo;
import dyna.common.dtomapper.FoundationObjectMapper;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.sqlbuilder.plmdynamic.select.DynamicSelectParamData;
import dyna.common.systemenum.*;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.common.exception.DynaDataExceptionAll;
import dyna.data.common.exception.DynaDataExceptionSQL;
import dyna.data.common.sqlbuilder.ClassInstanceGetSqlBuilder;
import dyna.data.context.DataServerContext;
import dyna.data.service.DSAbstractServiceStub;
import dyna.data.service.acl.AclService;
import dyna.data.service.sdm.SystemDataService;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DSInstanceGetStub extends DSAbstractServiceStub<InstanceServiceImpl>
{
	private static final String CLASSIFICATION_TABLE_AS = "CF$";

	@Autowired
	private FoundationObjectMapper          foundationObjectMapper;

	protected DSInstanceGetStub(DataServerContext context, InstanceServiceImpl service)
	{
		super(context, service);
	}

	/**
	 * 根据guid查找实例
	 * 
	 * @param objectGuid
	 * @param isCheckAcl
	 * @param sessionId
	 * @return
	 * @throws DynaDataException
	 */
	public FoundationObject get(ObjectGuid objectGuid, boolean isCheckAcl, String sessionId) throws ServiceRequestException
	{
		if (!StringUtils.isGuid(objectGuid.getGuid()))
		{
			return null;
		}

		String className = objectGuid.getClassGuid();
		if (StringUtils.isGuid(className))
		{
			className = DataServer.getClassModelService().getClassObjectByGuid(className).getName();
		}

		ClassObject classObject = DataServer.getClassModelService().getClassObject(className);
		if (classObject == null || classObject.hasInterface(ModelInterfaceEnum.IUser) || classObject.hasInterface(ModelInterfaceEnum.IGroup)
				|| classObject.hasInterface(ModelInterfaceEnum.IPMRole) || classObject.hasInterface(ModelInterfaceEnum.IPMCalendar))
		{
			return null;
		}
		DynamicSelectParamData paramData = ClassInstanceGetSqlBuilder.buildInstanceSearchParamData(objectGuid, sessionId);

		List<FoundationObject> list = DataServer.getDSCommonService().executeQuery(paramData);
		if (SetUtils.isNullList(list))
		{
			throw new DynaDataExceptionAll("NO DATA.", null, DataExceptionEnum.DS_SEARCHCONDITION_NO_RESULT);
		}

		AclService aclService = DataServer.getAclService();
		FoundationObject foundationObject = list.get(0);
		if (ISCHECKACL && isCheckAcl)
		{
			Session session = DataServer.getDSCommonService().getSession(sessionId);
			Folder libFolder = DataServer.getFolderService().getFolder(foundationObject.getLocationlib(), session.getUserGuid(), session.getLoginGroupGuid(),
					session.getLoginRoleGuid(), false);
			if (!libFolder.isValid())
			{
				throw new DynaDataExceptionAll("the lib of current data is not valid .", null, DataExceptionEnum.DS_ISNOTVALID);
			}

			// 判断权限
			if (!aclService.hasAuthority(foundationObject.getObjectGuid(), AuthorityEnum.READ, sessionId))
			{
				throw new DynaDataExceptionAll("NO READ AUTH.", null, DataExceptionEnum.DS_NO_READ_AUTH, objectGuid.getGuid());
			}
		}
		return foundationObject;

	}

	/**
	 * 根据guid查找实例
	 * 
	 * @param guid
	 * @param isCheckAcl
	 * @param sessionId
	 * @return
	 * @throws DynaDataException
	 */
	public FoundationObject getSystemFieldInfo(String guid, String classGuidOrClassName, boolean isCheckAcl, String sessionId) throws ServiceRequestException
	{
		if (!StringUtils.isGuid(guid))
		{
			return null;
		}

		String className = classGuidOrClassName;
		if (StringUtils.isGuid(classGuidOrClassName))
		{
			className = DataServer.getClassModelService().getClassObjectByGuid(classGuidOrClassName).getName();
		}

		ClassObject classObject = DataServer.getClassModelService().getClassObject(className);
		if (classObject == null || classObject.hasInterface(ModelInterfaceEnum.IUser) || classObject.hasInterface(ModelInterfaceEnum.IGroup)
				|| classObject.hasInterface(ModelInterfaceEnum.IPMRole) || classObject.hasInterface(ModelInterfaceEnum.IPMCalendar))
		{
			return null;
		}

		FoundationObject foundationObject;
		// 是否可以查看检出后的数据
		Map<String, Object> paraMap = new HashMap<>();
		paraMap.put("GUID", guid);
		paraMap.put("tablename", DataServer.getDSCommonService().getTableName(classGuidOrClassName));
		try
		{
			foundationObject = (FoundationObjectImpl) this.foundationObjectMapper.getFoundationByGuid(paraMap);
			if (foundationObject == null)
			{
				throw new DynaDataExceptionAll("NO DATA.", null, DataExceptionEnum.DS_SEARCHCONDITION_NO_RESULT);
			}

			// 变更864 第2条 库被卸载后不能查看数据 --除了系统管理员
			if (ISCHECKACL && isCheckAcl)
			{
				Session session = DataServer.getDSCommonService().getSession(sessionId);
				Folder libFolder = DataServer.getFolderService().getFolder(foundationObject.getLocationlib(), session.getUserGuid(), session.getLoginGroupGuid(),
						session.getLoginRoleGuid(), false);
				if (!libFolder.isValid())
				{
					throw new DynaDataExceptionAll("the lib of current data is not valid .", null, DataExceptionEnum.DS_ISNOTVALID);
				}

				// 判断权限
				if (!DataServer.getAclService().hasAuthority(foundationObject.getObjectGuid(), AuthorityEnum.READ, sessionId))
				{
					throw new DynaDataExceptionAll("NO READ AUTH.", null, DataExceptionEnum.DS_NO_READ_AUTH, guid);
				}
			}
		}
		catch (Exception e)
		{
			throw new DynaDataExceptionAll("query FoundationObject error.", null, DataExceptionEnum.SDS_SELECT, guid);
		}

		return foundationObject;
	}

	/**
	 * 查询指定实例的所有分类数据
	 * 
	 * @param foundationObjectGuid
	 * @param iterationId
	 * @param isCheckAcl
	 * @return
	 * @throws SQLException
	 */
	public FoundationObject getClassification(String foundationObjectGuid, String iterationId, String itemGuid, String sessionId) throws ServiceRequestException
	{
		try
		{
			Session session = DataServer.getDSCommonService().getSession(sessionId);
			StringBuilder selectColumnSb = new StringBuilder(",");

			CodeItem codeItem = DataServer.getCodeModelService().getCodeItemByGuid(itemGuid);
			if (codeItem == null)
			{
				return null;
			}

			CodeObject codeObject = DataServer.getCodeModelService().getCodeObjectByGuid(codeItem.getCodeGuid());
			List<ClassField> classFields = codeItem.getFieldList();
			if (StringUtils.isNullString(codeObject.getBaseTableName()))
			{
				return null;
			}
			if (SetUtils.isNullList(classFields))
			{
				return null;
			}
			String tableName;
			if (iterationId != null)
			{
				tableName = codeObject.getIterationTableName();
			}
			else
			{
				tableName = codeObject.getRevisionTableName();
			}

			for (ClassField classificationField : classFields)
			{
				if (StringUtils.isNullString(classificationField.getName()) || classificationField.getName().startsWith("SEPARATOR$"))
				{
					continue;
				}

				String columnName = CLASSIFICATION_TABLE_AS + "." + classificationField.getColumnName();
				if (classificationField.getType().equals(FieldTypeEnum.OBJECT))
				{
					ClassField classField = new ClassField();
					classField.putAll(classificationField);
					ObjectFieldTypeEnum fieldType = DataServer.getDSCommonService().getObjectFieldTypeOfField(classField, session.getBizModelName());
					if (fieldType != null)
					{
						String tmpFieldName = classificationField.getName();
						if (fieldType == ObjectFieldTypeEnum.OBJECT)
						{
							selectColumnSb.append(columnName).append(" as ").append(classificationField.getName());
							selectColumnSb.append(",");
							selectColumnSb.append(columnName).append("$class as ").append(tmpFieldName).append("$class");
							selectColumnSb.append(",");
							selectColumnSb.append(columnName).append("$master as ").append(tmpFieldName).append("$master,");
						}
						else
						{
							selectColumnSb.append(columnName).append(" as ").append(classificationField.getName()).append(",");
						}
					}
				}
				else
				{
					selectColumnSb.append(CLASSIFICATION_TABLE_AS + ".").append(classificationField.getColumnName()).append(" AS ").append(classificationField.getName())
							.append(",");
				}
			}

			StringBuilder whereSql = new StringBuilder();
			if (!StringUtils.isNullString(iterationId))
			{
				whereSql.append("and cf$.iterationid = '").append(iterationId).append("'");
			}

			Map<String, Object> param = new HashMap<>();
			param.put("FOUNDATIONFK", foundationObjectGuid);
			param.put("CFITEMFK", codeItem.getGuid());
			param.put("REVISIONCOLUMNS", selectColumnSb.substring(0, selectColumnSb.length() - 1));
			param.put("CFTABLENAME", tableName);
			param.put("WHERESQL", whereSql.toString());
			FoundationObject object = (FoundationObject) this.classificationModelMapper.selectItemData(param);
			if (object != null)
			{
				decorateClassification(object);
				return object;
			}

			return null;
		}
		catch (Exception e)
		{
			throw new DynaDataExceptionSQL("queryForClassification error. ", e, DataExceptionEnum.DS_LIST_CLASSIFICATION);
		}
	}

	public static void decorateClassification(FoundationObject foundationObject)
	{
		String classificationItemGuid = foundationObject.getClassificationGuid();
		if (StringUtils.isGuid(classificationItemGuid))
		{
			CodeItemInfo codeItemInfo = DataServer.getSystemDataService().get(CodeItemInfo.class, classificationItemGuid);
			if (codeItemInfo != null)
			{
				CodeObjectInfo codeObjectInfo = DataServer.getSystemDataService().get(CodeObjectInfo.class, codeItemInfo.getMasterGuid());
				foundationObject.put("CLASSIFICATIONGROUP$", codeItemInfo.getMasterGuid());
				foundationObject.put("CLASSIFICATIONGROUP$NAME", codeObjectInfo.getName());
				foundationObject.put("CLASSIFICATIONGROUP$TITLE", codeObjectInfo.getTitle());
				foundationObject.put("CLASSIFICATION$NAME", codeItemInfo.getName());
				foundationObject.put("CLASSIFICATION$TITLE", codeItemInfo.getTitle());
			}
		}
	}

	/**
	 * 根据objectGuid获取master，包含id、name、alterid信息
	 *
	 * @param objectGuid
	 * @return
	 * @throws DynaDataException
	 */
	public FoundationObject getMaster(ObjectGuid objectGuid) throws ServiceRequestException
	{
		ObjectGuid objectGuidNew = new ObjectGuid(objectGuid);
		FoundationObject masterFoundationObject;
		String masterGuid = objectGuidNew.getMasterGuid();
		Map<String, Object> selectMap = new HashMap<>();
		selectMap.put("MASTERGUID", masterGuid);
		selectMap.put("TABLENAME", DataServer.getDSCommonService().getMasterTableName(objectGuid.getClassGuid()));
		try
		{
			masterFoundationObject = (FoundationObject) this.dynaObjectMapper.getMasterByGuid(selectMap);
			objectGuidNew.setIsMaster(true);
			objectGuidNew.setGuid(null);
			masterFoundationObject.setObjectGuid(objectGuidNew);
			return masterFoundationObject;
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
			throw new DynaDataExceptionAll("getMaster other exception masterGuid =" + masterGuid, e, DataExceptionEnum.DS_QUERY_MASTER_ERROR);
		}

	}

	protected boolean hasMutliWorkVersion(ObjectGuid objectGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> filter = new HashMap<>();
		filter.put("MASTERFK", objectGuid.getMasterGuid());
		filter.put("tablename", DataServer.getDSCommonService().getTableName(objectGuid.getClassGuid()));

		List<FoundationObject> listWorkVersion = sds.query(FoundationObject.class, filter, "getWIPFOCount");

		if (!SetUtils.isNullList(listWorkVersion))
		{
			BigDecimal object = (BigDecimal) listWorkVersion.get(0).get("count");
			return object.longValue() > 0;
		}
		else
		{
			return false;
		}
	}

	public FoundationObject getWipSystemFieldInfoByMaster(String masterGuid, String classGuidOrClassName, boolean isCheckAcl, String sessionId) throws ServiceRequestException
	{

		if (!StringUtils.isGuid(masterGuid))
		{
			return null;
		}

		String className = classGuidOrClassName;
		if (StringUtils.isGuid(classGuidOrClassName))
		{
			className = DataServer.getClassModelService().getClassObjectByGuid(classGuidOrClassName).getName();
		}

		ClassObject classObject = DataServer.getClassModelService().getClassObject(className);
		if (classObject == null || classObject.hasInterface(ModelInterfaceEnum.IUser) || classObject.hasInterface(ModelInterfaceEnum.IGroup)
				|| classObject.hasInterface(ModelInterfaceEnum.IPMRole) || classObject.hasInterface(ModelInterfaceEnum.IPMCalendar))
		{
			return null;
		}

		FoundationObject foundationObject;
		// 是否可以查看检出后的数据
		Map<String, Object> paraMap = new HashMap<>();
		paraMap.put("MASTERGUID", masterGuid);
		paraMap.put("tablename", DataServer.getDSCommonService().getTableName(classGuidOrClassName));
		try
		{
			foundationObject = (FoundationObjectImpl) this.foundationObjectMapper.getFoundationByMaster(paraMap);
			if (foundationObject == null)
			{
				throw new DynaDataExceptionAll("NO DATA.", null, DataExceptionEnum.DS_SEARCHCONDITION_NO_RESULT);
			}

			// 变更864 第2条 库被卸载后不能查看数据 --除了系统管理员
			if (ISCHECKACL && isCheckAcl)
			{
				Session session = DataServer.getDSCommonService().getSession(sessionId);
				Folder libFolder = DataServer.getFolderService().getFolder(foundationObject.getLocationlib(), session.getUserGuid(), session.getLoginGroupGuid(),
						session.getLoginRoleGuid(), false);
				if (!libFolder.isValid())
				{
					throw new DynaDataExceptionAll("the lib of current data is not valid .", null, DataExceptionEnum.DS_ISNOTVALID);
				}

				// 判断权限
				if (!DataServer.getAclService().hasAuthority(foundationObject.getObjectGuid(), AuthorityEnum.READ, sessionId))
				{
					throw new DynaDataExceptionAll("NO READ AUTH.", null, DataExceptionEnum.DS_NO_READ_AUTH, masterGuid);
				}
			}
		}
		catch (Exception e)
		{
			throw new DynaDataExceptionAll("query FoundationObject error.", null, DataExceptionEnum.SDS_SELECT, masterGuid);
		}

		return foundationObject;
	}

	public FoundationObject getSystemFieldInfoByMasterContext(String masterGuid, String classGuidOrClassName, DataRule dataRule, boolean isCheckAcl, String sessionId)
			throws ServiceRequestException
	{
		if (!StringUtils.isGuid(masterGuid))
		{
			return null;
		}

		String className = classGuidOrClassName;
		if (StringUtils.isGuid(classGuidOrClassName))
		{
			className = DataServer.getClassModelService().getClassObjectByGuid(classGuidOrClassName).getName();
		}

		ClassObject classObject = DataServer.getClassModelService().getClassObject(className);
		if (classObject == null || classObject.hasInterface(ModelInterfaceEnum.IUser) || classObject.hasInterface(ModelInterfaceEnum.IGroup)
				|| classObject.hasInterface(ModelInterfaceEnum.IPMRole) || classObject.hasInterface(ModelInterfaceEnum.IPMCalendar))
		{
			return null;
		}

		FoundationObject foundationObject;
		Map<String, Object> paraMap = new HashMap<>();
		paraMap.put("MASTERGUID", masterGuid);
		paraMap.put("tablename", DataServer.getDSCommonService().getTableName(className));
		paraMap.put("RULETIME", dataRule.getLocateTime());
		try
		{
			foundationObject = (FoundationObjectImpl) this.foundationObjectMapper.getFoundationByTime(paraMap);
			if (foundationObject == null)
			{
				throw new DynaDataExceptionAll("NO DATA.", null, DataExceptionEnum.DS_SEARCHCONDITION_NO_RESULT);
			}

			// 变更864 第2条 库被卸载后不能查看数据 --除了系统管理员
			if (ISCHECKACL && isCheckAcl)
			{
				Session session = DataServer.getDSCommonService().getSession(sessionId);
				Folder libFolder = DataServer.getFolderService().getFolder(foundationObject.getLocationlib(), session.getUserGuid(), session.getLoginGroupGuid(),
						session.getLoginRoleGuid(), false);
				if (!libFolder.isValid())
				{
					throw new DynaDataExceptionAll("the lib of current data is not valid .", null, DataExceptionEnum.DS_ISNOTVALID);
				}

				// 判断权限
				if (!DataServer.getAclService().hasAuthority(foundationObject.getObjectGuid(), AuthorityEnum.READ, sessionId))
				{
					throw new DynaDataExceptionAll("NO READ AUTH.", null, DataExceptionEnum.DS_NO_READ_AUTH, masterGuid);
				}
			}
		}
		catch (Exception e)
		{
			throw new DynaDataExceptionAll("query FoundationObject error.", null, DataExceptionEnum.SDS_SELECT, masterGuid);
		}

		return foundationObject;
	}

}
