/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DataRMIClient
 * Wanglei 2010-12-7
 */
package dyna.data.connection;

import dyna.data.service.transaction.DataServerTransactionManager;
import dyna.common.conf.ConfigurableConnToDSImpl;
import dyna.common.conf.loader.ConfigLoaderFactory;
import dyna.common.util.StringUtils;
import dyna.net.connection.AbstractClient;
import dyna.net.impl.DataServiceProviderFactory;
import dyna.net.impl.data.ServiceLocatorDataServerRMIImpl;
import dyna.net.security.signature.Signature;
import dyna.net.spi.DataServiceLocator;

/**
 * @author Wanglei
 * 
 */
public class DSRMIClient extends AbstractClient
{
	private String                       moduleName         = null;
	private String                       configFilePath     = null;
	private ConfigurableConnToDSImpl     connToDSConfig     = null;
	private DataServiceLocator           dataServiceLocator = null;
	private DataServerTransactionManager manager            = null;

	/**
	 * @throws Exception
	 */
	public DSRMIClient() throws Exception
	{
		this(null, (String) null);
	}

	public DSRMIClient(String configFilePath) throws Exception
	{
		this(null, configFilePath);
	}

	public DSRMIClient(String moduleName, String configFilePath) throws Exception
	{
		super(null);
		this.moduleName = moduleName;
		this.configFilePath = configFilePath;

	}

	public DSRMIClient(String moduleName, ConfigurableConnToDSImpl connToDSConfig) throws Exception
	{
		super(null);
		this.moduleName = moduleName;
		this.connToDSConfig = connToDSConfig;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.connection.AbstractClient#open()
	 */
	@Override
	public void open() throws Exception
	{
		this.initServiceProvider();
		this.isOpened = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.connection.AbstractClient#close()
	 */
	@Override
	public void close()
	{
		DataServiceProviderFactory.getServiceProvider().destroy();
		this.isOpened = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.connection.AbstractClient#initServiceProvider()
	 */
	@Override
	protected void initServiceProvider() throws Exception
	{
		if (StringUtils.isNullString(this.moduleName))
		{
			this.moduleName = Signature.MODULE_MODELER;
		}
		if (this.connToDSConfig == null)
		{
			if (StringUtils.isNullString(this.configFilePath))
			{
				this.connToDSConfig = ConfigLoaderFactory.getLoader4ConnToDS().load();
			}
			else
			{
				this.connToDSConfig = ConfigLoaderFactory.getLoader4ConnToDS().load(this.configFilePath);
			}
		}
		this.dataServiceLocator = new ServiceLocatorDataServerRMIImpl(this, this.moduleName, this.connToDSConfig);
		this.dataServiceLocator.getServiceStateSync().setReactor(this.getSscReactor());

		DataServiceProviderFactory.createServiceProvider(this, this.dataServiceLocator);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.connection.AbstractClient#testConnection()
	 */
	@Override
	public boolean testConnection()
	{
		return this.dataServiceLocator.isAvailable();
	}

	public String getRequestDetail()
	{
		if (manager == null)
		{
			return null;
		}
		else
		{
			return manager.getTransactionId();
		}
	}

	public void setTransactionManager(DataServerTransactionManager manager)
	{
		this.manager = manager;
	}

}
