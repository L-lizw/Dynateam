package dyna.app.service.brs.boas.numbering.classification.strategy;

import java.util.HashMap;
import java.util.Map;

import dyna.app.service.DataAccessService;
import dyna.app.service.brs.boas.numbering.classification.AllocateParameter;
import dyna.app.service.brs.boas.numbering.classification.FieldStratery;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.bean.data.coding.ClassificationNumberTrans;
import dyna.common.bean.model.code.ClassficationFeatureItem;
import dyna.common.dto.cfm.ClassificationNumberField;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;
import dyna.net.service.brs.EMM;

public class ExpandSerialStrategy extends FieldStratery
{

	@Override
	protected String getSourceDate(AllocateParameter parameter) throws ServiceRequestException
	{
		String controlledNumberFieldGuid = parameter.field.getControlledNumberFieldGuid();
		if (StringUtils.isNullString(controlledNumberFieldGuid))
		{
			return this.getSerial(parameter.item, parameter.field, null, parameter.dataAccessService);
		}

		Map<String, ClassificationNumberField> numberFieldMap = new HashMap<String, ClassificationNumberField>();
		for (ClassificationNumberField numberField : parameter.classificationNumberFieldList)
		{
			numberFieldMap.put(numberField.getGuid(), numberField);
		}
		// boolean isChanged = false;
		String controlValue = "";
		String[] numberFieldGuidList = StringUtils.splitStringWithDelimiter(";", controlledNumberFieldGuid);
		if (numberFieldGuidList != null && numberFieldGuidList.length != 0)
		{
			for (String numberFieldGuid : numberFieldGuidList)
			{
				ClassificationNumberField controlNumberField = numberFieldMap.get(numberFieldGuid);
				if (controlNumberField == null)
				{
					continue;
				}

				if (!controlNumberField.isHasAllocate())
				{
					EMM emm = this.getService(parameter.dataAccessService, EMM.class);
					parameter.control.allocateNumberField(parameter.dataAccessService, parameter.item, parameter.classificationMap, parameter.classficationFeatureItemList,
							parameter.classificationNumberFieldList, controlNumberField, emm, parameter.isCreate);

				}

				// if (!StringUtils.isNullString(controlNumberField.getFieldName()))
				// {
				// if (controlNumberField.isFormClass() )
				// {
				// FoundationObject foundationObject = parameter.classificationMap
				// .get(ClassificationAllocate.TARGET_FOUNDATION);
				// // isChanged = foundationObject.isChanged(controlNumberField.getFieldName());
				// }
				// else if (!controlNumberField.isFormClass() )
				// {
				// FoundationObject foundationObject = parameter.classificationMap.get(parameter.item
				// .getClassificationItemGuid());
				// // isChanged = foundationObject.isChanged(controlNumberField.getFieldName());
				// }
				// }

				controlValue = controlValue + controlNumberField.getNumberFieldLastValue();
			}

		}
		// if (isChanged || parameter.isCreate)
		// {
		return this.getSerial(parameter.item, parameter.field, controlValue, parameter.dataAccessService);
		// }
		// else
		// {
		// parameter.item.setNoAllocate(true);
		// return null;
		// }
	}

	protected String getSerial(ClassficationFeatureItem item, ClassificationNumberField field, String controlValue, DataAccessService dataAccessService)
			throws DynaDataException, ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		long serialValue = 0;
		String startValue = "0";
		if (!StringUtils.isNullString(field.getTypeValue()))
		{
			startValue = field.getTypeValue();
		}

		if (StringUtils.isNullString(controlValue))
		{
			controlValue = null;
		}

		try
		{
			int save = 0;
			ClassificationNumberTrans classificationNumberTrans = null;
			if (!field.isUpdateSerial())
			{

				Map<String, Object> filter = new HashMap<String, Object>();
				if (!StringUtils.isGuid(item.getClassificationFeatureGuid()))
				{
					filter.put(ClassificationNumberTrans.CLASSIFICATIONFEATUREITEM, item.getClassGuid());
					filter.put(ClassificationNumberTrans.FIELDNAME, item.getFieldName());
				}
				else
				{
					filter.put(ClassificationNumberTrans.CLASSIFICATIONFEATUREITEM, item.getGuid());
				}
				filter.put(ClassificationNumberTrans.CONSENGVALUES, controlValue);
				classificationNumberTrans = sds.queryObject(ClassificationNumberTrans.class, filter, "select");
				if (classificationNumberTrans == null)
				{
					save = 0;
				}
				else
				{
					save = 1;
				}
			}
			else
			{
				classificationNumberTrans = new ClassificationNumberTrans();
				classificationNumberTrans.setUpdateUserGuid(dataAccessService.getUserSignature().getUserGuid());

				if (!StringUtils.isGuid(item.getClassificationFeatureGuid()))
				{
					classificationNumberTrans.setClassificationFeatureItem(item.getClassGuid());
					classificationNumberTrans.setFieldName(item.getFieldName());
				}
				else
				{
					classificationNumberTrans.setClassificationFeatureItem(item.getGuid());
				}
				classificationNumberTrans.setConsengValues(controlValue);

				save = sds.update(ClassificationNumberTrans.class, classificationNumberTrans, "updateModelSerial");

			}

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

				if (!StringUtils.isGuid(item.getClassificationFeatureGuid()))
				{
					classificationNumberTrans.setClassificationFeatureItem(item.getClassGuid());
					classificationNumberTrans.setFieldName(item.getFieldName());
				}
				else
				{
					classificationNumberTrans.setClassificationFeatureItem(item.getGuid());
				}
				classificationNumberTrans.setConsengValues(controlValue);
				classificationNumberTrans.setTransNumber(serialValue);

				classificationNumberTrans.setUpdateUserGuid(dataAccessService.getUserSignature().getUserGuid());
				classificationNumberTrans.setCreateUserGuid(dataAccessService.getUserSignature().getUserGuid());

				sds.save(classificationNumberTrans);
			}
			else
			{
				Map<String, Object> filter = new HashMap<String, Object>();
				if (!StringUtils.isGuid(item.getClassificationFeatureGuid()))
				{
					filter.put(ClassificationNumberTrans.CLASSIFICATIONFEATUREITEM, item.getClassGuid());
					filter.put(ClassificationNumberTrans.FIELDNAME, item.getFieldName());
				}
				else
				{
					filter.put(ClassificationNumberTrans.CLASSIFICATIONFEATUREITEM, item.getGuid());
				}
				filter.put(ClassificationNumberTrans.CONSENGVALUES, controlValue);
				classificationNumberTrans = sds.queryObject(ClassificationNumberTrans.class, filter, "select");
				serialValue = classificationNumberTrans.getTransNumber();

				if (!field.isUpdateSerial())
				{
					// 更新最大流水,流水初始化
					long parseLong = 0;
					try
					{
						parseLong = Long.parseLong(startValue);
					}
					catch (Exception e)
					{
						throw new ServiceRequestException("ID_APP_CLASSIFICATION_TRAN_LONG", "trans error");
					}

					if (parseLong > serialValue)
					{
						serialValue = parseLong;

						classificationNumberTrans.setTransNumber(serialValue);
						classificationNumberTrans.setUpdateUserGuid(dataAccessService.getUserSignature().getUserGuid());
						sds.save(classificationNumberTrans);
					}
				}

				// serial = this.fillLength(field.getFieldlength(), number + "");

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
		if (field.getFieldlength() != null && field.getFieldlength() > 0)
		{
			serialStr = this.fillLength(field.getFieldlength(), serialStr);
		}
		return serialStr;
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
}
