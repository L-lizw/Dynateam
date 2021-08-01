/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServerConfigLoader
 * Wanglei 2010-3-30
 */
package dyna.common.conf.loader;

import dyna.common.conf.ConfigurableClientImpl;
import dyna.common.conf.ConfigurableKVElementImpl;
import dyna.common.conf.ConnToASConfig;
import dyna.common.systemenum.ConnectionModeEnum;
import dyna.common.util.EnvUtils;
import dyna.common.util.FileUtils;
import dyna.common.util.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Iterator;

/**
 * 读取服务端配置
 *
 * @author Wanglei
 * @see dyna.common.conf.ConfigurableClientImpl
 */
@Component public class ConfigLoaderClientImpl extends AbstractConfigLoader<ConfigurableClientImpl>
{
	private ConfigurableClientImpl clientConfig = null;

	private String filePath = EnvUtils.getConfRootPath() + "conf/client.xml";

	protected ConfigLoaderClientImpl()
	{
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.common.conf.loader.ConfigLoader#load()
	 */
	@Override public synchronized void load(String xmlFilePath)
	{
		this.clientConfig = new ConfigurableClientImpl();

		this.setConfigFile(FileUtils.newFileEscape(xmlFilePath));
		ConfigurableKVElementImpl loader = super.loadDefault();

		this.clientConfig.setClientMode(ConnectionModeEnum.BUILT_IN_SERVER);
		this.clientConfig.setDefaultLookupServicePort(Integer.valueOf(loader.getElementValue("client.service-lookup.port")));

		this.configServerList(loader);

		this.clientConfig.setLookupServiceHost(this.clientConfig.getDefaultServiceHost());

		this.clientConfig.configured();

	}

	private void configServerList(ConfigurableKVElementImpl rootEl)
	{
		ConfigurableKVElementImpl asConf = null;
		int port = 0;
		boolean isDefault = false;
		String tmpStr = null;
		for (Iterator<ConfigurableKVElementImpl> iter = rootEl.iterator("client.server-list.server"); iter.hasNext(); )
		{
			asConf = iter.next();
			ConnToASConfig conf = new ConnToASConfig();

			tmpStr = asConf.getAttributeValue("default");
			isDefault = StringUtils.isNullString(tmpStr) ? false : Boolean.valueOf(tmpStr);
			conf.setDefault(isDefault);

			conf.setName(asConf.getElementValue("name"));
			conf.setIpAddress(asConf.getElementValue("ip"));

			tmpStr = asConf.getElementValue("port");
			port = StringUtils.isNullString(tmpStr) ? this.clientConfig.getDefaultLookupServicePort() : Integer.valueOf(tmpStr);
			conf.setPort(port);

			if (isDefault)
			{
				this.clientConfig.setDefaultServiceHost(conf);
			}

			this.clientConfig.addServiceHost(conf);
		}
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

	@Override public ConfigurableClientImpl getConfigurable()
	{
		return this.clientConfig;
	}

}
