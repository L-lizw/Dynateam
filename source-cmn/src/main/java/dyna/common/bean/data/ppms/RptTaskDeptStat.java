/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: TaskMember
 * WangLHB May 29, 2012
 */
package dyna.common.bean.data.ppms;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.ppm.RptTaskDeptStatMapper;

/**
 * @author fanjq
 * 
 */
@EntryMapper(RptTaskDeptStatMapper.class)
public class RptTaskDeptStat extends SystemObjectImpl implements SystemObject
{
	private static final long	serialVersionUID	= -5313799462059525539L;

	public static final String	DEPTGUID			= "DEPTGUID";
	public static final String	DEPT				= "DEPT";
	public static final String	SUMOFTASK			= "SUMOFTASK";
	public static final String	INITSTATE			= "INITSTATE";			// 初始化
	public static final String	RUNNINGSTATE		= "RUNNINGSTATE";
	public static final String	FINISHSTATE			= "FINISHSTATE";
	public static final String	PAUSESTATE			= "PAUSESTATE";
	public static final String	PENDINGSTATE		= "PENDINGSTATE";

	public String getDeptGuid()
	{
		return (String) super.get(DEPTGUID);
	}

	public void setDeptGuid(String deptGuid)
	{
		super.put(DEPTGUID, deptGuid);
	}

	public String getDept()
	{
		return (String) super.get(DEPT);
	}

	public void setDept(String dept)
	{
		super.put(DEPT, dept);
	}

	public int getSumOfTask()
	{
		return super.get(SUMOFTASK) == null ? 0 : ((Number) super.get(SUMOFTASK)).intValue();
	}

	public void setSumOfProject(int sumOfTask)
	{
		super.put(SUMOFTASK, sumOfTask);
	}

	public int getInitState()
	{
		return super.get(INITSTATE) == null ? 0 : ((Number) super.get(INITSTATE)).intValue();
	}

	public void setInitState(int initState)
	{
		super.put(INITSTATE, initState);
	}

	public int getRunningState()
	{
		return super.get(RUNNINGSTATE) == null ? 0 : ((Number) super.get(RUNNINGSTATE)).intValue();
	}

	public void setRunningState(int runningState)
	{
		super.put(RUNNINGSTATE, runningState);
	}

	public int getFinishState()
	{
		return super.get(FINISHSTATE) == null ? 0 : ((Number) super.get(FINISHSTATE)).intValue();
	}

	public void setFinishState(int finishState)
	{
		super.put(FINISHSTATE, finishState);
	}

	public int getPauseState()
	{
		return super.get(PAUSESTATE) == null ? 0 : ((Number) super.get(PAUSESTATE)).intValue();
	}

	public void setPauseState(int pauseState)
	{
		super.put(PAUSESTATE, pauseState);
	}

	public int getPendingState()
	{
		return super.get(PENDINGSTATE) == null ? 0 : ((Number) super.get(PENDINGSTATE)).intValue();
	}

	public void getPendingState(int pendingState)
	{
		super.put(PENDINGSTATE, pendingState);
	}

	public RptTaskDeptStat()
	{
		super();
	}

}
