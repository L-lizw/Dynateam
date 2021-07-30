/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkflowLockActivity
 * Jiagang 2010-10-9
 */
package dyna.common.bean.model.wf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import dyna.common.dto.model.wf.WorkflowActionActivityInfo;
import dyna.common.dto.model.wf.WorkflowActivityInfo;
import dyna.common.dto.model.wf.WorkflowActrtActionInfo;
import dyna.common.util.SetUtils;

/**
 * 工作流锁定活动
 * 
 * @author Jiagang
 * 
 */
public class WorkflowActionActivity extends WorkflowApplicationActivity
{

	/**
	 * 
	 */
	private static final long				serialVersionUID	= 132700179616549641L;

	private List<WorkflowActrtActionInfo>	actionList			= null;

	public WorkflowActionActivity()
	{
		this.workflowActivityInfo = new WorkflowActionActivityInfo();
	}

	public WorkflowActionActivity(WorkflowActionActivityInfo workflowActionActivityInfo)
	{
		this.workflowActivityInfo = workflowActionActivityInfo;
	}

	/**
	 * @return the actionList
	 */
	public List<WorkflowActrtActionInfo> getActionList()
	{
		if (this.actionList == null)
		{
			this.actionList = new ArrayList<WorkflowActrtActionInfo>();
		}
		return this.actionList;
	}

	/**
	 * @param actionList
	 *            the actionList to set
	 */
	public void setActionList(List<WorkflowActrtActionInfo> actionList)
	{
		this.actionList = actionList;
	}

	/**
	 * @param action
	 *            the action to add
	 */
	public void addAction(WorkflowActrtActionInfo action)
	{
		if (this.actionList == null)
		{
			this.actionList = new ArrayList<WorkflowActrtActionInfo>();
		}
		if (!this.actionList.contains(action) && this.getAction(action.getName()) == null)
		{
			this.actionList.add(action);
		}
	}

	public void removeAction(WorkflowActrtActionInfo action)
	{
		if (!SetUtils.isNullList(actionList))
		{
			Iterator<WorkflowActrtActionInfo> it = actionList.iterator();
			while (it.hasNext())
			{
				if (it.next().getGuid().equals(action.getGuid()))
				{
					it.remove();
					break;
				}
			}
		}
	}

	/**
	 * @param actionName
	 * @return action
	 */
	public WorkflowActrtActionInfo getAction()
	{
		if (!SetUtils.isNullList(this.actionList))
		{
			return this.actionList.get(0);
		}

		return null;
	}

	/**
	 * @param actionName
	 * @return action
	 */
	public WorkflowActrtActionInfo getAction(String actionName)
	{
		if (this.actionList == null)
		{
			return null;
		}

		for (WorkflowActrtActionInfo action : this.actionList)
		{
			if (actionName.equalsIgnoreCase(action.getName()))
			{
				return action;
			}
		}

		return null;
	}

	@Override
	public WorkflowActionActivityInfo getWorkflowActivityInfo()
	{
		return (WorkflowActionActivityInfo) workflowActivityInfo;
	}

	@Override
	public void setWorkflowActivityInfo(WorkflowActivityInfo workflowActionActivityInfo)
	{
		if (workflowActionActivityInfo.getClass() != WorkflowActionActivityInfo.class)
		{
			throw new UnsupportedOperationException("Invalid param on setWorkflowActivityInfo, use WorkflowActionActivityInfo instead.");
		}
		this.workflowActivityInfo = workflowActionActivityInfo;
	}
}
