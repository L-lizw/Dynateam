/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServiceLocatorDataServerRMIImpl
 * Wanglei 2010-11-30
 */
package dyna.net.impl.data;

import dyna.common.conf.ConfigurableConnToDSImpl;
import dyna.common.exception.ServiceNotFoundException;
import dyna.common.util.SetUtils;
import dyna.net.connection.GenericClient;
import dyna.net.impl.GenericRMIServiceLocator;
import dyna.net.service.Service;
import dyna.net.spi.DataServiceLocator;
import org.springframework.remoting.support.RemoteInvocationFactory;

/**
 * 数据服务定位器
 * 
 * @author Wanglei
 * 
 */
public class ServiceLocatorDataServerRMIImpl extends GenericRMIServiceLocator implements DataServiceLocator
{

	public ServiceLocatorDataServerRMIImpl(GenericClient client, String moduleName,
			ConfigurableConnToDSImpl conToDsConfig) throws Exception
	{
		super(client, moduleName, conToDsConfig.getLookupServiceHost(), conToDsConfig.getLookupServicePort(),
				"dataServiceDispatcher");
		this.serviceHost = conToDsConfig.getLookupServiceHost();
	}

	@Override
	public void init()
	{
		if (SetUtils.isNullList(this.serviceList))
		{
			return;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.spi.DataServiceLocator#lookup(java.lang.Class)
	 */
	@Override
	public <T extends Service> T lookup(Class<T> serviceClass) throws ServiceNotFoundException
	{
		return super.lookup(serviceClass, this.client.getClientIdentifier());
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
		return new SecureRemoteInvocationDSFactory(this.client, serviceClass, credential);
	}
}
