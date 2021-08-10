package dyna.app.service.brs.iop;

import java.util.Date;
import java.util.List;
import java.util.Map;

import dyna.app.service.BusinessRuleService;
import dyna.common.SearchCondition;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.configparamter.DrivenResult;
import dyna.common.bean.data.iopconfigparamter.IOPColumnTitle;
import dyna.common.bean.data.iopconfigparamter.IOPColumnValue;
import dyna.common.bean.data.iopconfigparamter.IOPConfigParameter;
import dyna.common.dto.DataRule;
import dyna.common.exception.ServiceNotFoundException;
import dyna.common.exception.ServiceRequestException;
import dyna.net.service.brs.BOAS;
import dyna.net.service.brs.IOP;
import dyna.net.service.das.MSRM;

public class IOPImpl extends BusinessRuleService implements IOP
{

	private IOPConfigParameterStub iopConfigParameterStub = null;
	private DrivenTestStub			drivenTestStub			= null;
	private PassiveUpdateConfig		passiveUpdateConfigStub	= null;
	
	/**
	 * @return the bomStub
	 */
	public IOPConfigParameterStub getIOPStub()
	{
		if (this.iopConfigParameterStub == null)
		{
			this.iopConfigParameterStub = new IOPConfigParameterStub(this.serviceContext, this);
		}
		return this.iopConfigParameterStub;
	}

	public DrivenTestStub getDrivenTestStub()
	{
		if (this.drivenTestStub == null)
		{
			this.drivenTestStub = new DrivenTestStub(this.serviceContext, this);
		}
		return this.drivenTestStub;
	}

	public PassiveUpdateConfig getPassiveUpdateConfigStub()
	{
		if (this.passiveUpdateConfigStub == null)
		{
			this.passiveUpdateConfigStub = new PassiveUpdateConfig(this.serviceContext, this);
		}
		return this.passiveUpdateConfigStub;
	}
	public synchronized BOAS getBOAS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(BOAS.class);
		}
		catch (ServiceNotFoundException e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}
	}

	public synchronized MSRM getMSRM() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(MSRM.class);
		}
		catch (ServiceNotFoundException e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}
	}

	@Override
	public void release(String masterGuid, String foundationId) throws ServiceRequestException
	{
		this.getPassiveUpdateConfigStub().release(masterGuid, foundationId);
	}

	@Override
	public List<IOPConfigParameter> listIOPConfigParameter(String masterGuid, Date end1ReleaseTime) throws ServiceRequestException
	{
		return this.getIOPStub().listIOPConfigParameter(masterGuid, end1ReleaseTime);
	}

	@Override
	public void syncIOPConfigParameter(ObjectGuid end1ObjectGuid, Date end1ReleaseTime, List<IOPColumnTitle> listTitles, Map<Integer, List<IOPColumnValue>> listValues)
			throws ServiceRequestException
	{
		this.getIOPStub().syncIOPConfigParameter(end1ObjectGuid, end1ReleaseTime, listTitles, listValues);
	}

	@Override
	public DrivenResult drivenTest(ObjectGuid objectGuid, SearchCondition condition, SearchCondition end2SearchCondition, DataRule rule, String codeValue, boolean isAppend)
			throws ServiceRequestException
	{
		return this.getDrivenTestStub().drivenTest(objectGuid, condition, end2SearchCondition, rule, codeValue, isAppend);
	}

	@Override
	public List<String> listLID(String masterGuid, Date end1ReleaseTime) throws ServiceRequestException
	{
		return this.getIOPStub().listLID(masterGuid, end1ReleaseTime);
	}

}
