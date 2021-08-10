/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: LocalServiceLocatorImpl
 * Wanglei 2010-4-6
 */
package dyna.app.net.impl.server;

import dyna.app.server.context.ServiceContext;
import dyna.common.bean.serv.ServiceBean;
import dyna.common.conf.ServiceDefinition;
import dyna.common.exception.ServiceNotFoundException;
import dyna.net.dispatcher.sync.ServiceStateExchanger;
import dyna.net.impl.AbstractServiceLocator;
import dyna.net.security.CredentialManager;
import dyna.net.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.reflect.Proxy;
import java.util.Observable;
import java.util.Observer;

/**
 * 服务定位器, 在服务端的实现, 直接通过服务上下文定位服务
 * 
 * @author Wanglei
 * 
 */
@org.springframework.stereotype.Service
public class ServiceLocatorServerImpl extends AbstractServiceLocator implements Observer
{

	@Autowired
	private ServiceContext			serviceContext	= null;
	@Qualifier(value = "serviceStateExchangerServerImpl")
	private ServiceStateExchanger	ssExchanger		= null;

	public ServiceLocatorServerImpl()
	{
		super();
		this.getServiceStateSync().setExchanger(this.ssExchanger);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.impl.AbstractServiceLocator#lookup(java.lang.Class, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Service> T lookup(Class<T> serviceClass, String credential) throws ServiceNotFoundException
	{

		if (credential == null)
		{
			credential = CredentialManager.INTERNAL_CREDENTIAL;
		}

		T service = super.lookup(serviceClass, credential);
		if (service != null)
		{
			return service;
		}

		ServiceBean sb = this.getServiceBean(serviceClass.getName());
		if (sb == null)
		{
			throw new ServiceNotFoundException("service not found: " + serviceClass.getName());
		}

		service = (T) Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class<?>[] { serviceClass },
				new ServiceDelegatorServerImpl(this.serviceContext, serviceClass, credential));

		this.addService(serviceClass, credential, service);

		return service;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public synchronized void update(Observable o, Object arg)
	{
		if (arg instanceof ServiceDefinition)
		{
			ServiceBean sb = new ServiceBean((ServiceDefinition) arg);
			String sid = sb.getServcieId();
			if (!this.serviceBeanMap.containsKey(sid))
			{
				this.serviceBeanMap.put(sid, sb);
				this.serviceBeanMap.put(sb.getServiceInterfaceName(), sb);
			}
		}
	}

}
