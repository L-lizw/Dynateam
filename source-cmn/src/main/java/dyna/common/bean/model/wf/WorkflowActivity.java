/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkflowActivity
 * Jiagang 2010-10-8
 */
package dyna.common.bean.model.wf;

import java.util.List;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dto.model.wf.WorkflowActivityInfo;
import dyna.common.dto.model.wf.WorkflowActrtLifecyclePhaseInfo;

/**
 * 工作流活动对象
 * 
 * @author Jiagang
 * 
 */
public class WorkflowActivity extends SystemObjectImpl implements SystemObject
{
	private static final long		serialVersionUID		= 1852157630126589315L;

	protected WorkflowActivityInfo	workflowActivityInfo	= null;

	@Override
	public String getGuid()
	{
		// TODO Auto-generated method stub
		return this.workflowActivityInfo.getGuid();
	}

	@Override
	public void setGuid(String guid)
	{
		// TODO Auto-generated method stub
		this.workflowActivityInfo.setGuid(guid);
	}

	@Override
	public String getName()
	{
		// TODO Auto-generated method stub
		return this.workflowActivityInfo.getName();
	}

	@Override
	public void setName(String name)
	{
		// TODO Auto-generated method stub
		this.workflowActivityInfo.setName(name);
	}

	public WorkflowActivity()
	{
		this.workflowActivityInfo = new WorkflowActivityInfo();
	}

	public WorkflowActivity(WorkflowActivityInfo workflowActivityInfo)
	{
		this.workflowActivityInfo = workflowActivityInfo;
	}

	public WorkflowActivityInfo getWorkflowActivityInfo()
	{
		return workflowActivityInfo;
	}

	public void setWorkflowActivityInfo(WorkflowActivityInfo workflowActivityInfo)
	{
		this.workflowActivityInfo = workflowActivityInfo;
	}

	/**
	 * @return the gate
	 */
	public int getGate()
	{
		return workflowActivityInfo.getGate();
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type)
	{
		if (this.workflowActivityInfo == null)
		{
			this.workflowActivityInfo = new WorkflowActivityInfo();
		}
		this.workflowActivityInfo.setType(type);
	}

	/**
	 * @return the type
	 */
	public String getType()
	{
		if (this.workflowActivityInfo == null)
		{
			return null;
		}
		return this.workflowActivityInfo.getType();
	}

}
