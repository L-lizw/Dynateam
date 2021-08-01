/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ConfigLoaderFactory
 * Wanglei 2010-11-26
 */
package dyna.common.conf.loader;

/**
 * @author Wanglei
 */
public class ConfigLoaderFactory
{

	private static ConfigLoaderClientImpl            clientLoader   = null;
	private static ConfigLoaderDSServerImpl          dsserverLoader = null;
	private static ConfigLoaderMSRImpl               msrLoader      = null;
	private static ConfigLoaderServerImpl            serverLoader   = null;
	private static ConfigLoaderServiceImpl           serviceLoader  = null;
	private static ConfigLoaderJSSImpl               jssLoader      = null;
	private static ConfigLoaderConnToGuardServerImpl connToGSLoader = null;

	public static ConfigLoaderClientImpl getLoader4Client()
	{
		if (clientLoader == null)
		{
			clientLoader = new ConfigLoaderClientImpl();
		}
		return clientLoader;
	}

	public static ConfigLoaderDSServerImpl getLoader4DSServer()
	{
		if (dsserverLoader == null)
		{
			dsserverLoader = new ConfigLoaderDSServerImpl();
		}
		return dsserverLoader;
	}

	public static ConfigLoaderMSRImpl getLoader4MSR()
	{
		if (msrLoader == null)
		{
			msrLoader = new ConfigLoaderMSRImpl();
		}
		return msrLoader;
	}

	public static ConfigLoaderJSSImpl getLoader4JSS()
	{
		if (jssLoader == null)
		{
			jssLoader = new ConfigLoaderJSSImpl();
		}
		return jssLoader;
	}

	public static ConfigLoaderServerImpl getLoader4Server()
	{
		if (serverLoader == null)
		{
			serverLoader = new ConfigLoaderServerImpl();
		}
		return serverLoader;
	}

	public static ConfigLoaderServiceImpl getLoader4Service()
	{
		if (serviceLoader == null)
		{
			serviceLoader = new ConfigLoaderServiceImpl();
		}
		return serviceLoader;
	}

	public static ConfigLoaderConnToGuardServerImpl getLoader4ConnToGS()
	{
		if (connToGSLoader == null)
		{
			connToGSLoader = new ConfigLoaderConnToGuardServerImpl();
		}
		return connToGSLoader;
	}

}
