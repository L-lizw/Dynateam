/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkflowNotifyActivity
 * Jiagang 2010-10-9
 */
package dyna.common.dto.model.wf;

import dyna.common.systemenum.WorkflowActivityType;
import dyna.common.systemenum.WorkflowApplicationType;

/**
 * 工作流通知活动
 * 
 * @author Jiagang
 * 
 */
public class WorkflowNotifyActivityInfo extends WorkflowApplicationActivityInfo
{
	private static final long serialVersionUID = -1935237994407585997L;

	public WorkflowNotifyActivityInfo()
	{
		this.setType(WorkflowActivityType.NOTIFY.getValue());
		this.setApplicationType(WorkflowApplicationType.NOTIFY);
	}

	@Override
	public WorkflowNotifyActivityInfo clone()
	{
		// TODO Auto-generated method stub
		return (WorkflowNotifyActivityInfo) super.clone();
	}
}
