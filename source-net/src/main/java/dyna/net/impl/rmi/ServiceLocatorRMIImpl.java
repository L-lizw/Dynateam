/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServcieLocatorClientImpl
 * Wanglei 2010-4-6
 */
package dyna.net.impl.rmi;

import org.springframework.remoting.support.RemoteInvocationFactory;

import dyna.common.bean.serv.ServiceBean;
import dyna.common.conf.ConfigurableClientImpl;
import dyna.common.exception.ServiceNotFoundException;
import dyna.net.connection.GenericClient;
import dyna.net.impl.GenericRMIServiceLocator;
import dyna.net.service.Service;

/**
 * 服务定位搜索器的远程搜索实现类
 * 
 * @author Wanglei
 * 
 */
public class ServiceLocatorRMIImpl extends GenericRMIServiceLocator
{

	public ServiceLocatorRMIImpl(GenericClient client, String moduleName, ConfigurableClientImpl clientConfig)
			throws Exception
	{
		super(client, moduleName, clientConfig.getLookupServiceHost().getIpAddress(), clientConfig
				.getLookupServiceHost().getPort(), "serviceDispatcher");
		this.serviceHost = clientConfig.getLookupServiceHost().getIpAddress();
	}

	@Override
	public ServiceBean getServiceBean(String serviceID)
	{
		return this.serviceBeanMap.get(serviceID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.impl.GenericRMIServiceLocator#lookup(java.lang.Class, java.lang.String)
	 */
	@Override
	public <T extends Service> T lookup(Class<T> serviceClass, String credential) throws ServiceNotFoundException
	{
		if (credential == null)
		{
			credential = this.client.getClientIdentifier();
		}
		return super.lookup(serviceClass, credential);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.impl.GenericRMIServiceLocator#createRemoteInvocationFactory(java.lang.Class, java.lang.String)
	 */
	@Override
	public RemoteInvocationFactory createRemoteInvocationFactory(Class<? extends Service> serviceClass,
			String credential)
	{
		return new SecureRemoteInvocationFactory(this.client, serviceClass, credential);
	}
}
