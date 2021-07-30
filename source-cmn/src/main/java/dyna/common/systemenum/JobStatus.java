/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: JobStatus
 * Wanglei 2011-11-8
 */
package dyna.common.systemenum;


/**
 * 序列工作任务状态
 * 
 * @author Wanglei
 * 
 */
public enum JobStatus
{
	/**
	 * 等待执行
	 */
	WAITING(0),
	/**
	 * 正在执行
	 */
	RUNNING(1),
	/**
	 * 取消执行
	 */
	CANCEL(2),
	/**
	 * 执行成功
	 */
	SUCCESSFUL(3),
	/**
	 * 执行失败
	 */
	FAILED(4),
	/**
	 * ERP执行中
	 */
	ERPEXECUTING(5);

	private int	value	= 0;

	public static JobStatus getStatus(int value)
	{
		for (JobStatus status : values())
		{
			if (value == status.getValue())
			{
				return status;
			}
		}
		return WAITING;
	}

	private JobStatus(int value)
	{
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public int getValue()
	{
		return this.value;
	}

}
