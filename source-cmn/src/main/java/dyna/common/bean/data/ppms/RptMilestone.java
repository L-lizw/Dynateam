/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: TaskMember
 * WangLHB May 29, 2012
 */
package dyna.common.bean.data.ppms;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.ppm.RptMilestoneMapper;
import dyna.common.util.DateFormat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

/**
 * @author fanjq
 * 
 */
@EntryMapper(RptMilestoneMapper.class)
public class RptMilestone extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID		= 6250249738659482088L;

	public static final String	NAME					= "MD_NAME";
	public static final String	ONTIMESTATETITLE		= "ONTIMESTATETITLE";
	public static final String	EXECUTESTATUSNAME		= "EXECUTESTATUSNAME";
	public static final String	EXECUTESTATUSTITLE		= "EXECUTESTATUSTITLE";
	public static final String	IMPORTANCELEVELNAME		= "IMPORTANCELEVELNAME";
	public static final String	IMPORTANCELEVELTITLE	= "IMPORTANCELEVELTITLE";
	public static final String	PLANNEDDURATION			= "PLANNEDDURATION";
	public static final String	PRJCOMPLETIONRATE		= "PRJCOMPLETIONRATE";
	public static final String	CHECKPOINTNAME			= "CHECKPOINTNAME";
	public static final String	ACTUALFINISHTIME		= "ACTUALFINISHTIME";
	public static final String	PLANFINISHTIME			= "PLANFINISHTIME";
	public static final String	COMPLETIONRATE			= "COMPLETIONRATE";
	public static final String	TASKPLANSTARTTIME		= "TASKPLANSTARTTIME";
	public static final String	TASKPLANFINISHTIME		= "TASKPLANFINISHTIME";
	public static final String	TASKACTUALFINISHTIME	= "TASKACTUALFINISHTIME";
	public static final String	SPI						= "SPI";

	public String getCompletionRate()
	{
		if (super.get(COMPLETIONRATE) != null && super.get(COMPLETIONRATE) instanceof BigDecimal)
		{
			BigDecimal completionRate = (BigDecimal) super.get(COMPLETIONRATE);
			return String.valueOf(completionRate.setScale(2, RoundingMode.HALF_UP).doubleValue());
		}
		return (String) super.get(COMPLETIONRATE);
	}

	public void setCompletionRate(String completionRate)
	{
		super.put(COMPLETIONRATE, completionRate);
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

	public String getActualFinishTime()
	{
		if (super.get(ACTUALFINISHTIME) != null && super.get(ACTUALFINISHTIME) instanceof Date)
		{
			Date actualFinishTime = (Date) super.get(ACTUALFINISHTIME);
			return DateFormat.formatYMD(actualFinishTime);
		}
		return (String) super.get(ACTUALFINISHTIME);
	}

	public void setActualFinishTime(String planStartTime)
	{
		super.put(ACTUALFINISHTIME, planStartTime);
	}

	public String getCheckpointName()
	{
		return (String) super.get(CHECKPOINTNAME);
	}

	public void setCheckpointName(String checkpointName)
	{
		super.put(CHECKPOINTNAME, checkpointName);
	}

	public String getPrjCompletionRate()
	{
		if (super.get(PRJCOMPLETIONRATE) != null && super.get(PRJCOMPLETIONRATE) instanceof BigDecimal)
		{
			BigDecimal prjCompletionRate = (BigDecimal) super.get(PRJCOMPLETIONRATE);
			return String.valueOf(prjCompletionRate.setScale(2, RoundingMode.HALF_UP).doubleValue());
		}
		return (String) super.get(PRJCOMPLETIONRATE);
	}

	public void setPrjCompletionRate(String prjCompletionRate)
	{
		super.put(PRJCOMPLETIONRATE, prjCompletionRate);
	}

	public String getPlannedDuration()
	{
		if (super.get(PLANNEDDURATION) != null && super.get(PLANNEDDURATION) instanceof BigDecimal)
		{
			BigDecimal plannedDuration = (BigDecimal) super.get(PLANNEDDURATION);
			return String.valueOf(plannedDuration.setScale(2, RoundingMode.HALF_UP).doubleValue());
		}
		return (String) super.get(PLANNEDDURATION);
	}

	public void setPlannedDuration(String plannedDuration)
	{
		super.put(PLANNEDDURATION, plannedDuration);
	}

	public String getImportanceLevelTitle()
	{
		return (String) super.get(IMPORTANCELEVELTITLE);
	}

	public void setImportanceLevelTitle(String importanceLevelTitle)
	{
		super.put(IMPORTANCELEVELTITLE, importanceLevelTitle);
	}

	public String getExecuteStatusName()
	{
		return (String) super.get(EXECUTESTATUSNAME);
	}

	public void setExecuteStatusName(String executeStatusName)
	{
		super.put(EXECUTESTATUSNAME, executeStatusName);
	}

	public String getOnTimeStateTitle()
	{
		return (String) super.get(ONTIMESTATETITLE);
	}

	public void setOnTimeStateTitle(String onTimeStateTitle)
	{
		super.put(ONTIMESTATETITLE, onTimeStateTitle);
	}

	@Override
	public String getName()
	{
		return (String) super.get(NAME);
	}

	@Override
	public void setName(String name)
	{
		super.put(NAME, name);
	}

	public String getSPI()
	{
		if (super.get(SPI) != null && super.get(SPI) instanceof BigDecimal)
		{
			BigDecimal spi = (BigDecimal) super.get(SPI);
			return String.valueOf(spi.setScale(2, RoundingMode.HALF_UP).doubleValue());
		}
		return (String) super.get(SPI);
	}

	public void setSPI(String sPI)
	{
		super.put(SPI, sPI);
	}

	public String getExecuteStatusTitle()
	{
		return (String) super.get(EXECUTESTATUSTITLE);
	}

	public void setExecuteStatusTitle(String executeStatusTitle)
	{
		super.put(EXECUTESTATUSTITLE, executeStatusTitle);
	}

	public String getTaskplanstarttime()
	{
		if (super.get(TASKPLANSTARTTIME) != null && super.get(TASKPLANSTARTTIME) instanceof Date)
		{
			Date taskPlanStartTime = (Date) super.get(TASKPLANSTARTTIME);
			return DateFormat.formatYMD(taskPlanStartTime);
		}
		return (String) super.get(TASKPLANSTARTTIME);
	}

	public void setTaskplanstarttime(String taskplanstarttime)
	{
		super.put(TASKPLANSTARTTIME, taskplanstarttime);
	}

	public String getTaskactualfinishtime()
	{
		if (super.get(TASKACTUALFINISHTIME) != null && super.get(TASKACTUALFINISHTIME) instanceof Date)
		{
			Date taskActualFinishTime = (Date) super.get(TASKACTUALFINISHTIME);
			return DateFormat.formatYMD(taskActualFinishTime);
		}
		return (String) super.get(TASKACTUALFINISHTIME);
	}

	public String getTaskplanfinishtime()
	{
		if (super.get(TASKPLANFINISHTIME) != null && super.get(TASKPLANFINISHTIME) instanceof Date)
		{
			Date taskPlanFinishTime = (Date) super.get(TASKPLANFINISHTIME);
			return DateFormat.formatYMD(taskPlanFinishTime);
		}
		return (String) super.get(TASKPLANFINISHTIME);
	}

	public void setTaskplanfinishtime(String taskplanfinishtime)
	{
		super.put(TASKPLANFINISHTIME, taskplanfinishtime);
	}

	public void setTaskactualfinishtime(String taskactualfinishtime)
	{
		super.put(TASKACTUALFINISHTIME, taskactualfinishtime);
	}

	public RptMilestone()
	{
		super();
	}

}
