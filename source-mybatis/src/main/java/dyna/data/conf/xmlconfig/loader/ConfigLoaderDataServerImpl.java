/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ConfigLoaderDataServerImpl
 * Wanglei 2010-3-30
 */
package dyna.data.conf.xmlconfig.loader;

import dyna.common.conf.ConfigurableKVElementImpl;
import dyna.common.conf.ServiceDefinition;
import dyna.common.conf.loader.AbstractConfigLoader;
import dyna.common.util.EnvUtils;
import dyna.common.util.FileUtils;
import dyna.common.util.StringUtils;
import dyna.data.conf.xmlconfig.ConfigurableDataServerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Iterator;

/**
 * 读取数据服务配置
 *
 * @author Lizw
 * @see ConfigurableDataServerImpl
 */
@Repository public class ConfigLoaderDataServerImpl extends AbstractConfigLoader<ConfigurableDataServerImpl>
{
	@Autowired
	private ConfigurableDataServerImpl dsConfig = null;

	private String filePath = EnvUtils.getConfRootPath() + "conf/dataserver.xml";

	protected ConfigLoaderDataServerImpl()
	{
		// do nothing
	}

	/* (non-Javadoc)
	 * @see dyna.common.conf.loader.ConfigLoader#load()
	 */
	@Override public synchronized void load(String xmlFilePath)
	{
		this.setConfigFile(FileUtils.newFileEscape(xmlFilePath));

		ConfigurableKVElementImpl kvElement = super.loadDefault();

		String ip = kvElement.getElementValue("dataserver.service-dispatch.ip");
		if (!StringUtils.isNullString(ip))
		{
			this.dsConfig.setServerIP(ip);
		}

		String portStr = kvElement.getElementValue("dataserver.service-dispatch.serverport");
		if (!StringUtils.isNullString(portStr))
		{
			this.dsConfig.setServiceDispatchPort(Integer.valueOf(portStr));
		}

		portStr = kvElement.getElementValue("dataserver.service-dispatch.rmiport");
		if (!StringUtils.isNullString(portStr))
		{
			this.dsConfig.setRmiRegistryPort(Integer.valueOf(portStr));
		}
		Iterator<ConfigurableKVElementImpl> elementIterator = kvElement.iterator("dataserver.service-list.service");
		ConfigurableKVElementImpl element = null;
		Iterator<ConfigurableKVElementImpl> paramIterator = null;
		ConfigurableKVElementImpl paramEl = null;
		while (elementIterator.hasNext())
		{
			element = elementIterator.next();

			ServiceDefinition sd = new ServiceDefinition();
			sd.setServcieID(element.getElementValue("id"));
			sd.setServcieName(element.getElementValue("name"));
			sd.setServiceInterfaceName(element.getElementValue("interface"));
			sd.setServiceImplName(element.getElementValue("implement"));
			if (StringUtils.isNullString(element.getElementValue("dispatch-port")) == false)
			{
				sd.setDispatchPort(Integer.valueOf(element.getElementValue("dispatch-port")));
			}
			paramIterator = element.iterator("init-param");
			while (paramIterator.hasNext())
			{
				paramEl = paramIterator.next();
				sd.setInitParameter(paramEl.getElementValue("name"), paramEl.getElementValue("value"));
			}

			this.dsConfig.addServiceDefinition(sd);
		}
		this.dsConfig.configured();
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

	@Override
	public ConfigurableDataServerImpl getConfigurable()
	{
		return this.dsConfig;
	}

}
