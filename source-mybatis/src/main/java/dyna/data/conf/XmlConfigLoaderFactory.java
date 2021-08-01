/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ConfigLoaderFactory
 * Wanglei 2010-11-26
 */
package dyna.data.conf;

import dyna.common.conf.loader.ConfigLoaderConnToDSImpl;
import dyna.data.conf.xmlconfig.loader.ConfigLoaderDataServerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author Wanglei
 * 
 */
@Repository
public class XmlConfigLoaderFactory
{
	@Autowired
	private        ConfigLoaderDataServerImpl dataserverLoader;
	@Autowired
	private        ConfigLoaderConnToDSImpl   connToDSLoader = null;


	public  ConfigLoaderDataServerImpl getLoader4DataServer()
	{
		return dataserverLoader;
	}

	public  ConfigLoaderConnToDSImpl getLoader4ConnToDS()
	{
		return connToDSLoader;
	}

	public void init()
	{
		this.dataserverLoader.load();
		this.connToDSLoader.load();
	}

}
