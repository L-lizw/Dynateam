/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkflowNotifyActivity
 * Jiagang 2010-10-9
 */
package dyna.common.bean.model.wf;

import dyna.common.dto.model.wf.WorkflowActivityInfo;
import dyna.common.dto.model.wf.WorkflowNotifyActivityInfo;
import dyna.common.systemenum.WorkflowActivityType;
import dyna.common.systemenum.WorkflowApplicationType;

/**
 * 工作流通知活动
 * 
 * @author Jiagang
 * 
 */
public class WorkflowNotifyActivity extends WorkflowApplicationActivity
{
	private static final long serialVersionUID = -1935237994407585997L;

	public WorkflowNotifyActivity()
	{
		this.workflowActivityInfo = new WorkflowNotifyActivityInfo();
	}

	public WorkflowNotifyActivity(WorkflowNotifyActivityInfo workflowNotifyActivityInfo)
	{
		this.workflowActivityInfo = (workflowNotifyActivityInfo);
	}

	@Override
	public WorkflowNotifyActivityInfo getWorkflowActivityInfo()
	{
		return (WorkflowNotifyActivityInfo) workflowActivityInfo;
	}

	@Override
	public void setWorkflowActivityInfo(WorkflowActivityInfo workflowNotifyActivityInfo)
	{
		if (workflowNotifyActivityInfo.getClass() != WorkflowNotifyActivityInfo.class)
		{
			throw new UnsupportedOperationException("Invalid param on setWorkflowActivityInfo, use WorkflowNotifyActivityInfo instead.");
		}
		this.workflowActivityInfo = workflowNotifyActivityInfo;
	}
}
