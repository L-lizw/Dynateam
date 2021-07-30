/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkflowSubProcessActivity
 * Jiagang 2010-10-9
 */
package dyna.common.dto.model.wf;

import java.io.Serializable;

import dyna.common.systemenum.WorkflowActivityType;

/**
 * 工作流子流程活动
 * 
 * @author Jiagang
 * 
 */
public class WorkflowSubProcessActivityInfo extends WorkflowActivityInfo
{
	private static final long serialVersionUID = 5622678257248696480L;

	public WorkflowSubProcessActivityInfo()
	{
		this.setType(WorkflowActivityType.SUB_PROCESS.getValue());
	}

	/**
	 * 子流程名称
	 */
	private String subProcessName = null;

	public String getSubProcessName()
	{
		return subProcessName;
	}

	public void setSubProcessName(String subProcessName)
	{
		this.subProcessName = subProcessName;
	}

	@Override
	public WorkflowSubProcessActivityInfo clone()
	{
		// TODO Auto-generated method stub
		return (WorkflowSubProcessActivityInfo) super.clone();
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
