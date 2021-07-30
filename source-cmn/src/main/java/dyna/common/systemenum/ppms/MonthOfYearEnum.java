/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: FiscalYearEnum
 * Administrator 2012-5-8
 */
package dyna.common.systemenum.ppms;

import java.util.Calendar;

/**
 * @author Duanll
 * 
 */
public enum MonthOfYearEnum
{
	JANUARY(Calendar.JANUARY, "ID_SYS_MONTH_JANUARY"),

	FEBRUARY(Calendar.FEBRUARY, "ID_SYS_MONTH_FEBRUARY"),

	MARCH(Calendar.MARCH, "ID_SYS_MONTH_MARCH"),

	APRIL(Calendar.APRIL, "ID_SYS_MONTH_APRIL"),

	MAY(Calendar.MAY, "ID_SYS_MONTH_MAY"),

	JUNE(Calendar.JUNE, "ID_SYS_MONTH_JUNE"),

	JULY(Calendar.JULY, "ID_SYS_MONTH_JULY"),

	AUGUST(Calendar.AUGUST, "ID_SYS_MONTH_AUGUST"),

	SEPTEMBER(Calendar.SEPTEMBER, "ID_SYS_MONTH_SEPTEMBER"),

	OCTOBER(Calendar.OCTOBER, "ID_SYS_MONTH_OCTOBER"),

	NOVEMBER(Calendar.NOVEMBER, "ID_SYS_MONTH_NOVEMBER"),

	DECEMBER(Calendar.DECEMBER, "ID_SYS_MONTH_DECEMBER");

	private Integer	month	= null;
	private String	msrId	= null;

	private MonthOfYearEnum(Integer month, String msrId)
	{
		this.month = month;
		this.msrId = msrId;
	}

	public static MonthOfYearEnum typeValueOf(Integer type)
	{
		if (type != null)
		{
			for (MonthOfYearEnum fiscalYearEnum : MonthOfYearEnum.values())
			{
				if (type.equals(fiscalYearEnum.getMonth()))
				{
					return fiscalYearEnum;
				}
			}
		}

		return null;
	}

	public Integer getMonth()
	{
		return this.month;
	}

	public String getMsrId()
	{
		return this.msrId;
	}
}
