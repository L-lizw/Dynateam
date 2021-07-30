/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ReplaceRangeEnum
 * wangweixia 2012-7-12
 */
package dyna.common.systemenum;

import dyna.common.util.StringUtils;

/**
 * 取替代范围枚举
 * 
 * @author wangweixia
 * 
 */
public enum ReplaceRangeEnum
{
	PART("Partial", "ID_SYS_REPLACERANGEENUM_PART"), // 局部
	GLOBAL("Global", "ID_SYS_REPLACERANGEENUM_GLOBAL"), // 全局
	PARTANDGLOBAL("", "ID_SYS_REPLACERANGEENUM_PART_AND_GLOBAL");// 局部&&全局
	private final String	value;
	private final String	msrId;

	private ReplaceRangeEnum(String value, String msrId)
	{
		this.value = value;
		this.msrId = msrId;
	}

	public String getMsrId()
	{
		return this.msrId;
	}

	public String getValue()
	{
		return this.value;
	}

	public static ReplaceRangeEnum typeValueOf(String value)
	{
		if (StringUtils.isNullString(value))
		{
			return null;
		}
		for (ReplaceRangeEnum range : ReplaceRangeEnum.values())
		{
			if (value.equals(range.getValue()))
			{
				return range;
			}
		}
		return null;
	}
}
