/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: RemoteClient
 * Wanglei 2010-11-30
 */
package dyna.net.connection;

import dyna.common.conf.ConfigurableClientImpl;
import dyna.common.conf.loader.ConfigLoaderFactory;
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
public class RMIClient extends AbstractClient
{

	private String			moduleName		= null;
	private ServiceLocator	serviceLocator	= null;

	public RMIClient() throws Exception
	{
		this(Signature.MODULE_CLIENT, ConfigLoaderFactory.getLoader4Client().load());
	}

	public RMIClient(String moduleName) throws Exception
	{
		this(moduleName, ConfigLoaderFactory.getLoader4Client().load());
	}

	/**
	 * @throws Exception
	 */
	public RMIClient(ConfigurableClientImpl clientConfig) throws Exception
	{
		this(Signature.MODULE_CLIENT, clientConfig);
	}

	/**
	 * @throws Exception
	 */
	public RMIClient(String moduleName, ConfigurableClientImpl clientConfig) throws Exception
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
		this.serviceLocator = new ServiceLocatorRMIImpl(this, this.moduleName, this.clientConfig);
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

}
