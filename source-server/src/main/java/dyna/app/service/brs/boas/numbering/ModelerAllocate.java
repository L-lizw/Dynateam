/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 生成编码
 * JiangHL 2011-5-10
 */
package dyna.app.service.brs.boas.numbering;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dyna.app.service.DataAccessService;
import dyna.app.service.brs.boas.BOASImpl;
import dyna.app.service.brs.emm.ClassStub;
import dyna.app.service.brs.emm.EMMImpl;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.coding.ClassificationNumberTrans;
import dyna.common.bean.model.cls.NumberingModel;
import dyna.common.bean.model.cls.NumberingObject;
import dyna.common.dto.aas.Group;
import dyna.common.dto.aas.User;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dto.model.cls.NumberingObjectInfo;
import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.dto.model.code.CodeObjectInfo;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.FieldTypeEnum;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.common.systemenum.NumberingTypeEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.common.util.Constants;
import dyna.data.service.sdm.SystemDataService;
import dyna.net.service.Service;
import dyna.net.service.brs.BOAS;
import dyna.net.service.brs.EMM;

/**
 * 生成编码
 * 
 * @author JiangHL
 */
public class ModelerAllocate
{

	protected <T extends Service> T getService(DataAccessService dataAccessService, Class<T> clazz) throws ServiceRequestException
	{
		try
		{
			return dataAccessService.getRefService(clazz);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	private String fillLength(Integer length, String number) throws ServiceRequestException
	{
		if (length == null)
		{
			return number;
		}

		int count = length - number.length();
		if (count < 0)
		{
			throw new ServiceRequestException("ID_APP_CLASSIFICATION_SERIAL_RUNOUT", "serial has been run out");
		}

		for (int i = 0; i < count; i++)
		{
			number = "0" + number;
		}
		return number;
	}

	protected String allocateUnique(FoundationObject foundationObject, String fieldName, boolean needMadeNumber, boolean onlyInsert, int stepLength,
			DataAccessService dataAccessService) throws ServiceRequestException
	{
		EMM emm = this.getService(dataAccessService, EMM.class);

		Map<String, Object> paramMap = new HashMap<String, Object>();
		String sessionId = dataAccessService.getUserSignature().getCredential();

		ClassStub.decorateObjectGuid(foundationObject.getObjectGuid(), dataAccessService);
		String className = foundationObject.getObjectGuid().getClassName();
		String classGuid = foundationObject.getObjectGuid().getClassGuid();

		// ClassInfo classInfo = emm.getClassByName(className);
		NumberingModel numberingModel = emm.lookupNumberingModel(classGuid, className, fieldName);
		// NumberingModel numberingModel = classObject.getNumberingModel(fieldName);

		// 如果没有编码规则，返回本身的值
		if (numberingModel == null)
		{
			return StringUtils.convertNULLtoString(foundationObject.get(fieldName));
		}
		// 插入流水码时，如果不强制使用编码规则，同时不是自动生成的 不需要做处理
		String allocateid = StringUtils.convertNULLtoString(foundationObject.get(FoundationObject.ALLOCATE_ID));

		if (onlyInsert && "".equals(allocateid))
		{
			return "";
		}

		// 是否使用扩展编码
		// boolean extendedserialnumber = numberingModel.isExtendedserialnumber();
		if (SetUtils.isNullList(numberingModel.getNumberingObjectList()))
		{
			// 判断是否符合编码长度
			// return this.checkNumberLength(numberingModel, fieldName,
			// StringUtils.convertNULLtoString(foundationObject.get(fieldName)));
			return (String) foundationObject.get(fieldName);
		}

		// 标示在编码规则中是否有流水码
		boolean isHaveSequence = false;
		// 流水码开始值
		String startValue = NumberingObjectInfo.DEFAULT_START_VALUE;

		List<String[]> numberingValueList = new ArrayList<String[]>();

		StringBuffer stringBuffer = new StringBuffer();

		for (NumberingObject numberingObject : numberingModel.getNumberingObjectList())
		{
			NumberingTypeEnum numberingTypeEnum = NumberingTypeEnum.typeValueOf(numberingObject.getType());

			if (NumberingTypeEnum.STRING.equals(numberingTypeEnum))
			{
				String[] str = { numberingObject.getTypevalue(), NumberingTypeEnum.STRING.toString() };

				// 码段为：字符串类型的处理
				numberingValueList.add(str);

				stringBuffer.append(numberingObject.getTypevalue());
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

				String[] str = { year, NumberingTypeEnum.YEAR.toString() };
				numberingValueList.add(str);
				stringBuffer.append(year);
			}
			else if (NumberingTypeEnum.MONTH.equals(numberingTypeEnum))
			{
				// 码段为：MONTH类型的处理
				Calendar calendarMonth = Calendar.getInstance();
				String month = String.valueOf(calendarMonth.get(Calendar.MONTH));
				month = String.valueOf(Integer.parseInt(month) + 1);
				String typeValue = numberingObject.getTypevalue();

				// 是否需要补成两位
				if (Constants.NUMBERING_MONTH_DAY_TWO.equals(typeValue))
				{
					if (month.length() < 2)
					{
						month = "0" + month;
					}
				}

				String[] str = { month, NumberingTypeEnum.MONTH.toString() };
				numberingValueList.add(str);
				stringBuffer.append(month);
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

				String[] str = { day, NumberingTypeEnum.DAY.toString() };
				numberingValueList.add(str);
				stringBuffer.append(day);
			}
			else if (NumberingTypeEnum.FIELD.equals(numberingTypeEnum))
			{
				// 码段为：FIELD类型的处理
				String _fieldName = numberingObject.getTypevalue();

				/*
				 * if (StringUtils.isNullString(StringUtils.convertNULLtoString(foundationObject.get(_fieldName))))
				 * {
				 * String uiFieldName = _fieldName;
				 * UIField uiField = emm.getUIFieldByName(className, _fieldName);
				 * if (uiField != null)
				 * {
				 * uiFieldName = uiField.getTitle(dataAccessService.getUserSignature().getLanguageEnum());
				 * }
				 * throw new ServiceRequestException("ID_APP_FIELD_IS_NULL", "AllocateId error. field " + uiFieldName +
				 * "[" + _fieldName + "]" + " is null", null, uiFieldName);
				 * }
				 */

				// ClassField classField = SystemClassFieldEnum.getSystemField(_fieldName);
				// if (classField != null)
				// {
				// }
				// else
				// {
				ClassField classField = emm.getFieldByName(className, _fieldName, true);
				// }

				FieldTypeEnum fieldType = classField.getType();

				String fieldValue = "";

				if (FieldTypeEnum.STRING.equals(fieldType) || FieldTypeEnum.INTEGER.equals(fieldType) || FieldTypeEnum.FLOAT.equals(fieldType))
				{
					// 字段类型为：String/Folat/Integer等类型的处理
					fieldValue = StringUtils.convertNULLtoString(foundationObject.get(_fieldName));
				}
				else if (FieldTypeEnum.CODE.equals(fieldType) //
						|| FieldTypeEnum.CLASSIFICATION.equals(fieldType)//
						|| FieldTypeEnum.MULTICODE.equals(fieldType)//
						|| FieldTypeEnum.CODEREF.equals(fieldType))
				{
					// 字段类型为：Code/Classification/multicode 类型的处理
					String value = StringUtils.convertNULLtoString(foundationObject.get(_fieldName));
					String[] codeGuidList = StringUtils.splitStringWithDelimiter(CodeObjectInfo.MULTI_CODE_DELIMITER_GUID, value);
					String type = numberingObject.getFieldType();
					if (StringUtils.isNullString(type))
					{
						type = "code";
					}
					if (codeGuidList != null)
					{
						for (String codeItemGuid : codeGuidList)
						{

							if (!StringUtils.isNullString(codeItemGuid))
							{
								CodeItemInfo codeItem = emm.getCodeItem(codeItemGuid);
								if (codeItem != null)
								{
									String codeName = "";

									if (type.equalsIgnoreCase("title"))
									{
										codeName = codeItem.getTitle(((EMMImpl) emm).getUserSignature().getLanguageEnum());
									}
									else if (type.equalsIgnoreCase("code"))
									{
										codeName = codeItem.getCode();
									}
									codeName = StringUtils.convertNULLtoString(codeName);
									fieldValue = codeName;
								}
							}
						}
					}

					// if (codeGuid != null)
					// {
					// List<String> codeList = new ArrayList<String>(Arrays.asList(StringUtils
					// .splitStringWithDelimiter(CodeModel.MULTI_CODE_DELIMITER_GUID, codeGuid)));
					// List<Map<String, String>> list = null;
					// try
					// {
					// list = this.sqlMapper.queryForList(SQLMAP_DYNAOBJECT + ".selectCodeName", codeList);
					// }
					// catch (SQLException e)
					// {
					// throw new DynaDataExceptionSQL("allocateUniqueId ", null,
					// DataExceptionEnum.DS_ALLOCATEUNIQUEID, foundationObject.getId());
					// }
					// if (list != null && list.size() > 0)
					// {
					// for (Map<String, String> codeNameMap : list)
					// {
					// String codeName = "";
					// String type = numberingObject.getFieldType();
					// if (StringUtils.isNullString(type))
					// {
					// codeName = String.valueOf(codeNameMap.get("CODE"));
					// }
					// else if (type.equalsIgnoreCase("title"))
					// {
					// codeName = StringUtils.getMsrTitle(codeNameMap.get("TITLE"), dataAccessService
					// .getUserSignature().getLanguageEnum().getType());
					// }
					// else if (type.equalsIgnoreCase("code"))
					// {
					// codeName = String.valueOf(codeNameMap.get("CODE"));
					// }
					//
					// String[] str = { codeName, NumberingTypeEnum.FIELD.toString() };
					// numberingValueList.add(str);
					// stringBuffer.append(codeName);
					// }
					// }
					// }
				}
				else if (FieldTypeEnum.OBJECT.equals(fieldType))
				{

					// 字段类型为：Object等类型的处理
					String numberValue = "";
					String guid = StringUtils.convertNULLtoString(foundationObject.get(_fieldName));
					// SystemDataService sds = this.context.getInternalService(SystemDataService.class);
					// Session session = sds.getSession(sessionId);
					// classField.getTypeValue(), session.getBizModelName());
					SystemDataService sds = DataServer.getSystemDataService();
					ClassInfo typeValueObject = emm.getClassByName(classField.getTypeValue());
					if (typeValueObject.hasInterface(ModelInterfaceEnum.IUser))
					{
						User user = new User();
						user.put("USERGUID", guid);
						numberValue = sds.save(user, "selectId");
					}
					else if (typeValueObject.hasInterface(ModelInterfaceEnum.IGroup))
					{
						Group group = new Group();
						group.put("GROUPGUID", guid);
						numberValue = sds.save(group, "selectId");
					}
					else
					{
						if (!StringUtils.isGuid(guid))
						{
							numberValue = "";
						}
						else
						{
							numberValue = this.getObjectValue(numberingObject, //
									guid, //
									sessionId, //
									StringUtils.convertNULLtoString(foundationObject.get(_fieldName + "$CLASS")), //
									typeValueObject.getName(), //
									numberingModel.getName(), //
									dataAccessService);
						}
					}
					numberValue = StringUtils.convertNULLtoString(numberValue);
					fieldValue = numberValue;
				}

				if (!StringUtils.isNullString(fieldValue))
				{
					String prefixDelimiter = StringUtils.convertNULLtoString(numberingObject.getPrefix());
					String suffixDelimiter = StringUtils.convertNULLtoString(numberingObject.getSuffix());
					fieldValue = prefixDelimiter + fieldValue + suffixDelimiter;
				}

				String[] str = { fieldValue, NumberingTypeEnum.FIELD.toString() };
				numberingValueList.add(str);
				stringBuffer.append(fieldValue);

			}
			else if (NumberingTypeEnum.SEQUENCE.equals(numberingTypeEnum))
			{
				startValue = numberingObject.getStartValue();
				// 码段为：SEQUENCE类型的处理
				String digit = numberingObject.getTypevalue();
				String[] str = { digit, NumberingTypeEnum.SEQUENCE.toString() };
				numberingValueList.add(str);
				isHaveSequence = true;
			}
		}

		// 如果编码规则中有流水码，那么获取流水码
		String result = null;

		if (isHaveSequence)
		{
			result = this.getSeries(numberingModel.getClassGuid(), fieldName, numberingModel, numberingValueList, stepLength, startValue, dataAccessService);
		}
		else
		{
			paramMap.put("NOSERIES", "NOSERIES");
			result = stringBuffer.toString();
		}
		return result;

	}

	/**
	 * 获取对象字段值
	 * 
	 * @param numberingObject
	 * @param guid
	 * @param sessionId
	 * @param className
	 * @param stringBuffer
	 * @param numberingValueList
	 * @return
	 * @throws ServiceRequestException
	 */
	private String getObjectValue(NumberingObject numberingObject, String guid, String sessionId, String classGuid, String className, String numbermodelFieldName, DataAccessService dataAccessService) throws ServiceRequestException
	{

		BOAS boas = this.getService(dataAccessService, BOAS.class);
		EMM emm = this.getService(dataAccessService, EMM.class);
		String fieldName = null;
		FieldTypeEnum fieldType = null;
		// boolean isSystemField = false;
		if (numberingObject.getChildObject() != null)
		{
			for (NumberingObject numberingObjectChild : numberingObject.getChildObject())
			{
				if (numberingObjectChild.getFieldClassName() != null && numberingObjectChild.getFieldClassName().equals(className))
				{
					fieldName = numberingObjectChild.getTypevalue();
					// ClassField cf = SystemClassFieldEnum.getSystemField(fieldName);
					// if (cf != null)
					// {
					// fieldType = cf.getType();
					// isSystemField = true;
					// }
					// else
					// {
					fieldType = emm.getFieldByName(className, fieldName, true).getType();
					// }
				}
			}
		}

		if (StringUtils.isNullString(fieldName))
		{
			throw new ServiceRequestException("ID_SYS_NUMBERING_OBJECT_CHILDTYPE_NOTMATCH", "NumberingObject typeValue has changed", null, numbermodelFieldName);
		}
		String numberValue = "";

		ObjectGuid objectGuid = new ObjectGuid();
		objectGuid.setGuid(guid);
		objectGuid.setClassGuid(classGuid);
		SearchCondition searchCondition = SearchConditionFactory.createSearchCondition(objectGuid, null, false);
		// if (!isSystemField)
		// {
		searchCondition.addResultField(fieldName);
		// }
		searchCondition.setPageSize(1);
		FoundationObject foundationObject = ((BOASImpl) boas).getFoundationStub().getObject(objectGuid, false);
		if (foundationObject != null)
		{
			if (FieldTypeEnum.OBJECT.equals(fieldType))
			{
				numberValue = StringUtils.convertNULLtoString(foundationObject.getId());
			}
			else if (FieldTypeEnum.CODE.equals(fieldType) //
					|| FieldTypeEnum.CLASSIFICATION.equals(fieldType)//
					|| FieldTypeEnum.MULTICODE.equals(fieldType)//
					|| FieldTypeEnum.CODEREF.equals(fieldType))
			{
				// 字段类型为：Code/Classification/multicode 类型的处理
				String value = StringUtils.convertNULLtoString(foundationObject.get(fieldName));
				String[] codeGuidList = StringUtils.splitStringWithDelimiter(CodeObjectInfo.MULTI_CODE_DELIMITER_GUID, value);
				String type = numberingObject.getFieldType();
				if (StringUtils.isNullString(type))
				{
					type = "code";
				}
				if (codeGuidList != null)
				{
					StringBuffer stringBuffer = new StringBuffer();
					for (String codeItemGuid : codeGuidList)
					{

						if (!StringUtils.isNullString(codeItemGuid))
						{
							CodeItemInfo codeItem = emm.getCodeItem(codeItemGuid);
							if (codeItem != null)
							{
								String codeName = "";

								if (type.equalsIgnoreCase("title"))
								{
									codeName = codeItem.getTitle(((EMMImpl) emm).getUserSignature().getLanguageEnum());
								}
								else if (type.equalsIgnoreCase("code"))
								{
									codeName = codeItem.getCode();
								}
								codeName = StringUtils.convertNULLtoString(codeName);
								String[] str = { codeName, NumberingTypeEnum.FIELD.toString() };
								stringBuffer.append(codeName);
							}
						}
					}
					numberValue = stringBuffer.toString();
				}

			}
			else
			{
				numberValue = StringUtils.convertNULLtoString(foundationObject.get(fieldName));
			}
		}
		return numberValue;
	}

	/**
	 * 获取流水码
	 * 
	 * @param classGuid
	 * @param numberingModel
	 * @param numberingValueList
	 * @param sessionId
	 * @return
	 * @throws ServiceRequestException
	 */
	private String getSeries(String classGuid, String fieldName, NumberingModel numberingModel, List<String[]> numberingValueList, int stepLength, String startValue,
			DataAccessService dataAccessService) throws ServiceRequestException
	{
		if (SetUtils.isNullList(numberingValueList))
		{
			return null;
		}

		boolean isSerialNumberWithField = numberingModel.isSerialNumberWithField();
		boolean isSerialNumberWithDate = numberingModel.isSerialNumberWithDate();
		boolean onlyClass = !isSerialNumberWithDate && !isSerialNumberWithField;

		String prestr = "";
		String sufstr = "";
		String templateNumber = "";
		int sequenceIndex = 0;
		int noLength = 1;

		for (int i = 0; i < numberingValueList.size(); i++)
		{
			String[] str = numberingValueList.get(i);
			if (NumberingTypeEnum.SEQUENCE.toString().equals(str[1]))
			{
				noLength = Integer.parseInt(str[0]);
				sequenceIndex = i;
				templateNumber = templateNumber + "[NUMBER]";
			}
			else if (NumberingTypeEnum.STRING.toString().equals(str[1]))
			{
				templateNumber = templateNumber + str[0];
			}
			else if (NumberingTypeEnum.YEAR.toString().equals(str[1]) || NumberingTypeEnum.MONTH.toString().equals(str[1]) || NumberingTypeEnum.DAY.toString().equals(str[1]))
			{
				templateNumber = templateNumber + str[0];

				// 如果日期参与流水，那么放入前缀
				if (isSerialNumberWithDate)
				{
					if (i > sequenceIndex)
					{
						sufstr = sufstr + str[0];
					}
					else
					{
						prestr = prestr + str[0];
					}
				}
			}
			else if (NumberingTypeEnum.FIELD.toString().equals(str[1]))
			{
				templateNumber = templateNumber + str[0];
				if (isSerialNumberWithField)
				{
					if (i > sequenceIndex)
					{
						sufstr = sufstr + str[0];
					}
					else
					{
						prestr = prestr + str[0];
					}
				}
			}
		}

		// 字段参与流水
		String controlValue = null;
		if (onlyClass)
		{
			// 字段不参与流水，整个类一个流水码
			controlValue = null;
		}
		else
		{
			controlValue = StringUtils.convertNULLtoString(prestr) + StringUtils.convertNULLtoString(sufstr);

		}

		SystemDataService sds = DataServer.getSystemDataService();

		long serialValue = 0;
		if (StringUtils.isNullString(startValue))
		{
			startValue = "0";
		}

		if (StringUtils.isNullString(controlValue))
		{
			controlValue = null;
		}

		ClassificationNumberTrans classificationNumberTrans = new ClassificationNumberTrans();
		classificationNumberTrans.setUpdateUserGuid(dataAccessService.getUserSignature().getUserGuid());

		classificationNumberTrans.setClassificationFeatureItem(classGuid);
		classificationNumberTrans.setConsengValues(controlValue);
		classificationNumberTrans.setFieldName(fieldName);

		// String uuid = UUID.randomUUID().toString();
		// DataServer.getTransactionManager().startTransaction(uuid);
		try
		{
			int save = sds.update(ClassificationNumberTrans.class, classificationNumberTrans, "updateModelSerial");

			// 新建
			if (0 == save)
			{
				try
				{
					serialValue = Long.parseLong(startValue);
				}
				catch (Exception e)
				{
					throw new ServiceRequestException("ID_APP_CLASSIFICATION_TRAN_LONG", "trans error");
				}
				classificationNumberTrans = new ClassificationNumberTrans();
				classificationNumberTrans.setClassificationFeatureItem(classGuid);
				classificationNumberTrans.setFieldName(fieldName);
				classificationNumberTrans.setConsengValues(controlValue);

				classificationNumberTrans.setTransNumber(serialValue);

				classificationNumberTrans.setUpdateUserGuid(dataAccessService.getUserSignature().getUserGuid());
				classificationNumberTrans.setCreateUserGuid(dataAccessService.getUserSignature().getUserGuid());

				sds.save(classificationNumberTrans);
			}
			else
			{

				Map<String, Object> filter = new HashMap<String, Object>();
				filter.put(ClassificationNumberTrans.CLASSIFICATIONFEATUREITEM, classGuid);
				filter.put(ClassificationNumberTrans.CONSENGVALUES, controlValue);
				filter.put(ClassificationNumberTrans.FIELDNAME, fieldName);
				classificationNumberTrans = sds.queryObject(ClassificationNumberTrans.class, filter, "select");
				serialValue = classificationNumberTrans.getTransNumber();
			}
			// DataServer.getTransactionManager().commitTransaction();
		}
		catch (DynaDataException e)
		{
			// DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(dataAccessService, e);
		}
		catch (Exception e)
		{
			// DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}
		String serialStr = String.valueOf(serialValue);
		if (noLength > 0)
		{
			serialStr = this.fillLength(noLength, serialStr);
		}
		String replace = templateNumber.replace("[NUMBER]", serialStr);
		return replace;
	}

}
