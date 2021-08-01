/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServerConfigLoader
 * Wanglei 2010-3-30
 */
package dyna.common.conf.loader;

import dyna.common.conf.ConfigurableDSServerImpl;
import dyna.common.conf.ConfigurableKVElementImpl;
import dyna.common.util.EnvUtils;
import dyna.common.util.FileUtils;

/**
 * 读取服务端配置
 *
 * @author Wanglei
 * @see dyna.common.conf.ConfigurableServerImpl
 */
public class ConfigLoaderDSServerImpl extends AbstractConfigLoader<ConfigurableDSServerImpl>
{

	private ConfigurableDSServerImpl serverConfig = null;

	private String filePath = EnvUtils.getConfRootPath() + "conf/dsserver.xml";

	protected ConfigLoaderDSServerImpl()
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

		this.setConfigFile(FileUtils.newFileEscape(xmlFilePath));

		ConfigurableKVElementImpl loader = super.loadDefault();

		this.serverConfig = new ConfigurableDSServerImpl();

		this.serverConfig.setRootDir(loader.getElementValue("dsserver.rootdir"));
		this.serverConfig.setAddress(loader.getElementValue("dsserver.address"));
		this.serverConfig.setPort(Integer.valueOf(loader.getElementValue("dsserver.port")));
		this.serverConfig.setPasvPorts(loader.getElementValue("dsserver.pasvports"));
		this.serverConfig.setPassiveExternalAddress(loader.getElementValue("dsserver.pasv-ext-address"));
		this.serverConfig.configured();
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

	@Override public ConfigurableDSServerImpl getConfigurable()
	{
		return serverConfig;
	}
}
