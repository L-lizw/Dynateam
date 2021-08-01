/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: TaskMember
 * WangLHB May 29, 2012
 */
package dyna.common.bean.data.ppms;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.ppm.RptTaskExecStateStatMapper;

/**
 * @author fanjq
 * 
 */
@EntryMapper(RptTaskExecStateStatMapper.class)
public class RptTaskExecStateStat extends SystemObjectImpl implements SystemObject
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -4438820449809043889L;
	public static final String	EXECUTESTATUSNAME	= "EXECUTESTATUSNAME";
	public static final String	SUMOFSTATE			= "SUMOFSTATE";

	public String getExecuteStatusName()
	{
		return (String) super.get(EXECUTESTATUSNAME);
	}

	public void setExecuteStatusName(String executeStatusName)
	{
		super.put(EXECUTESTATUSNAME, executeStatusName);
	}

	public int getSumOfState()
	{
		return super.get(SUMOFSTATE) == null ? 0 : ((Number) super.get(SUMOFSTATE)).intValue();
	}

	public void setSumOfState(int sumOfState)
	{
		super.put(SUMOFSTATE, sumOfState);
	}

	public RptTaskExecStateStat()
	{
		super();
	}

}
