/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: RemoteClient
 * Wanglei 2010-11-30
 */
package dyna.net.impl.rmipool;

import dyna.common.conf.ConfigurableClientImpl;
import dyna.common.conf.loader.ConfigLoaderFactory;
import dyna.net.connection.AbstractClient;
import dyna.net.impl.ServiceProviderFactory;
import dyna.net.impl.rmi.ServiceLocatorRMIImpl;
import dyna.net.security.signature.Signature;
import dyna.net.spi.ServiceLocator;

/**
 * 通过远程访问服务器的客户端
 * 
 * @author Wanglei
 * 
 */
public class StatelessRMIClient extends AbstractClient
{

	private String			moduleName		= null;
	private ServiceLocator	serviceLocator	= null;
	private ThreadLocal<String> localSession = new ThreadLocal<String>();
	public StatelessRMIClient() throws Exception
	{
		this(Signature.MODULE_CLIENT, ConfigLoaderFactory.getLoader4Client().load());
	}

	public StatelessRMIClient(String moduleName) throws Exception
	{
		this(moduleName, ConfigLoaderFactory.getLoader4Client().load());
	}

	/**
	 * @throws Exception
	 */
	public StatelessRMIClient(ConfigurableClientImpl clientConfig) throws Exception
	{
		this(Signature.MODULE_CLIENT, clientConfig);
	}

	/**
	 * @throws Exception
	 */
	public StatelessRMIClient(String moduleName, ConfigurableClientImpl clientConfig) throws Exception
	{
		super(clientConfig);
		this.moduleName = moduleName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.connection.AbstractClient#initServiceProvider()
	 */
	@Override
	protected void initServiceProvider() throws Exception
	{
		this.serviceLocator = new StatelessRMIServiceLocator(this, this.moduleName, this.clientConfig);
		this.serviceLocator.getServiceStateSync().setReactor(this.getSscReactor());

		ServiceProviderFactory.createServiceProvider(this, this.serviceLocator);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.connection.AbstractClient#testConnection()
	 */
	@Override
	public boolean testConnection()
	{
		return this.serviceLocator.isAvailable();
	}

	public String getSeesionId()
	{
		return localSession.get();
	}
	
	public void bindSeessionId(String credential)
	{
		localSession.set(credential);
	}

}
