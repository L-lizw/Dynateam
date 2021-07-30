/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkflowApplicationActivity
 * Jiagang 2010-10-9
 */
package dyna.common.bean.model.wf;

import dyna.common.dto.model.wf.WorkflowActivityInfo;
import dyna.common.dto.model.wf.WorkflowApplicationActivityInfo;

/**
 * 工作流自发活动
 *
 * @author Jiagang
 *
 */
public class WorkflowApplicationActivity extends WorkflowActivity
{

	private static final long		serialVersionUID	= 4725987910924725879L;

	public WorkflowApplicationActivity()
	{
		this.workflowActivityInfo = new WorkflowApplicationActivityInfo();
	}

	public WorkflowApplicationActivity(WorkflowActivityInfo workflowApplicationActivityInfo)
	{
		this.setWorkflowActivityInfo(workflowApplicationActivityInfo);
	}

	@Override
	public WorkflowApplicationActivityInfo getWorkflowActivityInfo()
	{
		return (WorkflowApplicationActivityInfo) workflowActivityInfo;
	}

	@Override
	public void setWorkflowActivityInfo(WorkflowActivityInfo workflowApplicationActivityInfo)
	{
		if (workflowApplicationActivityInfo.getClass() != WorkflowApplicationActivityInfo.class)
		{
			throw new UnsupportedOperationException("Invalid param on setWorkflowActivityInfo, use WorkflowApplicationActivityInfo instead.");
		}
		this.workflowActivityInfo = workflowApplicationActivityInfo;
	}

}
