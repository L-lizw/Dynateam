/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: PRStatusEnum
 * caogc 2010-04-02
 */
package dyna.common.systemenum;

/**
 * PRStatusEnum 问题报告类涉及到的状态枚举
 * 
 * @author caogc
 * 
 */
public enum PRStatusEnum
{
	INIT("1"), // 未处理,或者空也表示未处理
	CHANGING("2"), // 更改中
	CLOSED("3");// 关闭

	public static PRStatusEnum typeValueOf(String type)
	{
		for (PRStatusEnum locationTypeEnum : PRStatusEnum.values())
		{
			if (type.equals(locationTypeEnum.toString()))
			{
				return locationTypeEnum;
			}
		}

		return null;
	}

	private String type = null;

	private PRStatusEnum(String type)
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

	public int toValue()
	{
		return Integer.parseInt(this.type);
	}
}
