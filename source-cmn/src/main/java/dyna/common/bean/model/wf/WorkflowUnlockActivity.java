/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkflowUnlockActivity
 * Jiagang 2010-10-9
 */
package dyna.common.bean.model.wf;

import dyna.common.dto.model.wf.WorkflowActivityInfo;
import dyna.common.dto.model.wf.WorkflowUnlockActivityInfo;
import dyna.common.systemenum.WorkflowActivityType;
import dyna.common.systemenum.WorkflowApplicationType;

/**
 * 工作流解锁活动
 *
 * @author Jiagang
 *
 */
public class WorkflowUnlockActivity extends WorkflowApplicationActivity
{
	private static final long serialVersionUID = -4889973760632547408L;

	public WorkflowUnlockActivity()
	{
		this.workflowActivityInfo = new WorkflowUnlockActivityInfo();
	}

	public WorkflowUnlockActivity(WorkflowUnlockActivityInfo workflowUnlockActivityInfo)
	{
		this.workflowActivityInfo = workflowUnlockActivityInfo;
	}

	@Override
	public WorkflowUnlockActivityInfo getWorkflowActivityInfo()
	{
		return (WorkflowUnlockActivityInfo) workflowActivityInfo;
	}

	@Override
	public void setWorkflowActivityInfo(WorkflowActivityInfo workflowUnlockActivityInfo)
	{
		if (workflowUnlockActivityInfo.getClass() != WorkflowUnlockActivityInfo.class)
		{
			throw new UnsupportedOperationException("Invalid param on setWorkflowActivityInfo, use WorkflowUnlockActivityInfo instead.");
		}
		this.workflowActivityInfo = workflowUnlockActivityInfo;
	}
}
