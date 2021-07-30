/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkflowBeginActivity
 * Jiagang 2010-10-8
 */
package dyna.common.dto.model.wf;

import dyna.common.systemenum.WorkflowActivityType;

/**
 * 工作流流程入口活动
 *
 * @author Jiagang
 *
 */
public class WorkflowBeginActivityInfo extends WorkflowActivityInfo
{
	private static final long serialVersionUID = -2083152007495205681L;

	public WorkflowBeginActivityInfo()
	{
		this.setName(WorkflowActivityType.BEGIN.toString());
		this.setType(WorkflowActivityType.BEGIN.getValue());
	}

	@Override
	public WorkflowBeginActivityInfo clone()
	{
		// TODO Auto-generated method stub
		return (WorkflowBeginActivityInfo) super.clone();
	}
}
