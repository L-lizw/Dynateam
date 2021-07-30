/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DynaObjectPoolConfigImpl
 * Wanglei 2010-3-25
 */
package dyna.common.conf;

import org.apache.commons.pool.impl.GenericKeyedObjectPool;

/**
 * 池化对象参数配置
 * 
 * @author Wanglei
 * 
 */
public class ConfigurableObjectPoolImpl extends ConfigurableAdapter
{
	/**
	 * The default cap on the number of idle instances (per key) in the pool.
	 * 
	 * @see #getMaxIdle
	 * @see #setMaxIdle
	 */
	protected int		maxIdle							= GenericKeyedObjectPool.DEFAULT_MAX_IDLE;

	/**
	 * The default cap on the total number of active instances (per key)
	 * from the pool.
	 * 
	 * @see #getMaxActive
	 * @see #setMaxActive
	 */
	protected int		maxActive						= GenericKeyedObjectPool.DEFAULT_MAX_ACTIVE;

	/**
	 * The default cap on the the overall maximum number of objects that can
	 * exist at one time.
	 * 
	 * @see #getMaxTotal
	 * @see #setMaxTotal
	 */
	protected int		maxTotal						= GenericKeyedObjectPool.DEFAULT_MAX_TOTAL;

	/**
	 * The default minimum level of idle objects in the pool.
	 * 
	 * @see #setMinIdle
	 * @see #getMinIdle
	 */
	protected int		minIdle							= GenericKeyedObjectPool.DEFAULT_MIN_IDLE;

	/**
	 * The default maximum amount of time (in milliseconds) the
	 * <code>ObjectPool.borrowObject<code> method should block before throwing
	 * an exception when the pool is exhausted and the
	 * {@link #getWhenExhaustedAction "when exhausted" action} is
	 * {@link #WHEN_EXHAUSTED_BLOCK}.
	 * 
	 * @see #getMaxWait
	 * @see #setMaxWait
	 */
	protected long		maxWait							= GenericKeyedObjectPool.DEFAULT_MAX_WAIT;

	/**
	 * The default "when exhausted action" for the pool.
	 * 
	 * @see #WHEN_EXHAUSTED_BLOCK
	 * @see #WHEN_EXHAUSTED_FAIL
	 * @see #WHEN_EXHAUSTED_GROW
	 * @see #setWhenExhaustedAction
	 */
	protected byte		whenExhaustedAction				= GenericKeyedObjectPool.DEFAULT_WHEN_EXHAUSTED_ACTION;

	/**
	 * The default "test on borrow" value.
	 * 
	 * @see #getTestOnBorrow
	 * @see #setTestOnBorrow
	 */
	protected boolean	testOnBorrow					= GenericKeyedObjectPool.DEFAULT_TEST_ON_BORROW;

	/**
	 * The default "test on return" value.
	 * 
	 * @see #getTestOnReturn
	 * @see #setTestOnReturn
	 */
	protected boolean	testOnReturn					= GenericKeyedObjectPool.DEFAULT_TEST_ON_RETURN;

	/**
	 * The default "test while idle" value.
	 * 
	 * @see #getTestWhileIdle
	 * @see #setTestWhileIdle
	 * @see #getTimeBetweenEvictionRunsMillis
	 * @see #setTimeBetweenEvictionRunsMillis
	 */
	protected boolean	testWhileIdle					= GenericKeyedObjectPool.DEFAULT_TEST_WHILE_IDLE;

	/**
	 * The default "time between eviction runs" value.
	 * 
	 * @see #getTimeBetweenEvictionRunsMillis
	 * @see #setTimeBetweenEvictionRunsMillis
	 */
	protected long		timeBetweenEvictionRunsMillis	= GenericKeyedObjectPool.DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS;

	/**
	 * The default number of objects to examine per run in the
	 * idle object evictor.
	 * 
	 * @see #getNumTestsPerEvictionRun
	 * @see #setNumTestsPerEvictionRun
	 * @see #getTimeBetweenEvictionRunsMillis
	 * @see #setTimeBetweenEvictionRunsMillis
	 */
	protected int		numTestsPerEvictionRun			= GenericKeyedObjectPool.DEFAULT_NUM_TESTS_PER_EVICTION_RUN;

	/**
	 * The default value for {@link #getMinEvictableIdleTimeMillis}.
	 * 
	 * @see #getMinEvictableIdleTimeMillis
	 * @see #setMinEvictableIdleTimeMillis
	 */
	protected long		minEvictableIdleTimeMillis		= GenericKeyedObjectPool.DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS;

	/**
	 * @return the maxIdle
	 */
	public int getMaxIdle()
	{
		return this.maxIdle;
	}

	/**
	 * @param maxIdle
	 *            the maxIdle to set
	 */
	public void setMaxIdle(int maxIdle)
	{
		this.maxIdle = maxIdle;
	}

	/**
	 * @return the maxActive
	 */
	public int getMaxActive()
	{
		return this.maxActive;
	}

	/**
	 * @param maxActive
	 *            the maxActive to set
	 */
	public void setMaxActive(int maxActive)
	{
		this.maxActive = maxActive;
	}

	/**
	 * @return the maxTotal
	 */
	public int getMaxTotal()
	{
		return this.maxTotal;
	}

	/**
	 * @param maxTotal
	 *            the maxTotal to set
	 */
	public void setMaxTotal(int maxTotal)
	{
		this.maxTotal = maxTotal;
	}

	/**
	 * @return the minIdle
	 */
	public int getMinIdle()
	{
		return this.minIdle;
	}

	/**
	 * @param minIdle
	 *            the minIdle to set
	 */
	public void setMinIdle(int minIdle)
	{
		this.minIdle = minIdle;
	}

	/**
	 * @return the maxWait
	 */
	public long getMaxWait()
	{
		return this.maxWait;
	}

	/**
	 * @param maxWait
	 *            the maxWait to set
	 */
	public void setMaxWait(long maxWait)
	{
		this.maxWait = maxWait;
	}

	/**
	 * @return the whenExhaustedAction
	 */
	public byte getWhenExhaustedAction()
	{
		return this.whenExhaustedAction;
	}

	/**
	 * @param whenExhaustedAction
	 *            the whenExhaustedAction to set
	 */
	public void setWhenExhaustedAction(byte whenExhaustedAction)
	{
		this.whenExhaustedAction = whenExhaustedAction;
	}

	/**
	 * @return the testOnBorrow
	 */
	public boolean isTestOnBorrow()
	{
		return this.testOnBorrow;
	}

	/**
	 * @param testOnBorrow
	 *            the testOnBorrow to set
	 */
	public void setTestOnBorrow(boolean testOnBorrow)
	{
		this.testOnBorrow = testOnBorrow;
	}

	/**
	 * @return the testOnReturn
	 */
	public boolean isTestOnReturn()
	{
		return this.testOnReturn;
	}

	/**
	 * @param testOnReturn
	 *            the testOnReturn to set
	 */
	public void setTestOnReturn(boolean testOnReturn)
	{
		this.testOnReturn = testOnReturn;
	}

	/**
	 * @return the testWhileIdle
	 */
	public boolean isTestWhileIdle()
	{
		return this.testWhileIdle;
	}

	/**
	 * @param testWhileIdle
	 *            the testWhileIdle to set
	 */
	public void setTestWhileIdle(boolean testWhileIdle)
	{
		this.testWhileIdle = testWhileIdle;
	}

	/**
	 * @return the timeBetweenEvictionRunsMillis
	 */
	public long getTimeBetweenEvictionRunsMillis()
	{
		return this.timeBetweenEvictionRunsMillis;
	}

	/**
	 * @param timeBetweenEvictionRunsMillis
	 *            the timeBetweenEvictionRunsMillis to set
	 */
	public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis)
	{
		this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
	}

	/**
	 * @return the numTestsPerEvictionRun
	 */
	public int getNumTestsPerEvictionRun()
	{
		return this.numTestsPerEvictionRun;
	}

	/**
	 * @param numTestsPerEvictionRun
	 *            the numTestsPerEvictionRun to set
	 */
	public void setNumTestsPerEvictionRun(int numTestsPerEvictionRun)
	{
		this.numTestsPerEvictionRun = numTestsPerEvictionRun;
	}

	/**
	 * @return the minEvictableIdleTimeMillis
	 */
	public long getMinEvictableIdleTimeMillis()
	{
		return this.minEvictableIdleTimeMillis;
	}

	/**
	 * @param minEvictableIdleTimeMillis
	 *            the minEvictableIdleTimeMillis to set
	 */
	public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis)
	{
		this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
	}

}
