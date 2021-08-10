/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ScheduleManager 计划任务管理器
 * Wanglei 2011-4-20
 */
package dyna.app.core.sch;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 计划任务管理器
 * 
 * @author Wanglei
 * 
 */
@Component
public class SchedulerManager
{
	public static final String				MULTI_THREAD_QUEUED_TASK	= "MULTITHREADQUEUEDTASK";
	public static final String				SCHEDULED_TASK				= "SCHEDULEDTASK";

	private final Map<String, Scheduler>	schedulers					= new HashMap<String, Scheduler>();

	public Scheduler getMultiThreadQueuedTaskScheduler()
	{
		return this.getScheduler(MULTI_THREAD_QUEUED_TASK);
	}

	public Scheduler getScheduledTaskScheduler()
	{
		return this.getScheduler(SCHEDULED_TASK);
	}

	public void addScheduler(String schName, Scheduler scheduler)
	{
		synchronized (schedulers)
		{
			if (!this.schedulers.containsKey(schName))
			{
				this.schedulers.put(schName, scheduler);
			}
		}
	}

	public void createScheduler(String schName, int threadCount)
	{
		synchronized (schedulers)
		{
			if (!this.schedulers.containsKey(schName))
			{
				this.schedulers.put(schName, new SchedulerQueuedTaskImpl(threadCount));
			}
		}
	}

	public Scheduler getScheduler(String schName)
	{
		Scheduler scheduler = this.schedulers.get(schName);
		// if (scheduler == null)
		// {
		// throw new IllegalStateException("unmatch scheduler by name: " + schName);
		// }
		return scheduler;
	}

	public void shutdown(String schName)
	{
		this.getScheduler(schName).shutdown();
	}

	public void shutdownAll()
	{
		for (Iterator<Scheduler> iter = this.schedulers.values().iterator(); iter.hasNext();)
		{
			iter.next().shutdown();
		}
	}
}
