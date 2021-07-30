/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 日期范围的枚举
 * Majm 2011-6-8
 */
package dyna.common.systemenum;

/**
 * 日期范围的枚举
 * 
 * @author Majm
 * 
 */
public enum DateRangeEnum
{
	/**
	 * 今天
	 */
	TODAY("ID_SYS_DATERANGE_TODAY"),
	/**
	 * 本周
	 */
	THISWEEK("ID_SYS_DATERANGE_THISWEEK"),
	/**
	 * 本月
	 */
	THISMONTH("ID_SYS_DATERANGE_THISMONTH"),
	/**
	 * 昨天
	 */
	YESTERDAY("ID_SYS_DATERANGE_YESTERDAY"),
	/**
	 * 上周
	 */
	LASTWEEK("ID_SYS_DATERANGE_LASTWEEK"),
	/**
	 * 上月
	 */
	LASTMONTH("ID_SYS_DATERANGE_LASTMONTH"),
	/**
	 * 明天
	 */
	TOMORROW("ID_SYS_DATERANGE_TOMORROW"),
	/**
	 * 下周
	 */
	NEXTWEEK("ID_SYS_DATERANGE_NEXTWEEK"),
	/**
	 * 下月
	 */
	NEXTMONTH("ID_SYS_DATERANGE_NEXTMONTH");

	private String	msrId	= null;

	private DateRangeEnum(String msrId)
	{
		this.msrId = msrId;
	}

	public String getMsrId()
	{
		return msrId;
	}
}
