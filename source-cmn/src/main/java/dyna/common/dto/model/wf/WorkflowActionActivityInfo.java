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
public class WorkflowActionActivityInfo extends WorkflowApplicationActivityInfo
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 132700179616549641L;

	public WorkflowActionActivityInfo()
	{
		this.setType(WorkflowActivityType.APPLICATION.getValue());
		this.setApplicationType(WorkflowApplicationType.ACTION);
	}

	@Override
	public WorkflowActionActivityInfo clone()
	{
		// TODO Auto-generated method stub
		return (WorkflowActionActivityInfo) super.clone();
	}
}
