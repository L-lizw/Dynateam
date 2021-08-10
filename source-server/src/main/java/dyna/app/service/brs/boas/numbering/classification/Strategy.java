package dyna.app.service.brs.boas.numbering.classification;

import dyna.common.exception.ServiceRequestException;

public interface Strategy
{
	public String run(AllocateParameter parameter) throws ServiceRequestException;

}
