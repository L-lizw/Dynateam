/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ReplaceTypeEnum
 * wangweixia 2012-7-12
 */
package dyna.common.systemenum;

import dyna.common.util.StringUtils;

/**
 * 取替代类型
 * 
 * @author wangweixia
 * 
 */
public enum ReplaceTypeEnum
{
	QUDAI("Replace", "ID_SYS_REPLACETYPEENUM_QUDAI"), // 取代
	TIDAI("Substitute", "ID_SYS_REPLACETYPEENUM_TIDAI"), // 替代
	QUDAIANDTIDAI("", "ID_SYS_REPLACETYPEENUM_QUDAI_AND_TIDAI");// 取代&&替代
	private final String	msrId;
	private final String	value;

	private ReplaceTypeEnum(String value, String msrId)
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

	public static ReplaceTypeEnum typeValueOf(String value)
	{
		if (StringUtils.isNullString(value))
		{
			return null;
		}
		for (ReplaceTypeEnum ty : ReplaceTypeEnum.values())
		{
			if (value.equals(ty.getValue()))
			{
				return ty;
			}
		}
		return null;
	}
}
