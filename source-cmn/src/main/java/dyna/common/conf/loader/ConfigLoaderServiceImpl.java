/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServiceConfigLoader
 * Wanglei 2010-3-30
 */
package dyna.common.conf.loader;

import java.util.Iterator;

import dyna.common.conf.ConfigurableKVElementImpl;
import dyna.common.conf.ConfigurableServiceImpl;
import dyna.common.conf.ServiceDefinition;
import dyna.common.util.EnvUtils;
import dyna.common.util.FileUtils;
import dyna.common.util.StringUtils;

/**
 * 读取服务配置
 * 
 * @see dyna.common.conf.ConfigurableServiceImpl
 * @author Wanglei
 * 
 */
public class ConfigLoaderServiceImpl extends AbstractConfigLoader<ConfigurableServiceImpl>
{

	private ConfigurableServiceImpl	serviceConfig	= null;

	private String					filePath		= EnvUtils.getConfRootPath() + "conf/service.xml";

	protected ConfigLoaderServiceImpl()
	{
		// do nothing
	}

	/* (non-Javadoc)
	 * @see dyna.common.conf.loader.ConfigLoader#load()
	 */
	@Override
	public synchronized ConfigurableServiceImpl load(String xmlFilePath)
	{
		if (this.serviceConfig == null)
		{
			this.setConfigFile(FileUtils.newFileEscape(this.filePath));

			ConfigurableKVElementImpl kvElement = super.loadDefault();

			this.serviceConfig = new ConfigurableServiceImpl();

			Iterator<ConfigurableKVElementImpl> elementIterator = kvElement.iterator("services.service");
			ConfigurableKVElementImpl element = null;
			Iterator<ConfigurableKVElementImpl> paramIterator = null;
			ConfigurableKVElementImpl paramEl = null;
			while (elementIterator.hasNext())
			{
				element = elementIterator.next();

				ServiceDefinition sd = new ServiceDefinition();
				sd.setServcieID(element.getElementValue("id"));
				sd.setServcieName(element.getElementValue("name"));
				sd.setServcieDescription(element.getElementValue("description"));
				sd.setServiceInterfaceName(element.getElementValue("interface"));
				sd.setServiceImplName(element.getElementValue("implement"));
				if (StringUtils.isNullString(element.getElementValue("dispatch-port"))==false)
				{
					sd.setDispatchPort(Integer.valueOf(element.getElementValue("dispatch-port")));
				}
				paramIterator = element.iterator("init-param");
				while (paramIterator.hasNext())
				{
					paramEl = paramIterator.next();
					sd.setInitParameter(paramEl.getElementValue("name"), paramEl.getElementValue("value"));
				}

				this.serviceConfig.addServiceDefinition(sd);
			}
			this.serviceConfig.configured();
		}
		return this.serviceConfig;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.conf.loader.ConfigLoader#load(java.lang.String)
	 */
	@Override
	public ConfigurableServiceImpl load()
	{
		return this.load(this.filePath);
	}

}
