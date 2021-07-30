/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: StatisticResultEnum 统计结果类型枚举
 * caogc 2010-12-08
 */
package dyna.common.systemenum;

/**
 * StatisticResultEnum 统计结果类型枚举
 * 
 * @author caogc
 * 
 */
public enum StatisticResultEnum
{
	INTERFACE("1"), BUSINESSOBJECT("2"), CLASSIFICATION("3"), STATUS("4"), GROUP("5"), FOLDER("6");

	public static StatisticResultEnum typeValueOf(String type)
	{
		for (StatisticResultEnum locationTypeEnum : StatisticResultEnum.values())
		{
			if (type.equals(locationTypeEnum.toString()))
			{
				return locationTypeEnum;
			}
		}

		return null;
	}

	private String	type	= null;

	private StatisticResultEnum(String type)
	{
		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString()
	{
		return this.type;
	}

}
