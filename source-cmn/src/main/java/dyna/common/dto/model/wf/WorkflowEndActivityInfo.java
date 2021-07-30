/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkflowEndActivity
 * Jiagang 2010-10-8
 */
package dyna.common.dto.model.wf;

import dyna.common.systemenum.WorkflowActivityType;

/**
 * 工作流流程出口活动
 * 
 * @author Jiagang
 * 
 */
public class WorkflowEndActivityInfo extends WorkflowActivityInfo
{
	private static final long serialVersionUID = -7194819555564241873L;

	public WorkflowEndActivityInfo()
	{
		this.setName(WorkflowActivityType.END.toString());
		this.setType(WorkflowActivityType.END.getValue());
	}

	@Override
	public WorkflowEndActivityInfo clone()
	{
		// TODO Auto-generated method stub
		return (WorkflowEndActivityInfo) super.clone();
	}
}
