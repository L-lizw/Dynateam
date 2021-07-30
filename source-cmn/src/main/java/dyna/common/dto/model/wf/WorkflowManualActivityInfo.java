/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkflowManualActivity
 * Jiagang 2010-10-8
 */
package dyna.common.dto.model.wf;

import dyna.common.systemenum.ProcessingModeEnum;
import dyna.common.systemenum.WorkflowActivityType;

/**
 * 工作流人为活动
 * 
 * @author Jiagang
 * 
 */
public class WorkflowManualActivityInfo extends WorkflowActivityInfo
{
	private static final long	serialVersionUID	= 1099679198071708936L;

	private ProcessingModeEnum	processMode			= ProcessingModeEnum.OR;

	public WorkflowManualActivityInfo()
	{
		this.setType(WorkflowActivityType.MANUAL.getValue());
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

	@Override
	public WorkflowManualActivityInfo clone()
	{
		// TODO Auto-generated method stub
		return (WorkflowManualActivityInfo) super.clone();
	}
}
