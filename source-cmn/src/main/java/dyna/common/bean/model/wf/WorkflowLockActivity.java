/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkflowLockActivity
 * Jiagang 2010-10-9
 */
package dyna.common.bean.model.wf;

import dyna.common.dto.model.wf.WorkflowActivityInfo;
import dyna.common.dto.model.wf.WorkflowLockActivityInfo;
import dyna.common.systemenum.WorkflowActivityType;
import dyna.common.systemenum.WorkflowApplicationType;

/**
 * 工作流锁定活动
 * 
 * @author Jiagang
 * 
 */
public class WorkflowLockActivity extends WorkflowApplicationActivity
{
	private static final long serialVersionUID = -4261288191380586834L;

	public WorkflowLockActivity()
	{
		this.workflowActivityInfo = new WorkflowLockActivityInfo();
	}

	public WorkflowLockActivity(WorkflowLockActivityInfo workflowLockActivityInfo)
	{
		this.workflowActivityInfo = workflowLockActivityInfo;
	}

	@Override
	public WorkflowLockActivityInfo getWorkflowActivityInfo()
	{
		return (WorkflowLockActivityInfo) workflowActivityInfo;
	}

	@Override
	public void setWorkflowActivityInfo(WorkflowActivityInfo workflowLockActivityInfo)
	{
		if (workflowLockActivityInfo.getClass() != WorkflowLockActivityInfo.class)
		{
			throw new UnsupportedOperationException("Invalid param on setWorkflowActivityInfo, use WorkflowLockActivityInfo instead.");
		}
		this.workflowActivityInfo = workflowLockActivityInfo;
	}
}
