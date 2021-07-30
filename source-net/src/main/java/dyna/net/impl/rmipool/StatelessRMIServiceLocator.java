/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: GenericRMIServiceLocator
 * Wanglei 2010-11-30
 */
package dyna.net.impl.rmipool;

import org.springframework.remoting.rmi.RmiProxyFactoryBean;
import org.springframework.remoting.support.RemoteInvocationFactory;

import dyna.common.bean.serv.ServiceBean;
import dyna.common.conf.ConfigurableClientImpl;
import dyna.common.exception.ServiceNotFoundException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.util.SetUtils;
import dyna.net.dispatcher.DispatcherRemoteInvocationFactory;
import dyna.net.dispatcher.ServiceDispatcher;
import dyna.net.dispatcher.sync.ServiceStateExchanger;
import dyna.net.dispatcher.sync.ServiceStateExchangerRMIImpl;
import dyna.net.impl.AbstractServiceLocator;

import dyna.net.service.Service;

/**
 * 使用rmi协议的通用服务定位器
 * 
 * @author Wanglei
 * 
 */
public class StatelessRMIServiceLocator extends AbstractServiceLocator
{
	protected String					serviceHost				= null;

	protected String					lookupServiceHost		= null;
	protected int						lookupServicePort		= 1099;
	protected String					lookupService			= null;
	protected Class<ServiceDispatcher>	lookupServiceInterface	= ServiceDispatcher.class;

	protected StatelessRMIClient				client					= null;
	protected ServiceDispatcher			dispatcher				= null;

	private ServiceStateExchanger		ssExchanger				= null;

	private String						moduleName				= null;

	private RemoteInvocationFactory		remoteInvocationFactory	= null;


	public StatelessRMIServiceLocator(StatelessRMIClient client, String moduleName, ConfigurableClientImpl clientConfig) throws Exception
	{
		super();
		this.client = client;
		this.moduleName = moduleName;
		this.lookupServiceHost = clientConfig.getLookupServiceHost().getIpAddress();
		this.lookupServicePort = clientConfig.getLookupServiceHost().getPort();
		this.lookupService = "serviceDispatcher";
		this.serviceHost=lookupServiceHost;
		this.remoteInvocationFactory = new DispatcherRemoteInvocationFactory(client);
		this.retreiveAvailableServiceList();
		this.ssExchanger = new ServiceStateExchangerRMIImpl(this.dispatcher);
		this.getServiceStateSync().setExchanger(this.ssExchanger);
	}

	@Override
	public ServiceBean getServiceBean(String serviceID)
	{
		return this.serviceBeanMap.get(serviceID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.spi.ServiceLocator#lookup(java.lang.Class, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Service> T lookup(Class<T> serviceClass, String credential) throws ServiceNotFoundException
	{
		this.client.bindSeessionId(credential);
		T service = super.lookup(serviceClass, moduleName);
		if (service != null)
		{
			return service;
		}

		ServiceBean serviceBean = this.getServiceBean(serviceClass.getName());
		if (serviceBean == null)
		{
			throw new ServiceNotFoundException("service not found: " + serviceClass.getName());
		}

		String serviceUrl = "rmi://" + this.serviceHost + ":" + serviceBean.getDispatchPort() + "/"
				+ serviceBean.getServcieId();

		RmiProxyFactoryBean rmiFactory = new RmiProxyFactoryBean();
		rmiFactory.setServiceUrl(serviceUrl);
		rmiFactory.setServiceInterface(this.getServiceClassById(serviceBean.getServcieId()));

		rmiFactory.setRemoteInvocationFactory(this.createRemoteInvocationFactory(serviceClass));

		rmiFactory.setRefreshStubOnConnectFailure(true);

		 try
		 {
			 rmiFactory.afterPropertiesSet();
		 }
		 catch (Exception e)
		 {
		 	e.printStackTrace();
		 }


		service = (T) rmiFactory.getObject();

		this.addService(serviceClass, moduleName, service);

		return service;

	}

	private synchronized void retreiveAvailableServiceList() throws Exception
	{
		String serviceUrl = "rmi://" + this.lookupServiceHost + ":" + this.lookupServicePort + "/" + this.lookupService;

		RmiProxyFactoryBean rmiFactory = new RmiProxyFactoryBean();

		rmiFactory.setServiceInterface(this.lookupServiceInterface);
		rmiFactory.setServiceUrl(serviceUrl);

		rmiFactory.setRemoteInvocationFactory(this.remoteInvocationFactory);

		rmiFactory.setRefreshStubOnConnectFailure(true);

		rmiFactory.afterPropertiesSet();

		this.dispatcher = (ServiceDispatcher) rmiFactory.getObject();

		this.client.setClientIdentifier(this.dispatcher.getConnectionCredential(this.moduleName));

		this.serviceList = this.dispatcher.listService();
		if (SetUtils.isNullList(this.serviceList))
		{
			return;
		}

		String sid = null;
		for (ServiceBean bean : this.serviceList)
		{
			sid = bean.getServcieId();
			if (!this.serviceBeanMap.containsKey(sid))
			{
				this.serviceBeanMap.put(sid, bean);
				this.serviceBeanMap.put(bean.getServiceInterfaceName(), bean);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.impl.AbstractServiceLocator#shutdown()
	 */
	@Override
	public void shutdown()
	{
		super.shutdown();
		try
		{
			this.dispatcher.disconnect(this.moduleName);
		}
		catch (ServiceRequestException e)
		{
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.impl.AbstractServiceLocator#isAvailable()
	 */
	@Override
	public boolean isAvailable()
	{
		try
		{
			return this.dispatcher.isConnected();
		}
		catch (Exception e)
		{
			DynaLogger.error(e);
			return false;
		}
	}

	public RemoteInvocationFactory createRemoteInvocationFactory(Class<? extends Service> serviceClass)
	{
		return new SecureRemoteInvocationFactory(this.client, serviceClass);
	}
}
