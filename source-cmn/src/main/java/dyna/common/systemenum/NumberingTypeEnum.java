/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Numbering类型枚举
 * Jiagang 2010-7-13
 */
package dyna.common.systemenum;

/**
 * Numbering类型枚举
 *
 * @author Jiagang
 *
 */
public enum NumberingTypeEnum
{
	STRING("String"), FIELD("Field"), YEAR("Year"), MONTH("Month"), DAY("Day"), SEQUENCE("Serial Number");

	private final String	type;

	@Override
	public String toString()
	{
		return this.type;
	}

	private NumberingTypeEnum(String type)
	{
		this.type = type;
	}

	public static NumberingTypeEnum typeValueOf(String type)
	{
		for (NumberingTypeEnum numberingTypeEnum : NumberingTypeEnum.values())
		{
			if (type.equalsIgnoreCase(numberingTypeEnum.toString()))
			{
				return numberingTypeEnum;
			}
		}
		return valueOf(type.toUpperCase());
	}
}
