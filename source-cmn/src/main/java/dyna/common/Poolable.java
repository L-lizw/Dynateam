/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Poolable
 * Wanglei 2010-3-25
 */
package dyna.common;

/**
 * 池化对象接口
 * 
 * @author Wanglei
 * 
 */
public interface Poolable
{

	public String getObjectUID();

	/**
	 * Create an instance that can be served by the pool.
	 * 
	 * @return an instance that can be served by the pool.
	 * @throws Exception
	 *             if there is a problem creating a new instance,
	 *             this will be propagated to the code requesting an object.
	 */
	public void initObject(Object... initArgs) throws Exception;

	/**
	 * Destroy an instance no longer needed by the pool.
	 * <p>
	 * It is important for implementations of this method to be aware that there is no guarantee about what state
	 * <code>obj</code> will be in and the implementation should be prepared to handle unexpected errors.
	 * </p>
	 * <p>
	 * Also, an implementation must take in to consideration that instances lost to the garbage collector may never be
	 * destroyed.
	 * </p>
	 * 
	 * @throws Exception
	 *             should be avoided as it may be swallowed by
	 *             the pool implementation.
	 * @see #validateObject
	 */
	public void destroyObject() throws Exception;

	/**
	 * Ensures that the instance is safe to be returned by the pool.
	 * Returns <code>false</code> if <code>obj</code> should be destroyed.
	 * 
	 * @return <code>false</code> if <code>obj</code> is not valid and should
	 *         be dropped from the pool, <code>true</code> otherwise.
	 */
	public boolean validateObject();

	/**
	 * Reinitialize an instance to be returned by the pool.
	 * 
	 * @throws Exception
	 *             if there is a problem activating <code>obj</code>,
	 *             this exception may be swallowed by the pool.
	 * @see #destroyObject
	 */
	public void activateObject() throws Exception;

	/**
	 * Uninitialize an instance to be returned to the idle object pool.
	 * 
	 * @throws Exception
	 *             if there is a problem passivating <code>obj</code>,
	 *             this exception may be swallowed by the pool.
	 * @see #destroyObject
	 */
	public void passivateObject() throws Exception;

}
