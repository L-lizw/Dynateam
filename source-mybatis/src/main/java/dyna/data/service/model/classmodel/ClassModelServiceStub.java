package dyna.data.service.model.classmodel;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.model.IconEntry;
import dyna.common.bean.model.ReferenceCode;
import dyna.common.bean.model.ReferenceField;
import dyna.common.bean.model.Script;
import dyna.common.bean.model.bmbo.BusinessObject;
import dyna.common.bean.model.cls.ClassObject;
import dyna.common.bean.model.cls.NumberingModel;
import dyna.common.bean.model.cls.NumberingObject;
import dyna.common.bean.model.code.CodeItem;
import dyna.common.bean.model.itf.InterfaceObject;
import dyna.common.bean.model.ui.UIIcon;
import dyna.common.bean.model.ui.UIObject;
import dyna.common.bean.xml.UpperKeyMap;
import dyna.common.cache.*;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.dto.model.cls.*;
import dyna.common.dto.model.code.CodeObjectInfo;
import dyna.common.dto.model.lf.LifecycleInfo;
import dyna.common.dto.model.ui.UIAction;
import dyna.common.dto.model.ui.UIField;
import dyna.common.dto.model.ui.UIObjectInfo;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.FieldTypeEnum;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.systemenum.NumberingTypeEnum;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.context.DataServerContext;
import dyna.data.service.model.DataCacheServiceStub;
import dyna.data.service.sdm.FieldValueEqualsFilter;
import dyna.data.service.sdm.SystemDataService;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class ClassModelServiceStub extends DataCacheServiceStub<ClassModelServiceImpl>
{
	// classname:classguid
	private final Map<String, ClassObject>		CLASSOBJECT_NAME_MAP			= Collections.synchronizedMap(new HashMap<>());
	// classguid:classobject
	private final Map<String, String>			CLASSOBJECT_GUID_NAME_MAP		= Collections.synchronizedMap(new HashMap<>());
	private final Map<String, NumberingModel>	NUMBERING_MODEL_GUID_MAP		= Collections.synchronizedMap(new HashMap<>());
	private final Map<String, NumberingObject>	NUMBERING_OBJECT_GUID_MAP		= Collections.synchronizedMap(new HashMap<>());
	private final Map<String, ClassField>		CLASSFIELD_GUID_MAP				= Collections.synchronizedMap(new HashMap<>());
	private final Map<String, UIObject>			UIOBJECT_GUID_MAP				= Collections.synchronizedMap(new HashMap<>());
	private final Map<String, UIField>			UIFIELD_GUID_MAP				= Collections.synchronizedMap(new HashMap<>());

	private final Map<String, List<String>>		CLASSOBJECT_INTERFACE_NAME_MAP	= Collections.synchronizedMap(new HashMap<>());
	private final Map<String, String>			CLASS_BASE_TABLE_MAP_KEY_NAME	= Collections.synchronizedMap(new HashMap<>());
	private final Map<String, String>			CLASS_BASE_TABLE_MAP_KEY_GUID	= Collections.synchronizedMap(new HashMap<>());
	private final Map<String, String>			FIELD_TABLE_INDEX_MAP_KEY_NAME	= Collections.synchronizedMap(new HashMap<>());
	private final Map<String, String>			FIELD_TABLE_INDEX_MAP_KEY_GUID	= Collections.synchronizedMap(new HashMap<>());

	private final Map<String, ClassAction>		SCRIPT_ACTION_GUID_MAP			= Collections.synchronizedMap(new HashMap<>());

	private AbstractCacheInfo					cacheInfo						= null;

	protected ClassModelServiceStub(DataServerContext context, ClassModelServiceImpl service)
	{
		super(context, service);
		this.cacheInfo = new ClassModelCacheInfo();
		this.cacheInfo.register();
	}

	@Override
	public void loadModel() throws ServiceRequestException
	{
		CLASSOBJECT_NAME_MAP.clear();

		// 缓存所有的类基本信息
		this.loadBaseClassInfo();

		// 构造类上的各种信息
		this.buildClassObject();

		// 构造类模型树结构
		this.buildClassModelRelation(CLASSOBJECT_NAME_MAP);

		// 加载实现接口的所有类列表
		this.loadClassObjectOfInterface();

	}

	public void reLoadModel() throws ServiceRequestException
	{
		this.relaodModelBaseInfo();

		this.clearAllCache();

		this.loadModel();
	}

	protected void checkAndClearClassObject() throws ServiceRequestException
	{
		String tranid = StringUtils.generateRandomUID(32);
		try
		{
//			DataServer.getTransactionService().startTransaction(tranid);
			this.cacheInfo.setRemoveErrData(true);

			this.doCheckAndClearUIObject();

			this.doCheckAndClearScript();

			this.doCheckAndClearNumberModel();

			this.cacheInfo.setRemoveErrData(false);

//			DataServer.getTransactionService().commitTransaction(tranid);
		}
		catch (DynaDataException e)
		{
			this.cacheInfo.setRemoveErrData(false);
//			DataServer.getTransactionService().rollbackTransaction(tranid);
			throw e;
		}
		catch (Exception e)
		{
			this.cacheInfo.setRemoveErrData(false);
//			DataServer.getTransactionService().rollbackTransaction(tranid);
			throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
		}
	}

	/**
	 * 部署后检查和 处理ui信息
	 * 
	 * @throws ServiceRequestException
	 */
	private void doCheckAndClearUIObject() throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		List<UIObjectInfo> allUIList = sds.listFromCache(UIObjectInfo.class, null);
		if (!SetUtils.isNullList(allUIList))
		{
			for (UIObjectInfo uiObjectInfo : allUIList)
			{
				ClassInfo classInfo = sds.get(ClassInfo.class, uiObjectInfo.getClassGuid());
				if (classInfo == null)
				{
					this.deleteUIObject(uiObjectInfo);
				}

				this.doCheckAndClearUIField(uiObjectInfo, classInfo);
			}
		}
	}

	private void deleteUIObject(UIObjectInfo uiObjectInfo) throws ServiceRequestException
	{
		try
		{
			SystemDataService sds = DataServer.getSystemDataService();

			sds.deleteFromCache(UIField.class, new FieldValueEqualsFilter<>(UIField.UIGUID, uiObjectInfo.getGuid()));

			DynaLogger.info("UI delete: " + uiObjectInfo);
			sds.delete(uiObjectInfo);
		}
		catch (DynaDataException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
		}
	}

	/**
	 * 部署后检查ui字段记录的字段名在类中是否还存在
	 * 
	 * @param uiObjectInfo
	 * @throws ServiceRequestException
	 */
	private void doCheckAndClearUIField(UIObjectInfo uiObjectInfo, ClassInfo classInfo) throws ServiceRequestException
	{
		try
		{
			SystemDataService sds = DataServer.getSystemDataService();

			List<UIField> fieldList = sds.listFromCache(UIField.class, new FieldValueEqualsFilter<>(UIField.UIGUID, uiObjectInfo.getGuid()));
			if (!SetUtils.isNullList(fieldList))
			{
				for (UIField uiField : fieldList)
				{
					ClassField classField = this.getField(classInfo.getName(), uiField.getName());
					if (classField == null)
					{
						DynaLogger.info("UIField delete: " + uiField);
						sds.delete(uiField);
					}
				}
			}
		}
		catch (DynaDataException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
		}
	}

	/**
	 * 部署后检查和 处理ui信息
	 * 
	 * @throws ServiceRequestException
	 */
	private void doCheckAndClearScript() throws ServiceRequestException
	{
		try
		{
			SystemDataService sds = DataServer.getSystemDataService();
			List<ClassAction> allActionList = sds.listFromCache(ClassAction.class, null);
			if (!SetUtils.isNullList(allActionList))
			{
				for (ClassAction classAction : allActionList)
				{
					ClassInfo classInfo = sds.get(ClassInfo.class, classAction.getClassfk());
					if (classInfo == null)
					{
						DynaLogger.info("Script delete: " + classAction);
						sds.delete(classAction);
					}
				}
			}
		}
		catch (DynaDataException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
		}
	}

	/**
	 * 部署后检查和 处理编码规则和合成规则信息
	 * 
	 * @throws ServiceRequestException
	 */
	private void doCheckAndClearNumberModel() throws ServiceRequestException
	{
		try
		{
			SystemDataService sds = DataServer.getSystemDataService();
			List<NumberingModelInfo> numberingModelInfoList = sds.listFromCache(NumberingModelInfo.class, null);
			if (!SetUtils.isNullList(numberingModelInfoList))
			{
				for (NumberingModelInfo numberingModelInfo : numberingModelInfoList)
				{
					ClassInfo classInfo = sds.get(ClassInfo.class, numberingModelInfo.getClassGuid());
					if (classInfo == null)
					{
						DynaLogger.info("NumberingModel delete: " + numberingModelInfo);
						sds.deleteFromCache(NumberingObjectInfo.class, new FieldValueEqualsFilter<>(NumberingObjectInfo.NUMBERRULEFK, numberingModelInfo.getGuid()));
						sds.delete(numberingModelInfo);
						continue;
					}
					if (!numberingModelInfo.isNumbering())
					{
						continue;
					}
					// 检查编码规则是object类型时，记录的fieldclassname是否已经发生改变，对应object字段的类型值
					UpperKeyMap param = new UpperKeyMap();
					param.put(NumberingObjectInfo.NUMBERRULEFK, numberingModelInfo.getGuid());
					param.put(NumberingObjectInfo.TYPE, NumberingTypeEnum.FIELD.toString());
					List<NumberingObjectInfo> numberingObjectInfoList = sds.listFromCache(NumberingObjectInfo.class, new FieldValueEqualsFilter<>(param));
					if (!SetUtils.isNullList(numberingObjectInfoList))
					{
						for (NumberingObjectInfo numberingObjectInfo : numberingObjectInfoList)
						{
							String fieldName = numberingObjectInfo.getTypevalue();
							UpperKeyMap fieldParam = new UpperKeyMap();
							fieldParam.put(ClassField.CLASSGUID, classInfo.getGuid());
							fieldParam.put(ClassField.FIELDNAME, fieldName);
							List<ClassField> classFieldList = sds.listFromCache(ClassField.class, new FieldValueEqualsFilter<>(fieldParam));
							if (!SetUtils.isNullList(classFieldList))
							{
								ClassField classField = classFieldList.get(0);
								// field类型的编码规则可能是object类型字段，只有是object类型的时候才会选择子字段，有子编码规则
								if (classField.getType().equals(FieldTypeEnum.OBJECT))
								{
									// 最新设置的类型值（类名）
									String typeValue = classField.getTypeValue();
									ClassInfo currentClassInfo = sds.listFromCache(ClassInfo.class, new FieldValueEqualsFilter<>(ClassInfo.NAME, typeValue)).get(0);
									List<NumberingObjectInfo> childNumberingObjectInfoList = sds.listFromCache(NumberingObjectInfo.class,
											new FieldValueEqualsFilter<>(NumberingObjectInfo.PARENTGUID, numberingObjectInfo.getGuid()));
									if (!SetUtils.isNullList(childNumberingObjectInfoList))
									{
										for (NumberingObjectInfo childInfo : childNumberingObjectInfoList)
										{
											String oldFieldName = childInfo.getTypevalue();
											fieldParam.put(ClassField.CLASSGUID, currentClassInfo.getGuid());
											fieldParam.put(ClassField.FIELDNAME, oldFieldName);
											fieldParam.put(ClassField.ISSYSTEM, "Y");
											List<ClassField> result = sds.listFromCache(ClassField.class, new FieldValueEqualsFilter<>(fieldParam));
											ClassField oldField = SetUtils.isNullList(result) ? null : result.get(0);
											// 新的类型值，用旧的字段名，能找到此字段，且此字段是系统字段，则子编码规则修改记录的类名即可
											if (oldField != null && !childInfo.getFieldClassName().equals(typeValue))
											{
												DynaLogger.info("NumberingObjectInfo typeValue reset success, sourceType: " + childInfo.getFieldClassName() + "-> currentType: "
														+ typeValue);
												childInfo.setFieldClassName(typeValue);
												sds.save(childInfo);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		catch (DynaDataException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
		}
	}

	/**
	 * 重新构造类上的各种信息
	 */
	private void buildClassObject()
	{
		for (ClassObject classObject : CLASSOBJECT_NAME_MAP.values())
		{
			// 加载字段信息
			this.makeClassField(classObject);

			// 加载UI模型
			this.buildUIModel(classObject);

			// 加载编码合成规则
			this.buildNumberingModel(classObject);

			// 处理唯一值，如果合成字段包含唯一值，设为true
			this.buildCheckUnique(classObject);

			// 加载类的表名和类字段所属表的索引
			this.buildTableIndexInfo(classObject);

			this.setCodeRefrenceValueGuid(classObject);

			// 加载事件和脚本
			this.buildScript(classObject);
		}
	}

	private void loadClassObjectOfInterface()
	{
		for (ClassObject classObject : CLASSOBJECT_NAME_MAP.values())
		{
			List<ModelInterfaceEnum> interfaceEnumList = classObject.getInterfaceList();
			if (interfaceEnumList != null)
			{
				for (ModelInterfaceEnum interfaceEnum : interfaceEnumList)
				{
					CLASSOBJECT_INTERFACE_NAME_MAP.computeIfAbsent(interfaceEnum.name(), k -> new ArrayList<>());
					if (!CLASSOBJECT_INTERFACE_NAME_MAP.get(interfaceEnum.name()).contains(classObject.getGuid()))
					{
						CLASSOBJECT_INTERFACE_NAME_MAP.get(interfaceEnum.name()).add(classObject.getGuid());
					}
				}
			}
		}
	}

	private void buildScript(ClassObject classObject)
	{
		String classGuid = classObject.getGuid();
		List<ClassAction> classActionList = DataServer.getSystemDataService().listFromCache(ClassAction.class,
				new FieldValueEqualsFilter<ClassAction>(ClassAction.CLASSFK, classGuid));
		if (!SetUtils.isNullList(classActionList))
		{
			for (ClassAction classAction : classActionList)
			{
				UpperKeyMap param = new UpperKeyMap();
				param.put(ClassAction.CLASSFK, classGuid);
				param.put(ClassAction.PARENTGUID, classAction.getGuid());

				List<ClassAction> childList = DataServer.getSystemDataService().listFromCache(ClassAction.class, new FieldValueEqualsFilter<>(param));
				classAction.setChildren(childList);

				classAction.setClassName(CLASSOBJECT_GUID_NAME_MAP.get(classAction.getClassfk()));

				SCRIPT_ACTION_GUID_MAP.put(classAction.getGuid(), classAction);
			}
		}
		classObject.setClassActionList(classActionList);
	}

	protected Map<String, ClassObject> buildClassModelRelation(Map<String, ClassObject> classObjectMap)
	{
		// set parent-children relation
		List<String> processedClassNameList = new ArrayList<>();

		for (ClassObject classObject : classObjectMap.values())
		{
			this.processInheritedRelation(processedClassNameList, classObject, classObjectMap);

			this.setInterfaces(classObject);
		}
		for (ClassObject classObject : classObjectMap.values())
		{
			if (classObject.getChildList() != null)
			{
				Collections.sort(classObject.getChildList(), Comparator.comparing(ClassObject::getName));
			}
		}

		// 处理分类
		this.buildClassification(classObjectMap);

		this.resetCreateTable(classObjectMap);

		return classObjectMap;
	}

	/**
	 * @param superInterface
	 * @param subInterface
	 * @return
	 */
	private boolean isSuperInterface(ModelInterfaceEnum superInterface, ModelInterfaceEnum subInterface)
	{
		if (superInterface == subInterface)
		{
			return false;
		}
		InterfaceObject subInterfaceObject = DataServer.getInterfaceModelService().getInterface(subInterface);
		String parent = subInterfaceObject.getParentInterfaces();
		if (parent != null)
		{
			String[] parentInterfaceNameList = StringUtils.splitString(parent);
			for (String parentInterfaceName : parentInterfaceNameList)
			{
				if (superInterface.name().equalsIgnoreCase(parentInterfaceName) || this.isSuperInterface(superInterface, ModelInterfaceEnum.typeof(parentInterfaceName)))
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 处理继承关系
	 *
	 * @param classObject
	 */
	private void processInheritedRelation(List<String> processedClassNameList, ClassObject classObject, Map<String, ClassObject> classObjectMap)
	{
		if (processedClassNameList.contains(classObject.getName()))
		{
			// processed
			return;
		}

		if (classObject.getSuperclass() == null)
		{
			processedClassNameList.add(classObject.getName());
			return;
		}

		ClassObject superObject = classObjectMap.get(classObject.getSuperclass());
		if (!processedClassNameList.contains(superObject.getName()))
		{
			// process super class at first
			this.processInheritedRelation(processedClassNameList, superObject, classObjectMap);
		}

		if (StringUtils.isNullString(classObject.getLifecycle()))
		{
			classObject.setLifecycle(superObject.getLifecycle());
		}

		if (StringUtils.isNullString(classObject.getIterationLimit()))
		{
			classObject.setIterationLimit(superObject.getIterationLimit());
		}

		if (!SetUtils.isNullList(superObject.getInterfaceList()))
		{
			for (ModelInterfaceEnum interfaceEnum : superObject.getInterfaceList())
			{
				classObject.addInterface(interfaceEnum.name());
			}
		}

		if (!SetUtils.isNullList(superObject.getFieldList()))
		{
			Map<String, ClassField> fieldMap = new HashMap<>();
			if (classObject.getFieldList() != null)
			{
				for (ClassField field : classObject.getFieldList())
				{
					fieldMap.put(field.getName(), field);
				}
			}

			for (ClassField classField : superObject.getFieldList())
			{
				ClassField classFieldOwner = fieldMap.get(classField.getName());
				if (classFieldOwner == null)
				{
					ClassField field = classField.clone();
					field.setInherited(true);
					classObject.addField(field);
				}
				else
				{
					classFieldOwner.setInherited(true);
					// 根据父类重设子类的type和typevalue
					if (classField.getType() != FieldTypeEnum.OBJECT)
					{
						if (classField.getType() != classFieldOwner.getType())
						{
							classFieldOwner.setDefaultValue(null);
						}
						if (!StringUtils.isNullString(classField.getTypeValue()) && !classField.getTypeValue().equals(classFieldOwner.getTypeValue()))
						{
							classFieldOwner.setTypeValue(classField.getTypeValue());
						}
					}
					else
					{
						// 子类的TypeValue可以是父类的TypeValue的子类
						if (!StringUtils.isNullString(classFieldOwner.getTypeValue()) && !classFieldOwner.getTypeValue().equals(classField.getTypeValue())
								&& !this.isChild(classFieldOwner.getTypeValue(), classField.getTypeValue(), classObjectMap))
						{
							classFieldOwner.setTypeValue(classField.getTypeValue());
						}
					}
					classFieldOwner.setType(classField.getType());
				}
			}
		}
		this.processInheritedRelationFromInterface(classObject, classObjectMap);

		if (classObject.getUiObjectList() != null)
		{
			for (UIObject uiObject : classObject.getUiObjectList())
			{
				List<UIField> uiFieldList = uiObject.getFieldList();
				if (uiFieldList != null)
				{
					List<UIField> notInClassUIFieldList = new ArrayList<>(uiFieldList.size());
					for (UIField uiField : uiFieldList)
					{
						ClassField classField = classObject.getField(uiField.getName());
						if (classField != null)
						{
							uiField.setType(classField.getType());
						}
						else
						{
							if (!StringUtils.isNullString(uiField.getName()))
							{
								if (!uiField.getName().startsWith("SEPARATOR$"))
								{
									notInClassUIFieldList.add(uiField);
								}
							}
						}
					}
					uiFieldList.removeAll(notInClassUIFieldList);

					uiFieldList.sort(Comparator.comparingInt(UIField::getRowAmount).thenComparingInt(UIField::getColumnAmount));
				}
			}
		}

		superObject.addChild(classObject);

		processedClassNameList.add(classObject.getName());
	}

	/**
	 * 对继承接口的处理
	 *
	 * @param classObject
	 */
	private void processInheritedRelationFromInterface(ClassObject classObject, Map<String, ClassObject> classObjectMap)
	{
		// 继承接口中的ClassField
		List<ClassField> classFieldSet = this.getFieldFromInterfaces(classObject.getInterfaceList());
		Map<String, ClassField> fieldMap = new HashMap<>();
		if (classObject.getFieldList() != null)
		{
			for (ClassField field : classObject.getFieldList())
			{
				fieldMap.put(field.getName(), field);
			}
		}

		for (ClassField classField : classFieldSet)
		{
			// 接口字段的columnname和名字是一样的，如果column为空，用名字设值
			if (StringUtils.isNullString(classField.getColumnName()))
			{
				if (classField.isSystem())
				{
					classField.setColumnName(classField.getName().substring(0, classField.getName().length() - 1).toUpperCase());
				}
				else
				{
					classField.setColumnName(classField.getName().toUpperCase());
				}
			}
			if (!fieldMap.containsKey(classField.getName()))
			{
				ClassField field = classField.clone();
				field.setInherited(true);
				classObject.addField(field);
			}
			else
			{
				ClassField classField2 = fieldMap.get(classField.getName());
				classField2.setValueScope(classField.getValueScope());
				if (classField2.getType() != classField2.getType())
				{
					classField2.setType(classField.getType());
					classField2.setTypeValue(classField.getTypeValue());
				}
				else if (classField2.getType() == FieldTypeEnum.OBJECT
						&& !StringUtils.convertNULLtoString(classField.getTypeValue()).equals(StringUtils.convertNULLtoString(classField2.getTypeValue())))
				{
					if (!isChild(classField.getTypeValue(), classField2.getTypeValue(), classObjectMap))
					{
						classField2.setTypeValue(classField.getTypeValue());
					}
				}
				else if (!StringUtils.isNullString(classField.getTypeValue()))
				{
					classField2.setTypeValue(classField.getTypeValue());
				}
			}
		}
	}

	/**
	 * 得到接口中定义的字段
	 *
	 * @param interfaceEnumList
	 * @return
	 */
	private List<ClassField> getFieldFromInterfaces(List<ModelInterfaceEnum> interfaceEnumList)
	{
		List<ClassField> allFieldList = new ArrayList<>();
		if (interfaceEnumList != null)
		{
			List<String> fieldNameList = new ArrayList<>();
			for (ModelInterfaceEnum interfaceEnum : interfaceEnumList)
			{
				List<ClassField> fieldList = DataServer.getInterfaceModelService().listClassFieldOfInterface(interfaceEnum);
				if (!SetUtils.isNullList(fieldList))
				{
					for (ClassField field : fieldList)
					{
						if (!fieldNameList.contains(field.getName()))
						{
							fieldNameList.add(field.getName());
							allFieldList.add(field);
						}
					}
				}
			}
		}
		return allFieldList;
	}

	/**
	 * 根据系统字段CLASSIFICATION$的值设置类上CLASSIFICATION的值
	 *
	 * @param classObjectMap
	 */
	private void buildClassification(Map<String, ClassObject> classObjectMap)
	{
		for (ClassObject classObject : classObjectMap.values())
		{
			ClassField classificationField = classObject.getField(SystemClassFieldEnum.CLASSIFICATION.getName());
			if (classificationField != null && !StringUtils.isNullString(classificationField.getTypeValue()))
			{
				classObject.setClassification(classificationField.getTypeValue());
			}
		}
	}

	/**
	 * 当前类的接口是从父类继承的，自己没有接口，导致createtable标记没有根据接口设置，这里需要再根据父类设置一下
	 */
	private void resetCreateTable(Map<String, ClassObject> classObjectMap)
	{
		for (ClassObject classObject : classObjectMap.values())
		{
			if (!classObject.isCreateTable())
			{
				if (this.isSuperCreateTable(classObject.getSuperclass(), classObjectMap))
				{
					classObject.setCreateTable(true);
				}
			}
		}
	}

	private boolean isSuperCreateTable(String superClassName, Map<String, ClassObject> classObjectMap)
	{
		if (superClassName == null)
		{
			return false;
		}

		ClassObject classObject = classObjectMap.get(superClassName);
		if (classObject.isCreateTable())
		{
			return true;
		}
		return isSuperCreateTable(classObject.getSuperclass(), classObjectMap);
	}

	/**
	 * 设置基表名
	 *
	 * @param classObject
	 */
	private void setInterfaces(ClassObject classObject)
	{
		List<ModelInterfaceEnum> interfaceNameList = new ArrayList<>();
		List<ModelInterfaceEnum> interfaceEnumList = classObject.getInterfaceList();
		if (interfaceEnumList != null)
		{
			for (ModelInterfaceEnum interfaceEnum : interfaceEnumList)
			{
				InterfaceObject interfaceObject = DataServer.getInterfaceModelService().getInterface(interfaceEnum);
				if (interfaceObject != null && interfaceObject.isSystem())
				{
					continue;
				}
				interfaceNameList.add(interfaceEnum);
			}
		}

		// 去掉interfaceNameList中有继承关系的父接口，使用TreeSet来根据接口名字排序
		TreeSet<String> interfaceNameSet = new TreeSet<>();
		for (ModelInterfaceEnum superInterface : interfaceNameList)
		{
			boolean isSuperInterface = false;
			for (ModelInterfaceEnum subInterface : interfaceNameList)
			{
				if (this.isSuperInterface(superInterface, subInterface))
				{
					isSuperInterface = true;
					break;
				}
			}
			if (!isSuperInterface)
			{
				interfaceNameSet.add(superInterface.name());
			}
		}

		if (!interfaceNameSet.isEmpty())
		{
			String baseClass = this.concatStringWithDelimiter(";", interfaceNameSet.toArray(new String[0]));
			classObject.setBaseClass(baseClass);
		}
	}

	/**
	 * 使用delimiter连接字符串
	 *
	 * @param delimiter
	 * @param strs
	 * @return
	 */
	public String concatStringWithDelimiter(final String delimiter, final String... strs)
	{
		StringBuilder retStr = new StringBuilder();
		int size = strs.length;
		for (int i = 0; i < size; i++)
		{
			if (StringUtils.isNullString(strs[i]))
			{
				strs[i] = "";
			}
			retStr.append(strs[i]).append(delimiter);
		}
		return retStr.toString();
	}

	protected List<ClassInfo> listAllParentClassInfo(String classGuid)
	{
		List<ClassInfo> parentClassList = new ArrayList<>();
		ClassObject classObject = this.getClassObjectByGuid(classGuid);
		this.addAllSuperClass(parentClassList, classObject);
		return parentClassList;
	}

	protected List<String> listAllParentClassGuid(String classGuid)
	{
		List<ClassInfo> parentClassList = this.listAllParentClassInfo(classGuid);
		if (parentClassList != null)
		{
			return parentClassList.stream().map(ClassInfo::getGuid).collect(Collectors.toList());
		}
		return new ArrayList<>();
	}

	protected List<ClassInfo> listAllSubClassInfo(String classGuid)
	{
		List<ClassInfo> subClassList = new ArrayList<>();
		ClassObject classObject = this.getClassObjectByGuid(classGuid);
		this.addAllSubClass(subClassList, classObject);
		return subClassList;
	}

	protected List<String> listAllSubClassGuid(String classGuid)
	{
		List<ClassInfo> subClassList = this.listAllSubClassInfo(classGuid);
		if (subClassList != null)
		{
			return subClassList.stream().map(ClassInfo::getGuid).collect(Collectors.toList());
		}
		return new ArrayList<>();
	}

	protected List<ClassInfo> getClassInfoListImplInterface(ModelInterfaceEnum interfaceEnum)
	{
		List<ClassInfo> list = new ArrayList<>();
		if (CLASSOBJECT_INTERFACE_NAME_MAP.containsKey(interfaceEnum.name()))
		{
			CLASSOBJECT_INTERFACE_NAME_MAP.get(interfaceEnum.name()).forEach(classGuid -> {
				String className = CLASSOBJECT_GUID_NAME_MAP.get(classGuid);
				list.add(CLASSOBJECT_NAME_MAP.get(className).getInfo());
			});
		}
		return list;
	}

	protected List<String> getClassNameListImplInterface(ModelInterfaceEnum interfaceEnum)
	{
		List<ClassInfo> classList = this.getClassInfoListImplInterface(interfaceEnum);
		if (SetUtils.isNullList(classList))
		{
			return new ArrayList<>();
		}

		List<String> retList = new ArrayList<>();
		for (ClassInfo co : classList)
		{
			retList.add(co.getName());
		}

		return retList;
	}

	protected ClassObject getClassObjectByGuid(String classGuid)
	{
		String className = CLASSOBJECT_GUID_NAME_MAP.get(classGuid);
		return CLASSOBJECT_NAME_MAP.get(className);
	}

	protected ClassObject getClassObject(String className)
	{
		return CLASSOBJECT_NAME_MAP.get(className);
	}

	protected List<ClassField> listClassField(String className)
	{
		ClassObject classObject = this.getClassObject(className);
		return classObject == null ? null : classObject.getFieldList();
	}

	protected List<ClassAction> listClassAction(String classGuid)
	{
		ClassObject classObject = this.getClassObjectByGuid(classGuid);
		return classObject == null ? new ArrayList<ClassAction>() : classObject.getClassActionList();
	}

	protected Map<String, ClassObject> getClassObjectMap()
	{
		Map<String, ClassObject> map = new HashMap<>();
		for (ClassObject classObject : CLASSOBJECT_NAME_MAP.values())
		{
			map.put(classObject.getName(), classObject);
		}
		return map;
	}

	protected boolean hasInterface(String className, ModelInterfaceEnum interfaceEnum)
	{
		ClassObject classObject = this.getClassObject(className);
		return classObject != null && classObject.hasInterface(interfaceEnum);
	}

	protected ClassInfo getClassInfoByName(String className)
	{
		ClassObject classObject = this.getClassObject(className);
		return classObject == null ? null : classObject.getInfo();
	}

	protected ClassInfo getClassInfo(String classGuid)
	{
		ClassObject classObject = this.getClassObjectByGuid(classGuid);
		return classObject == null ? null : classObject.getInfo();
	}

	protected List<NumberingModelInfo> getNumberingModelInfoList(String className)
	{
		ClassObject classObject = this.getClassObject(className);
		List<NumberingModel> numberingModelList = classObject == null ? null : classObject.getNumberingModelList();
		if (numberingModelList != null)
		{
			return numberingModelList.stream().map(NumberingModel::getModelInfo).collect(Collectors.toList());
		}
		return null;
	}

	protected List<NumberingObjectInfo> listChildNumberingObjectInfo(String numberModelGuid, String numberObjectGuid)
	{
		if (StringUtils.isGuid(numberObjectGuid))
		{
			NumberingObject numberingObject = NUMBERING_OBJECT_GUID_MAP.get(numberObjectGuid);
			List<NumberingObject> numberingObjectList = numberingObject.getChildObject();
			return SetUtils.isNullList(numberingObjectList) ? null : numberingObjectList.stream().map(NumberingObject::getInfo).collect(Collectors.toList());
		}
		else if (StringUtils.isGuid(numberModelGuid))
		{
			NumberingModel numberingModel = NUMBERING_MODEL_GUID_MAP.get(numberModelGuid);
			List<NumberingObject> numberingObjectList = numberingModel.getNumberingObjectList();
			return SetUtils.isNullList(numberingObjectList) ? null : numberingObjectList.stream().map(NumberingObject::getInfo).collect(Collectors.toList());
		}
		return null;
	}

	protected ClassField getField(String className, String fieldName)
	{
		ClassObject classObject = this.getClassObject(className);
		return classObject == null ? null : classObject.getField(fieldName);
	}

	protected List<ClassInfo> listAllClass()
	{
		Map<String, ClassInfo> tempMap = new TreeMap<>();

		Map<String, ClassObject> classObjectMap = this.getClassObjectMap();
		if (classObjectMap != null)
		{
			Set<Map.Entry<String, ClassObject>> classObjectSet = classObjectMap.entrySet();

			for (Map.Entry<String, ClassObject> classItem : classObjectSet)
			{
				tempMap.put(classItem.getKey(), classItem.getValue().getInfo());
			}
		}
		return new ArrayList<>(tempMap.values());
	}

	private void relaodModelBaseInfo()
	{
		DataServer.getSystemDataService().reloadAllCache(ClassInfo.class);
		DataServer.getSystemDataService().reloadAllCache(ClassField.class);
	}

	private void clearAllCache()
	{
		CLASSOBJECT_NAME_MAP.clear();
		CLASSOBJECT_GUID_NAME_MAP.clear();
		CLASSOBJECT_INTERFACE_NAME_MAP.clear();
		CLASS_BASE_TABLE_MAP_KEY_NAME.clear();
		CLASS_BASE_TABLE_MAP_KEY_GUID.clear();
		FIELD_TABLE_INDEX_MAP_KEY_NAME.clear();
		FIELD_TABLE_INDEX_MAP_KEY_GUID.clear();
	}

	private void buildUIModel(ClassObject classObject)
	{
		String classGuid = classObject.getGuid();
		List<UIObjectInfo> uiObjectList = DataServer.getSystemDataService().listFromCache(UIObjectInfo.class, new FieldValueEqualsFilter<>(UIObjectInfo.CLASSFK, classGuid));
		if (!SetUtils.isNullList(uiObjectList))
		{
			for (UIObjectInfo uiObjectInfo : uiObjectList)
			{
				UIObject uiObject = new UIObject(uiObjectInfo);
				classObject.addUIObject(uiObject);
				List<UIField> uiFieldList = DataServer.getSystemDataService().listFromCache(UIField.class, new FieldValueEqualsFilter<>(UIField.UIGUID, uiObjectInfo.getGuid()));
				uiObject.setFieldList(uiFieldList);
				if (uiFieldList != null)
				{
					for (UIField uiField : uiFieldList)
					{
						uiField.setClassGuid(uiObject.getClassGuid());
						if (!uiField.isSeparator())
						{
							String className = CLASSOBJECT_GUID_NAME_MAP.get(uiObject.getClassGuid());
							ClassField field = CLASSOBJECT_NAME_MAP.get(className).getField(uiField.getName());
							if (field != null)
							{
								uiField.setType(CLASSOBJECT_NAME_MAP.get(className).getField(uiField.getName()).getType());
							}
						}
					}
				}
				List<UIAction> uiActionList = DataServer.getSystemDataService().listFromCache(UIAction.class,
						new FieldValueEqualsFilter<>(UIAction.UIGUID, uiObjectInfo.getGuid()));
				uiObject.setActionList(uiActionList);
				UIOBJECT_GUID_MAP.put(uiObjectInfo.getGuid(), uiObject);
			}
		}

		UIIcon uiIcon = new UIIcon();
		uiIcon.setClassIcon(new IconEntry(null, classObject.getIcon(), classObject.getIcon32(), classObject.isShowPreview()));
	}

	private void loadBaseClassInfo()
	{
		List<ClassInfo> classInfoList = DataServer.getSystemDataService().listFromCache(ClassInfo.class, null);
		classInfoList.forEach(classInfo -> {
			ClassObject classObject = this.makeClassObject(classInfo);
			CLASSOBJECT_GUID_NAME_MAP.put(classObject.getGuid(), classObject.getName());
			CLASSOBJECT_NAME_MAP.put(classObject.getName(), classObject);
		});
	}

	private ClassObject makeClassObject(ClassInfo classInfo)
	{
		ClassObject classObject = new ClassObject(classInfo);

		if (classObject.getInterfaces() != null)
		{
			String[] interfaceNames = StringUtils.splitString(classObject.getInterfaces());
			if (interfaceNames != null)
			{
				for (String interfaceName : interfaceNames)
				{
					classObject.addInterface(interfaceName);
				}
			}
		}

		if (!StringUtils.isNullString(classObject.getSuperInterface()))
		{
			String[] interfaceNames = StringUtils.splitString(classObject.getSuperInterface());
			if (interfaceNames != null)
			{
				for (String interfaceName : interfaceNames)
				{
					classObject.addInterface(interfaceName);
				}
			}
		}

		String classificationGuid = classObject.getClassification();
		if (StringUtils.isGuid(classificationGuid))
		{
			CodeObjectInfo codeObjectInfo = DataServer.getSystemDataService().get(CodeObjectInfo.class, classificationGuid);
			classObject.getInfo().setClassificationName(codeObjectInfo == null ? null : codeObjectInfo.getName());
		}

		String lifecycleGuid = classObject.getLifecycle();
		if (StringUtils.isGuid(lifecycleGuid))
		{
			LifecycleInfo lifecycleInfo = DataServer.getSystemDataService().get(LifecycleInfo.class, lifecycleGuid);
			classObject.getInfo().setLifecycleName(lifecycleInfo == null ? null : lifecycleInfo.getName());
		}

		return classObject;
	}

	private void buildCheckUnique(ClassObject classObject)
	{
		NumberingModel numberingModel = classObject.getNumberingModel(SystemClassFieldEnum.UNIQUES.getName());
		if (numberingModel != null)
		{
			List<NumberingObject> numberingObjectList = numberingModel.getNumberingObjectList();
			if (!SetUtils.isNullList(numberingObjectList))
			{
				classObject.setCheckUnique(true);
			}
		}
	}

	private void buildTableIndexInfo(ClassObject classObject)
	{
		if (classObject.isCreateTable())
		{
			CLASS_BASE_TABLE_MAP_KEY_GUID.put(classObject.getGuid(), classObject.getInfo().getRealBaseTableName());
			CLASS_BASE_TABLE_MAP_KEY_NAME.put(classObject.getName().toUpperCase(), classObject.getInfo().getRealBaseTableName());
			List<ClassField> fieldList = classObject.getFieldList();
			fieldList.forEach(field -> {
				FIELD_TABLE_INDEX_MAP_KEY_GUID.put(classObject.getGuid() + "." + field.getName().toUpperCase(), field.getTableIndex());
				FIELD_TABLE_INDEX_MAP_KEY_NAME.put(classObject.getName().toUpperCase() + "." + field.getName().toUpperCase(), field.getTableIndex());
			});
		}
	}

	private void buildNumberingModel(ClassObject classObject)
	{
		List<NumberingModel> numberingModelList = this.makeNumberingModel(classObject.getGuid());
		classObject.setNumberingModelList(numberingModelList);
	}

	private List<NumberingModel> makeNumberingModel(String classfk)
	{
		List<NumberingModel> numberingModelList = new ArrayList<>();

		List<NumberingModelInfo> numberingModelInfoList = DataServer.getSystemDataService().listFromCache(NumberingModelInfo.class,
				new FieldValueEqualsFilter<>(NumberingModelInfo.CLASSFK, classfk));
		if (numberingModelInfoList != null)
		{
			for (NumberingModelInfo numberingModelInfo : numberingModelInfoList)
			{
				NumberingModel numberingModel = new NumberingModel(numberingModelInfo);
				NUMBERING_MODEL_GUID_MAP.put(numberingModelInfo.getGuid(), numberingModel);
				numberingModel.setNumberingObjectList(this.makeNumberingObject(numberingModelInfo.getGuid()));
				numberingModelList.add(numberingModel);
			}
		}

		return numberingModelList;
	}

	private List<NumberingObject> makeNumberingObject(String numberrulefk)
	{
		List<NumberingObject> numberingObjectList = new ArrayList<>();

		List<NumberingObjectInfo> numberingObjectInfoList = DataServer.getSystemDataService().listFromCache(NumberingObjectInfo.class,
				new FieldValueEqualsFilter<>(NumberingObjectInfo.NUMBERRULEFK, numberrulefk));
		if (numberingObjectInfoList != null)
		{
			Map<String, NumberingObject> numberingObjectMap = new LinkedHashMap<>();
			for (NumberingObjectInfo numberingObjectInfo : numberingObjectInfoList)
			{
				NumberingObject numberingObject = new NumberingObject(numberingObjectInfo);
				NUMBERING_OBJECT_GUID_MAP.put(numberingObjectInfo.getGuid(), numberingObject);
				numberingObjectMap.put(numberingObjectInfo.getGuid(), numberingObject);
			}

			for (Entry<String, NumberingObject> objectEntry : numberingObjectMap.entrySet())
			{
				NumberingObject numberingObject = objectEntry.getValue();
				if (!StringUtils.isNullString(numberingObject.getParentGuid()))
				{
					NumberingObject numberingObjectParent = numberingObjectMap.get(numberingObject.getParentGuid());
					if (numberingObjectParent != null)
					{
						numberingObjectParent.addChildObject(numberingObject);
					}
					else
					{
						numberingObjectList.add(numberingObject);
					}
				}
				else
				{
					numberingObjectList.add(numberingObject);
				}
			}
		}
		sortNumberObject(numberingObjectList);
		return numberingObjectList;
	}

	private void sortNumberObject(List<NumberingObject> numberingObjectList)
	{
		if (numberingObjectList != null)
		{
			Collections.sort(numberingObjectList, Comparator.comparing(NumberingObject::getSequence));
			for (NumberingObject info : numberingObjectList)
			{
				sortNumberObject(info.getChildObject());
			}
		}

	}

	private void makeClassField(ClassObject classObject)
	{
		if (classObject.getTableIndexList() == null)
		{
			classObject.setTableIndexList(new ArrayList<>());
		}

		List<ClassField> classFieldList = DataServer.getSystemDataService().listFromCache(ClassField.class,
				new FieldValueEqualsFilter<>(ClassField.CLASSGUID, classObject.getGuid()));
		classObject.setFieldList(classFieldList);
		if (classFieldList != null)
		{
			classFieldList.forEach(field -> {
				String tableIndex = field.getTableIndex();
				if (!StringUtils.isNullString(tableIndex) && !classObject.getTableIndexList().contains(Integer.parseInt(tableIndex)))
				{
					classObject.getTableIndexList().add(Integer.parseInt(tableIndex));
				}
				if (field.getType() == FieldTypeEnum.CODEREF && !StringUtils.isNullString(field.getCoderefField()))
				{
					field.setCoderefField(field.getCoderefField());
					field.setReferenceCodeList(this.makeRefCode(field.getGuid(), field.getCoderefField()));
				}
			});
		}
	}

	private List<ReferenceCode> makeRefCode(String fieldGuid, String relationFields)
	{
		String[] relationFieldArray = StringUtils.splitString(relationFields);
		List<ReferenceCode> rcList = new ArrayList<>();
		List<ClassFieldCRDetail> queryForList = DataServer.getSystemDataService().listFromCache(ClassFieldCRDetail.class, new FieldValueEqualsFilter<>("FIELDFK", fieldGuid));
		if (queryForList != null)
		{
			for (ClassFieldCRDetail objectMap : queryForList)
			{
				ReferenceCode rc = new ReferenceCode();
				rc.setCodeName(DataServer.getCodeModelService().getCodeObjectByGuid(objectMap.getCode()).getName());

				String[] itemNames = StringUtils.splitStringWithDelimiter(";", objectMap.getValue());
				List<ReferenceField> fieldlist = new ArrayList<>();
				rc.setRefFieldList(fieldlist);
				for (int i = 0; i < relationFieldArray.length; i++)
				{
					if (itemNames == null || itemNames.length < i)
					{
						continue;
					}

					ReferenceField rf = new ReferenceField();
					rf.setFieldName(relationFieldArray[i]);
					rf.setItemName(itemNames[i]);
					fieldlist.add(rf);
				}

				rcList.add(rc);
			}
		}
		return rcList;
	}

	private void addAllSubClass(List<ClassInfo> subClassList, ClassObject classObject)
	{
		List<ClassObject> childList = classObject.getChildList();
		if (SetUtils.isNullList(childList))
		{
			return;
		}

		String subClassName;
		ClassInfo subClassInfo;
		for (ClassObject subClassObject : childList)
		{
			subClassName = subClassObject.getName();
			subClassInfo = this.getClassObject(subClassName).getInfo();

			if (!subClassList.contains(subClassInfo))
			{
				subClassList.add(subClassInfo);
				this.addAllSubClass(subClassList, subClassObject);
			}
		}
	}

	private void addAllSuperClass(List<ClassInfo> superClassList, ClassObject classObject)
	{
		String superClassGuid = classObject.getSuperClassGuid();

		if (!StringUtils.isGuid(superClassGuid))
		{
			return;
		}

		ClassObject superClassObject = this.getClassObjectByGuid(superClassGuid);
		Optional<ClassInfo> optional = superClassList.stream().filter(classInfo -> classInfo.getName().equals(superClassObject.getName())).findAny();
		if (!optional.isPresent())
		{
			superClassList.add(superClassObject.getInfo());
			this.addAllSuperClass(superClassList, superClassObject);
		}
	}

	private void setCodeRefrenceValueGuid(ClassObject classObject)
	{
		for (ClassField classfield : classObject.getFieldList())
		{
			if (!SetUtils.isNullList(classfield.getReferenceCodeList()))
			{
				for (ReferenceCode rfc : classfield.getReferenceCodeList())
				{
					if (!SetUtils.isNullList(rfc.getRefFieldList()))
					{
						for (ReferenceField rf : rfc.getRefFieldList())
						{
							if (!StringUtils.isNullString(rf.getFieldName()) && !StringUtils.isNullString(rf.getItemName()))
							{
								ClassField realationField = classObject.getField(rf.getFieldName());
								if (realationField != null && (realationField.getType() == FieldTypeEnum.CODE || realationField.getType() == FieldTypeEnum.CLASSIFICATION)
										&& !StringUtils.isNullString(realationField.getTypeValue()))
								{
									CodeItem codeitem = DataServer.getCodeModelService().getCodeItem(realationField.getTypeValue(), rf.getItemName());
									if (codeitem != null)
									{
										rf.setCodeItemGuid(codeitem.getGuid());
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private boolean isChild(String child, String parent, Map<String, ClassObject> classObjectMap)
	{
		return this.isChild(classObjectMap.get(child), classObjectMap.get(parent), classObjectMap);
	}

	private boolean isChild(ClassObject child, ClassObject parent, Map<String, ClassObject> classObjectMap)
	{
		ClassObject parent_ = classObjectMap.get(child.getSuperclass());
		if (parent_ == null)
		{
			return false;
		}
		if (!parent_.getName().equals(parent.getName()))
		{
			return this.isChild(parent_, parent, classObjectMap);
		}
		return true;
	}

	class ClassModelCacheInfo extends AbstractCacheInfo
	{
		/**
		 *
		 */
		private static final long serialVersionUID = -6833130955473254511L;

		public <T extends SystemObject> void refreshCache(T data, String type) throws ServiceRequestException
		{
			ClassInfo classInfo = this.refresh(data, type);
			if (classInfo != null)
			{
				ClassObject classObject = CLASSOBJECT_NAME_MAP.get(classInfo.getName());

				List<String> searchedClassNameList = new ArrayList<>();
				processInheritedRelation(searchedClassNameList, classObject, CLASSOBJECT_NAME_MAP);
			}
		}

		private <T extends SystemObject> ClassInfo refresh(T data, String type) throws ServiceRequestException
		{
			ClassInfo classObject = null;
			if (CacheConstants.CHANGE_TYPE.UPDATE.equals(type))
			{
				classObject = this.update(data);
			}

			return classObject;
		}

		private <T extends SystemObject> ClassInfo update(T data) throws ServiceRequestException
		{
			ClassInfo classInfo = null;
			if (data instanceof ClassInfo)
			{
				ClassObject classObject = CLASSOBJECT_NAME_MAP.get(data.getName());
				if (classObject != null)
				{
					classObject.getInfo().putAll(classInfo);
				}
			}
			return classInfo;
		}

		@Override
		public void register()
		{
			DynaObserverMediator mediator = DynaObserverMediator.getInstance();
			mediator.register(ClassInfo.class, new DynaCacheObserver<>(this, new CacheRefreshListener<ClassInfo>()));
			mediator.register(ClassField.class, new DynaCacheObserver<>(this, new CacheRefreshListener<ClassField>()));
			mediator.register(UIObjectInfo.class, new DynaCacheObserver<>(this, new CacheRefreshListener<UIObjectInfo>()));
			mediator.register(UIField.class, new DynaCacheObserver<>(this, new CacheRefreshListener<UIField>()));
			mediator.register(NumberingModelInfo.class, new DynaCacheObserver<>(this, new CacheRefreshListener<NumberingModelInfo>()));
			mediator.register(NumberingObjectInfo.class, new DynaCacheObserver<>(this, new CacheRefreshListener<NumberingObjectInfo>()));
			mediator.register(ClassAction.class, new DynaCacheObserver<>(this, new CacheRefreshListener<ClassAction>()));
		}

		@Override
		public <T extends SystemObject> void addToCache(T data) throws ServiceRequestException
		{
			if (data instanceof ClassInfo)
			{
				ClassInfo classInfo = (ClassInfo) data;
				throw new ServiceRequestException("Illegally Add/Delete ClassInfo.Name=" + classInfo.getName());
			}
			if (data instanceof ClassField)
			{
				ClassField field = (ClassField) data;
				String className = CLASSOBJECT_GUID_NAME_MAP.get(field.getClassGuid());
				ClassObject classObject = CLASSOBJECT_NAME_MAP.get(className);
				if (classObject == null)
				{
					throw new ServiceRequestException("ClassInfo Not In Cache.Guid=" + field.getClassGuid());
				}
				CLASSFIELD_GUID_MAP.put(field.getGuid(), field);
				classObject.addField(field);
			}
			else if (data instanceof UIObjectInfo)
			{
				UIObjectInfo uiObjectInfo = (UIObjectInfo) data;
				String className = CLASSOBJECT_GUID_NAME_MAP.get(uiObjectInfo.getClassGuid());
				ClassObject classObject = CLASSOBJECT_NAME_MAP.get(className);
				if (classObject == null)
				{
					throw new ServiceRequestException("ClassInfo Not In Cache.Guid=" + uiObjectInfo.getClassGuid());
				}
				UIObject uiObject = new UIObject(uiObjectInfo);
				classObject.addUIObject(uiObject);
				UIOBJECT_GUID_MAP.put(uiObjectInfo.getGuid(), uiObject);
			}
			else if (data instanceof UIField)
			{
				UIField uiField = (UIField) data;
				UIObject uiObject = UIOBJECT_GUID_MAP.get(uiField.getUIGuid());
				if (uiObject == null)
				{
					throw new ServiceRequestException("UIObject Not In Cache.Guid=" + uiField.getUIGuid());
				}
				uiField.setClassGuid(uiObject.getClassGuid());
				if (!uiField.isSeparator())
				{
					String className = CLASSOBJECT_GUID_NAME_MAP.get(uiObject.getClassGuid());

					ClassField classField = getClassField(uiObject.getClassGuid(), uiField.getName());
					if (classField == null)
					{
						throw new ServiceRequestException("classField not fount:className " + className + ";FieldName " + uiField.getName());
					}

					uiField.setType(classField.getType());
				}
				uiObject.addField(uiField);
				UIFIELD_GUID_MAP.put(uiField.getGuid(), uiField);
			}
			else if (data instanceof NumberingModelInfo)
			{
				NumberingModelInfo modelInfo = (NumberingModelInfo) data;
				String className = CLASSOBJECT_GUID_NAME_MAP.get(modelInfo.getClassGuid());
				ClassObject classObject = CLASSOBJECT_NAME_MAP.get(className);
				buildNumberingModel(classObject);
			}
			else if (data instanceof NumberingObjectInfo)
			{
				NumberingObjectInfo objectInfo = (NumberingObjectInfo) data;
				NumberingModel modelInfo = NUMBERING_MODEL_GUID_MAP.get(objectInfo.getNumberRuleFK());
				String className = CLASSOBJECT_GUID_NAME_MAP.get(modelInfo.getModelInfo().getClassGuid());
				ClassObject classObject = CLASSOBJECT_NAME_MAP.get(className);
				buildNumberingModel(classObject);
			}
			else if (data instanceof ClassAction)
			{
				ClassAction action = (ClassAction) data;
				action.setClassName(CLASSOBJECT_GUID_NAME_MAP.get(action.getClassfk()));

				String parentGuid = action.getParentGuid();
				if (StringUtils.isGuid(parentGuid))
				{
					SCRIPT_ACTION_GUID_MAP.get(parentGuid).addScript(action);
				}
				SCRIPT_ACTION_GUID_MAP.put(action.getGuid(), action);
				String className = CLASSOBJECT_GUID_NAME_MAP.get(action.getClassfk());
				CLASSOBJECT_NAME_MAP.get(className).addClassAction(action);
			}
		}

		private ClassField getClassField(String classGuid, String fieldName)
		{
			String className = CLASSOBJECT_GUID_NAME_MAP.get(classGuid);
			ClassObject classObject = CLASSOBJECT_NAME_MAP.get(className);
			ClassField classField = classObject.getField(fieldName);
			if (classField == null && classObject.getInfo().getSuperClassGuid() != null)
			{
				return getClassField(classObject.getInfo().getSuperClassGuid(), fieldName);
			}
			return classField;
		}

		@Override
		public <T extends SystemObject> void removeFromCache(T data) throws ServiceRequestException
		{
			if (data instanceof ClassInfo)
			{
				ClassInfo classInfo = (ClassInfo) data;
				throw new ServiceRequestException("Illegally Add/Delete ClassInfo.Name=" + classInfo.getName());
			}
			if (data instanceof ClassField)
			{
				ClassField field = (ClassField) data;
				String className = CLASSOBJECT_GUID_NAME_MAP.get(field.getClassGuid());
				ClassObject classObject = CLASSOBJECT_NAME_MAP.get(className);
				if (classObject == null)
				{
					if (this.isRemoveErrData() == false)
					{
						throw new ServiceRequestException("ClassInfo Not In Cache.Guid=" + field.getClassGuid());
					}
				}
				else
				{
					classObject.removeField(CLASSFIELD_GUID_MAP.get(field.getGuid()));
					CLASSFIELD_GUID_MAP.remove(field.getGuid());
				}
			}
			else if (data instanceof UIObjectInfo)
			{
				UIObjectInfo uiObjectInfo = (UIObjectInfo) data;
				String classGuid = uiObjectInfo.getClassGuid();
				String className = CLASSOBJECT_GUID_NAME_MAP.get(classGuid);
				ClassObject classObject = CLASSOBJECT_NAME_MAP.get(className);
				if (classObject == null)
				{
					if (this.isRemoveErrData() == false)
					{
						throw new ServiceRequestException("ClassInfo Not In Cache.Guid=" + classGuid);
					}
				}
				else
				{
					UIObject uiObject = UIOBJECT_GUID_MAP.get(uiObjectInfo.getGuid());
					if (uiObject == null)
					{
						DynaLogger.error("UIObject Not In Cache.");
					}
					else
					{
						Iterator<UIObject> it = classObject.getUiObjectList().iterator();
						while (it.hasNext())
						{
							if (it.next().getInfo().getGuid().equals(uiObject.getInfo().getGuid()))
							{
								it.remove();
								break;
							}
						}

						UIOBJECT_GUID_MAP.remove(uiObject.getGuid());
					}
				}
			}
			else if (data instanceof UIField)
			{
				UIField uiField = (UIField) data;
				UIObject uiObject = UIOBJECT_GUID_MAP.get(uiField.getUIGuid());
				if (uiObject == null)
				{
					if (this.isRemoveErrData() == false)
					{
						throw new ServiceRequestException("UIObject Not In Cache.Guid=" + uiField.getUIGuid());
					}
				}
				else
				{
					uiObject.getFieldList().remove(uiField);
					UIFIELD_GUID_MAP.remove(uiField.getGuid());
				}
			}
			else if (data instanceof NumberingModelInfo)
			{
				NumberingModelInfo modelInfo = (NumberingModelInfo) data;
				String className = CLASSOBJECT_GUID_NAME_MAP.get(modelInfo.getClassGuid());
				ClassObject classObject = CLASSOBJECT_NAME_MAP.get(className);
				if (classObject == null)
				{
					if (this.isRemoveErrData() == false)
					{
						throw new ServiceRequestException("ClassInfo Not In Cache.Guid=" + modelInfo.getClassGuid());
					}
				}
				else
				{
					NUMBERING_MODEL_GUID_MAP.remove(modelInfo.getGuid());
					Iterator<String> iterator = NUMBERING_OBJECT_GUID_MAP.keySet().iterator();
					while (iterator.hasNext())
					{
						NumberingObject numberingObect = NUMBERING_OBJECT_GUID_MAP.get(iterator.next());
						if (numberingObect.getNumberRuleFK().equals(modelInfo.getGuid()))
						{
							iterator.remove();
						}
					}
					buildNumberingModel(classObject);
				}
			}
			else if (data instanceof NumberingObjectInfo)
			{
				NumberingObjectInfo objectInfo = (NumberingObjectInfo) data;
				NumberingModel modelInfo = NUMBERING_MODEL_GUID_MAP.get(objectInfo.getNumberRuleFK());
				if (modelInfo != null)
				{
					modelInfo.setNumberingObjectList(null);
					Iterator<String> iterator = NUMBERING_OBJECT_GUID_MAP.keySet().iterator();
					while (iterator.hasNext())
					{
						NumberingObject numberingObect = NUMBERING_OBJECT_GUID_MAP.get(iterator.next());
						if (numberingObect.getNumberRuleFK().equals(objectInfo.getGuid()))
						{
							iterator.remove();
						}
					}
					String className = CLASSOBJECT_GUID_NAME_MAP.get(modelInfo.getModelInfo().getClassGuid());
					ClassObject classObject = CLASSOBJECT_NAME_MAP.get(className);
					if (classObject == null)
					{
						if (this.isRemoveErrData() == false)
						{
							throw new ServiceRequestException("ClassInfo Not In Cache.Guid=" + modelInfo.getClassGuid());
						}
					}
					else
					{
						buildNumberingModel(classObject);
					}
				}
			}
			else if (data instanceof ClassAction)
			{
				ClassAction action = (ClassAction) data;
				String parentGuid = action.getParentGuid();
				if (StringUtils.isGuid(parentGuid))
				{
					ClassAction parent = SCRIPT_ACTION_GUID_MAP.get(parentGuid);
					if (parent != null && parent.getChildren() != null)
					{
						Iterator<Script> it = parent.getChildren().iterator();
						while (it.hasNext())
						{
							ClassAction childAction = (ClassAction) it.next();
							if (childAction.getGuid().equals(action.getGuid()))
							{
								it.remove();
								break;
							}
						}
					}

				}
				SCRIPT_ACTION_GUID_MAP.remove(action.getGuid());
				String className = CLASSOBJECT_GUID_NAME_MAP.get(action.getClassfk());
				action.setClassName(className);
				ClassObject classObject = CLASSOBJECT_NAME_MAP.get(className);
				if (classObject == null)
				{
					if (this.isRemoveErrData() == false)
					{
						throw new ServiceRequestException("ClassInfo Not In Cache.Guid=" + action.getClassfk());
					}
				}
				else
				{
					CLASSOBJECT_NAME_MAP.get(className).getClassActionList().remove(action);
				}
			}
		}

		@Override
		public <T extends SystemObject> void updateToCache(T data) throws ServiceRequestException
		{
			if (data instanceof ClassInfo)
			{
				ClassInfo classInfo = (ClassInfo) data;
				ClassObject classObject = CLASSOBJECT_NAME_MAP.get(classInfo.getName());
				if (classObject == null)
				{
					throw new ServiceRequestException("ClassInfo Not In Cache.Guid=" + classInfo.getGuid());
				}
				classObject.getInfo().putAll(classInfo);

				List<BOInfo> boInfoList = DataServer.getSystemDataService().listFromCache(BOInfo.class, new FieldValueEqualsFilter<BOInfo>(BOInfo.CLASSGUID, classInfo.getGuid()));
				if (!SetUtils.isNullList(boInfoList))
				{
					boInfoList.forEach(boInfo -> {
						BusinessObject businessObject = DataServer.getBusinessModelService().getBusinessObjectByGuid(boInfo.getGuid());
						businessObject.setClassInfo(classInfo);
					});
				}
			}
			if (data instanceof ClassField)
			{
				ClassField field = (ClassField) data;
				String className = CLASSOBJECT_GUID_NAME_MAP.get(field.getClassGuid());
				ClassObject classObject = CLASSOBJECT_NAME_MAP.get(className);
				if (classObject == null)
				{
					throw new ServiceRequestException("ClassInfo Not In Cache.Guid=" + field.getClassGuid());
				}
				CLASSFIELD_GUID_MAP.get(field.getGuid()).putAll(field);
			}
			else if (data instanceof UIObjectInfo)
			{
				UIObjectInfo uiObjectInfo = (UIObjectInfo) data;
				String classGuid = uiObjectInfo.getClassGuid();
				String className = CLASSOBJECT_GUID_NAME_MAP.get(classGuid);
				ClassObject classObject = CLASSOBJECT_NAME_MAP.get(className);
				if (classObject == null)
				{
					throw new ServiceRequestException("ClassInfo Not In Cache.Guid=" + classGuid);
				}

				UIObject uiObject = UIOBJECT_GUID_MAP.get(uiObjectInfo.getGuid());
				if (uiObject == null)
				{
					DynaLogger.error("UIObject Not In Cache.");
				}
				else
				{
					uiObject.getInfo().putAll(uiObjectInfo);
				}
			}
			else if (data instanceof UIField)
			{
				UIField uiField = (UIField) data;
				UIObject uiObject = UIOBJECT_GUID_MAP.get(uiField.getUIGuid());
				if (uiObject == null)
				{
					throw new ServiceRequestException("UIObject Not In Cache.Guid=" + uiField.getUIGuid());
				}
				UIField uiField_ = UIFIELD_GUID_MAP.get(uiField.getGuid());
				if (uiField_ == null)
				{
					throw new ServiceRequestException("UIField Not In Cache.Guid=" + uiField.getGuid());
				}
				else
				{
					uiField_.putAll(uiField);
				}
				uiField.setClassGuid(uiObject.getClassGuid());
				if (!uiField.isSeparator())
				{
					String className = CLASSOBJECT_GUID_NAME_MAP.get(uiObject.getClassGuid());

					ClassField classField = getClassField(uiObject.getClassGuid(), uiField.getName());
					if (classField == null)
					{
						throw new ServiceRequestException("classField not fount:className " + className + ";FieldName " + uiField.getName());
					}

					uiField.setType(classField.getType());
				}
			}
			else if (data instanceof NumberingModelInfo)
			{
				NumberingModelInfo modelInfo = (NumberingModelInfo) data;
				String className = CLASSOBJECT_GUID_NAME_MAP.get(modelInfo.getClassGuid());
				ClassObject classObject = CLASSOBJECT_NAME_MAP.get(className);
				buildNumberingModel(classObject);
			}
			else if (data instanceof NumberingObjectInfo)
			{
				NumberingObjectInfo objectInfo = (NumberingObjectInfo) data;
				NumberingModel modelInfo = NUMBERING_MODEL_GUID_MAP.get(objectInfo.getNumberRuleFK());
				modelInfo.setNumberingObjectList(null);
				Iterator<String> iterator = NUMBERING_OBJECT_GUID_MAP.keySet().iterator();
				while (iterator.hasNext())
				{
					NumberingObject numberingObect = NUMBERING_OBJECT_GUID_MAP.get(iterator.next());
					if (numberingObect.getNumberRuleFK().equals(objectInfo.getNumberRuleFK()))
					{
						iterator.remove();
					}
				}
				String className = CLASSOBJECT_GUID_NAME_MAP.get(modelInfo.getModelInfo().getClassGuid());
				ClassObject classObject = CLASSOBJECT_NAME_MAP.get(className);
				buildNumberingModel(classObject);
			}
		}
	}

	public UIObject getUIObject(String guid)
	{
		return this.UIOBJECT_GUID_MAP.get(guid);
	}
}