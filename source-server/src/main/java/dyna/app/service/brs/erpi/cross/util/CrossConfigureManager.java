/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ConfigManager
 * WangLHB Oct 11, 2011
 */
package dyna.app.service.brs.erpi.cross.util;

import dyna.common.conf.ConfigurableKVElementImpl;
import dyna.common.conf.loader.ConfigLoaderDefaultImpl;
import dyna.common.dto.erp.CrossIntegrate;
import dyna.common.dto.erp.CrossServiceConfig;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.EnvUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author WangLHB
 * 
 */
public class CrossConfigureManager
{

	private static CrossConfigureManager	configureManager	= new CrossConfigureManager();

	private CrossServiceConfig				crossServiceConfig	= null;

	/**
	 * 取得当前唯一的实例
	 * 
	 * @return
	 */
	public static CrossConfigureManager getInstance()
	{
		return configureManager;
	}

	public CrossServiceConfig getCrossServiceConfig()
	{
		// if (crossServiceConfig == null)
		// {
		crossServiceConfig = getCrossServiceConfigXML();
		// }

		return crossServiceConfig;
	}

	/**
	 * 获取Cross服务器的相关配置
	 * <比如：>配置名称、Cross服务器地址、服务名称等
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public CrossServiceConfig getCrossServiceConfigXML()
	{
		CrossServiceConfig crossServiceConfig = new CrossServiceConfig();

		final ConfigLoaderDefaultImpl confLoader = new ConfigLoaderDefaultImpl();

		confLoader.setConfigFile(new java.io.File(EnvUtils.getConfRootPath() + "conf/crossconf.xml"));
		confLoader.load();
		ConfigurableKVElementImpl crossconfElement = confLoader.getConfigurable().iterator("cross").next();

		if (crossconfElement == null)
		{
			return null;
		}

		// 获取cross服务的信息
		ConfigurableKVElementImpl serviceconfElement = crossconfElement.iterator("crossserver").next();
		if (serviceconfElement != null)
		{
			crossServiceConfig.setCrossServerIP(serviceconfElement.iterator("ip").hasNext() ? serviceconfElement
					.iterator("ip").next().getElementValue() : null);

			crossServiceConfig.setCrossServerPort(serviceconfElement.iterator("port").hasNext() ? serviceconfElement
					.iterator("port").next().getElementValue() : null);

			crossServiceConfig
					.setCrossServerName(serviceconfElement.iterator("servername").hasNext() ? serviceconfElement
							.iterator("servername").next().getElementValue() : null);

			crossServiceConfig.setCrossRestUrl(serviceconfElement.iterator("resturl").hasNext() ? serviceconfElement
					.iterator("resturl").next().getElementValue() : null);
		}

		// 获取本地主机的信息
		ConfigurableKVElementImpl hostElement = crossconfElement.iterator("host").next();
		if (hostElement != null)
		{
			crossServiceConfig.setHostIP(hostElement.iterator("ip").hasNext() ? hostElement.iterator("ip").next()
					.getElementValue() : null);

			crossServiceConfig.setHostIsEncode(hostElement.iterator("isencoding").hasNext() ? hostElement
					.iterator("isencoding").next().getElementValue() : null);

			crossServiceConfig.setHostVer(hostElement.iterator("ver").hasNext() ? hostElement.iterator("ver").next()
					.getElementValue() : null);

			crossServiceConfig.setHostAcct(hostElement.iterator("acct").hasNext() ? hostElement.iterator("acct").next()
					.getElementValue() : null);

			crossServiceConfig.setHostID(hostElement.iterator("id").hasNext() ? hostElement.iterator("id").next()
					.getElementValue() : null);

			crossServiceConfig.setHostPort(hostElement.iterator("port").hasNext() ? hostElement.iterator("port").next()
					.getElementValue() : null);
			
			crossServiceConfig.setUID(hostElement.iterator("uid").hasNext() ? hostElement.iterator("uid").next()
					.getElementValue() : null);
			
			crossServiceConfig.setRestUrl(hostElement.iterator("resturl").hasNext() ? hostElement.iterator("resturl").next()
					.getElementValue() : null);

			crossServiceConfig.setHostServerName(hostElement.iterator("servername").hasNext() ? hostElement
					.iterator("servername").next().getElementValue() : null);

		}
		// 获取集成产品的信息
		if (crossconfElement.iterator("servers").hasNext())
		{
			ConfigurableKVElementImpl serviceElement = crossconfElement.iterator("servers").next();
			List<CrossIntegrate> serverList = new ArrayList<CrossIntegrate>();
			if (serviceElement != null)
			{
				// 遍历servers下面的server
				for (Iterator<ConfigurableKVElementImpl> objectIteratorField = serviceElement.iterator("server"); objectIteratorField
						.hasNext();)
				{
					CrossIntegrate crossIntegrateProd = new CrossIntegrate();
					ConfigurableKVElementImpl crossProdElement = objectIteratorField.next();
					crossIntegrateProd.setServiceIP(crossProdElement.iterator("ip").hasNext() ? crossProdElement
							.iterator("ip").next().getElementValue() : null);
					crossIntegrateProd.setServiceProd(crossProdElement.iterator("prod").hasNext() ? crossProdElement
							.iterator("prod").next().getElementValue() : null);
					crossIntegrateProd.setServiceID(crossProdElement.iterator("id").hasNext() ? crossProdElement
							.iterator("id").next().getElementValue() : null);
					crossIntegrateProd.setServiceVer(crossProdElement.iterator("ver").hasNext() ? crossProdElement
							.iterator("ver").next().getElementValue() : null);
					crossIntegrateProd.setUID(crossProdElement.iterator("uid").hasNext() ? crossProdElement
							.iterator("uid").next().getElementValue() : null);
					crossIntegrateProd.setRestUrl(crossProdElement.iterator("resturl").hasNext() ? crossProdElement
							.iterator("resturl").next().getElementValue() : null);
					serverList.add(crossIntegrateProd);
				}
			}
			if (serverList != null || serverList.size() != 0)
			{
				crossServiceConfig.setServices(serverList);
			}
		}

		return crossServiceConfig;
	}
}
