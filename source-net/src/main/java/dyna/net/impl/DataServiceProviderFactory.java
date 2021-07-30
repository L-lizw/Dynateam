/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DataServiceProviderFactory
 * Wanglei 2010-12-8
 */
package dyna.net.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import dyna.net.connection.GenericClient;
import dyna.net.spi.DataServiceLocator;
import dyna.net.spi.ServiceProvider;

/**
 * @author Wanglei
 *
 */
public class DataServiceProviderFactory
{
	private static ServiceProvider	serviceProvider	= null;
	private static Map<String, ServiceProvider>	spCache			= Collections
	.synchronizedMap(new HashMap<String, ServiceProvider>());
	/**
	 * 创建服务提供者
	 * 
	 * @param dsl
	 *            服务资源定位搜索器
	 */
	public static void createServiceProvider(DataServiceLocator dsl)
	{
		createServiceProvider(null, dsl);
	}

	/**
	 * 创建服务提供者
	 * 
	 * @param client
	 *            客户端
	 * @param sl
	 *            服务资源定位搜索器
	 */
	public static void createServiceProvider(GenericClient client, DataServiceLocator dsl)
	{
		ServiceProvider sp = null;
		if (serviceProvider == null)
		{
			sp = serviceProvider = new ServcieProviderDefaultImpl(dsl);
			dsl.init();
		}

		if (client != null)
		{
			if (sp == null)
			{
				sp = new ServcieProviderDefaultImpl(dsl);
				dsl.init();
			}

			spCache.put(client.getClientIdentifier(), sp);
		}
	}
	/**
	 * 获取服务提供者
	 * 
	 * @return
	 * @throws IllegalStateException
	 */
	public static ServiceProvider getServiceProvider() throws IllegalStateException
	{
		return getServiceProvider(null);
	}

	/**
	 * 获取服务提供者
	 * 
	 * @return
	 * @throws IllegalStateException
	 */
	public static ServiceProvider getServiceProvider(GenericClient client) throws IllegalStateException
	{
		ServiceProvider sp = null;
		if (client == null)
		{
			sp = serviceProvider;
		}
		else
		{
			if (!client.isOpened())
			{
				throw new IllegalStateException("Client is closed.");
			}
			sp = spCache.get(client.getClientIdentifier());
		}

		if (sp == null)
		{
			throw new IllegalStateException("Service Provider hasn't been initialized.");
		}

		return sp;
	}
}
