/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: TaskMember
 * WangLHB May 29, 2012
 */
package dyna.common.bean.data.ppms;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.ppm.RptWorkItemExecStateStatMapper;

/**
 * @author fanjq
 * 
 */
@EntryMapper(RptWorkItemExecStateStatMapper.class)
public class RptWorkItemExecStateStat extends SystemObjectImpl implements SystemObject
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -4438820449809043889L;
	public static final String	OPERATIONSTATE		= "OPERATIONSTATE";
	public static final String	SUMOFSTATE			= "SUMOFSTATE";

	public String getOperationState()
	{
		return (String) super.get(OPERATIONSTATE);
	}

	public void getStopState(String operationState)
	{
		super.put(OPERATIONSTATE, operationState);
	}

	public int getSumOfState()
	{
		return super.get(SUMOFSTATE) == null ? 0 : ((Number) super.get(SUMOFSTATE)).intValue();
	}

	public void setSumOfState(int sumOfState)
	{
		super.put(SUMOFSTATE, sumOfState);
	}

	public RptWorkItemExecStateStat()
	{
		super();
	}

}
