/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: JobExecutor
 * Wanglei 2011-11-8
 */
package dyna.app.service.das.jss;

import dyna.common.dto.Queue;

/**
 * 工作任务执行接口
 * 
 * @author Wanglei
 * 
 */
public interface JobExecutor
{

	/**
	 * 执行工作任务
	 * 
	 * @param jss
	 *            可通过此服务获取相关服务
	 * @param job
	 *            需要执行的任务
	 * @return 执行的结果
	 */
	public JobResult perform(JSSImpl jss, Queue job) throws Exception;
	
	public JobResult serverPerformFail(JSSImpl jss, Queue job) throws Exception;
}
