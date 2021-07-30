/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkflowChangePhaseActivity
 * Jiagang 2010-10-9
 */
package dyna.common.bean.model.wf;

import java.util.ArrayList;
import java.util.List;

import dyna.common.dto.model.wf.WorkflowActivityInfo;
import dyna.common.dto.model.wf.WorkflowActrtLifecyclePhaseInfo;
import dyna.common.dto.model.wf.WorkflowChangePhaseActivityInfo;
import dyna.common.dto.model.wf.WorkflowPhaseChangeInfo;

/**
 * 工作流阶段转变活动
 * 
 * @author Jiagang
 * 
 */
public class WorkflowChangePhaseActivity extends WorkflowApplicationActivity
{
	private static final long						serialVersionUID		= -3129272646160475111L;

	private List<WorkflowActrtLifecyclePhaseInfo>	actrtLifecyclePhaseList	= null;

	private List<WorkflowPhaseChangeInfo>			phaseChangeList			= null;

	public WorkflowChangePhaseActivity()
	{
		this.workflowActivityInfo = new WorkflowChangePhaseActivityInfo();
	}

	public WorkflowChangePhaseActivity(WorkflowChangePhaseActivityInfo workflowChangePhaseActivityInfo)
	{
		this.workflowActivityInfo = workflowChangePhaseActivityInfo;
	}

	/**
	 * @return the phaseChangeList
	 */
	public List<WorkflowPhaseChangeInfo> getPhaseChangeList()
	{
		if (this.phaseChangeList == null)
		{
			this.phaseChangeList = new ArrayList<WorkflowPhaseChangeInfo>();
		}
		return this.phaseChangeList;
	}

	/**
	 * @param phaseChangeList
	 *            the phaseChangeList to set
	 */
	public void setPhaseChangeList(List<WorkflowPhaseChangeInfo> phaseChangeList)
	{
		this.phaseChangeList = phaseChangeList;
	}

	public List<WorkflowActrtLifecyclePhaseInfo> getActrtLifecyclePhaseList()
	{
		if (actrtLifecyclePhaseList == null)
		{
			this.actrtLifecyclePhaseList = new ArrayList<WorkflowActrtLifecyclePhaseInfo>();
		}
		return actrtLifecyclePhaseList;
	}

	public void setActrtLifecyclePhaseList(List<WorkflowActrtLifecyclePhaseInfo> actrtLifecyclePhaseList)
	{
		this.actrtLifecyclePhaseList = actrtLifecyclePhaseList;
	}

	@Override
	public WorkflowChangePhaseActivityInfo getWorkflowActivityInfo()
	{
		return (WorkflowChangePhaseActivityInfo) workflowActivityInfo;
	}

	@Override
	public void setWorkflowActivityInfo(WorkflowActivityInfo workflowChangePhaseActivityInfo)
	{
		if (workflowChangePhaseActivityInfo.getClass() != WorkflowChangePhaseActivityInfo.class)
		{
			throw new UnsupportedOperationException("Invalid param on setWorkflowActivityInfo, use workflowChangePhaseActivityInfo instead.");
		}
		this.workflowActivityInfo = workflowChangePhaseActivityInfo;
	}

}
