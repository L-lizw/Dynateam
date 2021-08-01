/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServerConfigLoader
 * Wanglei 2010-3-30
 */
package dyna.common.conf.loader;

import dyna.common.conf.ConfigurableConnToDSImpl;
import dyna.common.conf.ConfigurableKVElementImpl;
import dyna.common.systemenum.ConnectionModeEnum;
import dyna.common.util.EnvUtils;
import dyna.common.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * 读取连接数据服务器配置
 *
 * @author Wanglei
 * @see dyna.common.conf.ConfigurableConnToDSImpl
 */
@Repository
public class ConfigLoaderConnToDSImpl extends AbstractConfigLoader<ConfigurableConnToDSImpl>
{
	@Autowired
	private ConfigurableConnToDSImpl connToDSConfig;

	private String filePath = EnvUtils.getConfRootPath() + "conf/dsconn.xml";

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.common.conf.loader.ConfigLoader#load()
	 */
	@Override public synchronized void load(String xmlFilePath)
	{
		this.setConfigFile(FileUtils.newFileEscape(xmlFilePath));
		ConfigurableKVElementImpl loader = super.loadDefault();

		this.connToDSConfig.setClientMode(ConnectionModeEnum.BUILT_IN_SERVER);
		this.connToDSConfig.setLookupServiceHost(loader.getElementValue("dsconn.service-lookup.host"));
		this.connToDSConfig.setLookupServicePort(Integer.valueOf(loader.getElementValue("dsconn.service-lookup.port")));
		this.connToDSConfig.configured();

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.common.conf.loader.ConfigLoader#load(java.lang.String)
	 */
	@Override public void load()
	{
		this.load(this.filePath);
	}

	@Override public ConfigurableConnToDSImpl getConfigurable()
	{
		return connToDSConfig;
	}
}
