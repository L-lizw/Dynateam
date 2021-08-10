/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SchedulerQueuedTaskImpl
 * Wanglei 2011-4-20
 */
package dyna.app.core.sch;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Wanglei
 * 
 */
public class SchedulerQueuedTaskImpl extends AbstractScheduler
{
	private ExecutorService	queueTaskExecutor	= null;

	/**
	 * @param threadSize
	 * @param delay
	 */
	public SchedulerQueuedTaskImpl(int threadSize)
	{
		if (threadSize < 1)
		{
			threadSize = DEFAULT_THREAD_SIZE;
		}

		// if (1 == threadSize)
		// {
		// queueTaskExecutor = Executors.newSingleThreadExecutor();
		// }
		// else
		// {
		// queueTaskExecutor = Executors.newFixedThreadPool(threadSize);
		// }

		// 使用具有优先级的无界队列PriorityBlockingQueue构造线程池
		queueTaskExecutor = new ThreadPoolExecutor(threadSize, threadSize, 0L, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<Runnable>());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.sch.Scheduler#addTask(dyna.app.core.sch.ScheduledTask)
	 */
	@Override
	public void addTask(ScheduledTask task)
	{
		queueTaskExecutor.execute(task);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.sch.Scheduler#clearTask()
	 */
	@Override
	public void clearTask()
	{
	}

	@Override
	public void submitTask(ScheduledTask task)
	{
		queueTaskExecutor.submit(task);
	}

	@Override
	public void shutdown()
	{
		queueTaskExecutor.shutdown();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.sch.Scheduler#schedule(dyna.app.core.sch.AbstractScheduledTask, java.util.Date)
	 */
	@Override
	public void schedule(AbstractScheduledTask task, Date time)
	{
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.sch.Scheduler#schedule(dyna.app.core.sch.AbstractScheduledTask, long)
	 */
	@Override
	public void schedule(AbstractScheduledTask task, long delay)
	{
		throw new UnsupportedOperationException();
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
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.sch.Scheduler#scheduleAtFixedRate(dyna.app.core.sch.AbstractScheduledTask, long, long)
	 */
	@Override
	public void scheduleAtFixedRate(AbstractScheduledTask task, long delay, long period)
	{
		throw new UnsupportedOperationException();
	}

	// private class PriorityComparator implements Comparator<ScheduledTask>
	// {
	// /*
	// * (non-Javadoc)
	// *
	// * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	// */
	// @Override
	// public int compare(ScheduledTask t1, ScheduledTask t2)
	// {
	// int p1 = t1.getPriority();
	// int p2 = t2.getPriority();
	//
	// if (p1 > p2)
	// {
	// return 1;
	// }
	// else if (p1 < p2)
	// {
	// return -1;
	// }
	// else
	// {
	// if (t1.getCreateTime() == null)
	// {
	// return 1;
	// }
	// else if (t2.getCreateTime() == null)
	// {
	// return -1;
	// }
	// else
	// {
	// return t1.getCreateTime().compareTo(t2.getCreateTime());
	// }
	// }
	// }
	// }

}
