/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SchedulerSessionCheckImpl 周期性检查会话有效期调度器
 * Wanglei 2011-4-20
 */
package dyna.app.core.sch;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import dyna.common.util.DateFormat;

/**
 * 周期性检查会话有效期调度器
 * 
 * @author Wanglei
 * 
 */
public class SchedulerTimerTaskImpl extends AbstractScheduler
{
	private ScheduledExecutorService	scheduledTaskExecutor	= null;

	/**
	 * @param threadSize
	 */
	public SchedulerTimerTaskImpl(int threadSize)
	{
		if (threadSize < 1)
		{
			threadSize = DEFAULT_SCHEDULED_THREAD_SIZE;
		}
		this.scheduledTaskExecutor = Executors.newScheduledThreadPool(threadSize);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.sch.Scheduler#schedule(dyna.app.core.sch.AbstractScheduledTask, java.util.Date)
	 */
	@Override
	public void schedule(AbstractScheduledTask task, Date time)
	{
		long delay = DateFormat.getDelay(time, 0);
		this.scheduledTaskExecutor.schedule(task, delay, TimeUnit.MILLISECONDS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.sch.Scheduler#schedule(dyna.app.core.sch.AbstractScheduledTask, long)
	 */
	@Override
	public void schedule(AbstractScheduledTask task, long delay)
	{
		this.scheduledTaskExecutor.schedule(task, delay, TimeUnit.MILLISECONDS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.sch.Scheduler#scheduleAtFixedRate(dyna.app.core.sch.AbstractScheduledTask, java.util.Date,
	 * long)
	 */
	@Override
	public void scheduleAtFixedRate(AbstractScheduledTask task, Date firstTime, long period)
	{
		long delay = DateFormat.getDelay(firstTime, period);
		this.scheduledTaskExecutor.scheduleAtFixedRate(task, delay, period, TimeUnit.MILLISECONDS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.sch.Scheduler#scheduleAtFixedRate(dyna.app.core.sch.AbstractScheduledTask, long, long)
	 */
	@Override
	public void scheduleAtFixedRate(AbstractScheduledTask task, long delay, long period)
	{
		this.scheduledTaskExecutor.scheduleAtFixedRate(task, delay, period, TimeUnit.MILLISECONDS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.sch.Scheduler#addTask(dyna.app.core.sch.ScheduledTask)
	 */
	@Override
	public void addTask(ScheduledTask task)
	{
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.sch.Scheduler#clearTask()
	 */
	@Override
	public void clearTask()
	{
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.sch.AbstractScheduler#submitTask(dyna.app.core.sch.ScheduledTask)
	 */
	@Override
	public void submitTask(ScheduledTask task)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void shutdown()
	{
		this.scheduledTaskExecutor.shutdown();
	}

}
