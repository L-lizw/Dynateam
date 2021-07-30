/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServerConfigLoader
 * Wanglei 2010-3-30
 */
package dyna.common.conf.loader;

import dyna.common.conf.ConfigurableConnToGuardServerImpl;
import dyna.common.conf.ConfigurableKVElementImpl;
import dyna.common.util.EnvUtils;
import dyna.common.util.FileUtils;

/**
 * 读取连接GuardServer服务器配置
 * 
 * @see dyna.common.conf.ConfigurableConnToDSImpl
 * @author Wanglei
 * 
 */
public class ConfigLoaderConnToGuardServerImpl extends AbstractConfigLoader<ConfigurableConnToGuardServerImpl>
{

	private ConfigurableConnToGuardServerImpl	connToGSConfig	= null;

	private String					filePath		= EnvUtils.getConfRootPath() + "conf/GuardServerConn.xml";

	protected ConfigLoaderConnToGuardServerImpl()
	{
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.conf.loader.ConfigLoader#load()
	 */
	@Override
	public synchronized ConfigurableConnToGuardServerImpl load(String xmlFilePath)
	{
		if (this.connToGSConfig == null)
		{
			this.connToGSConfig = new ConfigurableConnToGuardServerImpl();

			this.setConfigFile(FileUtils.newFileEscape(xmlFilePath));
			ConfigurableKVElementImpl loader = super.loadDefault();

			this.connToGSConfig.setLookupServiceHost(loader.getElementValue("GuardServerConn.service-lookup.host"));
			this.connToGSConfig.setLookupServicePort(Integer.valueOf(loader
					.getElementValue("GuardServerConn.service-lookup.port")));


			this.connToGSConfig.configured();
		}
		return this.connToGSConfig;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.conf.loader.ConfigLoader#load(java.lang.String)
	 */
	@Override
	public ConfigurableConnToGuardServerImpl load()
	{
		return this.load(this.filePath);
	}
}
