/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkflowChangePhaseActivity
 * Jiagang 2010-10-9
 */
package dyna.common.bean.model.wf;

import java.util.ArrayList;
import java.util.List;

import dyna.common.dto.model.wf.WorkflowActivityInfo;
import dyna.common.dto.model.wf.WorkflowActrtStatusInfo;
import dyna.common.dto.model.wf.WorkflowChangeStatusActivityInfo;

/**
 * 工作流阶段转变活动
 * 
 * @author Jiagang
 * 
 */
public class WorkflowChangeStatusActivity extends WorkflowApplicationActivity
{
	private static final long				serialVersionUID	= -3129272646160475111L;

	private List<WorkflowActrtStatusInfo>	statusChangeList	= null;

	public WorkflowChangeStatusActivity()
	{
		this.workflowActivityInfo = new WorkflowChangeStatusActivityInfo();
	}

	public WorkflowChangeStatusActivity(WorkflowChangeStatusActivityInfo workflowChangeStatusActivityInfo)
	{
		this.workflowActivityInfo = workflowChangeStatusActivityInfo;
	}

	/**
	 * @return the phaseChangeList
	 */
	public List<WorkflowActrtStatusInfo> getStatusChangeList()
	{
		if (statusChangeList == null)
		{
			this.statusChangeList = new ArrayList<WorkflowActrtStatusInfo>();
		}
		return this.statusChangeList;
	}

	/**
	 * @param phaseChangeList
	 *            the phaseChangeList to set
	 */
	public void setStatusChangeList(List<WorkflowActrtStatusInfo> statusChangeList)
	{
		this.statusChangeList = statusChangeList;
	}

	@Override
	public WorkflowChangeStatusActivityInfo getWorkflowActivityInfo()
	{
		return (WorkflowChangeStatusActivityInfo) workflowActivityInfo;
	}

	@Override
	public void setWorkflowActivityInfo(WorkflowActivityInfo workflowChangeStatusActivityInfo)
	{
		if (workflowChangeStatusActivityInfo.getClass() != WorkflowChangeStatusActivityInfo.class)
		{
			throw new UnsupportedOperationException("Invalid param on setWorkflowActivityInfo, use WorkflowChangeStatusActivityInfo instead.");
		}
		this.workflowActivityInfo = workflowChangeStatusActivityInfo;
	}
}
