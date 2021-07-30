/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SystemWorkflowActivity
 * WangLHB Jan 17, 2012
 */
package dyna.common.dto;

import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.systemenum.WorkflowActivityType;

/**
 * @author WangLHB
 * 
 */
public class SystemWorkflowActivity extends SystemObjectImpl
{

	/**
	 * 
	 */
	private static final long		serialVersionUID	= 1L;

	private String					description			= null;

	private WorkflowActivityType	activityType		= null;

	private int						gate				= 0;

	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return this.description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * @return the activityType
	 */
	public WorkflowActivityType getActivityType()
	{
		return this.activityType;
	}

	/**
	 * @param activityType
	 *            the activityType to set
	 */
	public void setActivityType(WorkflowActivityType activityType)
	{
		this.activityType = activityType;
	}

	/**
	 * @return the gate
	 */
	public int getGate()
	{
		return this.gate;
	}

	/**
	 * @param gate
	 *            the gate to set
	 */
	public void setGate(int gate)
	{
		this.gate = gate;
	}

}
