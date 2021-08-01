/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: TaskMember
 * WangLHB May 29, 2012
 */
package dyna.common.bean.data.ppms;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.ppm.RptWorkItemDeptStatMapper;

/**
 * @author fanjq
 * 
 */
@EntryMapper(RptWorkItemDeptStatMapper.class)
public class RptWorkItemDeptStat extends SystemObjectImpl implements SystemObject
{
	private static final long	serialVersionUID	= -5313799462059525539L;

	public static final String	DEPTGUID			= "DEPTGUID";
	public static final String	DEPT				= "DEPT";
	public static final String	SUMOFWORKITEM		= "SUMOFWORKITEM";
	public static final String	NOTASSIGNED			= "NOTASSIGNED";		// 未指派
	public static final String	INITSTATE			= "INITSTATE";			// 未启动
	public static final String	RUNNINGSTATE		= "RUNNINGSTATE";		// 运行中
	public static final String	FINISHSTATE			= "FINISHSTATE";		// 已完成
	public static final String	PAUSESTATE			= "PAUSESTATE";			// 暂停
	public static final String	CANCEL				= "CANCEL";				// 终止

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

	public int getSumOfWorkItem()
	{
		return super.get(SUMOFWORKITEM) == null ? 0 : ((Number) super.get(SUMOFWORKITEM)).intValue();
	}

	public void setSumOfWorkItem(int sumOfWorkItem)
	{
		super.put(SUMOFWORKITEM, sumOfWorkItem);
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

	public int getCalcelState()
	{
		return super.get(CANCEL) == null ? 0 : ((Number) super.get(CANCEL)).intValue();
	}

	public void setCancelState(int cancelState)
	{
		super.put(CANCEL, cancelState);
	}

	public int getNotAssigned()
	{
		return super.get(NOTASSIGNED) == null ? 0 : ((Number) super.get(NOTASSIGNED)).intValue();
	}

	public void setNotAssigned(int notAssigned)
	{
		super.put(NOTASSIGNED, notAssigned);
	}

	public RptWorkItemDeptStat()
	{
		super();
	}

}
