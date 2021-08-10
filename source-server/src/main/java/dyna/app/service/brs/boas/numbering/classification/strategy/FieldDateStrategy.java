package dyna.app.service.brs.boas.numbering.classification.strategy;

import java.util.Date;
import java.util.Map;

import dyna.app.service.brs.boas.numbering.classification.DateAndTimeStrategy;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.model.code.ClassficationFeatureItem;
import dyna.common.dto.cfm.ClassificationNumberField;
import dyna.common.util.DateFormat;

public class FieldDateStrategy extends DateAndTimeStrategy
{

	@Override
	protected String getDefaultFormat()
	{
		return DateFormat.PTN_YMD;
	}

	@Override
	protected Date getDate(Map<String, FoundationObject> classificationMap, ClassificationNumberField field,
			ClassficationFeatureItem item)
	{
		// FoundationObject sourceFoundation = this.getSourceFoundation(classificationMap, field,
		// classificationItemGuid);
		// Object object = sourceFoundation.get(field.getFieldName());
		Object object = this.getValue(classificationMap, field, item, field.getFieldName());
		if (object instanceof Date)
		{
			return (Date) object;
		}

		// if (object instanceof Timestamp)
		// {
		// return ((Timestamp) object).;
		// }

		return null;

	}
}
