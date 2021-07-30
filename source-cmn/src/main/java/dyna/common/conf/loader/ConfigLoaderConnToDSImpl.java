/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServerConfigLoader
 * Wanglei 2010-3-30
 */
package dyna.common.conf.loader;

import dyna.common.conf.ConfigurableConnToDSImpl;
import dyna.common.conf.ConfigurableKVElementImpl;
import dyna.common.systemenum.ConnectionMode;
import dyna.common.util.EnvUtils;
import dyna.common.util.FileUtils;

/**
 * 读取连接数据服务器配置
 * 
 * @see dyna.common.conf.ConfigurableConnToDSImpl
 * @author Wanglei
 * 
 */
public class ConfigLoaderConnToDSImpl extends AbstractConfigLoader<ConfigurableConnToDSImpl>
{

	private ConfigurableConnToDSImpl	connToDSConfig	= null;

	private String						filePath		= EnvUtils.getConfRootPath() + "conf/dsconn.xml";

	protected ConfigLoaderConnToDSImpl()
	{
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.conf.loader.ConfigLoader#load()
	 */
	@Override
	public synchronized ConfigurableConnToDSImpl load(String xmlFilePath)
	{
		if (this.connToDSConfig == null)
		{
			this.connToDSConfig = new ConfigurableConnToDSImpl();

			this.setConfigFile(FileUtils.newFileEscape(xmlFilePath));
			ConfigurableKVElementImpl loader = super.loadDefault();

			this.connToDSConfig.setClientMode(ConnectionMode.BUILT_IN_SERVER);
//			this.connToDSConfig
//			.setClientMode(ConnectionMode.getClientMode(loader.getAttributeValue("dsconn.mode.name")));
			this.connToDSConfig.setLookupServiceHost(loader.getElementValue("dsconn.service-lookup.host"));
			this.connToDSConfig.setLookupServicePort(Integer.valueOf(loader.getElementValue("dsconn.service-lookup.port")));

			this.connToDSConfig.configured();
		}
		return this.connToDSConfig;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.conf.loader.ConfigLoader#load(java.lang.String)
	 */
	@Override
	public ConfigurableConnToDSImpl load()
	{
		return this.load(this.filePath);
	}
}
