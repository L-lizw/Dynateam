/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: TaskMember
 * WangLHB May 29, 2012
 */
package dyna.common.bean.data.ppms;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;

/**
 * @author fanjq
 * 
 */
public class RptTaskExecutorStat extends SystemObjectImpl implements SystemObject
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -7426584595764881573L;
	public static final String	EXECUTORGUID		= "EXECUTORGUID";
	public static final String	EXECUTOR			= "EXECUTOR";
	public static final String	SUMOFTASK			= "SUMOFTASK";
	public static final String	NUMOFFINISH			= "NUMOFFINISH";

	public String getExecutorGuid()
	{
		return (String) super.get(EXECUTORGUID);
	}

	public void setExecutorGuid(String executorGuid)
	{
		super.put(EXECUTORGUID, executorGuid);
	}

	public String getExecutor()
	{
		return (String) super.get(EXECUTOR);
	}

	public void setExecutor(String executor)
	{
		super.put(EXECUTOR, executor);
	}

	public int getSumOfProject()
	{
		return super.get(SUMOFTASK) == null ? 0 : ((Number) super.get(SUMOFTASK)).intValue();
	}

	public void setSumOfProject(int sumOfProject)
	{
		super.put(SUMOFTASK, sumOfProject);
	}

	public int getNumOfFinish()
	{
		return super.get(NUMOFFINISH) == null ? 0 : ((Number) super.get(NUMOFFINISH)).intValue();
	}

	public void setNumOfFinish(int numOfFinish)
	{
		super.put(NUMOFFINISH, numOfFinish);
	}

	public RptTaskExecutorStat()
	{
		super();
	}

}
