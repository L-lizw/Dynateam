package dyna.app.service.brs.boas.numbering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dyna.app.service.DataAccessService;
import dyna.app.service.brs.boas.numbering.classification.AllocateParameter;
import dyna.app.service.brs.boas.numbering.classification.Strategy;
import dyna.app.service.brs.boas.numbering.classification.strategy.ExpandDateStrategy;
import dyna.app.service.brs.boas.numbering.classification.strategy.ExpandDateTimeStrategy;
import dyna.app.service.brs.boas.numbering.classification.strategy.ExpandFixedStrategy;
import dyna.app.service.brs.boas.numbering.classification.strategy.ExpandSerialStrategy;
import dyna.app.service.brs.boas.numbering.classification.strategy.FieldBooleanStrategy;
import dyna.app.service.brs.boas.numbering.classification.strategy.FieldCodeStrategy;
import dyna.app.service.brs.boas.numbering.classification.strategy.FieldDateStrategy;
import dyna.app.service.brs.boas.numbering.classification.strategy.FieldDateTimeStrategy;
import dyna.app.service.brs.boas.numbering.classification.strategy.FieldFloatStrategy;
import dyna.app.service.brs.boas.numbering.classification.strategy.FieldIntegerStrategy;
import dyna.app.service.brs.boas.numbering.classification.strategy.FieldMultiCodeStrategy;
import dyna.app.service.brs.boas.numbering.classification.strategy.FieldObjectStrategy;
import dyna.app.service.brs.boas.numbering.classification.strategy.FieldRefCodeStrategy;
import dyna.app.service.brs.boas.numbering.classification.strategy.FieldStringStrategy;
import dyna.app.service.brs.emm.ClassStub;
import dyna.app.service.brs.emm.EMMImpl;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.model.cls.NumberingModel;
import dyna.common.bean.model.cls.NumberingObject;
import dyna.common.bean.model.code.ClassficationFeatureItem;
import dyna.common.dto.cfm.ClassificationNumberField;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.ui.UIField;
import dyna.common.exception.ServiceNotFoundException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.FieldTypeEnum;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.common.systemenum.coding.CFMCodeRuleEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.net.service.brs.EMM;

public class ClassificationAllocate
{
	public static final String					TARGET_FOUNDATION	= "target$";
	private List<ClassficationFeatureItem>		allFeatureItemList	= null;
	private static final Map<Object, Strategy>	strategyMap			= new HashMap<>();
	static
	{
		strategyMap.put(CFMCodeRuleEnum.DATE, new ExpandDateStrategy());
		strategyMap.put(CFMCodeRuleEnum.DATETIME, new ExpandDateTimeStrategy());
		strategyMap.put(CFMCodeRuleEnum.FIXED, new ExpandFixedStrategy());
		strategyMap.put(CFMCodeRuleEnum.SERIAL, new ExpandSerialStrategy());

		strategyMap.put(FieldTypeEnum.BOOLEAN, new FieldBooleanStrategy());
		strategyMap.put(FieldTypeEnum.CODE, new FieldCodeStrategy());
		strategyMap.put(FieldTypeEnum.CLASSIFICATION, new FieldCodeStrategy());
		strategyMap.put(FieldTypeEnum.CODEREF, new FieldRefCodeStrategy());
		strategyMap.put(FieldTypeEnum.DATE, new FieldDateStrategy());
		strategyMap.put(FieldTypeEnum.DATETIME, new FieldDateTimeStrategy());
		strategyMap.put(FieldTypeEnum.FLOAT, new FieldFloatStrategy());
		strategyMap.put(FieldTypeEnum.INTEGER, new FieldIntegerStrategy());
		strategyMap.put(FieldTypeEnum.MULTICODE, new FieldMultiCodeStrategy());
		strategyMap.put(FieldTypeEnum.OBJECT, new FieldObjectStrategy());
		strategyMap.put(FieldTypeEnum.STRING, new FieldStringStrategy());
	}

	public List<ClassficationFeatureItem> getAllFeatureItemList()
	{
		return this.allFeatureItemList;
	}

	public Map<String, FoundationObject> getFoundationMap(FoundationObject foundationObject)
	{

		List<FoundationObject> restoreAllClassification = foundationObject.restoreAllClassification(false);
		Map<String, FoundationObject> classificationMap = new HashMap<>();
		classificationMap.put(ClassificationAllocate.TARGET_FOUNDATION, foundationObject);

		if (!SetUtils.isNullList(restoreAllClassification))
		{
			for (FoundationObject foundation : restoreAllClassification)
			{
				classificationMap.put(foundation.getClassificationGroupName(), foundation);
			}
		}

		return classificationMap;
	}

	public List<ClassficationFeatureItem> listFeatureItem(FoundationObject foundationObject, DataAccessService dataAccessService) throws ServiceRequestException
	{
		ObjectGuid objectGuid = foundationObject.getObjectGuid();
		String classGuid = objectGuid.getClassGuid();
		String className = objectGuid.getClassName();
		if (StringUtils.isNullString(className))
		{
			ClassStub.decorateObjectGuid(objectGuid, dataAccessService);
			className = objectGuid.getClassName();
		}
		EMM emm;

		try
		{
			emm = dataAccessService.getRefService(EMM.class);
		}
		catch (ServiceNotFoundException e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

		List<FoundationObject> restoreAllClassification = foundationObject.restoreAllClassification(false);
		List<String> classificationItemGuidList = new ArrayList<>();
		if (!SetUtils.isNullList(restoreAllClassification))
		{
			for (FoundationObject foundation : restoreAllClassification)
			{
				classificationItemGuidList.add(foundation.getClassificationGuid());
			}
		}
		if (StringUtils.isGuid(foundationObject.getClassificationGuid()) && !classificationItemGuidList.contains(foundationObject.getClassificationGuid()))
		{
			classificationItemGuidList.add(foundationObject.getClassificationGuid());
		}

		List<ClassficationFeatureItem> allItem = new ArrayList<>();
		List<String> itemNameList = new ArrayList<>();

		// 分类
		List<ClassficationFeatureItem> listAllFeatureItemByClassGuid = ((EMMImpl) emm).getClassificationStub().listAllSuperClassficationFeatureItem(classGuid,
				classificationItemGuidList);

		if (!SetUtils.isNullList(listAllFeatureItemByClassGuid))
		{
			allItem.addAll(listAllFeatureItemByClassGuid);
			for (ClassficationFeatureItem itemCLassification : listAllFeatureItemByClassGuid)
			{
				itemNameList.add(itemCLassification.getFieldName());
			}
		}

		// 建模器
		List<ClassField> classFieldList = DataServer.getClassModelService().listClassField(className);
		for (ClassField mfield : classFieldList)
		{
			if (!itemNameList.contains(mfield.getName()))
			{
				NumberingModel model = emm.lookupNumberingModel(classGuid, className, mfield.getName(), true);
				if (model == null)
				{
					continue;
				}
				ClassInfo classInfo = emm.getClassByGuid(model.getClassGuid());
				ClassficationFeatureItem item = this.makeFeatureItem(classInfo, model);
				if (classInfo != null && item != null)
				{
					allItem.add(item);
				}
			}
		}
		this.allFeatureItemList = allItem;
		return allItem;
	}

	public ClassficationFeatureItem makeFeatureItem(ClassInfo classInfo, NumberingModel model)
	{
		ClassficationFeatureItem item = null;
		if (model != null && !model.isNumbering())
		{
			item = new ClassficationFeatureItem();
			item.setNumbering(model.isNumbering());
			item.setName(model.getName());
			item.setFieldName(model.getName());
			item.setClassGuid(classInfo.getGuid());
			if (!SetUtils.isNullList(model.getNumberingObjectList()))
			{
				List<ClassificationNumberField> fieldList = new ArrayList<ClassificationNumberField>();
				item.setFieldList(fieldList);
				ClassificationNumberField field = null;
				for (NumberingObject numbering : model.getNumberingObjectList())
				{
					field = new ClassificationNumberField();
					field.setName(numbering.getName());
					if (StringUtils.isNullString(numbering.getFieldLength()))
					{
						field.put(ClassificationNumberField.FIELDLENGTH, null);
					}
					else
					{
						field.setFieldlength(Integer.valueOf(numbering.getFieldLength()));
					}

					field.setPrefix(numbering.getPrefix());
					field.setSuffix(numbering.getSuffix());
					field.setControlledNumberFieldGuid(numbering.getSerialField());
					field.setGuid(numbering.getGuid());
					field.setType(CFMCodeRuleEnum.typeValueOf(numbering.getFieldType()));
					field.setTypeValue(numbering.getTypevalue());

					if (numbering.getFieldTypeEnum() == CFMCodeRuleEnum.FIELD)
					{
						field.setFormClass(true);
						field.setFieldName(field.getName());
					}
					else
					{
						field.setFormClass(false);
					}
					fieldList.add(field);
				}
			}
		}

		return item;
	}

	public void allocateNumberItem(DataAccessService dataAccessService, ClassficationFeatureItem item, Map<String, FoundationObject> classificationMap,
			List<ClassficationFeatureItem> classficationFeatureItemList, boolean isCreate, boolean needSerial) throws ServiceRequestException
	{
		EMM emm;

		try
		{
			emm = dataAccessService.getRefService(EMM.class);
		}
		catch (ServiceNotFoundException e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

		if (item.isHasAllocate())
		{
			return;
		}

		String className;
		FoundationObject foundationObject = classificationMap.get(TARGET_FOUNDATION);
		if (foundationObject != null)
		{
			className = foundationObject.getObjectGuid().getClassName();
		}
		else
		{
			return;
		}

		if (isCreate)
		{
			item.setNeedAllocate(true);
		}

		// 2014 0409变更 id编码的规则：当id无值，则重新编码，id有值时，不重新编码；
		if (SystemClassFieldEnum.ID.getName().equalsIgnoreCase(item.getFieldName()))
		{
			if (StringUtils.isNullString(foundationObject.getId()))
			{
				item.setNeedAllocate(true);
			}
			else
			{
				item.setNeedAllocate(false);
				item.setFieldValue(foundationObject.getId());
				item.setHasAllocate(true);
				return;
			}
		}

		// 5、 参与编码的字段变了之后是否影响编号
		// 是否影响根据该编码/合成字段是否是frozen来判断：若该字段是frozen，那么不受影响，只合成一次。其他情况下，每次都重新合成
		UIField uiField = emm.getUIFieldByName(className, item.getFieldName());
		if (uiField != null && uiField.isFroze() && !isCreate)
		{
			Object object = foundationObject.get(uiField.getName());
			if (object != null && !StringUtils.isNullString(object.toString()))
			{
				item.setNeedAllocate(false);
				return;
			}
		}

		List<ClassificationNumberField> classificationNumberFieldList = item.getFieldList();

		StringBuilder mergeFieldValue = new StringBuilder();
		if (!SetUtils.isNullList(classificationNumberFieldList))
		{
			List<ClassificationNumberField> serialField = new ArrayList<>();
			for (ClassificationNumberField field : classificationNumberFieldList)
			{
				// 流水码最后计算
				if (field.getType() == CFMCodeRuleEnum.SERIAL)
				{
					serialField.add(field);
					continue;
				}

				if (!field.isHasAllocate())
				{
					this.allocateNumberField(dataAccessService, item, classificationMap, classficationFeatureItemList, classificationNumberFieldList, field, emm, isCreate);
				}
			}

			// 流水码最后计算
			// 其他码段未产生之前，无法得知此字段是否需要重新编码
			// 需要重新编码，则生成流水码
			if (item.isNeedAllocate())
			{
				for (ClassificationNumberField field : serialField)
				{
					if (needSerial)
					{
						this.allocateNumberField(dataAccessService, item, classificationMap, classficationFeatureItemList, classificationNumberFieldList, field, emm, isCreate);
					}
					else
					{
						if (field.getFieldlength() != null)
						{
							StringBuilder serial = new StringBuilder();
							for (int i = 0; i < field.getFieldlength(); i++)
							{
								serial.append("\t");
							}
							field.setNumberFieldLastValue(StringUtils.convertNULLtoString(field.getPrefix()) + serial + StringUtils.convertNULLtoString(field.getSuffix()));
						}
					}
				}
			}

			// List<String> fieldLastValueList = new ArrayList<String>();
			// List<String> fieldValueList = new ArrayList<String>();
			for (ClassificationNumberField field : classificationNumberFieldList)
			{
				mergeFieldValue.append(StringUtils.convertNULLtoString(field.getNumberFieldLastValue()));
			}
		}

		item.setHasAllocate(true);
		item.setFieldValue(mergeFieldValue.toString());
		if (item.isNeedAllocate())
		{
			foundationObject.put(item.getFieldName(), item.getFieldValue());
		}
		else
		{
			item.setFieldValue(StringUtils.convertNULLtoString(foundationObject.get(item.getFieldName())));
		}
	}

	public void allocateNumberField(DataAccessService dataAccessService, ClassficationFeatureItem item, Map<String, FoundationObject> classificationMap,
			List<ClassficationFeatureItem> classficationFeatureItemList, List<ClassificationNumberField> classificationNumberFieldList, ClassificationNumberField field, EMM emm,
			boolean isCreate) throws ServiceRequestException
	{
		ClassField numberclassField = null;
		if (field.isFormClass())
		{
			String className;
			FoundationObject foundationObject = classificationMap.get(TARGET_FOUNDATION);
			if (foundationObject != null)
			{
				ClassStub.decorateObjectGuid(foundationObject.getObjectGuid(), dataAccessService);
				className = foundationObject.getObjectGuid().getClassName();
			}
			else
			{
				return;
			}

			numberclassField = emm.getFieldByName(className, field.getFieldName(), true);
			// if (SystemClassFieldEnum.ID.getName().equals(item.getFieldName()))
			// {
			// if (CFMCodeRuleEnum.FIELD == field.getType())
			// {
			// Object val = foundationObject.get(field.getFieldName());
			// if (val == null || StringUtils.isNullString(val.toString()))
			// {
			// String uiFieldName = field.getFieldName();
			// UIField uiField_ = emm.getUIFieldByName(className, field.getFieldName());
			// if (uiField_ != null)
			// {
			// uiFieldName = uiField_.getTitle(dataAccessService.getUserSignature().getLanguageEnum());
			// }
			// throw new ServiceRequestException("ID_APP_FIELD_IS_NULL", "AllocateId error. field " + uiFieldName + "["
			// + field.getFieldName() + "]" + " is null", null,
			// uiFieldName);
			// }
			// }
			// }
		}
		else if (!field.isFormClass())
		{
			numberclassField = ((EMMImpl) emm).getClassificationStub().getClassificationField(item.getClassificationItemGuid(), field.getFieldName());
			if (numberclassField != null)
			{
				// if (SystemClassFieldEnum.ID.getName().equals(item.getFieldName()))
				// {
				// if (CFMCodeRuleEnum.FIELD == field.getType())
				// {
				// CodeItemInfo itemInfo = emm.getCodeItem(item.getClassificationItemGuid());
				// if (itemInfo == null)
				// {
				// throw new ServiceRequestException("ID_APP_CLASSIFICATION_EXCEPTIONG", null);
				// }
				// CodeObjectInfo codeObjectInfo = emm.getCode(itemInfo.getCodeGuid());
				// if (codeObjectInfo == null)
				// {
				// throw new ServiceRequestException("ID_APP_CLASSIFICATION_EXCEPTIONG", null);
				// }
				// }
				// }
			}
		}

		Strategy strategy = getStrategy(field, numberclassField);
		if (strategy != null)
		{
			AllocateParameter makeParameter = makeParameter(classificationMap, field, item, numberclassField, dataAccessService, this, classficationFeatureItemList,
					classificationNumberFieldList, isCreate);
			strategy.run(makeParameter);
		}
	}

	public void updateFieldMaxSerial(FoundationObject foundationObject, DataAccessService dataAccessService, String fieldName, int startValue) throws ServiceRequestException
	{
		EMM emm;
		try
		{
			emm = dataAccessService.getRefService(EMM.class);
		}
		catch (ServiceNotFoundException e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

		List<ClassficationFeatureItem> sllFeatureItemList = this.listFeatureItem(foundationObject, dataAccessService);
		Map<String, FoundationObject> classificationMap = this.getFoundationMap(foundationObject);

		if (!SetUtils.isNullList(sllFeatureItemList))
		{
			for (ClassficationFeatureItem item : sllFeatureItemList)
			{
				if (fieldName.equalsIgnoreCase(item.getFieldName()))
				{

					if (!SetUtils.isNullList(item.getFieldList()))
					{
						for (ClassificationNumberField field : item.getFieldList())
						{
							// 更新流水码
							if (field.getType() == CFMCodeRuleEnum.SERIAL)
							{
								field.setTypeValue(startValue + "");
								field.setUpdateSerial(false);
								this.allocateNumberField(dataAccessService, item, classificationMap, sllFeatureItemList, item.getFieldList(), field, emm, true);

							}
						}
					}
				}
			}
		}
	}

	@Deprecated
	public void updateIdSerial(FoundationObject foundationObject, DataAccessService dataAccessService, int startValue) throws ServiceRequestException
	{
		EMM emm;
		try
		{
			emm = dataAccessService.getRefService(EMM.class);
		}
		catch (ServiceNotFoundException e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

		List<ClassficationFeatureItem> sllFeatureItemList = this.listFeatureItem(foundationObject, dataAccessService);
		Map<String, FoundationObject> classificationMap = this.getFoundationMap(foundationObject);

		if (!SetUtils.isNullList(sllFeatureItemList))
		{
			for (ClassficationFeatureItem item : sllFeatureItemList)
			{
				if (SystemClassFieldEnum.ID.getName().equalsIgnoreCase(item.getFieldName()))
				{

					if (!SetUtils.isNullList(item.getFieldList()))
					{
						for (ClassificationNumberField field : item.getFieldList())
						{
							// 更新流水码
							if (field.getType() == CFMCodeRuleEnum.SERIAL)
							{
								field.setTypeValue(startValue + "");
								field.setUpdateSerial(false);
								this.allocateNumberField(dataAccessService, item, classificationMap, sllFeatureItemList, item.getFieldList(), field, emm, true);

							}
						}
					}
				}
			}
		}
	}

	public String startSingleNumberItem(FoundationObject foundationObject, DataAccessService dataAccessService, String fieldName, boolean needSerial) throws ServiceRequestException
	{
		if (StringUtils.isNullString(fieldName))
		{
			return null;
		}

		List<ClassficationFeatureItem> sllFeatureItemList = this.listFeatureItem(foundationObject, dataAccessService);
		Map<String, FoundationObject> classificationMap = this.getFoundationMap(foundationObject);

		if (!SetUtils.isNullList(sllFeatureItemList))
		{
			for (ClassficationFeatureItem item : sllFeatureItemList)
			{
				if (fieldName.equalsIgnoreCase(item.getFieldName()))
				{

					foundationObject.put("-" + fieldName + "ItemGuid", item.getGuid());
					foundationObject.put("-" + fieldName + "classificationItem", item.getClassificationItemGuid());

					this.allocateNumberItem(dataAccessService, item, classificationMap, sllFeatureItemList, false, needSerial);
					return item.getFieldValue();
				}
			}
		}
		return null;
	}

	public String start(FoundationObject foundationObject, DataAccessService dataAccessService, boolean isCreate) throws ServiceRequestException
	{

		List<ClassficationFeatureItem> sllFeatureItemList = this.listFeatureItem(foundationObject, dataAccessService);
		Map<String, FoundationObject> classificationMap = this.getFoundationMap(foundationObject);

		if (!SetUtils.isNullList(sllFeatureItemList))
		{
			for (ClassficationFeatureItem item : sllFeatureItemList)
			{

				if (!item.isHasAllocate())
				{
					this.allocateNumberItem(dataAccessService, item, classificationMap, sllFeatureItemList, isCreate, true);
				}
			}

			for (ClassficationFeatureItem item : sllFeatureItemList)
			{
				if (!item.isNeedAllocate())
				{
					continue;
				}

				foundationObject.put(item.getFieldName(), item.getFieldValue());
				item.clearAllocate();
			}

		}
		return "";
	}

	public static Strategy getStrategy(ClassificationNumberField field, ClassField classField)
	{
		if (classField != null)
		{
			return strategyMap.get(classField.getType());
		}
		else
		{
			return strategyMap.get(field.getType());
		}
	}

	public static AllocateParameter makeParameter(Map<String, FoundationObject> classificationMap, ClassificationNumberField field, ClassficationFeatureItem item,
			ClassField numberClassField, DataAccessService dataAccessService, ClassificationAllocate control, List<ClassficationFeatureItem> classficationFeatureItemList,
			List<ClassificationNumberField> classificationNumberFieldList, boolean isCreate)
	{
		AllocateParameter parameter = new AllocateParameter();
		parameter.classficationFeatureItemList = classficationFeatureItemList;
		parameter.classificationMap = classificationMap;
		parameter.classificationNumberFieldList = classificationNumberFieldList;
		parameter.control = control;
		parameter.dataAccessService = dataAccessService;
		parameter.field = field;
		parameter.item = item;
		parameter.numberClassField = numberClassField;
		parameter.isCreate = isCreate;
		return parameter;
	}

}
