/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: TaskMember
 * WangLHB May 29, 2012
 */
package dyna.common.bean.data.ppms;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.ppm.RptDeptStatMapper;

import java.math.BigDecimal;

/**
 * @author fanjq
 * 
 */
@EntryMapper(RptDeptStatMapper.class)
public class RptDeptStat extends SystemObjectImpl implements SystemObject
{
	private static final long	serialVersionUID	= 1389968479796991751L;

	public static final String	DEPT				= "DEPT";
	public static final String	SUMOFPROJECT		= "SUMOFPROJECT";
	public static final String	INITSTATE			= "INITSTATE";			// 初始化
	public static final String	RUNNINGSTATE		= "RUNNINGSTATE";
	public static final String	ALTERATIONSTATE		= "ALTERATIONSTATE";
	public static final String	FINISHSTATE			= "FINISHSTATE";
	public static final String	OBSOLETESTATE		= "OBSOLETESATE";
	public static final String	PAUSESTATE			= "PAUSESTATE";
	public static final String	PENDINGSTATE		= "PENDINGSTATE";
	public static final String	DEPTGUID			= "DEPTGUID";

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

	public String getSumOfProject()
	{
		if (super.get(SUMOFPROJECT) != null && super.get(SUMOFPROJECT) instanceof BigDecimal)
		{
			BigDecimal sumOfProject = (BigDecimal) super.get(SUMOFPROJECT);
			return String.valueOf(sumOfProject.setScale(0).intValue());
		}
		return (String) super.get(SUMOFPROJECT);
	}

	public void setSumOfProject(String sumOfProject)
	{
		super.put(SUMOFPROJECT, sumOfProject);
	}

	public String getInitState()
	{
		if (super.get(INITSTATE) != null && super.get(INITSTATE) instanceof BigDecimal)
		{
			BigDecimal initState = (BigDecimal) super.get(INITSTATE);
			return String.valueOf(initState.setScale(0).intValue());
		}
		return (String) super.get(INITSTATE);
	}

	public void setInitState(String initState)
	{
		super.put(INITSTATE, initState);
	}

	public String getRunningState()
	{
		if (super.get(RUNNINGSTATE) != null && super.get(RUNNINGSTATE) instanceof BigDecimal)
		{
			BigDecimal runingState = (BigDecimal) super.get(RUNNINGSTATE);
			return String.valueOf(runingState.setScale(0).intValue());
		}
		return (String) super.get(RUNNINGSTATE);
	}

	public void setRunningState(String runningState)
	{
		super.put(RUNNINGSTATE, runningState);
	}

	public String getAlterationState()
	{
		if (super.get(ALTERATIONSTATE) != null && super.get(ALTERATIONSTATE) instanceof BigDecimal)
		{
			BigDecimal alterationState = (BigDecimal) super.get(ALTERATIONSTATE);
			return String.valueOf(alterationState.setScale(0).intValue());
		}
		return (String) super.get(ALTERATIONSTATE);
	}

	public void setAlterationState(String alterationState)
	{
		super.put(ALTERATIONSTATE, alterationState);
	}

	public String getFinishState()
	{
		if (super.get(FINISHSTATE) != null && super.get(FINISHSTATE) instanceof BigDecimal)
		{
			BigDecimal finishState = (BigDecimal) super.get(FINISHSTATE);
			return String.valueOf(finishState.setScale(0).intValue());
		}
		return (String) super.get(FINISHSTATE);
	}

	public void setFinishState(String finishState)
	{
		super.put(FINISHSTATE, finishState);
	}

	public String getObsoleteState()
	{
		if (super.get(OBSOLETESTATE) != null && super.get(OBSOLETESTATE) instanceof BigDecimal)
		{
			BigDecimal obsoleteState = (BigDecimal) super.get(OBSOLETESTATE);
			return String.valueOf(obsoleteState.setScale(0).intValue());
		}
		return (String) super.get(OBSOLETESTATE);
	}

	public void setObsoletestate(String obsoleteState)
	{
		super.put(OBSOLETESTATE, obsoleteState);
	}

	public String getPauseState()
	{
		if (super.get(PAUSESTATE) != null && super.get(PAUSESTATE) instanceof BigDecimal)
		{
			BigDecimal pauseState = (BigDecimal) super.get(PAUSESTATE);
			return String.valueOf(pauseState.setScale(0).intValue());
		}
		return (String) super.get(PAUSESTATE);
	}

	public void setPauseState(String pauseState)
	{
		super.put(PAUSESTATE, pauseState);
	}

	public String getPendingState()
	{
		if (super.get(PENDINGSTATE) != null && super.get(PENDINGSTATE) instanceof BigDecimal)
		{
			BigDecimal pendingState = (BigDecimal) super.get(PENDINGSTATE);
			return String.valueOf(pendingState.setScale(0).intValue());
		}
		return (String) super.get(PENDINGSTATE);
	}

	public void getPendingState(String pendingState)
	{
		super.put(PENDINGSTATE, pendingState);
	}

	public RptDeptStat()
	{
		super();
	}

}
