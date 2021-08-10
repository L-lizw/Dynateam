/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DynaObjectPool
 * Wanglei 2010-3-25
 */
package dyna.app.core.pool;

import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;

import dyna.common.log.DynaLogger;

/**
 * 对象池
 * 
 * @author Wanglei
 * 
 */
public class ObjectPool extends GenericKeyedObjectPool
{

	private Class<?>		poolClass	= null;

	protected ObjectPool(Class<?> cls, KeyedPoolableObjectFactory factory)
	{
		super(factory);
		this.setMaxIdle(64);
		this.setMaxActive(640);
		this.poolClass = cls;
		this.initialize();
	}

	protected void initialize()
	{
		if (DynaLogger.isDebugEnabled())
		{
			DynaLogger.debug("pool [" + this.poolClass.getSimpleName() + "] initialize....");
		}

		try
		{
			/*for (int i = 0; i < 5; i++)
			{
				this.addObject(this.poolClass);
			}*/

			// init objects by size of pool (default to 1).
			this.addObject(this.poolClass);

			if (DynaLogger.isDebugEnabled())
			{
				DynaLogger.debug("pool [" + this.poolClass.getSimpleName() + "] initialized successfully.");
			}
		}
		catch (Exception e)
		{
			DynaLogger.error("error occurs on DynaObjectPool init", e);
		}
	}

	public Class<?> getPoolClass()
	{
		return this.poolClass;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.pool.impl.GenericKeyedObjectPool#addObject(java.lang.Object)
	 */
	@Override
	public void addObject(Object key) throws Exception
	{
		if (DynaLogger.isDebugEnabled())
		{
			DynaLogger.debug("pool [" + this.poolClass.getSimpleName() + "] add PoolableObject with key: [" + key + "]");
		}
		super.addObject(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.pool.impl.GenericKeyedObjectPool#borrowObject(java.lang.Object)
	 */
	@Override
	public Object borrowObject(Object key) throws Exception
	{
		if (DynaLogger.isDebugEnabled())
		{
			DynaLogger.debug("pool [" + this.poolClass.getSimpleName() + "] borrow PoolableObject with key: [" + key
					+ "]");
		}

		try
		{
			return super.borrowObject(key);
		}
		finally
		{
			if (DynaLogger.isDebugEnabled())
			{
				DynaLogger.debug("borrow [" + this.poolClass.getSimpleName() + "] -> active: " + this.getNumActive(key) + ", idle: " + this.getNumIdle(key));
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.pool.impl.GenericKeyedObjectPool#invalidateObject(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void invalidateObject(Object key, Object obj) throws Exception
	{
		if (DynaLogger.isDebugEnabled())
		{
			DynaLogger.debug("pool [" + this.poolClass.getSimpleName() + "] invalidate PoolableObject [" + obj
					+ "] with key: [" + key + "]");
		}
		super.invalidateObject(key, obj);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.pool.impl.GenericKeyedObjectPool#returnObject(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void returnObject(Object key, Object obj) throws Exception
	{
		if (DynaLogger.isDebugEnabled())
		{
			DynaLogger.debug("pool [" + this.poolClass.getSimpleName() + "] return PoolableObject [" + obj
					+ "] with key: [" + key + "]");
		}
		try
		{
			super.returnObject(key, obj);
		}
		finally
		{
			if (DynaLogger.isDebugEnabled())
			{
				DynaLogger.debug("return [" + this.poolClass.getSimpleName() + "] -> active: " + this.getNumActive(key) + ", idle: " + this.getNumIdle(key));
			}
		}
	}

}
