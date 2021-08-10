package dyna.app.service.brs.boas.numbering.classification.strategy;

import dyna.app.service.brs.boas.numbering.classification.AllocateParameter;
import dyna.app.service.brs.boas.numbering.classification.FieldStratery;
import dyna.common.exception.ServiceRequestException;

public class FieldBooleanStrategy extends FieldStratery
{

	@Override
	protected String getSourceDate(AllocateParameter parameter) throws ServiceRequestException
	{

		Object value = this.getValue(parameter.classificationMap, parameter.field, parameter.item,
				parameter.field.getFieldName());
		return (String) value;
	}
}
