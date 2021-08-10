/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DynaObjectPoolFactory
 * Wanglei 2010-3-25
 */
package dyna.app.core.pool;

import dyna.app.net.ServiceFactory;

/**
 * 对象池工厂
 * 
 * @author Wanglei
 * 
 */
public class ObjectPoolFactory
{

	public static ObjectPool createDynaObjectPool(Class<?> keyCls, ServiceFactory sf)
	{
		return new ObjectPool(keyCls, PoolableObjectFactory.synchronizedFactory(sf));
	}
}
