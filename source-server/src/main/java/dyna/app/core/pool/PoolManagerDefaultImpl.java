/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DefaultPoolManagerImpl
 * Wanglei 2010-3-31
 */
package dyna.app.core.pool;

import dyna.common.Poolable;
import dyna.common.log.DynaLogger;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 对象池管理器默认实现
 * 
 * @author Wanglei
 * 
 */
@Component
public class PoolManagerDefaultImpl implements PoolManager
{

	protected Map<Class<?>, ObjectPool>	poolMap	= Collections
	.synchronizedMap(new HashMap<Class<?>, ObjectPool>());

	/* (non-Javadoc)
	 * @see dyna.app.core.pool.PoolManager#addPool(dyna.app.core.pool.DynaObjectPool)
	 */
	@Override
	public void addPool(ObjectPool pool)
	{
		if (this.poolMap.containsKey(pool.getPoolClass()))
		{
			return;
		}
		this.poolMap.put(pool.getPoolClass(), pool);
	}

	/* (non-Javadoc)
	 * @see dyna.app.core.pool.PoolManager#getPool(java.lang.Class)
	 */
	@Override
	public ObjectPool getPool(Class<?> clas)
	{
		return this.poolMap.get(clas);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.pool.PoolManager#getObjectFromPool(java.lang.Class)
	 */
	@Override
	public Poolable getObjectFromPool(Class<?> clas) throws Exception
	{
		ObjectPool pool = this.getPool(clas);
		if (pool == null)
		{
			throw new Exception("pool isn't exist named: " + clas.getName());
		}

		return (Poolable) pool.borrowObject(clas);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.pool.PoolManager#releaseObject(java.lang.Class, dyna.app.core.pool.Poolable)
	 */
	@Override
	public void releaseObject(Class<?> clas, Poolable object)
	{
		ObjectPool pool = this.getPool(clas);
		try
		{
			if (pool == null)
			{
				throw new IllegalStateException(clas + " pool not found.");
			}
			pool.returnObject(clas, object);
		}
		catch (Exception e)
		{
			DynaLogger.error("failed to return object to pool [" + clas + "] ");
			e.printStackTrace();
		}

	}

}
