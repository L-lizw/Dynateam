/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: TaskMember
 * WangLHB May 29, 2012
 */
package dyna.common.bean.data.ppms;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.util.DateFormat;

/**
 * @author fanjq
 * 
 */
public class RptWorkItem extends SystemObjectImpl implements SystemObject
{
	private static final long	serialVersionUID	= -2379774949869019380L;

	public static final String	GUID				= "GUID";
	public static final String	CLASSGUID			= "CLASSGUID";
	public static final String	ID					= "MD_ID";
	public static final String	NAME				= "MD_NAME";
	public static final String	SPI					= "SPI";
	public static final String	PRJNAME				= "PRJNAME";
	public static final String	EXECUTORNAME		= "EXECUTORNAME";
	public static final String	OWNERGROUPNAME		= "OWNERGROUPNAME";
	public static final String	EXECUTESTATUSNAME	= "EXECUTESTATUSNAME";
	public static final String	ONTIMESTATENAME		= "ONTIMESTATENAME";
	public static final String	COMPLETIONRATE		= "COMPLETIONRATE";
	public static final String	PLANSTARTTIME		= "PLANSTARTTIME";
	public static final String	PLANFINISHTIME		= "PLANFINISHTIME";
	public static final String	ACTUALSTARTTIME		= "ACTUALSTARTTIME";
	public static final String	ACTUALFINISHTIME	= "ACTUALFINISHTIME";
	public static final String	SUMOFRECORDS		= "SUMOFRECORDS";

	public String getGuid()
	{
		return (String) super.get(GUID);
	}

	public void setGuid(String guid)
	{
		super.put(GUID, guid);
	}

	public String getClassGuid()
	{
		return (String) super.get(CLASSGUID);
	}

	public void setClassGuid(String classGuid)
	{
		super.put(CLASSGUID, classGuid);
	}

	public String getSpi()
	{
		if (super.get(SPI) != null && super.get(SPI) instanceof BigDecimal)
		{
			BigDecimal spi = (BigDecimal) super.get(SPI);
			return String.valueOf(spi.setScale(2, RoundingMode.HALF_UP).doubleValue());
		}
		return (String) super.get(SPI);
	}

	public void setSpi(String spi)
	{
		super.put(SPI, spi);
	}

	public String getSumOfRecords()
	{
		if (super.get(SUMOFRECORDS) != null && super.get(SUMOFRECORDS) instanceof BigDecimal)
		{
			BigDecimal sumOfRecords = (BigDecimal) super.get(SUMOFRECORDS);
			return String.valueOf(sumOfRecords.setScale(0).intValue());
		}
		return (String) super.get(SUMOFRECORDS);
	}

	public void setSumOfRecords(String sumOfRecords)
	{
		super.put(SUMOFRECORDS, sumOfRecords);
	}

	public String getActualFinishTime()
	{
		if (super.get(ACTUALFINISHTIME) != null && super.get(ACTUALFINISHTIME) instanceof Date)
		{
			Date actualFinishTime = (Date) super.get(ACTUALFINISHTIME);
			return DateFormat.formatYMD(actualFinishTime);
		}
		return (String) super.get(ACTUALFINISHTIME);
	}

	public void setActualFinishTime(String actualFinishTime)
	{
		super.put(ACTUALFINISHTIME, actualFinishTime);
	}

	public String getActualStartTime()
	{
		if (super.get(ACTUALSTARTTIME) != null && super.get(ACTUALSTARTTIME) instanceof Date)
		{
			Date actualStartTime = (Date) super.get(ACTUALSTARTTIME);
			return DateFormat.formatYMD(actualStartTime);
		}
		return (String) super.get(ACTUALSTARTTIME);
	}

	public void setActualStartTime(String actualStartTime)
	{
		super.put(ACTUALSTARTTIME, actualStartTime);
	}

	public String getPlanFinishTime()
	{
		if (super.get(PLANFINISHTIME) != null && super.get(PLANFINISHTIME) instanceof Date)
		{
			Date planFinishTime = (Date) super.get(PLANFINISHTIME);
			return DateFormat.formatYMD(planFinishTime);
		}
		return (String) super.get(PLANFINISHTIME);
	}

	public void setPlanFinishTime(String planFinishTime)
	{
		super.put(PLANFINISHTIME, planFinishTime);
	}

	public String getPlanStartTime()
	{
		if (super.get(PLANSTARTTIME) != null && super.get(PLANSTARTTIME) instanceof Date)
		{
			Date planStartTime = (Date) super.get(PLANSTARTTIME);
			return DateFormat.formatYMD(planStartTime);
		}
		return (String) super.get(PLANSTARTTIME);
	}

	public void setPlanStartTime(String planStartTime)
	{
		super.put(PLANSTARTTIME, planStartTime);
	}

	public String getOnTimeStateName()
	{
		return (String) super.get(ONTIMESTATENAME);
	}

	public void setOnTimeStateName(String onTimeStateName)
	{
		super.put(ONTIMESTATENAME, onTimeStateName);
	}

	public String getExecuteStatusName()
	{
		return (String) super.get(EXECUTESTATUSNAME);
	}

	public void setExecuteStatusName(String executeStatusName)
	{
		super.put(EXECUTESTATUSNAME, executeStatusName);
	}

	public String getExecutorName()
	{
		return (String) super.get(EXECUTORNAME);
	}

	public void setExecutorName(String executorName)
	{
		super.put(EXECUTORNAME, executorName);
	}

	public String getPrjName()
	{
		return (String) super.get(PRJNAME);
	}

	public void setPrjName(String prjName)
	{
		super.put(PRJNAME, prjName);
	}

	public String getId()
	{
		return (String) super.get(ID);
	}

	public void setId(String id)
	{
		super.put(ID, id);
	}

	public String getName()
	{
		return (String) super.get(NAME);
	}

	public void setName(String name)
	{
		super.put(NAME, name);
	}

	public String getOwnerGroupName()
	{
		return (String) super.get(OWNERGROUPNAME);
	}

	public void setOwnerGroupName(String ownerGroupName)
	{
		super.put(OWNERGROUPNAME, ownerGroupName);
	}

	public String getCompletionRate()
	{
		if (super.get(COMPLETIONRATE) != null && super.get(COMPLETIONRATE) instanceof BigDecimal)
		{
			BigDecimal completionrate = (BigDecimal) super.get(COMPLETIONRATE);
			return String.valueOf(completionrate.setScale(2, RoundingMode.HALF_UP).doubleValue());
		}
		return (String) super.get(COMPLETIONRATE);
	}

	public void setCompletionRate(String completionRate)
	{
		super.put(COMPLETIONRATE, completionRate);
	}

	public RptWorkItem()
	{
		super();
	}

}
