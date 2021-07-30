/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkflowLockActivity
 * Jiagang 2010-10-9
 */
package dyna.common.dto.model.wf;

import dyna.common.systemenum.WorkflowActivityType;
import dyna.common.systemenum.WorkflowApplicationType;

/**
 * 工作流锁定活动
 * 
 * @author Jiagang
 * 
 */
public class WorkflowLockActivityInfo extends WorkflowApplicationActivityInfo
{
	private static final long serialVersionUID = -4261288191380586834L;

	public WorkflowLockActivityInfo()
	{
		this.setType(WorkflowActivityType.APPLICATION.getValue());
		this.setApplicationType(WorkflowApplicationType.LOCK);
	}

	@Override
	public WorkflowLockActivityInfo clone()
	{
		// TODO Auto-generated method stub
		return (WorkflowLockActivityInfo) super.clone();
	}
}
