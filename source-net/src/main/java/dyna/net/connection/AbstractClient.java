/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: AbstractClient
 * Wanglei 2010-11-30
 */
package dyna.net.connection;

import dyna.common.conf.ConfigurableClientImpl;
import dyna.net.dispatcher.sync.ServiceStateChangeReactor;
import dyna.net.dispatcher.sync.ServiceStateChangeReactorDefaultImpl;
import dyna.net.impl.ServiceProviderFactory;
import dyna.net.service.das.CSA;

/**
 * @author Wanglei
 *
 */
public abstract class AbstractClient extends GenericClient
{

	protected ConfigurableClientImpl	clientConfig	= null;

	private ServiceStateChangeReactor	sscReactor		= new ServiceStateChangeReactorDefaultImpl();

	protected boolean					isOpened		= false;

	protected AbstractClient(ConfigurableClientImpl clientConfig) throws Exception
	{
		this.clientConfig = clientConfig;
	}

	protected abstract void initServiceProvider() throws Exception;

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.connection.Connection#open()
	 */
	@Override
	public void open() throws Exception
	{
		this.initServiceProvider();
		this.isOpened = true;
		ServiceProviderFactory.getServiceProvider(this).setSessionMgrCredential(
				ServiceProviderFactory.getServiceProvider(this).getServiceInstance(CSA.class).registerSessionManager());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.connection.Connection#close()
	 */
	@Override
	public void close()
	{
		ServiceProviderFactory.getServiceProvider(this).destroy();
		this.isOpened = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.connection.Connection#destroySession(java.lang.String)
	 */
	@Override
	public void destroySession(String sessionId)
	{
		ServiceProviderFactory.getServiceProvider(this).clearServiceInstance(sessionId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.connection.Connection#isOpened()
	 */
	@Override
	public boolean isOpened()
	{
		return this.isOpened;
	}

	/**
	 * 获取服务状态变更协调器
	 * 
	 * @return the sscReactor
	 */
	public ServiceStateChangeReactor getSscReactor()
	{
		return this.sscReactor;
	}

	/**
	 * 设置服务状态变更协调器
	 * 
	 * @param sscReactor
	 *            the sscReactor to set
	 */
	public void setSscReactor(ServiceStateChangeReactor sscReactor)
	{
		this.sscReactor = sscReactor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.connection.Connection#testConnection()
	 */
	@Override
	public boolean testConnection()
	{
		return true;
	}

}
