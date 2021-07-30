/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServiceStateSync
 * Wanglei 2011-1-14
 */
package dyna.net.dispatcher.sync;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 服务状态同步器
 * 
 * @author Wanglei
 * 
 */
public class ServiceStateSync implements Runnable
{

	private static final int			INTERVAL	= 30;
	private static final int			DELAY		= 30;

	private ScheduledExecutorService	scheduler	= null;
	private ServiceStateExchanger		exchanger	= null;

	private ServiceStateChangeReactor	reactor		= null;

	public void start()
	{
		if (this.scheduler == null)
		{
			this.scheduler = Executors.newSingleThreadScheduledExecutor();
			this.scheduler.scheduleWithFixedDelay(this, INTERVAL, DELAY, TimeUnit.SECONDS);
		}
	}

	public void shutdown()
	{
		if (this.scheduler != null)
		{
			this.scheduler.shutdown();
			this.scheduler = null;
		}
	}

	/**
	 * @return the exchanger
	 */
	public ServiceStateExchanger getExchanger()
	{
		return this.exchanger;
	}

	/**
	 * @param exchanger
	 *            the exchanger to set
	 */
	public void setExchanger(ServiceStateExchanger exchanger)
	{
		this.exchanger = exchanger;
	}

	/**
	 * 获取服务状态改变反应器
	 * 
	 * @return the reactor
	 */
	public ServiceStateChangeReactor getReactor()
	{
		return this.reactor;
	}

	/**
	 * 设置服务状态改变反应器
	 * 
	 * @param reactor
	 *            the reactor to set
	 */
	public void setReactor(ServiceStateChangeReactor reactor)
	{
		this.reactor = reactor;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public synchronized void run()
	{
		if (this.exchanger == null)
		{
			return;
		}
		this.exchanger.exchangeState(this.getReactor());
	}

}
