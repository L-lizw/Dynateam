/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkflowRouteActivity
 * Jiagang 2010-10-8
 */
package dyna.common.bean.model.wf;

import java.util.List;

import dyna.common.dto.model.wf.WorkflowActivityInfo;
import dyna.common.dto.model.wf.WorkflowRouteActivityInfo;
import dyna.common.systemenum.WorkflowActivityType;
import dyna.common.systemenum.WorkflowRouteModeType;
import dyna.common.systemenum.WorkflowRouteType;

/**
 * 工作流路由选择活动
 * 
 * @author Jiagang
 * 
 */
public class WorkflowRouteActivity extends WorkflowActivity
{
	private static final long		serialVersionUID	= 2209642857543593011L;

	private WorkflowRouteModeType	routeMode			= null;

	private WorkflowRouteType		routeType			= null;

	private List<String>			transitionRefList	= null;

	private String					route				= "";

	private TransitionRestriction	transitionRestriction;
	

	public WorkflowRouteActivity()
	{
		this.workflowActivityInfo = new WorkflowRouteActivityInfo();
		this.setType(WorkflowActivityType.ROUTE.getValue());
	}

	public WorkflowRouteActivity(WorkflowRouteActivityInfo info)
	{
		this.workflowActivityInfo = info;
		this.setType(WorkflowActivityType.ROUTE.getValue());
	}

	@Override
	public WorkflowRouteActivityInfo getWorkflowActivityInfo()
	{
		return (WorkflowRouteActivityInfo) workflowActivityInfo;
	}

	@Override
	public void setWorkflowActivityInfo(WorkflowActivityInfo workflowRouteActivityInfo)
	{
		if (workflowRouteActivityInfo.getClass() != WorkflowRouteActivityInfo.class)
		{
			throw new UnsupportedOperationException("Invalid param on setWorkflowActivityInfo, use WorkflowRouteActivityInfo instead.");
		}
		this.workflowActivityInfo = workflowRouteActivityInfo;
	}

	/**
	 * @param routeMode
	 *            the routeMode to set
	 */
	public void setRouteMode(WorkflowRouteModeType routeMode)
	{
		this.routeMode = routeMode;
	}

	/**
	 * @return the routeMode
	 */
	public WorkflowRouteModeType getRouteMode()
	{
		return this.routeMode;
	}

	/**
	 * @param routeType
	 *            the routeType to set
	 */
	public void setRouteType(WorkflowRouteType routeType)
	{
		this.routeType = routeType;
	}

	/**
	 * @return the routeType
	 */
	public WorkflowRouteType getRouteType()
	{
		return this.routeType;
	}

	/**
	 * @param transitionRefList
	 *            the transitionRefList to set
	 */
	public void setTransitionRefList(List<String> transitionRefList)
	{
		this.transitionRefList = transitionRefList;
	}

	/**
	 * @return the transitionRefList
	 */
	public List<String> getTransitionRefList()
	{
		return this.transitionRefList;
	}

	/**
	 * @param route
	 *            the route to set
	 */
	public void setRoute(String route)
	{
		this.route = route;
	}

	/**
	 * @return the route
	 */
	public String getRoute()
	{
		return this.route;
	}

	/**
	 * @param transitionRestriction
	 *            the transitionRestriction to set
	 */
	public void setTransitionRestriction(TransitionRestriction transitionRestriction)
	{
		this.transitionRestriction = transitionRestriction;
	}

	/**
	 * @return the transitionRestriction
	 */
	public TransitionRestriction getTransitionRestriction()
	{
		return this.transitionRestriction;
	}

}
