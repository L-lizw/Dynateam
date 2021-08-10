/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DynaPoolableObjectFactory
 * Wanglei 2010-3-25
 */
package dyna.app.core.pool;

import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.apache.commons.pool.PoolUtils;

import dyna.app.net.ServiceFactory;
import dyna.common.Poolable;
import dyna.common.log.DynaLogger;

/**
 * 创建池化对象的工厂
 * 
 * @author Wanglei
 * 
 */
public class PoolableObjectFactory implements KeyedPoolableObjectFactory
{
	private ServiceFactory	serviceFactory	= null;

	public static KeyedPoolableObjectFactory synchronizedFactory(ServiceFactory serviceFactory)
	{
		return PoolUtils.synchronizedPoolableFactory(new PoolableObjectFactory(serviceFactory));
	}

	protected PoolableObjectFactory(ServiceFactory serviceFactory)
	{
		this.serviceFactory = serviceFactory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.pool.BaseKeyedPoolableObjectFactory#makeObject(java.lang.Object)
	 */
	@Override
	public Object makeObject(Object key) throws Exception
	{
		Object obj = this.serviceFactory.createService();

		if (DynaLogger.isDebugEnabled())
		{
			DynaLogger.debug("PoolableObjectFactory new Instance, get " + obj);
		}

		return obj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.pool.KeyedPoolableObjectFactory#activateObject(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void activateObject(Object key, Object obj) throws Exception
	{
		if (DynaLogger.isDebugEnabled())
		{
			DynaLogger.debug("PoolableObjectFactory activate PoolableObject [" + obj + "] with key: [" + key + "]");
		}
		((Poolable) obj).activateObject();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.pool.KeyedPoolableObjectFactory#destroyObject(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void destroyObject(Object key, Object obj) throws Exception
	{
		if (DynaLogger.isDebugEnabled())
		{
			DynaLogger.debug("PoolableObjectFactory destroy PoolableObject [" + obj + "] with key: [" + key + "]");
		}
		((Poolable) obj).destroyObject();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.pool.KeyedPoolableObjectFactory#passivateObject(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void passivateObject(Object key, Object obj) throws Exception
	{
		if (DynaLogger.isDebugEnabled())
		{
			DynaLogger.debug("PoolableObjectFactory passivate PoolableObject [" + obj + "] with key: [" + key + "]");
		}
		((Poolable) obj).passivateObject();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.pool.KeyedPoolableObjectFactory#validateObject(java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean validateObject(Object key, Object obj)
	{
		if (obj == null)
		{
			return false;
		}
		if (DynaLogger.isDebugEnabled())
		{
			DynaLogger.debug("PoolableObjectFactory validate PoolableObject [" + obj + "] with key: [" + key + "]");
		}
		return ((Poolable) obj).validateObject();
	}

}
