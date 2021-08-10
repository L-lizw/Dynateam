package dyna.app.service.brs.boas.numbering.classification;

import java.util.Map;

import dyna.app.service.DataAccessService;
import dyna.app.service.brs.boas.numbering.ClassificationAllocate;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.model.code.ClassficationFeatureItem;
import dyna.common.dto.cfm.ClassificationNumberField;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.FieldTypeEnum;
import dyna.common.systemenum.SystemClassFieldEnum;
import dyna.common.util.NumberUtils;
import dyna.common.util.StringUtils;
import dyna.net.service.Service;

public abstract class AbstractStratery implements Strategy
{

	@Override
	public String run(AllocateParameter parameter) throws ServiceRequestException
	{
		String sourceDate = StringUtils.convertNULLtoString(this.getSourceDate(parameter));

		if (!StringUtils.isNullString(sourceDate))
		{
			if (parameter.field.getFieldlength() != 0)
			{
				if (sourceDate.length() > parameter.field.getFieldlength())
				{
					sourceDate = this.cut(sourceDate, parameter.field.getFieldlength());
				}
				else
				{
					sourceDate = this.fill(sourceDate, parameter.field.getFieldlength() - sourceDate.length());
				}
			}

			ClassField classField = parameter.numberClassField;
			if (classField != null && FieldTypeEnum.FLOAT.equals(classField.getType()))
			{
				String tempValue = StringUtils.convertNULLtoString(sourceDate);
				if (!StringUtils.isNullString(tempValue))
				{
					sourceDate = NumberUtils.getDecimalValueOfClassFieldSize(classField.getFieldSize(), tempValue);
				}
			}

			parameter.field.setNumberFieldValue(sourceDate);

			if (!StringUtils.isNullString(parameter.field.getPrefix()))
			{
				sourceDate = parameter.field.getPrefix() + sourceDate;
			}

			if (!StringUtils.isNullString(parameter.field.getSuffix()))
			{
				sourceDate = sourceDate + parameter.field.getSuffix();
			}

			parameter.field.setNumberFieldLastValue(sourceDate);
		}
		else
		{
			parameter.field.setNumberFieldValue(sourceDate);
			parameter.field.setNumberFieldLastValue(sourceDate);
		}
		parameter.field.setHasAllocate(true);
		return sourceDate;
	}

	protected String getSourceDate(AllocateParameter parameter) throws ServiceRequestException
	{
		throw new ServiceRequestException("not implemented");
	}

	protected String getFillValue()
	{
		return "0";
	}

	protected String cut(String value, int length)
	{
		return value.substring(0, length);
	}

	protected String fill(String value, int length)
	{
		String fillValue = this.getFillValue();
		if (StringUtils.isNullString(fillValue))
		{
			return value;
		}

		for (int i = 0; i < length; i++)
		{
			value = fillValue + value;
		}

		return value;
	}

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

	protected Object getValue(Map<String, FoundationObject> classificationMap, ClassificationNumberField field, ClassficationFeatureItem item, String fieldName)
	{
		FoundationObject foundation = null;
		if (field.isFormClass())
		{
			foundation = classificationMap.get(ClassificationAllocate.TARGET_FOUNDATION);
		}
		else
		{
			foundation = classificationMap.get(item.getInfo().get("CLASSIFICATIONGROUPNAME"));
		}
		if (foundation == null)
		{
			return null;
		}
		// 判断参与编码的字段值有无变化，若无变化则不合成
		// if (foundation.isChanged(fieldName)
		// || (!field.isFormClass() && !StringUtils.isNullString((String) foundation
		// .get(FoundationObjectImpl.ORICLASSIFICATIONITEM)))
		// || (!field.isFormClass() && StringUtils.isNullString(foundation.getObjectGuid().getGuid())))
		if (foundation.isChanged(fieldName) || (foundation.isChanged(SystemClassFieldEnum.CLASSIFICATION.getName()))
				|| (!field.isFormClass() && StringUtils.isNullString(foundation.getObjectGuid().getGuid())))

		{
			item.setNeedAllocate(true);
		}

		return foundation.get(fieldName);

	}

	// protected FoundationObject getFoundation(Map<String, FoundationObject> classificationMap,
	// ClassificationNumberField field, ClassficationFeatureItem item)
	// {
	// if (field.isFormClass())
	// {
	// return classificationMap.get(ClassificationAllocate.TARGET_FOUNDATION);
	// }
	// else
	// {
	// return classificationMap.get(item.getClassificationItemGuid());
	// }
	//
	// }
}
