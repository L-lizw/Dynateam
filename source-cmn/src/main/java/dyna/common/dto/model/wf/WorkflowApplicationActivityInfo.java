/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkflowApplicationActivity
 * Jiagang 2010-10-9
 */
package dyna.common.dto.model.wf;

import dyna.common.systemenum.WorkflowApplicationType;

/**
 * 工作流自发活动
 *
 * @author Jiagang
 *
 */
public class WorkflowApplicationActivityInfo extends WorkflowActivityInfo
{
	private static final long		serialVersionUID	= 4725987910924725879L;

	private WorkflowApplicationType	applicationType		= null;

	/**
	 * @param applicationType
	 *            the applicationType to set
	 */
	public void setApplicationType(WorkflowApplicationType applicationType)
	{
		this.applicationType = applicationType;
	}

	/**
	 * @return the applicationType
	 */
	public WorkflowApplicationType getApplicationType()
	{
		return this.applicationType;
	}

	@Override
	public WorkflowApplicationActivityInfo clone()
	{
		// TODO Auto-generated method stub
		return (WorkflowApplicationActivityInfo) super.clone();
	}
}
