/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ClassStub
 * Wanglei 2010-8-11
 */
package dyna.app.service.brs.emm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.DataAccessService;
import dyna.common.FieldOrignTypeEnum;
import dyna.common.SearchCondition;
import dyna.common.bean.data.FoundationObjectImpl;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.foundation.ViewObject;
import dyna.common.bean.model.cls.NumberingModel;
import dyna.common.bean.model.cls.NumberingObject;
import dyna.common.dto.model.bmbo.BOInfo;
import dyna.common.dto.model.cls.ClassAction;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.cls.NumberingModelInfo;
import dyna.common.dto.model.cls.NumberingObjectInfo;
import dyna.common.dto.model.ui.UIField;
import dyna.common.exception.ServiceNotFoundException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.FieldTypeEnum;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.service.model.classmodel.ClassModelService;
import dyna.net.service.brs.EMM;

/**
 * 与class 相关的操作分支
 *
 * @author Wanglei
 */
public class ClassStub extends AbstractServiceStub<EMMImpl>
{

	public static final String FIELD_NAME_SYMBOL = ":";

	/**
	 * @param context
	 * @param service
	 */
	public ClassStub(ServiceContext context, EMMImpl service)
	{
		super(context, service);
	}

	public List<String> getClassNameListImplInterface(ModelInterfaceEnum interfaceEnum) throws ServiceRequestException
	{
		ClassModelService cs = this.stubService.getDomainServiceStub().getClassModelService();
		return cs.getClassNameListImplInterface(interfaceEnum);
	}

	protected ClassInfo getClassByGuid(String classGuid) throws ServiceRequestException
	{
		return this.getClassByGuid(classGuid, true);
	}

	public ClassInfo getClassByGuid(String classGuid, boolean isThrowException) throws ServiceRequestException
	{
		ClassModelService cs = this.stubService.getDomainServiceStub().getClassModelService();
		ClassInfo classInfo = cs.getClassInfo(classGuid);
		if (classInfo == null)
		{
			if (!isThrowException)
			{
				return null;
			}
			throw new ServiceRequestException("ID_APP_NO_FOUND_CALSS", "not found class object guid: " + classGuid, null, classGuid);
		}
		try
		{
			return this.getClassByName(classInfo.getName());
		}
		catch (ServiceRequestException e)
		{
			if (!isThrowException)
			{
				return null;
			}
			throw e;

		}
	}

	protected ClassInfo getClassByName(String classObjectName) throws ServiceRequestException
	{
		ClassModelService cs = this.stubService.getDomainServiceStub().getClassModelService();
		ClassInfo classInfo = cs.getClassInfoByName(classObjectName);
		if (classInfo == null)
		{
			throw new ServiceRequestException("ID_APP_NO_FOUND_CALSS", "not found class object name: " + classObjectName, null, classObjectName);
		}
		return classInfo;
	}

	public ClassInfo getClassInfo(String className, String classGuid) throws ServiceRequestException
	{
		ClassInfo classInfo = null;
		if (!StringUtils.isNullString(classGuid))
		{
			classInfo = this.getClassByGuid(classGuid);
		}

		if (classInfo != null)
		{
			return classInfo;
		}

		if (!StringUtils.isNullString(className))
		{
			classInfo = this.getClassByName(className);
		}

		return classInfo;
	}

	protected NumberingModelInfo getNumberingModelWithSynthetise(String classGuid, String className, String classFieldName) throws ServiceRequestException
	{
		ClassInfo classInfo = this.getClassInfo(className, classGuid);
		if (classInfo == null)
		{
			return null;
		}
		ClassModelService cs = this.stubService.getDomainServiceStub().getClassModelService();
		List<NumberingModelInfo> numberModelInfoList = cs.getNumberingModelInfoList(classInfo.getName());
		if (numberModelInfoList != null)
		{
			Optional<NumberingModelInfo> optional = numberModelInfoList.stream().filter(modelInfo -> modelInfo.getName().equals(classFieldName)).findAny();
			if (optional.isPresent())
			{
				return optional.get();
			}
		}
		return null;
	}

	protected NumberingModelInfo getNumberingModel(String classGuid, String className, String classFieldName) throws ServiceRequestException
	{
		NumberingModelInfo modelInfo = this.getNumberingModelWithSynthetise(classGuid, className, classFieldName);
		return modelInfo == null ? null : (modelInfo.isNumbering() ? modelInfo : null);
	}

	protected NumberingModel lookupNumberingModel(String classGuid, String className, String classFieldName) throws ServiceRequestException
	{
		ClassInfo classInfo = this.getClassInfo(className, classGuid);
		if (classInfo == null)
		{
			return null;
		}
		NumberingModelInfo modelInfo = null;
		while (classGuid != null)
		{
			modelInfo = this.getNumberingModel(classGuid, null, classFieldName);
			if (modelInfo != null)
			{
				return buildNumberModel(modelInfo);
			}
			else if (StringUtils.isGuid(classInfo.getSuperClassGuid()))
			{
				classInfo = this.getClassInfo(null, classInfo.getSuperClassGuid());
			}
			else
			{
				return null;
			}
		}
		return null;
	}

	protected NumberingModel lookupNumberingModel(String classGuid, String className, String classFieldName, boolean isContainSynthesis) throws ServiceRequestException
	{
		ClassInfo classInfo = this.getClassInfo(className, classGuid);
		if (classInfo == null)
		{
			return null;
		}

		NumberingModelInfo modelInfo = null;
		while (classInfo != null)
		{
			modelInfo = this.getNumberingModelWithSynthetise(classGuid, className, classFieldName);
			if (modelInfo != null)
			{
				return buildNumberModel(modelInfo);
			}
			else if (StringUtils.isGuid(classInfo.getSuperClassGuid()))
			{
				classInfo = this.getClassInfo(null, classInfo.getSuperClassGuid());
			}
			else
			{
				return null;
			}
		}
		return null;
	}

	protected List<NumberingModel> listNumberingModel(String classGuid, String className, boolean isNumbering) throws ServiceRequestException
	{
		List<NumberingModel> resutlList = new ArrayList<NumberingModel>();
		ClassInfo classInfo = this.getClassInfo(className, classGuid);
		if (classInfo == null)
		{
			return null;
		}

		ClassModelService cs = this.stubService.getDomainServiceStub().getClassModelService();
		List<NumberingModelInfo> numberModelInfoList = cs.getNumberingModelInfoList(classInfo.getName());

		Iterator<NumberingModelInfo> it = numberModelInfoList.iterator();
		while (it.hasNext())
		{
			NumberingModelInfo modelInfo = it.next();
			if (modelInfo.isNumbering() != isNumbering)
			{
				it.remove();
				continue;
			}

			NumberingModel model = buildNumberModel(modelInfo);
			resutlList.add(model);
		}

		return resutlList;
	}

	private NumberingModel buildNumberModel(NumberingModelInfo modelInfo) throws ServiceRequestException
	{
		NumberingModel model = new NumberingModel(modelInfo);
		List<NumberingObject> numberObjectList = new ArrayList<NumberingObject>();
		List<NumberingObjectInfo> firstLevelList = this.listNumberingObjectInfo(modelInfo.getGuid(), null);
		if (!SetUtils.isNullList(firstLevelList))
		{
			for (NumberingObjectInfo numberObjectInfo : firstLevelList)
			{
				numberObjectList.add(this.buildNumberobject(numberObjectInfo));
			}
		}
		model.setNumberingObjectList(numberObjectList);
		return model;
	}

	private NumberingObject buildNumberobject(NumberingObjectInfo numberObjectInfo) throws ServiceRequestException
	{
		NumberingObject result = new NumberingObject(numberObjectInfo);
		List<NumberingObject> childNumberObjectList = new ArrayList<NumberingObject>();
		List<NumberingObjectInfo> firstLevelList = this.listNumberingObjectInfo(numberObjectInfo.getNumberRuleFK(), numberObjectInfo.getGuid());
		if (!SetUtils.isNullList(firstLevelList))
		{
			for (NumberingObjectInfo subObjectInfo : firstLevelList)
			{
				childNumberObjectList.add(this.buildNumberobject(subObjectInfo));
			}
		}
		result.setChildObject(childNumberObjectList);
		return result;
	}

	protected List<NumberingObjectInfo> listNumberingObjectInfo(String modelGuid, String childGuid) throws ServiceRequestException
	{
		ClassModelService cs = this.stubService.getDomainServiceStub().getClassModelService();
		return cs.listChildNumberingObjectInfo(modelGuid, childGuid);
	}

	private ClassInfo getSuperClass(String className) throws ServiceRequestException
	{
		ClassInfo classObject = getClassByName(className);
		String superClassName = classObject.getSuperclass();
		return this.getClassByName(superClassName);
	}

	protected boolean isSuperClass(String classNameSub, String classNameSuper) throws ServiceRequestException
	{
		List<ClassInfo> classInfoList = this.listAllSuperClass(classNameSub, null);
		if (SetUtils.isNullList(classInfoList))
		{
			return false;
		}

		for (ClassInfo classInfo : classInfoList)
		{
			if (classNameSuper.equals(classInfo.getName()))
			{
				return true;
			}
		}
		return false;
	}

	protected List<ClassInfo> listAllClass(ModelInterfaceEnum removeInterface) throws ServiceRequestException
	{
		ClassModelService cs = this.stubService.getDomainServiceStub().getClassModelService();
		List<ClassInfo> classInfoList = cs.listAllClass();
		if (removeInterface == null)
		{
			return classInfoList;
		}
		else
		{
			return classInfoList.stream().filter(classInfo -> !classInfo.hasInterface(removeInterface)).collect(Collectors.toList());
		}
	}

	protected List<ClassInfo> listAllClassObject() throws ServiceRequestException
	{
		return this.listAllClass(null);
	}

	protected List<ClassInfo> listAllSuperClass(String className, String classGuid) throws ServiceRequestException
	{
		if (!StringUtils.isGuid(classGuid))
		{
			ClassInfo classInfo = this.getClassByName(className);
			classGuid = classInfo.getGuid();
		}
		ClassModelService cs = this.stubService.getDomainServiceStub().getClassModelService();
		return cs.listAllParentClassInfo(classGuid);
	}

	public List<ClassInfo> listAllSubClassInfoOnlyLeaf(ModelInterfaceEnum interfaceEnum, String className) throws ServiceRequestException
	{
		List<ClassInfo> classInfoList = null;
		if (!StringUtils.isNullString(className))
		{
			classInfoList = new ArrayList<>();
			ClassInfo classInfo = this.getClassByName(className);
			listAllSubClassInfoOnlyLeaf(classInfoList, classInfo);
			if (SetUtils.isNullList(classInfoList))
			{
				if (!classInfo.isAbstract())
				{
					classInfoList.add(classInfo);
				}
			}
		}
		else if (interfaceEnum != null)
		{
			ClassModelService cs = this.stubService.getDomainServiceStub().getClassModelService();
			classInfoList = cs.getClassInfoListImplInterface(interfaceEnum);
			if (!SetUtils.isNullList(classInfoList))
			{
				List<ClassInfo> tempList = new ArrayList<>();
				for (ClassInfo classInfo : classInfoList)
				{
					List<ClassInfo> childList = cs.listChildren(classInfo.getGuid());
					if (SetUtils.isNullList(childList))
					{
						if (!classInfo.isAbstract() || "ViewObject".equalsIgnoreCase(classInfo.getName()))
						{
							tempList.add(classInfo);
						}
					}
				}
				if (!SetUtils.isNullList(tempList))
				{
					classInfoList.addAll(tempList);
				}
			}
		}
		return classInfoList;

	}

	private boolean listAllSubClassInfoOnlyLeaf(List<ClassInfo> retBoInfoList, ClassInfo classInfo) throws ServiceRequestException
	{
		ClassModelService cs = this.stubService.getDomainServiceStub().getClassModelService();
		List<ClassInfo> childList = cs.listChildren(classInfo.getGuid());
		if (SetUtils.isNullList(childList))
		{
			return false;
		}
		for (ClassInfo classNext : childList)
		{
			boolean hasNext = listAllSubClassInfoOnlyLeaf(retBoInfoList, classNext);
			if (!hasNext)
			{
				retBoInfoList.add(classNext);
			}
		}
		return true;
	}

	protected List<ClassInfo> listClassByInterface(ModelInterfaceEnum interfaceEnum) throws ServiceRequestException
	{
		return this.stubService.getDomainServiceStub().getClassModelService().getClassInfoListImplInterface(interfaceEnum);
	}

	protected List<ClassAction> listClassAction(String classGuid) throws ServiceRequestException
	{
		ClassInfo classInfo = this.getClassByGuid(classGuid);
		if (classInfo != null)
		{
			return this.stubService.getDomainServiceStub().getClassModelService().listClassAction(classGuid);
		}
		return null;
	}

	protected List<ClassInfo> listSubClass(String className, String classGuid, boolean cascade, String removeClassName, ModelInterfaceEnum removeInterface)
			throws ServiceRequestException
	{
		List<ClassInfo> retList = new ArrayList<>();

		if (!StringUtils.isGuid(classGuid))
		{
			ClassInfo classInfo = this.getClassByName(className);
			classGuid = classInfo.getGuid();
		}
		if (cascade)
		{
			List<ClassInfo> childList = this.stubService.getDomainServiceStub().getClassModelService().listAllSubClassInfo(classGuid);
			if (childList != null)
			{
				Iterator<ClassInfo> iterator = childList.iterator();
				while (iterator.hasNext())
				{
					ClassInfo classInfo_ = iterator.next();
					if (!StringUtils.isNullString(removeClassName) && classInfo_.getName().equals(removeClassName))
					{
						continue;
					}
					else if (removeInterface != null && classInfo_.hasInterface(removeInterface))
					{
						continue;
					}
					retList.add(classInfo_);
				}
			}
		}
		else
		{
			String subClassName;
			List<ClassInfo> childList = this.stubService.getDomainServiceStub().getClassModelService().listChildren(classGuid);
			if (!SetUtils.isNullList(childList))
			{
				for (ClassInfo subClassObject : childList)
				{
					subClassName = subClassObject.getName();
					// 剔除class名字为removeClassName的类
					if (subClassName.equals(removeClassName))
					{
						continue;
					}

					if (removeInterface != null && subClassObject.hasInterface(removeInterface))
					{
						continue;
					}

					retList.add(subClassObject);
				}
			}
		}
		return retList;
	}

	protected ClassInfo getFirstLevelClassByInterface(ModelInterfaceEnum interfaceType, List<String> excludeList) throws ServiceRequestException
	{
		List<ClassInfo> classOfInterfaceList = new ArrayList<>();
		List<ClassInfo> listClassByInterface = this.listClassByInterface(interfaceType);
		if (!SetUtils.isNullList(listClassByInterface))
		{
			for (ClassInfo classInfo : listClassByInterface)
			{
				if (hasInterface(classInfo, excludeList))
				{
					continue;
				}
				classOfInterfaceList.add(classInfo);
			}
		}

		if (!SetUtils.isNullList(classOfInterfaceList))
		{
			for (ClassInfo classInfo : classOfInterfaceList)
			{
				ClassInfo superClassInfo = this.getSuperClass(classInfo.getName());
				if (superClassInfo == null || !superClassInfo.hasInterface(interfaceType))
				{
					return classInfo;
				}
			}
		}

		return null;
	}

	private boolean hasInterface(ClassInfo classInfo, List<String> interfaceList)
	{
		if (SetUtils.isNullList(interfaceList))
		{
			return false;
		}
		for (String interfaceName : interfaceList)
		{
			if (classInfo.hasInterface(ModelInterfaceEnum.typeof(interfaceName)))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * 为保证ObjectGuid中的classGuid与className都有值、特定义该方法
	 * 如果该二字段中有一个有值，那么把另外一个的对应的值也获取到并赋予其值
	 *
	 * @param objectGuid
	 * @author caogc
	 */
	public static void decorateObjectGuid(ObjectGuid objectGuid, DataAccessService service) throws ServiceRequestException
	{
		if (objectGuid == null)
		{
			return;
		}

		try
		{
			EMM emm = service.getRefService(EMM.class);
			if (objectGuid.getClassGuid() != null && StringUtils.isNullString(objectGuid.getClassName()))
			{
				ClassInfo classInfo = emm.getClassByGuid(objectGuid.getClassGuid());
				if (classInfo == null)
				{
					throw new ServiceRequestException("ID_APP_NO_FOUND_CALSS", "class is not found: " + objectGuid.getClassGuid(), null, objectGuid.getClassGuid());
				}

				objectGuid.setClassName(classInfo.getName());
			}
			else if (StringUtils.isNullString(objectGuid.getClassGuid()) && objectGuid.getClassName() != null)
			{
				ClassInfo classInfo = emm.getClassByName(objectGuid.getClassName());
				if (classInfo == null)
				{
					throw new ServiceRequestException("ID_APP_NO_FOUND_CALSS", "class is not found: " + objectGuid.getClassName(), null, objectGuid.getClassName());
				}

				objectGuid.setClassGuid(classInfo.getGuid());
			}

			if (StringUtils.isNullString(objectGuid.getBizObjectGuid()))
			{
				BOInfo boInfo = ((EMMImpl) emm).getBMStub().getBizObject(service.getUserSignature().getLoginGroupBMGuid(), objectGuid.getClassGuid(), null);
				if (boInfo != null)
				{
					objectGuid.setBizObjectGuid(boInfo.getGuid());
				}
			}
		}
		catch (ServiceNotFoundException e)
		{
			throw new ServiceRequestException("", null, e);
		}
	}

	protected ClassField getFieldByName(String classObjectName, String fieldName, boolean isThrowException) throws ServiceRequestException
	{
		ClassField classField = this.stubService.getDomainServiceStub().getClassModelService().getField(classObjectName, fieldName);
		if (classField == null)
		{
			classField = this.stubService.getDomainServiceStub().getClassModelService().getField(classObjectName, fieldName + "$");
			if (isThrowException && classField == null)
			{
				throw new ServiceRequestException("ID_APP_NO_FOUND_CALSS_FIELD", "not found classField object: " + classObjectName + " " + fieldName, null, classObjectName,
						fieldName);
			}
		}

		return classField;
	}

	private boolean isUnusualObjectField(ClassField classField) throws ServiceRequestException
	{
		ClassInfo classInfo = this.stubService.getClassByName(classField.getTypeValue());
		//
		if (classInfo == null)
		{
			return false;
		}
		return classInfo.hasInterface(ModelInterfaceEnum.IUser) //
				|| classInfo.hasInterface(ModelInterfaceEnum.IGroup)
				// 2012-10-29项目管理变更，增加接口IPMRole，IPMCalendar
				|| classInfo.hasInterface(ModelInterfaceEnum.IPMRole)//
				|| classInfo.hasInterface(ModelInterfaceEnum.IPMCalendar);
	}

	protected List<ClassField> listFieldOfClass(String className) throws ServiceRequestException
	{
		if (className == null)
		{
			return null;
		}

		return this.stubService.getDomainServiceStub().getClassModelService().listClassField(className);
	}

	protected Set<String> getAllSuperClassGuidSet(String classGuid) throws ServiceRequestException
	{
		Set<String> returnSet = new HashSet<String>();
		returnSet.add(classGuid);
		List<ClassInfo> list = this.stubService.listAllSuperClass(null, classGuid);
		if (list != null)
		{
			for (ClassInfo info : list)
			{
				returnSet.add(info.getGuid());
			}
		}
		return returnSet;
	}

	protected Set<String> getObjectFieldNamesInSC(SearchCondition condition, boolean isObjectField) throws ServiceRequestException
	{
		if (condition.getObjectGuid() == null)
		{
			return null;
		}

		ClassStub.decorateObjectGuid(condition.getObjectGuid(), this.stubService);
		String className = condition.getObjectGuid().getClassName();

		List<String> classNameList = new ArrayList<>();
		if (StringUtils.isNullString(className))
		{
			List<String> classes = condition.getLimitClassList();
			if (!SetUtils.isNullList(classes))
			{
				classNameList.addAll(classes);
			}
		}
		else
		{
			classNameList.add(className);
		}

		// check result ui
		List<String> uiName = condition.listResultUINameList();

		// check result fields
		List<String> resultFields = condition.getResultFieldList();

		return this.getObjectFieldNamesInSC(classNameList, uiName, resultFields, isObjectField);
	}

	protected Set<String> getObjectFieldNamesInSC(String className, List<String> uiNameList, List<String> resultFields, boolean isObjectField) throws ServiceRequestException
	{
		List<String> classNameList = new ArrayList<>();
		classNameList.add(className);
		return this.getObjectFieldNamesInSC(classNameList, uiNameList, resultFields, isObjectField);
	}

	protected Set<String> getObjectFieldNamesInSC(String className, List<String> uiNameList, List<String> resultFields, boolean isObjectField, boolean isThrowWhenFieldNotExist)
			throws ServiceRequestException
	{
		if (resultFields == null)
		{
			resultFields = new ArrayList<>();
		}
		List<ClassField> fieldList = this.listFieldOfClass(className);
		if (!SetUtils.isNullList(fieldList))
		{
			for (ClassField classField : fieldList)
			{
				if (!resultFields.contains(classField.getName()))
				{
					resultFields.add(classField.getName());
				}
			}
		}

		Set<String> retSet = new HashSet<>();
		List<String> existFieldList = new ArrayList<>();
		if (!SetUtils.isNullList(uiNameList))
		{
			for (String uiName : uiNameList)
			{
				// check result ui
				if (!StringUtils.isNullString(uiName) && !StringUtils.isNullString(className))
				{
					try
					{
						List<UIField> uiFields = this.stubService.listUIFieldByUIObject(className, uiName);
						if (!SetUtils.isNullList(uiFields))
						{
							for (UIField uiField : uiFields)
							{
								if (uiField.getType() == FieldTypeEnum.OBJECT && isObjectField)
								{
									ClassField classField = this.getFieldByName(className, uiField.getName(), true);
									boolean isUserOrGroup = this.isUnusualObjectField(classField);

									if (!isUserOrGroup)
									{
										retSet.add(uiField.getName());
										existFieldList.add(uiField.getName().toUpperCase());
									}
									else
									{
										ClassInfo classInfo = this.stubService.getClassByName(classField.getTypeValue());
										retSet.add(uiField.getName() + FIELD_NAME_SYMBOL + classInfo.getName());
										existFieldList.add((uiField.getName() + FIELD_NAME_SYMBOL + classInfo.getName()).toUpperCase());
									}
								}
								else if ((uiField.getType() == FieldTypeEnum.CODE || //
										uiField.getType() == FieldTypeEnum.CLASSIFICATION || //
										uiField.getType() == FieldTypeEnum.MULTICODE || //
										uiField.getType() == FieldTypeEnum.CODEREF)//
										&& !isObjectField)
								{
									retSet.add(uiField.getName());
									existFieldList.add(uiField.getName().toUpperCase());
								}

							}
						}
					}
					catch (ServiceRequestException e)
					{
						DynaLogger.error("FieldStub.getObjectFieldNamesInSC", e);
					}
				}
			}
		}

		// check result fields
		if (!SetUtils.isNullList(resultFields))
		{
			for (String fieldName : resultFields)
			{
				if (fieldName.startsWith(ViewObject.PREFIX_END1) || fieldName.startsWith(ViewObject.PREFIX_END2))
				{
					continue;
				}

				if (StringUtils.isNullString(fieldName) || fieldName.startsWith("SEPARATOR$"))
				{
					continue;
				}
				try
				{
					ClassField field = null;
					if (fieldName.startsWith(FieldOrignTypeEnum.CLASSIFICATION + FoundationObjectImpl.separator))
					{
						continue;
					}
					else
					{
						if (fieldName.startsWith(FieldOrignTypeEnum.CLASS + FoundationObjectImpl.separator))
						{
							fieldName = fieldName.substring(6);
						}
						if (isThrowWhenFieldNotExist)
						{
							field = this.getFieldByName(className, fieldName, true);
						}
						else
						{
							if (className == null)
							{
								continue;
							}

							try
							{
								field = this.getFieldByName(className, fieldName, false);
							}
							catch (ServiceRequestException ignored)
							{
							}

							if (field == null)
							{
								continue;
							}
						}
					}

					if (field.getType() == FieldTypeEnum.OBJECT && isObjectField)
					{
						if (!existFieldList.contains(fieldName.toUpperCase()))
						{
							boolean isUserOrGroup = this.isUnusualObjectField(field);
							if (!isUserOrGroup)
							{
								retSet.add(field.getName());
								existFieldList.add(field.getName().toUpperCase());
							}
							else
							{
								ClassInfo classInfo = this.stubService.getClassByName(field.getTypeValue());
								retSet.add(field.getName() + FIELD_NAME_SYMBOL + classInfo.getName());
								existFieldList.add((field.getName() + FIELD_NAME_SYMBOL + classInfo.getName()).toUpperCase());
							}
						}
					}
					else if ((field.getType() == FieldTypeEnum.CODE || field.getType() == FieldTypeEnum.CLASSIFICATION || field.getType() == FieldTypeEnum.MULTICODE || //
							field.getType() == FieldTypeEnum.CODEREF) && !isObjectField)
					{
						if (!existFieldList.contains(fieldName.toUpperCase()))
						{
							existFieldList.add(fieldName.toUpperCase());
							retSet.add(fieldName);
						}
					}
				}
				catch (ServiceRequestException e)
				{
					DynaLogger.error("FieldStub.getObjectFieldNamesInSC", e);
				}
			}
		}
		return retSet;
	}

	protected Set<String> getObjectFieldNamesInSC(List<String> classNameList, List<String> uiNameList, List<String> resultFields, boolean isObjectField)
			throws ServiceRequestException
	{
		if (SetUtils.isNullList(classNameList))
		{
			return null;
		}

		Set<String> resultFieldList = new HashSet<>();
		for (String className : classNameList)
		{
			resultFieldList.addAll(this.getObjectFieldNamesInSC(className, uiNameList, resultFields, isObjectField, classNameList.size() == 1));
		}
		return resultFieldList;
	}

}
