/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkflowUnlockActivity
 * Jiagang 2010-10-9
 */
package dyna.common.dto.model.wf;

import dyna.common.systemenum.WorkflowActivityType;
import dyna.common.systemenum.WorkflowApplicationType;

/**
 * 工作流解锁活动
 *
 * @author Jiagang
 *
 */
public class WorkflowUnlockActivityInfo extends WorkflowApplicationActivityInfo
{
	private static final long serialVersionUID = -4889973760632547408L;

	public WorkflowUnlockActivityInfo()
	{
		this.setType(WorkflowActivityType.APPLICATION.getValue());
		this.setApplicationType(WorkflowApplicationType.UNLOCK);
	}

	@Override
	public WorkflowUnlockActivityInfo clone()
	{
		// TODO Auto-generated method stub
		return (WorkflowUnlockActivityInfo) super.clone();
	}
}
