/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkflowBeginActivity
 * Jiagang 2010-10-8
 */
package dyna.common.bean.model.wf;

import dyna.common.dto.model.wf.WorkflowActivityInfo;
import dyna.common.dto.model.wf.WorkflowBeginActivityInfo;
import dyna.common.systemenum.WorkflowActivityType;

/**
 * 工作流流程入口活动
 *
 * @author Jiagang
 *
 */
public class WorkflowBeginActivity extends WorkflowActivity
{
	private static final long serialVersionUID = -2083152007495205681L;

	public WorkflowBeginActivity()
	{
		this.workflowActivityInfo = new WorkflowBeginActivityInfo();
		this.setName(WorkflowActivityType.BEGIN.toString());
		this.setType(WorkflowActivityType.BEGIN.getValue());
	}

	public WorkflowBeginActivity(WorkflowBeginActivityInfo info)
	{
		this.workflowActivityInfo = info;
		this.setName(WorkflowActivityType.BEGIN.toString());
		this.setType(WorkflowActivityType.BEGIN.getValue());
	}

	@Override
	public WorkflowBeginActivityInfo getWorkflowActivityInfo()
	{
		return (WorkflowBeginActivityInfo) workflowActivityInfo;
	}

	@Override
	public void setWorkflowActivityInfo(WorkflowActivityInfo workflowBeginActivityInfo)
	{
		if (workflowBeginActivityInfo.getClass() != WorkflowBeginActivityInfo.class)
		{
			throw new UnsupportedOperationException("Invalid param on setWorkflowActivityInfo, use WorkflowBeginActivityInfo instead.");
		}
		this.workflowActivityInfo = workflowBeginActivityInfo;
	}
}
