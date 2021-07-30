package dyna.data.service.common;

import dyna.common.SearchCondition;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.model.cls.ClassObject;
import dyna.common.dto.Mail;
import dyna.common.dto.Queue;
import dyna.common.dto.Search;
import dyna.common.dto.Session;
import dyna.common.dto.aas.Group;
import dyna.common.dto.aas.Role;
import dyna.common.dto.aas.User;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.code.CodeObjectInfo;
import dyna.common.dto.model.ui.UIField;
import dyna.common.dtomapper.FoundationObjectMapper;
import dyna.common.dtomapper.MailMapper;
import dyna.common.dtomapper.QueueMapper;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.sqlbuilder.plmdynamic.select.DynamicSelectParamData;
import dyna.common.systemenum.*;
import dyna.common.util.EnvUtils;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.common.exception.DynaDataExceptionAll;
import dyna.data.common.exception.DynaDataExceptionSQL;
import dyna.common.bean.xml.UpperKeyMap;
import dyna.data.context.DataServerContext;
import dyna.data.service.DSAbstractServiceStub;
import dyna.data.service.sdm.FieldValueEqualsFilter;
import dyna.data.service.sdm.SystemDataService;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

public class DSCommonStub extends DSAbstractServiceStub<DSCommonServiceImpl>
{
	@Autowired
	private MailMapper              mailMapper;
	@Autowired
	private QueueMapper             queueMapper;
	@Autowired
	private FoundationObjectMapper  foundationObjectMapper;

	protected DSCommonStub(DataServerContext context, DSCommonServiceImpl service)
	{
		super(context, service);
	}

	/**
	 * 若查询的字段,仅有系统字段时,直接返回0表
	 *
	 * @param classGuidOrName
	 * @return
	 */
	public String getTableName(String classGuidOrName) throws ServiceRequestException
	{
		return this.getTableName(classGuidOrName, SystemClassFieldEnum.CLASSGUID.getName());
	}

	protected String getTableName(String classGuidOrName, String fieldName) throws ServiceRequestException
	{
		String realBaseTableName = this.getRealBaseTableName(classGuidOrName);
		if (SystemClassFieldEnum.GUID.getName().equalsIgnoreCase(fieldName))
		{
			return realBaseTableName + "_0";
		}

		String tableIndex = this.getTableIndex(classGuidOrName, fieldName);
		if (StringUtils.isNullString(tableIndex))
		{
			throw new DynaDataExceptionAll("getTableName error, filed is not exist in database.class=" + classGuidOrName + ", fieldName is " + fieldName, null,
					DataExceptionEnum.DS_MODEL_TABLENAME_IS_NULL);
		}

		return realBaseTableName + "_" + tableIndex;
	}

	protected String getTableIndex(String classGuidOrName, String fieldName) throws ServiceRequestException
	{
		ClassField field;
		if (StringUtils.isGuid(classGuidOrName))
		{
			ClassObject classInfo = DataServer.getClassModelService().getClassObjectByGuid(classGuidOrName);
			field = classInfo.getField(fieldName);
		}
		else
		{
			ClassObject classInfo = DataServer.getClassModelService().getClassObject(classGuidOrName);
			field = classInfo.getField(fieldName);
		}

		String tableIndex = field.getTableIndex();
		if (StringUtils.isNullString(tableIndex))
		{
			throw new DynaDataExceptionAll("getTableName error, filed is not exist in database.class=" + classGuidOrName + ", fieldName is " + fieldName, null,
					DataExceptionEnum.DS_MODEL_TABLENAME_IS_NULL);
		}
		return tableIndex;
	}

	protected String getRealBaseTableName(String classGuidOrName) throws ServiceRequestException
	{
		ClassInfo classInfo;
		if (StringUtils.isGuid(classGuidOrName))
		{
			classInfo = DataServer.getClassModelService().getClassInfo(classGuidOrName);
		}
		else
		{
			classInfo = DataServer.getClassModelService().getClassInfoByName(classGuidOrName);
		}
		String realBaseTableName = classInfo.getRealBaseTableName();
		if (StringUtils.isNullString(realBaseTableName))
		{
			String className = classInfo.getName();
			throw new DynaDataExceptionAll("query error, database. classname is " + className, null, DataExceptionEnum.DS_TABLENAME_NULL_EXCEPTION, classGuidOrName);
		}
		return classInfo.getRealBaseTableName();
	}

	protected String getMainTableAlias(DynamicSelectParamData paramData)
	{
		String maAlias = paramData.getTableName();
		maAlias = maAlias.trim();
		int indexOf = maAlias.lastIndexOf(" ");
		if (indexOf > 0)
		{
			return maAlias.substring(indexOf + 1);
		}
		return null;
	}

	protected int getRecordCount(DynamicSelectParamData paramData)
	{
		try
		{
			String alias = this.getMainTableAlias(paramData);
			if (alias == null)
			{
				alias = "";
			}
			else
			{
				alias = alias + ".";
			}
			DynamicSelectParamData paramData_ = new DynamicSelectParamData();
			paramData_.setWhereParamList(paramData.getWhereParamList());
			paramData_.setJoinTableList(paramData.getJoinTableList());
			paramData_.setJoinTableMap(paramData.getJoinTableMap());
			paramData_.setTableName(paramData.getTableName());
			paramData_.setWhereSql(paramData.getWhereSql());
			paramData_.setFieldSql("COUNT(" + alias + "GUID) ROWCOUNT$");
			FoundationObject object = null;
			try
			{
				object = (FoundationObject) foundationObjectMapper.selectDynamic(paramData_);

			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			if (object != null)
			{
				return object.getRowCount();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new DynaDataExceptionAll("query error.", e, DataExceptionEnum.DS_QUERY_DATA_EXCEPTION);
		}

		return 0;
	}

	protected String getDataServerConfRootPath()
	{
		return EnvUtils.getConfRootPath();
	}

	@SuppressWarnings("unchecked")
	public List<FoundationObject> executeQuery(DynamicSelectParamData paramData)
	{
		try
		{
			return (List<FoundationObject>) foundationObjectMapper.selectDynamic(paramData);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new DynaDataExceptionAll("query error.", e, DataExceptionEnum.DS_QUERY_DATA_EXCEPTION);
		}
	}

	@SuppressWarnings("unchecked")
	protected List<FoundationObject> executePageQuery(DynamicSelectParamData paramData)
	{
		try
		{
			//TODO lizw
//			return this.sqlSessionFactory.queryForList(FoundationObject.class.getName() + ".selectDynamic", paramData, paramData.getCurrentPage(), paramData.getRowsPerPage());
			return (List<FoundationObject>) foundationObjectMapper.selectDynamic(paramData);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new DynaDataExceptionAll("query error.", e, DataExceptionEnum.DS_QUERY_DATA_EXCEPTION);
		}

	}

	public Session getSession(String guid) throws ServiceRequestException
	{
		return DataServer.getSystemDataService().get(Session.class, guid);
	}

	protected Session getSystemInternal() throws ServiceRequestException
	{
		User systemUser = DataServer.getSystemDataService().findInCache(User.class, new FieldValueEqualsFilter<>("USERID", "SYSTEM.INTERNAL"));
		Role systemRole = DataServer.getSystemDataService().findInCache(Role.class, new FieldValueEqualsFilter<>("ROLEID", "ADMINISTRATOR"));
		Group systemGroup = DataServer.getSystemDataService().findInCache(Group.class, new FieldValueEqualsFilter<>("GROUPID", "ADMINISTRATOR"));
		Session session = DataServer.getSystemDataService().findInCache(Session.class, new FieldValueEqualsFilter<>("USERGUID", systemUser.getGuid()));
		if (session == null)
		{
			session = new Session();
			session.setUserGuid(systemUser.getGuid());
			session.setLoginGroupGuid(systemGroup.getGuid());
			session.setLoginRoleGuid(systemRole.getGuid());
			session.setBizModelGuid(systemGroup.getBizModelGuid());
			String guid = DataServer.getSystemDataService().save(session);
			session = this.getSession(guid);
		}
		session.setBizModelGuid(systemGroup.getBizModelGuid());
		session.put("BMNAME", systemGroup.getBizModelName());
		return session;
	}

	protected String updateSession(String guid) throws ServiceRequestException
	{
		Session session = DataServer.getSystemDataService().get(Session.class, guid);
		session.setUpdateTime(new Date());
		session.setLastAccesseTime(new Date());
		DataServer.getSystemDataService().save(session);
		return guid;
	}

	protected void updateSessionActiveTime(String guid) throws ServiceRequestException
	{
		this.updateSession(guid);
	}

	public String getFieldColumn(String mainTableAlias, String fieldName) throws ServiceRequestException
	{
		String[] s = fieldName.split("\\.");
		if (s.length == 2)
		{
			String className = s[0];
			String fieldName_ = s[1];
			ClassObject classObject = DataServer.getClassModelService().getClassObject(className);
			if (classObject == null)
			{
				throw new DynaDataExceptionAll(fieldName, null, DataExceptionEnum.DS_NO_FIELD);
			}
			ClassField classField = classObject.getField(fieldName_);
			if (classField == null)
			{
				throw new DynaDataExceptionAll(fieldName, null, DataExceptionEnum.DS_NO_FIELD);
			}

			String column = classField.getColumnName();
			if (column == null)
			{
				throw new DynaDataExceptionAll(fieldName, null, DataExceptionEnum.DS_NO_FIELD);
			}
			else
			{
				String tableName = this.stubService.getTableName(className, fieldName_);
				if (StringUtils.isNullString(tableName))
				{
					return null;
				}
				String tableIndex = tableName.substring(tableName.length() - 1);
				if ("0".equals(tableIndex))
				{
					column = mainTableAlias + "." + column;
				}
				else
				{
					column = mainTableAlias + tableIndex + "." + column;
				}
			}
			return column;
		}
		else
		{
			throw new DynaDataExceptionAll(fieldName, null, DataExceptionEnum.DS_NO_FIELD);
		}
	}

	public ObjectFieldTypeEnum getObjectFieldTypeOfField(ClassField field, String bizModelName) throws ServiceRequestException
	{
		if (field.getType() == FieldTypeEnum.OBJECT && !StringUtils.isNullString(field.getTypeValue()))
		{
			ClassObject classObj = DataServer.getClassModelService().getClassObject(field.getTypeValue());
			if (classObj == null)
			{
				throw new DynaDataExceptionAll("query error ,the object type filed has not type value. fieldName is " + field.getName(), null,
						DataExceptionEnum.DS_MODEL_OBJECT_TYPEVALUE_ERROR, field.getName());
			}
			if (classObj.hasInterface(ModelInterfaceEnum.IUser))
			{
				return ObjectFieldTypeEnum.USER;
			}
			else if (classObj.hasInterface(ModelInterfaceEnum.IGroup))
			{
				return ObjectFieldTypeEnum.GROUP;
			}
			else if (classObj.hasInterface(ModelInterfaceEnum.IPMRole))
			{
				return ObjectFieldTypeEnum.PMROLE;
			}
			else if (classObj.hasInterface(ModelInterfaceEnum.IPMCalendar))
			{
				return ObjectFieldTypeEnum.PMCALENDAR;
			}
			return ObjectFieldTypeEnum.OBJECT;
		}
		return null;
	}

	public ObjectFieldTypeEnum getObjectFieldTypeOfField(String className, String fieldName, String bizModelName) throws ServiceRequestException
	{
		ClassObject classObject = DataServer.getClassModelService().getClassObject(className);
		if (classObject == null)
		{
			throw new DynaDataExceptionAll("query error ,the object type filed has not type value. the className is " + className, null, DataExceptionEnum.SDS_GET_MA_CLASS,
					className);
		}

		ClassField field = classObject.getField(fieldName);
		if (field == null)
		{
			throw new DynaDataExceptionAll("query error ,the object type filed has not type value. the className is " + className + " ;fieldName is " + fieldName, null,
					DataExceptionEnum.DS_NO_FIELD, fieldName);
		}

		return this.getObjectFieldTypeOfField(field, bizModelName);
	}

	/**
	 * 取得接口和UI上的所有字段
	 *
	 * @param searchCondition
	 *            为空,则只取系统字段
	 * @return
	 */
	public List<String> getAllFieldsFromSC(SearchCondition searchCondition) throws ServiceRequestException
	{
		if (searchCondition == null)
		{
			return null;
		}

		List<String> fieldNameList = new ArrayList<>();
		if (!SetUtils.isNullList(searchCondition.getResultFieldList()))
		{
			for (String fieldName : searchCondition.getResultFieldList())
			{
				if (!fieldNameList.contains(fieldName.toUpperCase()))
				{
					fieldNameList.add(fieldName.toUpperCase());
				}
			}
		}

		ClassObject classObject = DataServer.getClassModelService().getClassObject(searchCondition.getObjectGuid().getClassName());
		if (classObject == null)
		{
			return new ArrayList<>();
		}
		List<ModelInterfaceEnum> interfaceList = classObject.getInterfaceList();
		if (interfaceList != null)
		{
			for (ModelInterfaceEnum modelInterfaceEnum : interfaceList)
			{
				List<ClassField> listClassFieldInInterface = DataServer.getInterfaceModelService().listClassFieldOfInterface(modelInterfaceEnum);
				if (listClassFieldInInterface != null)
				{
					for (ClassField classField : listClassFieldInInterface)
					{
						if (!fieldNameList.contains(classField.getName().toUpperCase()))
						{
							fieldNameList.add(classField.getName().toUpperCase());
						}
					}
				}
			}
		}
		List<String> resultUINameList = searchCondition.listResultUINameList();
		if (resultUINameList != null && resultUINameList.size() > 0)
		{
			List<UIField> fieldList = new ArrayList<>();
			for (String resultUiName : resultUINameList)
			{
				if (classObject.getUIObject(resultUiName) != null)
				{
					if (classObject.getUIObject(resultUiName).getFieldList() != null)
					{
						fieldList.addAll(classObject.getUIObject(resultUiName).getFieldList());
					}
				}
			}
			for (UIField field : fieldList)
			{
				// 非系统字段加入查询结果列表
				ClassField classField = classObject.getField(field.getName());
				if (!field.getName().startsWith("SEPARATOR$") && !classField.isSystem())
				{
					if (!fieldNameList.contains(field.getName().toUpperCase()))
					{
						fieldNameList.add(field.getName().toUpperCase());
					}
				}
			}
		}
		return fieldNameList;
	}

	/**
	 * 取得master表名
	 *
	 * @param classGuidOrName
	 * @return
	 */
	protected String getMasterTableName(String classGuidOrName) throws ServiceRequestException
	{
		ClassObject classObject = null;
		if (StringUtils.isGuid(classGuidOrName))
		{
			classObject = DataServer.getClassModelService().getClassObjectByGuid(classGuidOrName);
		}
		else
		{
			classObject = DataServer.getClassModelService().getClassObject(classGuidOrName);
		}
		if (classObject.hasInterface(ModelInterfaceEnum.IBOMStructure) || classObject.hasInterface(ModelInterfaceEnum.IStructureObject))
		{
			return null;
		}

		String realBaseTableName = this.getRealBaseTableName(classGuidOrName);
		return realBaseTableName + "_MAST";
	}

	protected boolean isSearchNameUnique(String name, boolean isPublic, PMSearchTypeEnum pmTypeEnum, String userGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		Map<String, Object> searchConditionMap = new HashMap<>();

		// 项目管理角色，1：管理者，2：参与者，3：追踪者
		if (pmTypeEnum != null)
		{
			searchConditionMap.put("NAME", name);
			searchConditionMap.put("PMTYPE", pmTypeEnum.getValue());
			Search search = sds.queryObject(Search.class, searchConditionMap, "checkNameUniqueForPM");
			BigDecimal countBigDecimal = (BigDecimal) search.get("CN");
			BigDecimal zeroBigDecimal = new BigDecimal(0);
			return zeroBigDecimal.compareTo(countBigDecimal) < 0;
		}
		else if (isPublic)
		{
			searchConditionMap.put("NAME", name);
			Search search = sds.queryObject(Search.class, searchConditionMap, "checkNameUniqueForPublic");
			BigDecimal countBigDecimal = (BigDecimal) search.get("CN");
			BigDecimal zeroBigDecimal = new BigDecimal(0);
			return zeroBigDecimal.compareTo(countBigDecimal) < 0;
		}
		else
		{
			searchConditionMap.put("NAME", name);
			searchConditionMap.put("USERGUID", userGuid);
			Search search = sds.queryObject(Search.class, searchConditionMap, "checkNameUniqueForSystem");
			BigDecimal countBigDecimal = (BigDecimal) search.get("CN");
			BigDecimal zeroBigDecimal = new BigDecimal(0);
			return zeroBigDecimal.compareTo(countBigDecimal) < 0;
		}
	}

	protected void deleteMail(String userGuid, int messageDay, boolean isWorkflow) throws ServiceRequestException
	{
		Date effTime = new Date(System.currentTimeMillis() - messageDay * 24000 * 3600);
		Map<String, Object> map = new HashMap<>();
		map.put("USERGUID", userGuid);
		map.put("MESSAGEDAY", effTime);
		try
		{
			if (isWorkflow)
			{
				this.mailMapper.deleteWorkflowOutOfDate(map);
				this.mailMapper.deleteWorkflowReceiverOutOfDate(map);
			}
			else
			{
				this.mailMapper.deleteMailOutOfDate(map);
				this.mailMapper.deleteMailReceiverOutOfDate(map);
			}
		}
		catch (Exception e)
		{
			//TODO
//			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof DynaDataExceptionSQL)
			{
				throw (DynaDataExceptionSQL) e;
			}
			else if (e instanceof DynaDataExceptionAll)
			{
				throw (DynaDataExceptionAll) e;
			}
			throw new DynaDataExceptionAll("deleteMail()", e, DataExceptionEnum.SDS_DELETE_SQLSTATEMENTID);
		}

	}

	/**
	 * 当类中的字段类型指定为code时，包含了组码和分类两种，这里，要根据code名字，区分是组码还是分类。
	 *
	 * @param fieldName
	 *            code名字
	 * @return
	 */
	protected FieldTypeEnum getFieldTypeByName(String codeName) throws ServiceRequestException
	{
		UpperKeyMap parameterMap = new UpperKeyMap();
		parameterMap.put("GROUPNAME", codeName);
		CodeObjectInfo codeMaster = DataServer.getSystemDataService().findInCache(CodeObjectInfo.class, new FieldValueEqualsFilter<>(parameterMap));
		if (codeMaster == null)
		{
			return null;
		}

		if (codeMaster.isClassification())
		{
			return FieldTypeEnum.CLASSIFICATION;
		}
		else
		{
			return FieldTypeEnum.CODE;
		}
	}

	@SuppressWarnings("deprecation")
	protected String saveQueue(Queue queue) throws DynaDataException
	{
		// 生成编号
		// 规则：根据数据库系统时间（精确到毫秒）生成ID，例如：20120425174359123
		// 如果重复重新获取时间
		try
		{
			Date time = new Date();
			int year = time.getYear() + 1900;
			int month = time.getMonth() + 1;
			String months = "" + month;
			if (String.valueOf(month).length() == 1)
			{
				months = "0" + month;
			}
			int day = time.getDate();
			String days = "" + day;
			if (String.valueOf(day).length() == 1)
			{
				days = "0" + day;
			}
			int hour = time.getHours();
			String hours = "" + hour;
			if (String.valueOf(hour).length() == 1)
			{
				hours = "0" + hour;
			}
			int minute = time.getMinutes();
			String minutes = "" + minute;
			if (String.valueOf(minute).length() == 1)
			{
				minutes = "0" + minute;
			}
			int second = time.getSeconds();
			String seconds = "" + second;
			if (String.valueOf(second).length() == 1)
			{
				seconds = "0" + second;
			}
			long nano = System.nanoTime();
			// 去掉后面6个0
			String nanos = String.valueOf(nano);
			if (nanos.length() <= 6)
			{
				nanos = "000";
			}
			else
			{
				nanos = nanos.substring(0, nanos.length() - 6);
			}
			String id = "" + year + months + days + hours + minutes + seconds + nanos;
			queue.setId(id);
			String sqlStatementId = queue.getClass().getName() + ".insert";
			queue.put("CURRENTTIME", new Date());
			this.queueMapper.insert(queue);
			return null;
		}
		//TODO
//		catch (Exception e)
//		{
//			// 如果报唯一约束异常需要重新执行此方法
//			if (e.getErrorCode() == 1)
//			{
//				return this.saveQueue(queue);
//			}
//			else
//			{
//				throw new DynaDataExceptionSQL("saveQueue ", e, DataExceptionEnum.SDS_INSERT);
//			}
//		}
		catch (Exception e)
		{
			throw new DynaDataExceptionAll("saveQueue  ", e, DataExceptionEnum.SDS_INSERT);
		}
	}

	/**
	 * 取得指定类的所有子类
	 *
	 * @param className
	 * @param classNameList
	 */
	protected void getAllSubClass(String className, List<String> classNameList) throws ServiceRequestException
	{
		if (!classNameList.contains(className))
		{
			classNameList.add(className);
		}

		ClassObject classObject = DataServer.getClassModelService().getClassObject(className);
		if (classObject != null)
		{
			List<ClassObject> childList = classObject.getChildList();
			if (!SetUtils.isNullList(childList))
			{
				for (ClassObject child : childList)
				{
					this.getAllSubClass(child.getName(), classNameList);
				}
			}
		}
	}

	protected void decorateClass(SearchCondition searchCondition) throws ServiceRequestException
	{
		ObjectGuid objectGuid = searchCondition.getObjectGuid();
		if (StringUtils.isGuid(objectGuid.getGuid()) && StringUtils.isNullString(objectGuid.getClassName()))
		{
			ClassObject classObject = DataServer.getClassModelService().getClassObjectByGuid(objectGuid.getGuid());
			if (classObject != null)
			{
				searchCondition.getObjectGuid().setClassName(classObject.getName());
			}
		}
		else if (!StringUtils.isGuid(objectGuid.getGuid()) && !StringUtils.isNullString(objectGuid.getClassName()))
		{
			ClassObject classObject = DataServer.getClassModelService().getClassObject(objectGuid.getClassName());
			if (classObject != null)
			{
				searchCondition.getObjectGuid().setClassGuid(classObject.getGuid());
			}
		}
	}
}
