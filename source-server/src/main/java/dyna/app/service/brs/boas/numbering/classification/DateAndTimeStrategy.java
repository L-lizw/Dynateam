package dyna.app.service.brs.boas.numbering.classification;

import java.util.Date;
import java.util.Map;

import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.model.code.ClassficationFeatureItem;
import dyna.common.dto.cfm.ClassificationNumberField;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.DateFormat;
import dyna.common.util.StringUtils;

public abstract class DateAndTimeStrategy extends AbstractStratery
{

	@Override
	protected String getSourceDate(AllocateParameter parameter) throws ServiceRequestException
	{

		String value = "";
		Date date = this.getDate(parameter.classificationMap, parameter.field, parameter.item);
		if (date != null)
		{
			if (!StringUtils.isNullString(parameter.field.getTypeValue()))
			{
				value = DateFormat.format(date, parameter.field.getTypeValue());
			}
			else
			{
				value = DateFormat.format(date, this.getDefaultFormat());
			}
		}
		return value;
	}

	protected abstract String getDefaultFormat();

	protected Date getDate(Map<String, FoundationObject> classificationMap, ClassificationNumberField field,
			ClassficationFeatureItem item)
	{
		Date date = null;
		if (field.isDateByUser())
		{
			date = DateFormat.parse(field.getTypeValue(), this.getDefaultFormat());
		}
		else
		{
			date = DateFormat.getSysDate();
		}

		return date;
	}

}
