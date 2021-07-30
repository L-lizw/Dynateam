/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServerConfigLoader
 * Wanglei 2010-3-30
 */
package dyna.common.conf.loader;

import java.util.Iterator;

import dyna.common.bean.serv.DSServerBean;
import dyna.common.bean.serv.DSStorage;
import dyna.common.conf.ConfigurableKVElementImpl;
import dyna.common.conf.ConfigurableServerImpl;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.util.EnvUtils;
import dyna.common.util.FileUtils;
import dyna.common.util.StringUtils;

/**
 * 读取服务端配置
 * 
 * @see dyna.common.conf.ConfigurableServerImpl
 * @author Wanglei
 * 
 */
public class ConfigLoaderServerImpl extends AbstractConfigLoader<ConfigurableServerImpl>
{

	private ConfigurableServerImpl	serverConfig	= null;

	private String					filePath		= EnvUtils.getConfRootPath() + "conf/server.xml";

	protected ConfigLoaderServerImpl()
	{
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.conf.loader.ConfigLoader#load()
	 */
	@Override
	public synchronized ConfigurableServerImpl load(String xmlFilePath)
	{
		if (this.serverConfig == null)
		{
			this.setConfigFile(FileUtils.newFileEscape(xmlFilePath));

			ConfigurableKVElementImpl loader = super.loadDefault();

			this.serverConfig = new ConfigurableServerImpl();

			this.serverConfig.setServerId(loader.getElementValue("server.id"));
			this.serverConfig.setServerDescription(loader.getElementValue("server.description"));

			String ip = loader.getElementValue("server.service-dispatch.ip");
			if (!StringUtils.isNullString(ip))
			{
				this.serverConfig.setServerIP(ip);
			}

			String portStr = loader.getElementValue("server.service-dispatch.serverport");
			if (!StringUtils.isNullString(portStr))
			{
				this.serverConfig.setServiceDispatchPort(Integer.valueOf(portStr));
			}

			portStr = loader.getElementValue("server.service-dispatch.rmiport");
			if (!StringUtils.isNullString(portStr))
			{
				this.serverConfig.setRmiRegistryPort(Integer.valueOf(portStr));
			}

			portStr = loader.getElementValue("server.session-timeout");
			if (!StringUtils.isNullString(portStr))
			{
				this.serverConfig.setSessionTimeout(Integer.valueOf(portStr));
			}

			portStr = loader.getElementValue("server.session-prompt");
			if (!StringUtils.isNullString(portStr))
			{
				this.serverConfig.setSessionPromptTime(Integer.valueOf(portStr));
			}

			String threadPoolCount = loader.getElementValue("server.thread-pool.count");
			if (!StringUtils.isNullString(threadPoolCount))
			{
				this.serverConfig.setThreadPoolCount(Integer.valueOf(threadPoolCount));
			}

			String threadPoolDelay = loader.getElementValue("server.thread-pool.delay");
			if (!StringUtils.isNullString(threadPoolDelay))
			{
				this.serverConfig.setThreadPoolDelay(Integer.valueOf(threadPoolDelay));
			}

			String scheduledThreadPoolCount = loader.getElementValue("server.scheduled-thread-pool.count");
			if (!StringUtils.isNullString(scheduledThreadPoolCount))
			{
				this.serverConfig.setScheduledThreadPoolCount(Integer.valueOf(scheduledThreadPoolCount));
			}

			String language = loader.getElementValue("server.language");
			if (!StringUtils.isNullString(language))
			{
				this.serverConfig.setLanguage(LanguageEnum.getById(language));
			}

			this.configStorage(loader);
			this.configDSServer(loader);

			this.serverConfig.configured();
		}
		return this.serverConfig;
	}

	private void configStorage(ConfigurableKVElementImpl rootEl)
	{
		ConfigurableKVElementImpl storageEl = null;
		for (Iterator<ConfigurableKVElementImpl> iter = rootEl.iterator("server.storage-list.storage"); iter.hasNext();)
		{
			storageEl = iter.next();
			DSStorage storage = new DSStorage();
			storage.setId(storageEl.getElementValue("id"));
			storage.setName(storageEl.getElementValue("name"));
			storage.setDsserverId(storageEl.getElementValue("dsserver"));
			storage.setPath(storageEl.getElementValue("path"));
			this.serverConfig.addDSStorage(storage);
		}
	}

	private void configDSServer(ConfigurableKVElementImpl rootEl)
	{
		ConfigurableKVElementImpl dsserverEl = null;
		for (Iterator<ConfigurableKVElementImpl> iter = rootEl.iterator("server.dsserver-list.dsserver"); iter.hasNext();)
		{
			dsserverEl = iter.next();
			DSServerBean dsserverBean = new DSServerBean();
			dsserverBean.setId(dsserverEl.getElementValue("id"));
			dsserverBean.setIp(dsserverEl.getElementValue("ip"));
			dsserverBean.setPort(Integer.valueOf(dsserverEl.getElementValue("port")));
			this.serverConfig.addDSServerBean(dsserverBean);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.conf.loader.ConfigLoader#load(java.lang.String)
	 */
	@Override
	public ConfigurableServerImpl load()
	{
		return this.load(this.filePath);
	}
}
