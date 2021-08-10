package dyna.app.service.helper.decorate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dyna.app.service.brs.emm.EMMImpl;
import dyna.common.bean.data.DynaObject;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ppms.PMCalendar;
import dyna.common.bean.data.ppms.ProjectRole;
import dyna.common.dto.Folder;
import dyna.common.dto.aas.Group;
import dyna.common.dto.aas.User;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.exception.DecorateException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.DataExceptionEnum;
import dyna.common.systemenum.FieldTypeEnum;
import dyna.common.systemenum.FolderTypeEnum;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.common.exception.DynaDataExceptionAll;
import dyna.data.service.sdm.SystemDataService;
import dyna.net.service.brs.EMM;

public class ObjectFieldDecorator extends FullNameDecorator implements Decorator
{

	/**
	 * @param fieldNames
	 * @param objectList
	 * @param args
	 *            0:EMM, 1:AAS, 2:SystemDataService, 3:DataService, 4:sessionId, 5:是否是分类字段：true 分类；
	 * @throws DecorateException
	 */
	public <T extends DynaObject> void decorateWithField(Set<String> fieldNames, List<T> objectList, EMM emm, String sessionId, boolean isclassification) throws DecorateException
	{
		if (objectList == null || objectList.isEmpty())
		{
			return;
		}
		if (fieldNames == null)
		{
			fieldNames = new HashSet<>();
		}
		// 普通object类型字段和项目日历类型字段批量处理，以提高效率
		List<String> objectFieldList = new ArrayList<String>();
		List<String> folderFieldList = new ArrayList<String>();
		List<String> folderGuidList = new ArrayList<String>();
		Map<String, List<String>> decoratorFieldNameMap = new HashMap<String, List<String>>();
		Map<String, PMCalendar> calendarMap = null;
		try
		{
			List<User> allUsers = (((EMMImpl) emm).getAAS()).listAllUser(null, true);
			Map<String, User> userMap = new HashMap<String, User>();
			for (User user : allUsers)
			{
				userMap.put(user.getGuid(), user);
			}

			List<Group> allGroups = (((EMMImpl) emm).getAAS()).listAllGroup();
			Map<String, Group> groupMap = new HashMap<String, Group>();
			for (Group group : allGroups)
			{
				groupMap.put(group.getGuid(), group);
			}
			
			Map<String, ClassInfo> classInfoMap = new HashMap<String, ClassInfo>();
			Map<String, HashMap<String, ClassField>> fieldMap = new HashMap<String, HashMap<String, ClassField>>();
			Map<String, HashMap<String, ClassField>> classificationFieldMap = new HashMap<String, HashMap<String, ClassField>>();
			
			for (T object : objectList)
			{
				String classname = null;
				if (!isclassification)
				{
					String classGuid = (String) object.get(SystemClassFieldEnum.CLASSGUID.getName());
					if (!StringUtils.isGuid(classGuid))
					{
						continue;
					}
					
					ClassInfo classInfo = classInfoMap.get(classGuid);
					if (!classInfoMap.containsKey(classGuid))
					{
						classInfo = ((EMMImpl) emm).getClassStub().getClassByGuid(classGuid, false);
						classInfoMap.put(classInfo.getName(), classInfo);
					}
					if (classInfo == null)
					{
						continue;
					}
					
					if (!fieldMap.containsKey(classInfo.getName()))
					{
						List<ClassField> fieldList = emm.listFieldOfClass(classInfo.getName());
						HashMap<String, ClassField> map = new HashMap<String, ClassField>();
						for (ClassField field : fieldList)
						{
							map.put(field.getName().toUpperCase(), field);
						}
						if (!map.containsKey("GUID$"))
						{
							map.put("GUID$", emm.getFieldByName(classInfo.getName(), "GUID$", false));
						}
						fieldMap.put(classInfo.getName(), map);
					}
					
					List<String> fieldNames_ = this.getSystemClassField(classInfo, fieldMap.get(classInfo.getName()), decoratorFieldNameMap);
					if (!SetUtils.isNullList(fieldNames_))
					{
						fieldNames.addAll(fieldNames_);
					}
					classname = classInfo.getName();
				}
				else
				{
					String classification = (String) object.get(SystemClassFieldEnum.CLASSIFICATION.getName());
					if (!StringUtils.isGuid(classification))
					{
						continue;
					}
					CodeItemInfo classificationItem = emm.getCodeItem(classification);
					if (classificationItem == null)
					{
						continue;
					}
					classname = classificationItem.getGuid();
					
					if (!classificationFieldMap.containsKey(classname))
					{
						List<ClassField> fieldList = emm.listClassificationField(classname);
						HashMap<String, ClassField> map = new HashMap<String, ClassField>();
						for (ClassField field : fieldList)
						{
							map.put(field.getName().toUpperCase(), field);
						}
						classificationFieldMap.put(classname, map);
					}
				}

				for (String fieldName : fieldNames)
				{
					if (object.get(fieldName) == null)
					{
						if (fieldName.indexOf(":") != -1)
						{
							fieldName = fieldName.split(":")[0];
						}
						else
						{
							continue;
						}
					}

					if (!(object.get(fieldName) instanceof String))
					{
						continue;
					}

					String guid = (String) object.get(fieldName);
					if (!StringUtils.isGuid(guid))
					{
						continue;
					}

					String newFieldName = fieldName;
					if (fieldName.endsWith("$"))
					{
						newFieldName = fieldName.substring(0, fieldName.length() - 1);
					}
					ClassField classField = null;
					if (isclassification)
					{
						classField = SetUtils.isNullMap(classificationFieldMap.get(classname)) ? null : classificationFieldMap.get(classname).get(newFieldName.toUpperCase());
					}
					else
					{
						classField = fieldMap.get(classname).get(fieldName.toUpperCase());
					}
					if (classField != null)
					{
						if (classField.getType() == FieldTypeEnum.OBJECT)
						{
							ClassInfo classInfo_ = null;
							if (classField.getTypeValue() != null)
							{
								classInfo_ = classInfoMap.get(classField.getTypeValue());
								if (!classInfoMap.containsKey(classField.getTypeValue()))
								{
									classInfo_ = emm.getClassByName(classField.getTypeValue());
									classInfoMap.put(classField.getTypeValue(), classInfo_);
								}
							}
							if (classInfo_ == null)
							{
								throw new DynaDataExceptionAll("query error ,the object type filed has not type value. fieldName is " + classField.getName(), null,
										DataExceptionEnum.DS_MODEL_OBJECT_TYPEVALUE_ERROR, classField.getName());
							}
							if (classInfo_.hasInterface(ModelInterfaceEnum.IUser))
							{
								User user = userMap.get(guid);
								if (user != null)
								{
									object.put(newFieldName + "$NAME", user.getUserName());
									object.put(newFieldName + "$ID", user.getUserId());
								}
							}
							else if (classInfo_.hasInterface(ModelInterfaceEnum.IGroup))
							{
								Group group = groupMap.get(guid);
								if (group != null)
								{
									object.put(newFieldName + "$NAME", group.getGroupName());
									object.put(newFieldName + "$ID", group.getGroupId());
								}
							}
							else if (classInfo_.hasInterface(ModelInterfaceEnum.IPMCalendar))
							{
								if (calendarMap == null)
								{
									calendarMap = this.getAllPMCalendar(DataServer.getSystemDataService());
								}

								PMCalendar calendar = calendarMap.get(guid);
								if (calendar != null)
								{
									object.put(newFieldName + "$NAME", calendar.getName());
									object.put(newFieldName + "$ID", calendar.getId());
								}
							}
							else if (classInfo_.hasInterface(ModelInterfaceEnum.IPMRole))
							{
								String pmRoleGuid = (String) object.get(newFieldName);
								if (StringUtils.isGuid(pmRoleGuid))
								{
									ProjectRole projectRole = new ProjectRole();
									projectRole.setGuid(pmRoleGuid);
									projectRole = DataServer.getSystemDataService().queryObject(ProjectRole.class, projectRole);
									if (projectRole != null)
									{
										object.put(newFieldName + "$NAME", projectRole.getRoleName());
										object.put(newFieldName + "$ID", projectRole.getRoleId());
									}
								}
							}
							else
							{
								if (!objectFieldList.contains(fieldName))
								{
									objectFieldList.add(fieldName);
								}
							}
						}
						else if (classField.getType() == FieldTypeEnum.FOLDER)
						{
							if (!folderFieldList.contains(fieldName))
							{
								folderFieldList.add(fieldName);
							}
							if (!folderGuidList.contains(guid))
							{
								folderGuidList.add(guid);
							}
						}
					}
				}
			}

			// 记录表的一个类,以便查询系统字段
			Map<String, String> classNameMap = new HashMap<String, String>();
			Map<String, List<String>> classOfTableMap = new HashMap<String, List<String>>();
			if (!SetUtils.isNullList(objectFieldList))
			{
				for (String fieldName : objectFieldList)
				{
					String newFieldName = fieldName;

					if (fieldName.endsWith("$"))
					{
						newFieldName = fieldName.substring(0, fieldName.length() - 1);
					}

					for (T object : objectList)
					{
						if (!StringUtils.isNullString((String) object.get(newFieldName + "$NAME")) && !"/.".equals(object.get(newFieldName + "$NAME")))
						{
							continue;
						}

						String classGuid = (String) object.get(newFieldName + "$CLASS");
						if (!StringUtils.isGuid(classGuid))
						{
							classGuid = (String) object.get(newFieldName + "$CLASSGUID");
						}
						if (StringUtils.isGuid(classGuid))
						{
							ClassInfo classInfo = emm.getClassByGuid(classGuid);
							String tableName = classInfo.getRealBaseTableName();
							classNameMap.put(tableName, classInfo.getName());
							List<String> guidList = classOfTableMap.get(tableName);
							if (guidList == null)
							{
								guidList = new LinkedList<>();
								classOfTableMap.put(tableName, guidList);
							}
							guidList.add((String) object.get(fieldName));
						}
					}
				}
			}

			Map<String, FoundationObject> dataMap = new HashMap<String, FoundationObject>();
			for (String tableName : classOfTableMap.keySet())
			{
				List<String> guidList = classOfTableMap.get(tableName);
				String className = classNameMap.get(tableName);
				dataMap.putAll(this.getObject(className, guidList, emm, sessionId));
			}

			for (String fieldName : objectFieldList)
			{
				String newFieldName = fieldName;
				if (fieldName.endsWith("$"))
				{
					newFieldName = fieldName.substring(0, fieldName.length() - 1);
				}

				for (T object : objectList)
				{
					String guid = (String) object.get(fieldName);
					if ((StringUtils.isNullString((String) object.get(newFieldName + "$NAME")) || "/.".equals(object.get(newFieldName + "$NAME"))) && StringUtils.isGuid(guid))
					{
						FoundationObject foundationObject = dataMap.get(guid);
						object.clear(newFieldName + "$NAME");
						if (foundationObject != null)
						{
							object.put(newFieldName + "$NAME", foundationObject.getFullName());
							object.put(newFieldName + "$FULLNAME", foundationObject.getFullName());
						}
						else
						{
							object.clear(fieldName);
							object.put(fieldName, null);
							if (fieldName.endsWith("$"))
							{
								object.clear(fieldName + "CLASS");
								object.clear(fieldName + "MASTER");
								object.put(fieldName + "CLASS", null);
								object.put(fieldName + "MASTER", null);
							}
							else
							{
								object.clear(fieldName + "$CLASS");
								object.clear(fieldName + "$MASTER");
								object.put(fieldName + "$CLASS", null);
								object.put(fieldName + "$MASTER", null);
							}
						}
					}
				}
			}

			if (!SetUtils.isNullList(folderFieldList))
			{
				Map<String, Folder> folderMap = this.getFolder(folderGuidList, DataServer.getSystemDataService());
				for (String fieldName : folderFieldList)
				{
					String newFieldName = fieldName;
					if (fieldName.endsWith("$"))
					{
						newFieldName = fieldName.substring(0, fieldName.length() - 1);
					}

					for (T object : objectList)
					{
						String guid = (String) object.get(fieldName);
						if (StringUtils.isGuid(guid))
						{
							Folder folder = folderMap.get(guid);
							if (folder != null)
							{
								if (folder.getType() == FolderTypeEnum.LIBRARY)
								{
									object.put(newFieldName + "$NAME", folder.getName());
								}
								else
								{
									String libraryGuid = folder.getLibraryUser();
									Folder library = folderMap.get(libraryGuid);
									if (library == null)
									{
										List<String> folderGuidList_ = new ArrayList<String>();
										folderGuidList_.add(libraryGuid);
										Map<String, Folder> folderMap_ = this.getFolder(folderGuidList_, DataServer.getSystemDataService());
										library = folderMap_.get(libraryGuid);
									}
									object.put(newFieldName + "$NAME", "/" + library.getName() + folder.getHierarchy());
								}
							}
						}
					}
				}
			}
		}
		catch (ServiceRequestException e)
		{
			DynaLogger.warn("decorate exception: ObjectFieldDecorator.decorateWithField", e);
		}
	}

	public <T extends DynaObject> void decorateWithField(Set<String> fieldNames, T object) throws DecorateException
	{
	}

	private List<String> getSystemClassField(ClassInfo classInfo, HashMap<String, ClassField> fieldMap, Map<String, List<String>> decoratorFieldNameMap) throws ServiceRequestException
	{
		List<String> fieldNames_ = decoratorFieldNameMap.get(classInfo.getGuid());
		if (!decoratorFieldNameMap.containsKey(classInfo.getGuid()))
		{
			fieldNames_ = this.getDecoratorFieldNameList(classInfo, fieldMap);
			decoratorFieldNameMap.put(classInfo.getGuid(), fieldNames_);
		}
		return fieldNames_;
	}

	private Map<String, PMCalendar> getAllPMCalendar(SystemDataService sds)
	{
		List<PMCalendar> calendarList = sds.query(PMCalendar.class, null);
		Map<String, PMCalendar> calendarMap = new HashMap<String, PMCalendar>();
		if (!SetUtils.isNullList(calendarList))
		{
			for (PMCalendar calendar : calendarList)
			{
				calendarMap.put(calendar.getGuid(), calendar);
			}
		}
		return calendarMap;
	}

	private Map<String, FoundationObject> getObject(String className, List<String> guidList, EMM emm, String sessionId) throws DecorateException, ServiceRequestException
	{
		Map<String, FoundationObject> result = new HashMap<String, FoundationObject>();
		List<FoundationObject> resultList = DataServer.getInstanceService().queryByGuidList(className, guidList, sessionId);
		if (!SetUtils.isNullList(resultList))
		{
			for (FoundationObject foundationObject : resultList)
			{
				super.decorate(foundationObject.getObjectGuid().getClassGuid(), foundationObject, emm);
				result.put(foundationObject.getObjectGuid().getGuid(), foundationObject);
			}
		}
		return result;
	}

	private Map<String, Folder> getFolder(List<String> guidList, SystemDataService sds) throws DecorateException
	{
		Map<String, Folder> result = new HashMap<>();
		for (String folderGuid : guidList)
		{
			Folder folder = sds.get(Folder.class, folderGuid);
			if (folder != null)
			{
				result.put(folder.getGuid(), folder);
			}
		}
		return result;
	}

	private List<String> getDecoratorFieldNameList(ClassInfo classInfo, HashMap<String, ClassField> fieldMap) throws ServiceRequestException
	{
		List<String> systemClassFieldList = SystemClassFieldEnum.getFoundationSystemClassFieldList();
		if (classInfo.hasInterface(ModelInterfaceEnum.IStructureObject) || classInfo.hasInterface(ModelInterfaceEnum.IBOMStructure))
		{
			systemClassFieldList = SystemClassFieldEnum.getStructureSystemClassFieldList();
		}

		List<String> fieldNames = new ArrayList<String>();
		for (String classFieldName : systemClassFieldList)
		{
			ClassField classField = fieldMap.get(classFieldName);
			if (classField.getType() == FieldTypeEnum.OBJECT || classField.getType() == FieldTypeEnum.FOLDER)
			{
				if (!fieldNames.contains(classFieldName))
				{
					fieldNames.add(classFieldName);
				}
			}
		}
		return fieldNames;
	}

	public <T extends DynaObject> void decorateWithField(Set<String> fieldNames, T object, EMM emm, String sessionId, boolean isclassification) throws DecorateException
	{
		if (object == null)
		{
			return;
		}

		List<T> objectFieldSet = new ArrayList<T>();
		objectFieldSet.add(object);
		this.decorateWithField(fieldNames, objectFieldSet, emm, sessionId, isclassification);
	}
}