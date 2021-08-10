/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: JobResult
 * Wanglei 2011-11-8
 */
package dyna.app.service.das.jss;

import dyna.common.systemenum.JobStatus;

/**
 * 工作任务执行结果
 * 
 * @author Wanglei
 * 
 */
public class JobResult
{

	private String		message	= null;
	private JobStatus	status	= JobStatus.SUCCESSFUL;
	private boolean isSendNotify=true;
	/**
	 * 执行成功
	 * 
	 * @param message
	 *            成功的消息
	 */
	public static JobResult succeed(String message)
	{
		return new JobResult(message, JobStatus.SUCCESSFUL);
	}
	
	/**
	 * 执行成功
	 * 
	 * @param message
	 *            成功的消息
	 */
	public static JobResult succeed(String message,boolean isSendNotify)
	{
		JobResult xJobResult=new JobResult(message, JobStatus.SUCCESSFUL);
		xJobResult.setSendNotify(isSendNotify);
		return xJobResult;
	}
	
	public static JobResult erpExecuting(String message,boolean isSendNotify)
	{
		JobResult xJobResult=new JobResult(message, JobStatus.ERPEXECUTING);
		xJobResult.setSendNotify(isSendNotify);
		return xJobResult;
	}

	/**
	 * 执行失败
	 * 
	 * @param message
	 *            失败的消息
	 */
	public static JobResult failed(String message)
	{
		return new JobResult(message, JobStatus.FAILED);
	}
	
	/**
	 * 执行失败
	 * 
	 * @param message
	 *            失败的消息
	 */
	public static JobResult failed(String message,boolean isSendNotify)
	{
		JobResult xJobResult=new JobResult(message, JobStatus.FAILED);
		xJobResult.setSendNotify(isSendNotify);
		return xJobResult;
	}

	protected JobResult(String message, JobStatus status)
	{
		this.message = message;
		this.status = status;
	}

	/**
	 * @return the message
	 */
	public String getMessage()
	{
		return this.message;
	}

	/**
	 * @return the status
	 */
	public JobStatus getStatus()
	{
		return this.status;
	}

	/**
	 * @return the isSendNotify
	 */
	public boolean isSendNotify()
	{
		return isSendNotify;
	}

	/**
	 * @param isSendNotify the isSendNotify to set
	 */
	public void setSendNotify(boolean isSendNotify)
	{
		this.isSendNotify = isSendNotify;
	}

}
