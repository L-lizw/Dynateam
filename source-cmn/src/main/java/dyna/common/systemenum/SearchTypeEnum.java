/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ChangePhaseActivetyType
 * WangLHB 2011-4-20
 */
package dyna.common.systemenum;

/**
 * 状态变换类型枚举
 * 
 * @author WangLHB
 * 
 */
public enum SearchTypeEnum
{
	AUTO("1"), USER("2"), PUBLIC("3");

	private final String	value;

	private SearchTypeEnum(String value)
	{
		this.value = value;
	}

	public String getValue()
	{
		return this.value;
	}

	public static SearchTypeEnum getEnum(String value)
	{
		for (SearchTypeEnum type : SearchTypeEnum.values())
		{
			if (type.getValue().equalsIgnoreCase(value))
			{
				return type;
			}
		}
		return null;
	}
}
