/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 重复发生方式  每天、每周、每月、每年
 * Administrator 2012-5-9
 */
package dyna.common.systemenum.ppms;

/**
 * @author Duanll
 * 
 */
public enum RepeatModeEnum
{
	EVERYDAY("ID_PM_REPEAT_MODE_EVERYDAY", 1),

	EVERYWEEK("ID_PM_REPEAT_MODE_EVERYWEEK", 2),

	EVERYMONTH("ID_PM_REPEAT_MODE_EVERYMONTH", 3),

	EVERYYEAR("ID_PM_REPEAT_MODE_EVERYYEAR", 4);

	private String	msrId;

	private Integer	mode;

	private RepeatModeEnum(String msrId, Integer mode)
	{
		this.msrId = msrId;
		this.mode = mode;
	}

	public static RepeatModeEnum typeValueOf(Integer mode)
	{
		if (mode != null)
		{
			for (RepeatModeEnum repeatMode : RepeatModeEnum.values())
			{
				if (mode.equals(repeatMode.getMode()))
				{
					return repeatMode;
				}
			}
		}
		return null;
	}

	/**
	 * 重复模式：每天，每周，每月，每年
	 * 
	 * @return
	 */
	public String getMsrId()
	{
		return this.msrId;
	}

	/**
	 * 重复模式值：1，2，3，4
	 * 
	 * @return
	 */
	public Integer getMode()
	{
		return this.mode;
	}
}
