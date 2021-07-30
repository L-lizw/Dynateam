/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: CalendarEnum
 * Administrator 2012-5-8
 */
package dyna.common.systemenum.ppms;

import java.util.Calendar;

/**
 * @author Duanll
 * 
 */
public enum DayOfWeekEnum
{
	SUNDAY(Calendar.SUNDAY, "ID_SYS_WEEK_SUNDAY"),

	MONDAY(Calendar.MONDAY, "ID_SYS_WEEK_MONDAY"),

	TUESDAY(Calendar.TUESDAY, "ID_SYS_WEEK_TUESDAY"),

	WEDNESDAY(Calendar.WEDNESDAY, "ID_SYS_WEEK_WEDNESDAY"),

	THURSDAY(Calendar.THURSDAY, "ID_SYS_WEEK_THURSDAY"),

	FRIDAY(Calendar.FRIDAY, "ID_SYS_WEEK_FRIDAY"),

	SATURDAY(Calendar.SATURDAY, "ID_SYS_WEEK_SATURDAY");

	private Integer	day		= null;
	private String	msrId	= null;

	private DayOfWeekEnum(Integer day, String msrId)
	{
		this.day = day;
		this.msrId = msrId;
	}

	public static DayOfWeekEnum typeValueOf(Integer type)
	{
		if (type != null)
		{
			for (DayOfWeekEnum dayOfWeekEnum : DayOfWeekEnum.values())
			{
				if (type.equals(dayOfWeekEnum.getDay()))
				{
					return dayOfWeekEnum;
				}
			}
		}

		return null;
	}

	public Integer getDay()
	{
		return this.day;
	}

	public String getMsrId()
	{
		return this.msrId;
	}
}
