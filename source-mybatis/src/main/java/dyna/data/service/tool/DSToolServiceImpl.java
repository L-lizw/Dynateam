package dyna.data.service.tool;

import dyna.common.bean.data.FoundationObject;
import dyna.common.conf.ServiceDefinition;
import dyna.common.exception.ServiceRequestException;
import dyna.data.context.DataServerContext;
import dyna.data.service.DataRuleService;

import java.util.Map;

public class DSToolServiceImpl extends DataRuleService implements DSToolService
{
	private DSToolStub toolStub;

	public DSToolServiceImpl(DataServerContext context, ServiceDefinition sd)
	{
		super(context, sd);
	}

	protected DSToolStub getDSToolStub()
	{
		return this.toolStub;
	}

	@Override
	public void clearErrDataInMast(String className) throws ServiceRequestException
	{
		this.getDSToolStub().clearErrDataInMast(className);
	}

	@Override
	public void changeFoundationStatus(Map<String, Object> parameterMap) throws ServiceRequestException
	{
		this.getDSToolStub().changeFoundationStatus(parameterMap);
	}

	@Override
	public void refreshMergeFieldValue(FoundationObject foundationObject, String fieldName) throws ServiceRequestException
	{
		this.getDSToolStub().refreshMergeFieldValue(foundationObject, fieldName);
	}
}
