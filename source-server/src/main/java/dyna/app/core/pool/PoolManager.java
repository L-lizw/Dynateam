/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: PoolManager
 * Wanglei 2010-3-31
 */
package dyna.app.core.pool;

import dyna.common.Poolable;

/**
 * 对象池管理器接口
 * 
 * @author Wanglei
 * 
 */
public interface PoolManager
{

	public Poolable getObjectFromPool(Class<?> clas) throws Exception;

	public void releaseObject(Class<?> clas, Poolable object);

	public void addPool(ObjectPool pool);

	public ObjectPool getPool(Class<?> clas);

}
