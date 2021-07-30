package dyna.data.service;

import dyna.common.conf.ServiceDefinition;
import dyna.common.exception.ServiceNotFoundException;
import dyna.data.context.DataServerContext;
import dyna.net.service.Service;

public class DataRuleService implements Service
{
	protected DataServerContext	serviceContext	= null;
	protected ServiceDefinition	serviceDef		= null;

	public DataRuleService(DataServerContext context, ServiceDefinition sd)
	{
		this.serviceContext = context;
		this.serviceDef = sd;
		this.init();
	}

	protected void init()
	{
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
