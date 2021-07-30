/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: LocationTypeEnum
 * caogc 2010-12-08
 */
package dyna.common.systemenum;

/**
 * Location类型枚举
 * 
 * @author caogc
 * 
 */
public enum LocationTypeEnum
{
	PRIVATE("1"), LIBRARY("2"), PRIVATE_BOMVIEW("3"), LIBRARY_BOMVIEW("4"), PRIVATE_VIEW("5"), LIBRARY_VIEW("6");

	public static LocationTypeEnum typeValueOf(String type)
	{
		for (LocationTypeEnum locationTypeEnum : LocationTypeEnum.values())
		{
			if (type.equals(locationTypeEnum.toString()))
			{
				return locationTypeEnum;
			}
		}

		return null;
	}

	private String type = null;

	private LocationTypeEnum(String type)
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
