package dyna.app.service.brs.dcr.checkrule;

import dyna.app.service.DataAccessService;
import dyna.common.bean.data.FoundationObject;
import dyna.common.exception.ServiceNotFoundException;
import dyna.common.exception.ServiceRequestException;
import dyna.net.service.Service;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractCondition implements ICondition
{
	private Map<String, FoundationObject>	dataMap				= new HashMap<>();

	private FoundationObject				foundationObject	= null;

	private String							sessionId			= null;

	private DataAccessService				service				= null;

	public <T extends Service> T getServiceInstance(Class<T> serviceClass) throws ServiceRequestException
	{
		try
		{
			return this.service.getRefService(serviceClass);
		}
		catch (ServiceNotFoundException e)
		{
			throw new ServiceRequestException(null, null, e);
		}
	}

	@Override
	public abstract boolean check() throws ServiceRequestException;

	@Override
	public FoundationObject getFoundationObject()
	{
		return foundationObject;
	}

	@Override
	public void setFoundationObject(FoundationObject foundationObject)
	{
		this.foundationObject = foundationObject;
	}

	@Override
	public String getSessionId()
	{
		return sessionId;
	}

	@Override
	public void setSessionId(String sessionId)
	{
		this.sessionId = sessionId;
	}

	@Override
	public Map<String, FoundationObject> getDataMap()
	{
		return dataMap;
	}

	@Override
	public void setDataMap(Map<String, FoundationObject> dataMap)
	{
		this.dataMap = dataMap;
	}

	@Override
	public void setService(DataAccessService service)
	{
		this.service = service;
	}
}
