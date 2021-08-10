package dyna.app.service.brs.boas.numbering.classification.strategy;

import dyna.app.service.brs.boas.numbering.classification.AbstractStratery;
import dyna.app.service.brs.boas.numbering.classification.AllocateParameter;
import dyna.common.exception.ServiceRequestException;

public class ExpandFixedStrategy extends AbstractStratery
{

	@Override
	protected String getSourceDate(AllocateParameter parameter) throws ServiceRequestException
	{
		return parameter.field.getTypeValue();
	}
}
