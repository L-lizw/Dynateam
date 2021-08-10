/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Scheduler 计划任务调度器
 * Wanglei 2011-4-20
 */
package dyna.app.core.sch;

import java.util.Date;

/**
 * 计划任务调度器
 * 
 * @author Wanglei
 * 
 */
public interface Scheduler
{
	public final static int	DEFAULT_THREAD_SIZE				= 16;
	public final static int	DEFAULT_SCHEDULED_THREAD_SIZE	= 5;
	public final static int	DEFAULT_DELAY					= 30;

	/**
	 * 安排在指定的时间执行指定的任务
	 * 
	 * @param task
	 * @param time
	 */
	public void schedule(AbstractScheduledTask task, Date time);

	/**
	 * 安排在指定延迟后执行指定的任务
	 * 
	 * @param task
	 * @param delay
	 */
	public void schedule(AbstractScheduledTask task, long delay);

	/**
	 * 安排指定的任务在指定的时间开始进行重复的固定速率执行<br>
	 * 如果设置的firstTime比程序运行的当前时间早, 则调度会立即自动计算firstTime与当前时间的长度内, period的次数来运行task
	 * 
	 * @param task
	 * @param firstTime
	 * @param period
	 */
	public void scheduleAtFixedRate(AbstractScheduledTask task, Date firstTime, long period);

	/**
	 * 安排指定的任务在指定的延迟后开始进行重复的固定速率执行
	 * 
	 * @param task
	 * @param delay
	 * @param period
	 */
	public void scheduleAtFixedRate(AbstractScheduledTask task, long delay, long period);

	/**
	 * 添加计划任务, 系统将根据当前任务情况, 安排适当的执行时间
	 * 
	 * @param task
	 */
	public void addTask(ScheduledTask task);

	/**
	 * 提交计划任务, 并且使用合适的线程立即执行.如果没有可用的线程, 将等待空闲线程再执行
	 * 
	 * @param task
	 */
	public void submitTask(ScheduledTask task);

	/**
	 * 清除队列中的所有任务
	 */
	public void clearTask();

	/**
	 * 关闭调度器, 所有任务将不再执行, 调度器将不可用
	 */
	public void shutdown();
}
