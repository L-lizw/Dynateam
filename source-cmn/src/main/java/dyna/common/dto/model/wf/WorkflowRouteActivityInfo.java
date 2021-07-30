/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkflowRouteActivity
 * Jiagang 2010-10-8
 */
package dyna.common.dto.model.wf;

import java.util.List;

import dyna.common.systemenum.WorkflowActivityType;
import dyna.common.systemenum.WorkflowRouteModeType;
import dyna.common.systemenum.WorkflowRouteType;

/**
 * 工作流路由选择活动
 * 
 * @author Jiagang
 * 
 */
public class WorkflowRouteActivityInfo extends WorkflowActivityInfo
{
	private static final long		serialVersionUID	= 2209642857543593011L;

	private WorkflowRouteModeType	routeMode			= null;

	private WorkflowRouteType		routeType			= null;

	private List<String>			transitionRefList	= null;

	private String					route				= "";

	public WorkflowRouteActivityInfo()
	{
		this.setType(WorkflowActivityType.ROUTE.getValue());

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

	@Override
	public WorkflowRouteActivityInfo clone()
	{
		// TODO Auto-generated method stub
		return (WorkflowRouteActivityInfo) super.clone();
	}

}
