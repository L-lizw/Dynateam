package dyna.data.service;

import dyna.common.conf.ServiceDefinition;
import dyna.common.exception.ServiceNotFoundException;
import dyna.data.context.DataServerContext;
import dyna.net.service.Service;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class DataRuleService implements Service
{
	@Autowired
	protected DataServerContext	serviceContext	= null;
	protected ServiceDefinition	serviceDef		= null;

	@Override public void init()
	{

	}

	@Override
	public void setServiceDefinition(ServiceDefinition serviceDef)
	{
		this.serviceDef = serviceDef;
	}

	/**
	 * get other available service from current service
	 * 
	 * @param <T>
	 * @param serviceClass
	 * @return
	 * @throws ServiceNotFoundException
	 */
	public <T extends Service> T getRefService(Class<T> serviceClass) throws ServiceNotFoundException
	{
		return (T) this.serviceContext.getInternalService(serviceClass);
	}
}
