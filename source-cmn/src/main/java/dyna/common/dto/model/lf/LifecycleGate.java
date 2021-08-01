/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: LifecycleGate
 * Jiagang 2010-9-28
 */
package dyna.common.dto.model.lf;

import dyna.common.annotation.Cache;
import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.model.lf.LifecycleGateMapper;
import dyna.common.systemenum.EventTypeEnum;
import dyna.common.systemenum.LifecycleGateType;

/**
 * @author Jiagang
 *
 */
@Cache
@EntryMapper(LifecycleGateMapper.class)
public class LifecycleGate extends SystemObjectImpl implements SystemObject
{
	private static final long	serialVersionUID	= 4309944830943953803L;

	public static final String	DETAILFK			= "DETAILFK";

	public static final String	TYPE				= "GATETYPE";

	public static final String	FROMPHASE			= "FROMPHASE";

	public static final String	CLASSNAME			= "CLASSNAME";

	public static final String	ACTIONNAME			= "ACTIONNAME";

	public static final String	WORKFLOWNAME		= "WORKFLOWNAME";

	public static final String	ACTIVITYNAME		= "ACTIVITYNAME";

	public String getDetailfk()
	{
		return (String) this.get(DETAILFK);
	}

	public void setDetailfk(String detailfk)
	{
		this.put(DETAILFK, detailfk);
	}

	public String getFromPhase()
	{
		return (String) this.get(FROMPHASE);
	}

	public void setFromPhase(String fromPhase)
	{
		this.put(FROMPHASE, fromPhase);
	}

	public String getClassName()
	{
		return (String) this.get(CLASSNAME);
	}

	public void setClassName(String className)
	{
		this.put(CLASSNAME, className);
	}

	public String getActionName()
	{
		return (String) this.get(CLASSNAME);
	}

	public void setActionName(String className)
	{
		this.put(CLASSNAME, className);
	}

	/**
	 * @return the workflowName
	 */
	public String getWorkflowName()
	{
		return (String) this.get(WORKFLOWNAME);
	}

	/**
	 * @param workflowName
	 *            the workflowName to set
	 */
	public void setWorkflowName(String workflowName)
	{
		this.put(WORKFLOWNAME, workflowName);
	}

	/**
	 * @return the activityName
	 */
	public String getActivityName()
	{
		return (String) this.get(ACTIVITYNAME);
	}

	/**
	 * @param activityName
	 *            the activityName to set
	 */
	public void setActivityName(String activityName)
	{
		this.put(ACTIVITYNAME, activityName);
	}

	public EventTypeEnum getEventType()
	{
		if (this.getType() == LifecycleGateType.EVENT)
		{
			return EventTypeEnum.typeValueOf(this.getActionName());
		}
		return null;
	}

	public LifecycleGateType getType()
	{
		return LifecycleGateType.valueOf((String) this.get(TYPE));
	}

	public void setType(LifecycleGateType type)
	{
		this.put(TYPE, type.toString());
	}
}
