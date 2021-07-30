/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServiceConfigBean
 * Qiuxq 2012-4-24
 */
package dyna.common.conf;

import java.io.Serializable;



/**
 * Job类型配置定义
 * 
 * @author Qiuxq
 * 
 */
public class JobDefinition extends InitParameter implements Serializable
{


	/**
	 * 
	 */
	private static final long	serialVersionUID	= 5423549735424960218L;

	private String		jobID				= null;

	private String		jobName				= null;

	private String		msrId		= null;
	
	private boolean		isSingleThread		= true;

	private int		priority			= 0;

	private String		executorClassName	= null;

	private Class<?>	executorClass		= null;
	
	private String		schedulerID				= null;
	
	private int		timeOut			= 0;

	/**
	 * @return the jobID
	 */
	public String getJobID()
	{
		return jobID;
	}

	/**
	 * @param jobID the jobID to set
	 */
	public void setJobID(String jobID)
	{
		this.jobID = jobID;
	}

	/**
	 * @return the jobName
	 */
	public String getJobName()
	{
		return jobName;
	}

	/**
	 * @param jobName the jobName to set
	 */
	public void setJobName(String jobName)
	{
		this.jobName = jobName;
	}

	/**
	 * @return the msrId
	 */
	public String getMsrId()
	{
		return msrId;
	}

	/**
	 * @param msrid the msrId to set
	 */
	public void setMsrId(String msrId)
	{
		this.msrId = msrId;
	}

	/**
	 * @return the isSingleThread
	 */
	public boolean isSingleThread()
	{
		return isSingleThread;
	}

	/**
	 * @param isSingleThread the isSingleThread to set
	 */
	public void setSingleThread(boolean isSingleThread)
	{
		this.isSingleThread = isSingleThread;
	}

	/**
	 * @return the priority
	 */
	public int getPriority()
	{
		return priority;
	}

	/**
	 * @param priority the priority to set
	 */
	public void setPriority(int priority)
	{
		this.priority = priority;
	}

	/**
	 * @return the executorClassName
	 */
	public String getExecutorClassName()
	{
		return executorClassName;
	}

	/**
	 * @param executorClassName the executorClassName to set
	 */
	public void setExecutorClassName(String executorClassName)
	{
		this.executorClassName = executorClassName;
	}

	/**
	 * @return the executorClass
	 * @throws ClassNotFoundException 
	 */
	public Class<?> getExecutorClass() throws ClassNotFoundException
	{
		if (this.executorClass == null)
		{
			this.executorClass = Class.forName(executorClassName);
		}
		return executorClass;
	}

	public String getSchedulerID() {
		return schedulerID;
	}

	public void setSchedulerID(String schedulerID) {
		this.schedulerID = schedulerID;
	}

	public int getTimeOut()
	{
		return timeOut;
	}

	public void setTimeOut(int timeOut)
	{
		this.timeOut = timeOut;
	}

}
