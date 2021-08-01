/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkflowTransition
 * Jiagang 2010-10-13
 */
package dyna.common.dto.model.wf;

import dyna.common.annotation.Cache;
import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.model.wf.WorkflowTransitionInfoMapper;
import dyna.common.systemenum.WorkflowTransitionConditionType;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 工作流程活动变迁
 * 
 * @author Jiagang
 * 
 */
@Cache
@EntryMapper(WorkflowTransitionInfoMapper.class)
public class WorkflowTransitionInfo extends SystemObjectImpl implements SystemObject
{
	private static final long	serialVersionUID	= 4899567710245121300L;

	public static final String	WFFK				= "WFFK";

	public static final String	TRANSNAME			= "TRANSNAME";

	public static final String	DESCRIPTION			= "DESCRIPTION";

	public static final String	ACTFROMGUID			= "ACTFROMGUID";

	public static final String	ACTTOGUID			= "ACTTOGUID";

	public static final String	TRANSTYPE			= "TRANSTYPE";

	public static final String	SEQUENCE			= "DATASEQ";

	private String				position			= null;

	public String				actrtFromName		= null;

	public String				actrtToName			= null;

	public String getWorkflowGuid()
	{
		return (String) this.get(WFFK);
	}

	public void setWorkflowGuid(String workflowGuid)
	{
		super.put(WFFK, workflowGuid);
	}

	public String getActrtFromName()
	{
		return actrtFromName;
	}

	public void setActrtFromName(String actrtFromName)
	{
		this.actrtFromName = actrtFromName;
	}

	public String getActrtToName()
	{
		return actrtToName;
	}

	public void setActrtToName(String actrtToName)
	{
		this.actrtToName = actrtToName;
	}

	public String getPosition()
	{
		return position;
	}

	public void setPosition(String position)
	{
		this.position = position;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	@Override
	public void setName(String name)
	{
		this.put(TRANSNAME, name);
	}

	/**
	 * @return the name
	 */
	@Override
	public String getName()
	{
		return (String) this.get(TRANSNAME);
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description)
	{
		this.put(DESCRIPTION, description);
	}

	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return (String) this.get(DESCRIPTION);
	}

	public String getActFromGuid()
	{
		return (String) this.get(ACTFROMGUID);
	}

	public void setActFromGuid(String actFromGuid)
	{
		this.put(ACTFROMGUID, actFromGuid);
	}

	public String getActToGuid()
	{
		return (String) this.get(ACTTOGUID);
	}

	public void setActToGuid(String actToGuid)
	{
		this.put(ACTTOGUID, actToGuid);
	}

	public WorkflowTransitionConditionType getTransType()
	{
		return this.get(TRANSTYPE) == null ? null : WorkflowTransitionConditionType.valueOf(((String) this.get(TRANSTYPE)).toUpperCase());
	}

	public void setTransType(WorkflowTransitionConditionType workflowTransitionConditionType)
	{
		this.put(TRANSTYPE, workflowTransitionConditionType.name());
	}

	public void setSequence(int sequence)
	{
		this.put(SEQUENCE, new BigDecimal(String.valueOf(sequence)));
	}

	public int getSequence()
	{
		return this.get(SEQUENCE) == null ? 0 : ((Number) this.get(SEQUENCE)).intValue();
	}
}

class TransitionType implements Cloneable, Serializable
{
	/**
		 * 
		 */
	private static final long	serialVersionUID	= -4021088791484357527L;
	private String				name				= null;

	public TransitionType(String conditionType)
	{
		this.name = conditionType;
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
