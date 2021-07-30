/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkflowEndActivity
 * Jiagang 2010-10-8
 */
package dyna.common.bean.model.wf;

import dyna.common.dto.model.wf.WorkflowActivityInfo;
import dyna.common.dto.model.wf.WorkflowEndActivityInfo;
import dyna.common.systemenum.WorkflowActivityType;

/**
 * 工作流流程出口活动
 * 
 * @author Jiagang
 * 
 */
public class WorkflowEndActivity extends WorkflowActivity
{
	private static final long serialVersionUID = -7194819555564241873L;

	public WorkflowEndActivity()
	{
		this.workflowActivityInfo = new WorkflowEndActivityInfo();
		this.setName(WorkflowActivityType.END.toString());
		this.setType(WorkflowActivityType.END.getValue());
	}

	public WorkflowEndActivity(WorkflowEndActivityInfo info)
	{
		this.workflowActivityInfo = info;
		this.setName(WorkflowActivityType.END.toString());
		this.setType(WorkflowActivityType.END.getValue());
	}

	@Override
	public WorkflowEndActivityInfo getWorkflowActivityInfo()
	{
		return (WorkflowEndActivityInfo) workflowActivityInfo;
	}

	@Override
	public void setWorkflowActivityInfo(WorkflowActivityInfo workflowEndActivityInfo)
	{
		if (workflowEndActivityInfo.getClass() != WorkflowEndActivityInfo.class)
		{
			throw new UnsupportedOperationException("Invalid param on setWorkflowActivityInfo, use WorkflowEndActivityInfo instead.");
		}
		this.workflowActivityInfo = workflowEndActivityInfo;
	}

}
