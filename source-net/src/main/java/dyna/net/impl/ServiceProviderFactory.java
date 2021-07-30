/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServiceProviderFactory
 * Wanglei 2010-4-6
 */
package dyna.net.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import dyna.net.connection.GenericClient;
import dyna.net.spi.ServiceLocator;
import dyna.net.spi.ServiceProvider;

/**
 * 服务提供者工厂
 * 
 * @author Wanglei
 * 
 */
public class ServiceProviderFactory
{
	private static ServiceProvider				serviceProvider	= null;
	private static Map<String, ServiceProvider>	spCache			= Collections
	.synchronizedMap(new HashMap<String, ServiceProvider>());

	/**
	 * 创建服务提供者
	 * 
	 * @param sl
	 *            服务资源定位搜索器
	 */
	public static void createServiceProvider(ServiceLocator sl)
	{
		createServiceProvider(null, sl);
	}

	/**
	 * 创建服务提供者
	 * 
	 * @param client
	 *            客户端
	 * @param sl
	 *            服务资源定位搜索器
	 */
	public static void createServiceProvider(GenericClient client, ServiceLocator sl)
	{
		ServiceProvider sp = null;
		if (serviceProvider == null)
		{
			sp = serviceProvider = new ServcieProviderDefaultImpl(sl);
		}

		if (client != null)
		{
			if (sp == null)
			{
				sp = new ServcieProviderDefaultImpl(sl);
			}

			spCache.put(client.getClientIdentifier(), sp);
		}
	}

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
