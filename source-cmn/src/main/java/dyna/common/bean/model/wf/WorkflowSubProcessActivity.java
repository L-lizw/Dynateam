/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkflowSubProcessActivity
 * Jiagang 2010-10-9
 */
package dyna.common.bean.model.wf;

import java.io.Serializable;

import dyna.common.dto.model.wf.WorkflowActivityInfo;
import dyna.common.dto.model.wf.WorkflowSubProcessActivityInfo;
import dyna.common.systemenum.WorkflowActivityType;

/**
 * 工作流子流程活动
 * 
 * @author Jiagang
 * 
 */
public class WorkflowSubProcessActivity extends WorkflowActivity
{
	private static final long	serialVersionUID	= 5622678257248696480L;

	/**
	 * 子流程名称
	 */
	private String				subProcessName		= null;

	public WorkflowSubProcessActivity()
	{
		this.workflowActivityInfo = new WorkflowSubProcessActivityInfo();
		this.setType(WorkflowActivityType.SUB_PROCESS.getValue());
	}

	public WorkflowSubProcessActivity(WorkflowSubProcessActivityInfo info)
	{
		this.workflowActivityInfo = info;
		this.setType(WorkflowActivityType.SUB_PROCESS.getValue());
	}

	public String getSubProcessName()
	{
		return subProcessName;
	}

	public void setSubProcessName(String subProcessName)
	{
		this.subProcessName = subProcessName;
	}

	@Override
	public WorkflowSubProcessActivityInfo getWorkflowActivityInfo()
	{
		return (WorkflowSubProcessActivityInfo) workflowActivityInfo;
	}

	@Override
	public void setWorkflowActivityInfo(WorkflowActivityInfo workflowSubProcessActivityInfo)
	{
		if (workflowSubProcessActivityInfo.getClass() != WorkflowSubProcessActivityInfo.class)
		{
			throw new UnsupportedOperationException("Invalid param on setWorkflowActivityInfo, use WorkflowSubProcessActivityInfo instead.");
		}
		this.workflowActivityInfo = workflowSubProcessActivityInfo;
	}
}

class SubProcess implements Cloneable, Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -5544863709508415202L;
	private String				process				= null;

	public SubProcess(String process)
	{
		this.process = process;
	}

	/**
	 * @param process
	 *            the process to set
	 */
	public void setProcess(String process)
	{
		this.process = process;
	}

	/**
	 * @return the process
	 */
	public String getProcess()
	{
		return this.process;
	}
}
