/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: JobCallback
 * Wanglei 2011-11-9
 */
package dyna.app.service.das.jss;

import dyna.common.dto.Queue;

/**
 * 工作任务执行过程的回调接口
 * 
 * @author Wanglei
 * 
 */
public interface JobCallback
{

	/**
	 * 执行工作任务前自动调用
	 * 
	 * @param job
	 */
	public void beforePerform(Queue job);

	/**
	 * 执行工作任务后自动调用
	 * 
	 * @param job
	 */
	public void afterPerform(Queue job);
}
