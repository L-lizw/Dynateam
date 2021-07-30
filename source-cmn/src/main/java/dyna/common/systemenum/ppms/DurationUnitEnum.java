/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ProjectStatusEnum
 * WangLHB Apr 27, 2011
 */
package dyna.common.systemenum.ppms;

/**
 * 周期单位枚举
 * 
 * @author WangLHB
 * 
 */
public enum DurationUnitEnum
{
	// 工时、工作日、工作周、天
	WORKHOURS("WrokingHour"), // 工时
	WORKDAYS("WorkingDay"), // 工作日
	WORKWEEK("WorkingWeek"), // 工作周
	DAY("Day"); // 天

	private String	value	= null;

	DurationUnitEnum(String value)
	{
		this.value = value;
	}

	public String getValue()
	{
		return this.value;
	}

	public static DurationUnitEnum getStatusEnum(Object status)
	{
		for (DurationUnitEnum type : DurationUnitEnum.values())
		{
			if (type.value.equals(status))
			{
				return type;
			}
		}
		return null;
	}
}
