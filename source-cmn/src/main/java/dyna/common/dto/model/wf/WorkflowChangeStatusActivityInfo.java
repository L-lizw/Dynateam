/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkflowChangePhaseActivity
 * Jiagang 2010-10-9
 */
package dyna.common.dto.model.wf;

import dyna.common.systemenum.WorkflowActivityType;
import dyna.common.systemenum.WorkflowApplicationType;

/**
 * 工作流阶段转变活动
 * 
 * @author Jiagang
 * 
 */
public class WorkflowChangeStatusActivityInfo extends WorkflowApplicationActivityInfo
{
	private static final long serialVersionUID = -3129272646160475111L;

	public WorkflowChangeStatusActivityInfo()
	{
		this.setType(WorkflowActivityType.APPLICATION.getValue());
		this.setApplicationType(WorkflowApplicationType.CHANGE_STATUS);
	}

	@Override
	public WorkflowChangeStatusActivityInfo clone()
	{
		// TODO Auto-generated method stub
		return (WorkflowChangeStatusActivityInfo) super.clone();
	}

}
