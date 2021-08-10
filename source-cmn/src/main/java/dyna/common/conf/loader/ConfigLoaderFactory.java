/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ConfigLoaderFactory
 * Wanglei 2010-11-26
 */
package dyna.common.conf.loader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Wanglei
 */
@Component
public class ConfigLoaderFactory
{
	@Autowired
	private  ConfigLoaderClientImpl            clientLoader   = null;
	@Autowired
	private  ConfigLoaderDSServerImpl          dsserverLoader = null;
	@Autowired
	private  ConfigLoaderMSRImpl               msrLoader      = null;
	@Autowired
	private  ConfigLoaderServerImpl            serverLoader   = null;
	@Autowired
	private  ConfigLoaderServiceImpl           serviceLoader  = null;
	@Autowired
	private  ConfigLoaderJSSImpl               jssLoader      = null;
	@Autowired
	private  ConfigLoaderConnToGuardServerImpl connToGSLoader = null;
	@Autowired
	private        ConfigLoaderConnToDSImpl   connToDSLoader = null;

	public  ConfigLoaderClientImpl getLoader4Client()
	{
		return clientLoader;
	}

	public  ConfigLoaderDSServerImpl getLoader4DSServer()
	{
		return dsserverLoader;
	}

	public  ConfigLoaderMSRImpl getLoader4MSR()
	{
		return msrLoader;
	}

	public  ConfigLoaderJSSImpl getLoader4JSS()
	{
		return jssLoader;
	}

	public  ConfigLoaderServerImpl getLoader4Server()
	{
		return serverLoader;
	}

	public  ConfigLoaderServiceImpl getLoader4Service()
	{
		return serviceLoader;
	}

	public  ConfigLoaderConnToGuardServerImpl getLoader4ConnToGS()
	{
		return connToGSLoader;
	}

	public  ConfigLoaderConnToDSImpl getLoader4ConnToDS()
	{
		return connToDSLoader;
	}


}
