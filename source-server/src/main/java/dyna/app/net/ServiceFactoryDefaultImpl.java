/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServiceFactoryDefaultImpl
 * Wanglei 2011-4-18
 */
package dyna.app.net;

import dyna.app.server.context.ServiceContext;
import dyna.common.Poolable;
import dyna.common.conf.ServiceDefinition;

/**
 * @author Wanglei
 *
 */
public class ServiceFactoryDefaultImpl implements ServiceFactory
{
	private Class<?>			serviceImpl			= null;
	private ServiceContext		serviceContext		= null;
	private ServiceDefinition	serviceDefinition	= null;

	public ServiceFactoryDefaultImpl(Class<?> serviceImpl, ServiceContext serviceContext, ServiceDefinition serviceDefinition)
	{
		this.serviceImpl = serviceImpl;
		this.serviceContext = serviceContext;
		this.serviceDefinition = serviceDefinition;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.net.ServiceFactory#createService()
	 */
	@Override
	public Object createService() throws Exception
	{
		Object obj = this.serviceImpl.newInstance();

		((Poolable) obj).initObject(this.serviceContext, this.serviceDefinition);

		return obj;
	}

}
