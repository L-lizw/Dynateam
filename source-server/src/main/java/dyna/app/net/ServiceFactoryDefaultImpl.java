/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServiceFactoryDefaultImpl
 * Wanglei 2011-4-18
 */
package dyna.app.net;

import dyna.common.Poolable;
import dyna.common.conf.ServiceDefinition;

/**
 * @author Wanglei
 *
 */
public class ServiceFactoryDefaultImpl implements ServiceFactory
{
	private Class<?>			serviceImpl			= null;
	private ServiceDefinition	serviceDefinition	= null;

	public ServiceFactoryDefaultImpl(Class<?> serviceImpl , ServiceDefinition serviceDefinition)
	{
		this.serviceImpl = serviceImpl;
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

		((Poolable) obj).initObject(this.serviceDefinition);

		return obj;
	}

}
