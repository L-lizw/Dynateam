/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkflowActivity
 * Jiagang 2010-10-8
 */
package dyna.common.dto.model.wf;

import java.math.BigDecimal;

import dyna.common.annotation.Cache;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.bean.model.Implementation;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.systemenum.ProcessingModeEnum;
import dyna.common.util.StringUtils;

/**
 * 工作流活动对象
 * 
 * @author Jiagang
 * 
 */
@Cache
public class WorkflowActivityInfo extends SystemObjectImpl implements SystemObject
{
	private static final long	serialVersionUID	= 1852157630126589315L;

	public static final String	MAWFFK				= "MAWFFK";

	public static final String	ACTNAME				= "ACTNAME";

	public static final String	DESCRIPTION			= "DESCRIPTION";

	public static final String	TITLE				= "TITLE";

	public static final String	POSITION			= "POSITION";

	public static final String	GATE				= "GATE";

	public static final String	TYPE				= "ACTIVITYTYPE";

	public static final String	STARTMIN			= "STARTMIN";

	public static final String	STARTMAX			= "STARTMAX";

	public static final String	SUBWFNAME			= "SUBWFNAME";

	public static final String	SUBTYPE				= "SUBTYPE";

	public static final String	ROUTEMODEL			= "ROUTEMODEL";

	public static final String	SEQUENCE			= "DATASEQ";

	public static final String	PROCESSMODE			= "PROCESSMODE";

	private Implementation		implementation		= null;

	public String getWorkflowGuid()
	{
		return (String) this.get(MAWFFK);
	}

	public void setWorkflowGuid(String workflowguid)
	{
		this.put(MAWFFK, workflowguid);
	}

	/**
	 * @param name
	 *            the name to set
	 */
	@Override
	public void setName(String name)
	{
		this.put(ACTNAME, name);
	}

	/**
	 * @return the name
	 */
	@Override
	public String getName()
	{
		return (String) this.get(ACTNAME);
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title)
	{
		this.put(TITLE, title);
	}

	/**
	 * @return the title
	 */
	public String getTitle()
	{
		return (String) this.get(TITLE);
	}

	public String getTitle(LanguageEnum lang)
	{
		return StringUtils.getMsrTitle(this.getTitle(), lang.getType());
	}

	public int getSequence()
	{
		return this.get(SEQUENCE) == null ? 0 : ((Number) (this.get(SEQUENCE))).intValue();
	}

	public void setSequence(int sequence)
	{
		this.put(SEQUENCE, new BigDecimal(String.valueOf(sequence)));
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type)
	{
		this.put(TYPE, type);
	}

	/**
	 * @return the type
	 */
	public String getType()
	{
		return (String) this.get(TYPE);
	}

	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return (String) this.get(DESCRIPTION);
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
	 * @param gate
	 *            the gate to set
	 */
	public void setGate(int gate)
	{
		this.put(GATE, new BigDecimal(String.valueOf(gate)));
	}

	/**
	 * @return the gate
	 */
	public int getGate()
	{
		if (this.get(GATE) instanceof String)
		{
			return this.get(GATE) == null ? -1 : Integer.parseInt((String) this.get(GATE));
		}
		return this.get(GATE) == null ? -1 : ((Number) (this.get(GATE))).intValue();
	}

	/**
	 * @param position
	 *            the position to set
	 */
	public void setPosition(String position)
	{
		this.put(POSITION, position);
	}

	/**
	 * @return the position
	 */
	public String getPosition()
	{
		return (String) this.get(POSITION);
	}

	/**
	 * @param processMode
	 *            the processMode to set
	 */
	public void setProcessMode(ProcessingModeEnum processMode)
	{
		this.put(PROCESSMODE, processMode == null ? null : processMode.name());
	}

	/**
	 * @return the processMode
	 */
	public ProcessingModeEnum getProcessMode()
	{
		return this.get(PROCESSMODE) == null ? null : ProcessingModeEnum.valueOf((String) this.get(PROCESSMODE));
	}

	/**
	 * @param subType
	 *            the subType to set
	 */
	public void setSubType(String subType)
	{
		this.put(SUBTYPE, subType);
	}

	/**
	 * @return the subType
	 */
	public String getSubType()
	{
		return (String) this.get(SUBTYPE);
	}

	/**
	 * @param routeModel
	 *            the routeModel to set
	 */
	public void setRouteModel(String routeModel)
	{
		this.put(ROUTEMODEL, routeModel);
	}

	/**
	 * @return the routeModel
	 */
	public String getRouteModel()
	{
		return (String) this.get(ROUTEMODEL);
	}

	/**
	 * @param startMax
	 *            the startMax to set
	 */
	public void setStartMax(int startMax)
	{
		this.put(STARTMAX, new BigDecimal(String.valueOf(startMax)));
	}

	/**
	 * @return the startMax
	 */
	public int getStartMax()
	{
		return this.get(STARTMAX) == null ? 0 : new BigDecimal((String) this.get(STARTMAX)).intValue();
	}

	/**
	 * @param startMin
	 *            the startMin to set
	 */
	public void setStartMin(int startMin)
	{
		this.put(STARTMIN, new BigDecimal(String.valueOf(startMin)));
	}

	/**
	 * @return the startMin
	 */
	public int getStartMin()
	{
		return this.get(STARTMIN) == null ? 0 : new BigDecimal((String) this.get(STARTMIN)).intValue();
	}

	/**
	 * @param subWFName
	 *            the subWFName to set
	 */
	public void setSubWFName(String subWFName)
	{
		this.put(SUBWFNAME, subWFName);
	}

	/**
	 * @return the subWFName
	 */
	public String getSubWFName()
	{
		return (String) this.get(SUBWFNAME);
	}

	/**
	 * @param implementation
	 *            the implementation to set
	 */
	public void setImplementation(Implementation implementation)
	{
		this.implementation = implementation;
	}

	/**
	 * @return the implementation
	 */
	public Implementation getImplementation()
	{
		return this.implementation;
	}

	@Override
	public WorkflowActivityInfo clone()
	{
		// TODO Auto-generated method stub
		return (WorkflowActivityInfo) super.clone();
	}

}
