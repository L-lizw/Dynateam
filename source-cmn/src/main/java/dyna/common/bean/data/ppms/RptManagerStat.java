/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: TaskMember
 * WangLHB May 29, 2012
 */
package dyna.common.bean.data.ppms;

import java.math.BigDecimal;
import java.math.RoundingMode;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;

/**
 * @author fanjq
 * 
 */
public class RptManagerStat extends SystemObjectImpl implements SystemObject
{
	private static final long	serialVersionUID	= 4430230225486868103L;

	public static final String	MANAGERGUID			= "MANAGERGUID";
	public static final String	MANAGER				= "MANAGER";
	public static final String	SUMOFPROJECT		= "SUMOFPROJECT";
	public static final String	NUMOFFINISH			= "NUMOFFINISH";

	public String getManagerGuid()
	{
		return (String) super.get(MANAGERGUID);
	}

	public void setManagerGuid(String managerGuid)
	{
		super.put(MANAGERGUID, managerGuid);
	}

	public String getManager()
	{
		return (String) super.get(MANAGER);
	}

	public void setManager(String manager)
	{
		super.put(MANAGER, manager);
	}

	public String getSumOfProject()
	{
		if (super.get(SUMOFPROJECT) != null && super.get(SUMOFPROJECT) instanceof BigDecimal)
		{
			BigDecimal sumOfProject = (BigDecimal) super.get(SUMOFPROJECT);
			return String.valueOf(sumOfProject.setScale(0).intValue());
		}
		return (String) super.get(SUMOFPROJECT);
	}

	public void setSumOfProject(String sumOfProject)
	{
		super.put(SUMOFPROJECT, sumOfProject);
	}

	public String getNumOfFinish()
	{
		if (super.get(NUMOFFINISH) != null && super.get(NUMOFFINISH) instanceof BigDecimal)
		{
			BigDecimal numOfFinish = (BigDecimal) super.get(NUMOFFINISH);
			return String.valueOf(numOfFinish.setScale(0).intValue());
		}
		return (String) super.get(NUMOFFINISH);
	}

	public void setNumOfFinish(String numOfFinish)
	{
		super.put(NUMOFFINISH, numOfFinish);
	}

	public RptManagerStat()
	{
		super();
	}

}
