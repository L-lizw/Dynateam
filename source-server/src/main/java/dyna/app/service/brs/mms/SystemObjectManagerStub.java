package dyna.app.service.brs.mms;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.boas.numbering.NumberAllocate;
import dyna.app.service.brs.emm.ClassStub;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.coding.ClassificationNumberTrans;
import dyna.common.bean.model.cls.NumberingModel;
import dyna.common.bean.model.cls.NumberingObject;
import dyna.common.dto.aas.Group;
import dyna.common.dto.aas.User;
import dyna.common.dto.cfm.ClassficationFeatureItemInfo;
import dyna.common.dto.cfm.ClassificationNumberField;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.FieldTypeEnum;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.systemenum.NumberingTypeEnum;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.common.systemenum.coding.CFMCodeRuleEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.common.util.Constants;
import dyna.data.service.sdm.SystemDataService;
import dyna.net.security.signature.UserSignature;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SystemObjectManagerStub extends AbstractServiceStub<MMSImpl>
{

	private Map<String, Long> maxSerialIdMap = new HashMap<String, Long>();

	protected <T extends SystemObject> String saveSystemObject(T systemObject) throws ServiceRequestException
	{
		SystemDataService systemDataService = DataServer.getSystemDataService();
		String ownerUserGuid = ((UserSignature) stubService.getSignature()).getUserGuid();
		systemObject.setUpdateUserGuid(ownerUserGuid);
		return systemDataService.save(systemObject);
	}

	/**
	 * 更新流水号
	 * 
	 * @param foundationObject
	 * @throws ServiceRequestException
	 */
	protected void updateSerialNumber(FoundationObject foundationObject) throws ServiceRequestException
	{

		foundationObject.restoreAllClassification(false);

		ObjectGuid objectGuid = foundationObject.getObjectGuid();
		ClassStub.decorateObjectGuid(objectGuid, this.stubService);
		ClassInfo classInfo = this.stubService.getEMM().getClassByGuid(objectGuid.getClassGuid());
		List<ClassficationFeatureItemInfo> sllFeatureItemList = this.stubService.getEMM().listClassficationFeatureItem(classInfo.getClassification(),
				foundationObject.getClassificationGuid());

		// 分类管理中是否有id,name,alterid编码
		boolean hasClfId = false;
		boolean hasClfAlter = false;
		boolean hasClfName = false;

		if (!SetUtils.isNullList(sllFeatureItemList))
		{
			for (ClassficationFeatureItemInfo item : sllFeatureItemList)
			{
				if (SystemClassFieldEnum.ID.getName().equalsIgnoreCase(item.getFieldName()))
				{
					hasClfId = true;
				}
				else if (SystemClassFieldEnum.ALTERID.getName().equalsIgnoreCase(item.getFieldName()))
				{
					hasClfAlter = true;
				}
				else if (SystemClassFieldEnum.NAME.getName().equalsIgnoreCase(item.getFieldName()))
				{
					hasClfName = true;
				}

				if (hasClfId || hasClfAlter || hasClfName)
				{
					List<ClassificationNumberField> listClassificationNumberField = this.stubService.getEMM().listClassificationNumberField(item.getGuid());
					if (!SetUtils.isNullList(listClassificationNumberField))
					{
						for (ClassificationNumberField numberField : listClassificationNumberField)
						{
							if (numberField.getType() == CFMCodeRuleEnum.SERIAL)
							{
								NumberAllocate na = new NumberAllocate();
								na.updateFieldMaxSerial(foundationObject, this.stubService, item.getFieldName());
								break;
							}
						}
					}
				}
			}
		}

		if (foundationObject.getObjectGuid() != null
				&& (StringUtils.isGuid(foundationObject.getObjectGuid().getClassGuid()) || !StringUtils.isNullString(foundationObject.getObjectGuid().getClassName())))
		{
			String classguid = foundationObject.getObjectGuid().getClassGuid();
			String classname = foundationObject.getObjectGuid().getClassName();

			if (hasClfId == false)
			{
				NumberingModel numberModel = this.stubService.getEMM().lookupNumberingModel(classguid, classname, SystemClassFieldEnum.ID.getName());
				if (numberModel != null)
				{
					List<NumberingObject> numberingObjectList = numberModel.getNumberingObjectList();
					if (!SetUtils.isNullList(numberingObjectList))
					{

						for (NumberingObject numberObject : numberingObjectList)
						{
							NumberingTypeEnum numberingTypeEnum = numberObject.getTypeOEnum();
							if (NumberingTypeEnum.SEQUENCE.equals(numberingTypeEnum))
							{
								this.updateSerialNumberByFoundation(foundationObject, numberModel);
								break;
							}
						}
					}
				}
			}

			if (hasClfAlter == false)
			{
				NumberingModel numberModel = this.stubService.getEMM().lookupNumberingModel(classguid, classname, SystemClassFieldEnum.ALTERID.getName());
				if (numberModel != null)
				{
					List<NumberingObject> numberingObjectList = numberModel.getNumberingObjectList();
					if (!SetUtils.isNullList(numberingObjectList))
					{

						for (NumberingObject numberObject : numberingObjectList)
						{
							NumberingTypeEnum numberingTypeEnum = numberObject.getTypeOEnum();
							if (NumberingTypeEnum.SEQUENCE.equals(numberingTypeEnum))
							{
								this.updateSerialNumberByFoundation(foundationObject, numberModel);
								break;
							}
						}
					}
				}
			}

			if (hasClfName == false)
			{
				NumberingModel numberModel = this.stubService.getEMM().lookupNumberingModel(classguid, classname, SystemClassFieldEnum.NAME.getName());
				if (numberModel != null)
				{
					List<NumberingObject> numberingObjectList = numberModel.getNumberingObjectList();
					if (!SetUtils.isNullList(numberingObjectList))
					{

						for (NumberingObject numberObject : numberingObjectList)
						{
							NumberingTypeEnum numberingTypeEnum = numberObject.getTypeOEnum();
							if (NumberingTypeEnum.SEQUENCE.equals(numberingTypeEnum))
							{
								this.updateSerialNumberByFoundation(foundationObject, numberModel);
								break;
							}
						}
					}
				}
			}
		}

	}

	/**
	 * 
	 * @param foun
	 * @param numberModel
	 * @throws ServiceRequestException
	 */
	private void updateSerialNumberByFoundation(FoundationObject foun, NumberingModel numberModel) throws ServiceRequestException
	{
		// 判断此规则组装的Id长度是否与foun中的Id长度一致,流水号用*显示
		if (numberModel != null)
		{
			List<NumberingObject> numberingObjectList = numberModel.getNumberingObjectList();
			if (!SetUtils.isNullList(numberingObjectList))
			{
				String founId = foun.getId();
				Map<Integer, String> valueMap = new LinkedHashMap<Integer, String>();// key为编码顺序,alue为每个值
				String id = this.getIdValue(numberingObjectList, foun, valueMap);
				// 长度相同时再检查
				if (!StringUtils.isNullString(id) && id.length() == founId.length())
				{
					StringBuffer idValue = new StringBuffer(founId); // 序遍历的Id值
					StringBuffer preBuffer = new StringBuffer();// 流水号前缀(无固定值)
					StringBuffer serialNumber = new StringBuffer();// 流水
					boolean isSucess = this.getSerialNumber(idValue, preBuffer, serialNumber, numberModel, valueMap);
					if (isSucess && !StringUtils.isNullString(serialNumber.toString()))
					{
						StringBuffer exitSerial = new StringBuffer();
						String serialNo = this.getRightValue(serialNumber.toString());
						boolean isRight = this.checkSeriNo(serialNo);
						String resultType = this.isInsertValue(numberModel.getClassGuid(), preBuffer.toString(), serialNo, exitSerial, isRight);
						if (resultType.equals("1"))
						{
							if (isRight)
							{
								// 插入记录
								this.addSerialNumber(numberModel.getClassGuid(), preBuffer.toString(), Long.valueOf(serialNo));
							}
						}
						else if (resultType.equals("2"))
						{
							boolean existRight = true;
							if (exitSerial.length() != 0)
							{
								existRight = this.checkSeriNo(exitSerial.toString());
							}
							if (isRight && existRight)
							{
								this.editSerialNumber(numberModel.getClassGuid(), preBuffer.toString(), serialNo, false);
							}
							else if (!isRight && !existRight)
							{
								this.editSerialNumber(numberModel.getClassGuid(), preBuffer.toString(), "1", true);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 判断了流水号是否正确
	 * 
	 * @param serialNo
	 * @return
	 */
	private boolean checkSeriNo(String serialNo)
	{
		String numberString = "^[0-9]*$";
		Pattern pattern = Pattern.compile(numberString);
		Matcher matcher = pattern.matcher(serialNo);
		if (!matcher.find())
		{
			return false;
		}
		return true;
	}

	/**
	 * @param foun
	 * @param preString
	 * @param sufString
	 * @param serialNo
	 * @param b
	 * @param noLocation
	 * @throws ServiceRequestException
	 */
	private void editSerialNumber(String classGuid, String preString, String serialNo, boolean isUpdate) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			HashMap<String, Object> filter = new HashMap<String, Object>();
			filter.put(ClassificationNumberTrans.CLASSIFICATIONFEATUREITEM, classGuid);
			if (preString != null && preString.trim().equals(""))
			{
				filter.put(ClassificationNumberTrans.CONSENGVALUES, null);
			}
			else
			{
				filter.put(ClassificationNumberTrans.CONSENGVALUES, preString);
			}
			filter.put(ClassificationNumberTrans.FIELDNAME, SystemClassFieldEnum.ID.getName());

			// 记录最大流水
			String key = filter.toString();
			Long max = this.maxSerialIdMap.get(key);
			long serialNoL = Long.parseLong(serialNo);
			if (max == null || serialNoL > max)
			{
				this.maxSerialIdMap.put(key, serialNoL);
				isUpdate = true;
			}

			if (isUpdate)
			{
				filter.put(ClassificationNumberTrans.TRANSNUMBER, serialNo);
				sds.update(ClassificationNumberTrans.class, filter, "update");
			}
		}
		catch (DynaDataException e)
		{
			ServiceRequestException.createByDynaDataException(e);
		}
	}

	/**
	 * @param foun
	 * @param string
	 * @param string2
	 * @param string3
	 * @param noLocation
	 * @throws ServiceRequestException
	 */
	private void addSerialNumber(String classGuid, String preString, long serialNo) throws ServiceRequestException
	{
		String operatorGuid = this.stubService.getOperatorGuid();
		SystemDataService sds = DataServer.getSystemDataService();
		ClassificationNumberTrans serial = new ClassificationNumberTrans();
		serial.setClassificationFeatureItem(classGuid);
		serial.setFieldName(SystemClassFieldEnum.ID.getName());
		serial.setConsengValues(preString);
		serial.setTransNumber(serialNo);
		serial.put(SystemObject.CREATE_USER_GUID, operatorGuid);
		serial.put(SystemObject.UPDATE_USER_GUID, operatorGuid);
		sds.save(serial, "insert");

	}

	/**
	 * @param serialNumber
	 * @return
	 */
	private String getRightValue(String serialNumber)
	{
		String resultValue = serialNumber;
		int lenght = serialNumber.length();
		StringBuffer sv = new StringBuffer(serialNumber);
		for (int i = 0; i < lenght; i++)
		{
			char ch = serialNumber.charAt(i);
			if (ch != '0')
			{
				resultValue = sv.substring(i);
				break;
			}
		}
		return resultValue;
	}

	/**
	 * @param foun
	 * @param exitSerial
	 * @param isRight
	 * @param string
	 * @param string2
	 * @param string3
	 * @param noLocation
	 * @return
	 * 		返回值为1：插入记录
	 *         返回值为2：修改记录
	 * @throws ServiceRequestException
	 */
	private String isInsertValue(String classGuid, String preString, String serialNo, StringBuffer exitSerial, boolean isRight) throws ServiceRequestException
	{
		String valueType = "1";
		SystemDataService sds = DataServer.getSystemDataService();
		HashMap<String, Object> filter = new HashMap<String, Object>();
		filter.put(ClassificationNumberTrans.CLASSIFICATIONFEATUREITEM, classGuid);
		filter.put(ClassificationNumberTrans.FIELDNAME, SystemClassFieldEnum.ID.getName());

		if (preString != null && preString.trim().equalsIgnoreCase(""))
		{
			filter.put(ClassificationNumberTrans.CONSENGVALUES, null);
		}
		else
		{
			filter.put(ClassificationNumberTrans.CONSENGVALUES, preString);
		}

		String key = filter.toString();
		List<ClassificationNumberTrans> resultList = sds.query(ClassificationNumberTrans.class, filter);
		if (!SetUtils.isNullList(resultList))
		{
			if (resultList.size() == 1)
			{
				valueType = "2";
				exitSerial.append(resultList.get(0).getTransNumber());
			}
			else
			{
				throw new ServiceRequestException("the result size >1 !");
			}
		}
		else
		{
			// 记录最大流水
			if (isRight)
			{
				long serialNoL = Long.parseLong(serialNo);
				this.maxSerialIdMap.put(key, serialNoL);
			}
		}

		return valueType;
	}

	/**
	 * @param idValue
	 *            要遍历的id
	 * @param preBuffer
	 *            去掉固定值后流水号前缀字符串
	 * @param sufBuffer
	 *            去掉固定值流水号后缀字符串
	 * @param numberingObjectList
	 *            遍历的编码规则
	 * @param valueMap
	 *            每个编码规则对应的值
	 * @return
	 */
	private boolean getSerialNumber(StringBuffer idValue, StringBuffer preBuffer, StringBuffer serialNumber, NumberingModel numberModel, Map<Integer, String> valueMap)
	{
		boolean isSuc = true;
		boolean isContinue = true;
		// boolean isPre = true; // 是否是前缀--正确做法
		// int sequenceIndex = 0;// 要与数据层一致，错误做法
		boolean isSerialNumberWithDate = numberModel.isSerialNumberWithDate();
		boolean isSerialNumberWithField = numberModel.isSerialNumberWithField();
		List<NumberingObject> numberingObjectList = numberModel.getNumberingObjectList();
		for (int i = 0; i < numberingObjectList.size(); i++)
		{
			NumberingObject numberObject = numberingObjectList.get(i);
			NumberingTypeEnum numberingTypeEnum = numberObject.getTypeOEnum();
			;
			if (numberingTypeEnum.equals(NumberingTypeEnum.STRING))
			{
				String value = valueMap.get(i);
				if (!StringUtils.isNullString(value) && idValue != null && idValue.length() >= value.length())
				{
					String pv = idValue.substring(0, value.length());
					if (pv.equals(value))
					{
						String sv = idValue.substring(value.length());
						idValue.delete(0, idValue.length());
						idValue.append(sv);
					}
					else
					{
						isContinue = false;
					}
				}
				else
				{
					isContinue = false;
				}
			}
			else if (numberingTypeEnum.equals(NumberingTypeEnum.DAY) || numberingTypeEnum.equals(NumberingTypeEnum.MONTH) || numberingTypeEnum.equals(NumberingTypeEnum.YEAR))
			{
				String value = valueMap.get(i);
				if (!StringUtils.isNullString(value) && idValue != null && idValue.length() >= value.length())
				{
					String pv = idValue.substring(0, value.length());
					String sv = idValue.substring(value.length());
					idValue.delete(0, idValue.length());
					idValue.append(sv);
					if (isSerialNumberWithDate)
					{
						preBuffer.append(pv);
					}
				}
				else
				{
					isContinue = false;
				}
			}
			else if (numberingTypeEnum.equals(NumberingTypeEnum.FIELD))
			{
				String value = StringUtils.convertNULLtoString(valueMap.get(i));
				if (idValue != null && idValue.length() >= value.length())
				{
					String pv = idValue.substring(0, value.length());
					if (pv.equals(value))
					{
						String sv = idValue.substring(value.length());
						idValue.delete(0, idValue.length());
						idValue.append(sv);
						if (isSerialNumberWithField)
						{
							preBuffer.append(pv);
						}
					}
					else
					{
						isContinue = false;
					}
				}
				else
				{
					isContinue = false;
				}
			}
			else if (numberingTypeEnum.equals(NumberingTypeEnum.SEQUENCE))
			{
				String value = valueMap.get(i);
				if (!StringUtils.isNullString(value) && idValue != null && idValue.length() >= value.length())
				{
					String pv = idValue.substring(0, value.length());
					String sv = idValue.substring(value.length());
					idValue.delete(0, idValue.length());
					idValue.append(sv);
					serialNumber.append(pv);
					// isPre = false;
					// sequenceIndex = i;
				}
				else
				{
					isContinue = false;
				}
			}

			if (!isContinue)
			{
				isSuc = false;
				break;
			}
		}
		return isSuc;
	}

	/**
	 * @param numberingObjectList
	 * @param valueMap
	 * @return
	 * @throws ServiceRequestException
	 */
	private String getIdValue(List<NumberingObject> numberingObjectList, FoundationObject foun, Map<Integer, String> valueMap) throws ServiceRequestException
	{
		ClassStub.decorateObjectGuid(foun.getObjectGuid(), this.stubService);

		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < numberingObjectList.size(); i++)
		{
			NumberingObject numberingObject = numberingObjectList.get(i);
			NumberingTypeEnum numberingTypeEnum = numberingObject.getTypeOEnum();
			;

			if (NumberingTypeEnum.STRING.equals(numberingTypeEnum))
			{
				stringBuffer.append(numberingObject.getTypevalue());
				valueMap.put(i, numberingObject.getTypevalue());
			}
			else if (NumberingTypeEnum.YEAR.equals(numberingTypeEnum))
			{
				// 码段为：YEAR类型的处理
				Calendar calendarYear = Calendar.getInstance();
				String year = String.valueOf(calendarYear.get(Calendar.YEAR));
				String typeValue = numberingObject.getTypevalue();
				if (Constants.NUMBERING_YEAR_TWO.equals(typeValue))
				{
					year = year.substring(2, 4);
				}
				stringBuffer.append(year);
				valueMap.put(i, year);
			}
			else if (NumberingTypeEnum.MONTH.equals(numberingTypeEnum))
			{
				// 码段为：MONTH类型的处理
				Calendar calendarMonth = Calendar.getInstance();
				String month = String.valueOf(calendarMonth.get(Calendar.MONTH));
				month = String.valueOf(Integer.parseInt(month) + 1);
				if (month.length() < 2)
				{
					month = "0" + month;
				}
				stringBuffer.append(month);
				valueMap.put(i, month);
			}
			else if (NumberingTypeEnum.DAY.equals(numberingTypeEnum))
			{
				// 码段为：DAY类型的处理
				Calendar calendarDay = Calendar.getInstance();
				String day = String.valueOf(calendarDay.get(Calendar.DAY_OF_MONTH));
				String typeValue = numberingObject.getTypevalue();

				// 是否需要补成两位
				if (Constants.NUMBERING_MONTH_DAY_TWO.equals(typeValue))
				{
					if (day.length() < 2)
					{
						day = "0" + day;
					}
				}

				stringBuffer.append(day);
				valueMap.put(i, day);
			}
			else if (NumberingTypeEnum.FIELD.equals(numberingTypeEnum))
			{
				// 码段为：FIELD类型的处理
				String _fieldName = numberingObject.getTypevalue();

				if (StringUtils.isNullString(StringUtils.convertNULLtoString(foun.get(_fieldName))))
				{
					// throw new DynaDataExceptionSQL("AllocateId error. ", null, DataExceptionEnum.FIELD_IS_NULL);
				}

				ClassField classField = this.stubService.getEMM().getFieldByName(foun.getObjectGuid().getClassName(), _fieldName, true);

				FieldTypeEnum fieldType = classField.getType();

				if (FieldTypeEnum.STRING.equals(fieldType) || FieldTypeEnum.INTEGER.equals(fieldType) || FieldTypeEnum.FLOAT.equals(fieldType))
				{
					// 字段类型为：String/Folat/Integer等类型的处理
					stringBuffer.append(StringUtils.convertNULLtoString(foun.get(_fieldName)));
					valueMap.put(i, StringUtils.convertNULLtoString(foun.get(_fieldName)));
				}
				else if (FieldTypeEnum.CODE.equals(fieldType) //
						|| FieldTypeEnum.CLASSIFICATION.equals(fieldType)//
						|| FieldTypeEnum.MULTICODE.equals(fieldType)//
						|| FieldTypeEnum.CODEREF.equals(fieldType))
				{
					// 字段类型为：Code/Classification/multicode 类型的处理
					String codeGuid = StringUtils.convertNULLtoString(foun.get(_fieldName));
					if (codeGuid != null)
					{
						String type = numberingObject.getFieldType();
						String value = StringUtils.convertNULLtoString(this.getCodeValue(codeGuid, type));
						stringBuffer.append(value);
						valueMap.put(i, value);
					}
				}
				else if (FieldTypeEnum.OBJECT.equals(fieldType))
				{
					// 字段类型为：Object等类型的处理
					String numberValue = "";
					String guid = StringUtils.convertNULLtoString(foun.get(_fieldName));
					ClassInfo classInfo = this.stubService.getEMM().getClassByName(classField.getTypeValue());
					if (StringUtils.isGuid(guid))
					{
						if (classInfo.hasInterface(ModelInterfaceEnum.IUser))
						{
							User user = this.stubService.getAAS().getUser(guid);
							if (user != null)
							{
								numberValue = user.getUserId();
							}
						}
						else if (classInfo.hasInterface(ModelInterfaceEnum.IGroup))
						{
							Group group = this.stubService.getAAS().getGroup(guid);
							if (group != null)
							{
								numberValue = group.getGroupId();
							}
						}
						else
						{
							numberValue = this.getObjectValue(numberingObject, guid, StringUtils.convertNULLtoString(foun.get(_fieldName + "$CLASS")), classInfo.getName(),
									stringBuffer);
						}
					}
					numberValue = StringUtils.convertNULLtoString(numberValue);
					stringBuffer.append(numberValue);
					valueMap.put(i, numberValue);
				}
			}
			else if (NumberingTypeEnum.SEQUENCE.equals(numberingTypeEnum))
			{
				// 流水号类型时,值以"*"填充
				String digit = numberingObject.getTypevalue();
				String value = this.serialValue(digit);
				stringBuffer.append(value);
				valueMap.put(i, value);
			}
		}
		return stringBuffer.toString();
	}

	private String getObjectValue(NumberingObject numberingObject, String guid, String classGuid, String className, StringBuffer stringBuffer) throws ServiceRequestException
	{
		String fieldName = null;
		FieldTypeEnum fieldType = null;
		// boolean isSystemField = false;
		List<NumberingObject> numberingObjectChildList = numberingObject.getChildObject();
		for (NumberingObject numberingObjectChild : numberingObjectChildList)
		{
			if (numberingObjectChild.getFieldClassName() != null && numberingObjectChild.getFieldClassName().equals(className))
			{
				fieldName = numberingObjectChild.getTypevalue();

				fieldType = this.stubService.getEMM().getFieldByName(className, fieldName, true).getType();
			}
		}

		String numberValue = "";
		ObjectGuid objectGuid = new ObjectGuid();
		objectGuid.setGuid(guid);
		objectGuid.setClassGuid(classGuid);
		objectGuid.setClassName(className);
		SearchCondition searchCondition = SearchConditionFactory.createSearchCondition(objectGuid, null, false);
		// if (!isSystemField)
		// {
		searchCondition.addResultField(fieldName);
		// }
		searchCondition.setPageSize(1);
		List<FoundationObject> foList = this.stubService.getBOAS().listObject(searchCondition);
		if (foList != null && foList.size() > 0)
		{
			if (FieldTypeEnum.OBJECT.equals(fieldType))
			{
				numberValue = StringUtils.convertNULLtoString(foList.get(0).getId());
			}
			else if (FieldTypeEnum.CODE.equals(fieldType) || FieldTypeEnum.CLASSIFICATION.equals(fieldType) || FieldTypeEnum.MULTICODE.equals(fieldType)
					|| FieldTypeEnum.CODEREF.equals(fieldType))
			{
				// 字段类型为：Code/Classification/multicode 类型的处理
				String codeGuid = StringUtils.convertNULLtoString(foList.get(0).get(fieldName));
				if (!StringUtils.isNullString(codeGuid))
				{
					String type = numberingObject.getFieldType();
					numberValue = this.getCodeValue(codeGuid, type);
				}
			}
			else
			{
				numberValue = StringUtils.convertNULLtoString(foList.get(0).get(fieldName));
			}
		}
		return numberValue;
	}

	/**
	 * @param fieldType
	 * @param codeGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	private String getCodeValue(String codeGuid, String type) throws ServiceRequestException
	{
		String[] codeGuidList = StringUtils.splitStringWithDelimiter(";", codeGuid);
		String str = "";
		if (codeGuidList == null)
		{
			return "";
		}
		for (int i = 0; i < codeGuidList.length; i++)
		{
			if (!StringUtils.isNullString(codeGuidList[i]))
			{
				CodeItemInfo codeItem = this.stubService.getEMM().getCodeItem(codeGuidList[i]);
				if (codeItem == null)
				{
					return "";
				}
				if (StringUtils.isNullString(type))
				{
					type = "code";
				}
				if (type.equalsIgnoreCase("title"))
				{
					str = str + StringUtils.convertNULLtoString(codeItem.getTitle(this.stubService.getUserSignature().getLanguageEnum()));
				}
				else if (type.equalsIgnoreCase("code"))
				{
					str = str + StringUtils.convertNULLtoString(codeItem.getCode());
				}
				else
				{
					str = str + StringUtils.convertNULLtoString(codeItem.getCode());
				}
			}
		}
		return str;
	}

	/**
	 * @param digit
	 * @return
	 */
	private String serialValue(String digit)
	{
		String value = "";
		if (!StringUtils.isNullString(digit))
		{
			int size = Integer.parseInt(digit);
			for (int i = 0; i < size; i++)
			{
				value = value + "*";
			}
		}
		return value;
	}

}
