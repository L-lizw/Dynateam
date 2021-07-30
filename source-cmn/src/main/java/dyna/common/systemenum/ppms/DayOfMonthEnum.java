/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 月中的所有日期
 * duanll 2012-5-9
 */
package dyna.common.systemenum.ppms;

/**
 * @author duanll
 * 
 */
public enum DayOfMonthEnum
{
	FIRST(1),

	SECOND(2),

	THIRD(3),

	FOURTH(4),

	FIFTH(5),

	SIXTH(6),

	SEVENTH(7),

	EITHTH(8),

	NINTH(9),

	TENTH(10),

	ELEVENTH(11),

	TWELFTH(12),

	THIRTEENTH(13),

	FOURTEENTH(14),

	FIFTEENTH(15),

	SIXTEENTH(16),

	SEVENTEENTH(17),

	EIGHTEENTH(18),

	NINETEENTH(19),

	TWENTIETH(20),

	TWENTYFIRST(21),

	TWENTYSECOND(22),

	TWENTYTHIRD(23),

	TWENTYFOURTH(24),

	TWENTYFIFTH(25),

	TWENTYSIXTH(26),

	TWENTYSEVENTH(27),

	TWENTYEIGHTH(28),

	TWENTYNINTH(29),

	THIRTIETH(30),

	THIRTYFIRST(31), ;

	private Integer	day;

	private DayOfMonthEnum(Integer day)
	{
		this.day = day;
	}

	public static DayOfMonthEnum typeValueOf(Integer type)
	{
		if (type != null)
		{
			for (DayOfMonthEnum dayOfMonthEnum : DayOfMonthEnum.values())
			{
				if (type.equals(dayOfMonthEnum.getDay()))
				{
					return dayOfMonthEnum;
				}
			}
		}

		return null;
	}

	public Integer getDay()
	{
		return this.day;
	}
}
