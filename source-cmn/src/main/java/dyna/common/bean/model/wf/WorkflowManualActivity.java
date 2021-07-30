/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkflowManualActivity
 * Jiagang 2010-10-8
 */
package dyna.common.bean.model.wf;

import dyna.common.dto.model.wf.WorkflowActivityInfo;
import dyna.common.dto.model.wf.WorkflowManualActivityInfo;
import dyna.common.systemenum.ProcessingModeEnum;
import dyna.common.systemenum.WorkflowActivityType;

/**
 * 工作流人为活动
 * 
 * @author Jiagang
 * 
 */
public class WorkflowManualActivity extends WorkflowActivity
{
	private static final long	serialVersionUID	= 1099679198071708936L;

	private ProcessingModeEnum	processMode			= ProcessingModeEnum.OR;

	public WorkflowManualActivity()
	{
		this.workflowActivityInfo = new WorkflowManualActivityInfo();
		this.setType(WorkflowActivityType.MANUAL.getValue());
	}

	public WorkflowManualActivity(WorkflowManualActivityInfo info)
	{
		this.workflowActivityInfo = info;
		this.setType(WorkflowActivityType.MANUAL.getValue());
	}

	@Override
	public WorkflowManualActivityInfo getWorkflowActivityInfo()
	{
		return (WorkflowManualActivityInfo) workflowActivityInfo;
	}

	@Override
	public void setWorkflowActivityInfo(WorkflowActivityInfo workflowManualActivityInfo)
	{
		if (workflowManualActivityInfo.getClass() != WorkflowManualActivityInfo.class)
		{
			throw new UnsupportedOperationException("Invalid param on setWorkflowActivityInfo, use WorkflowManualActivityInfo instead.");
		}
		this.workflowActivityInfo = workflowManualActivityInfo;
	}

	/**
	 * @param processMode
	 *            the processMode to set
	 */
	public void setProcessMode(ProcessingModeEnum processMode)
	{
		this.processMode = processMode;
	}

	/**
	 * @return the processMode
	 */
	public ProcessingModeEnum getProcessMode()
	{
		return this.processMode;
	}

}
