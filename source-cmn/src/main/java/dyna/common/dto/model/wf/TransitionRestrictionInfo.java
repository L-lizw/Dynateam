/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: TransitionRestriction
 * WangLHB Jul 25, 2011
 */
package dyna.common.dto.model.wf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Element;

import dyna.common.systemenum.WorkflowRouteModeType;
import dyna.common.systemenum.WorkflowRouteType;

/**
 * @author WangLHB
 * 
 */
public class TransitionRestrictionInfo implements Cloneable, Serializable
{
	@Element(name = "split", required = false)
	TransitionSplit				split				= null;

	@Element(name = "join", required = false)
	TransitionSplit				join				= null;

	/**
		 * 
		 */
	private static final long	serialVersionUID	= -7343049675896513935L;

	public TransitionRestrictionInfo(WorkflowRouteType routeType, WorkflowRouteModeType routeModel, List<String> transitionRefList)
	{
		if (routeType == WorkflowRouteType.SPLIT)
		{
			this.split = new TransitionSplit(routeModel, transitionRefList);
		}
		else if (routeType == WorkflowRouteType.JOIN)
		{
			this.join = new TransitionSplit(routeModel, transitionRefList);
		}
	}

}

class TransitionSplit implements Cloneable, Serializable
{
	/**
	 * 
	 */
	private static final long			serialVersionUID	= 5429599991838469599L;

	private WorkflowRouteModeType		routeModel			= null;

	private List<TransitionAttribute>	transitionRefList	= null;

	public TransitionSplit(WorkflowRouteModeType routeModel, List<String> transitionRefList)
	{
		this.routeModel = routeModel;
		if (transitionRefList != null)
		{
			this.transitionRefList = new ArrayList<TransitionAttribute>();
			for (String string : transitionRefList)
			{
				this.transitionRefList.add(new TransitionAttribute(string));
			}
		}
	}

	/**
	 * @return the routeType
	 */
	public WorkflowRouteModeType getRouteModel()
	{
		return this.routeModel;
	}

}

class TransitionAttribute implements Cloneable, Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -4021088791484357527L;
	private String				name				= null;

	public TransitionAttribute(String name)
	{
		this.name = name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return this.name;
	}

}
