/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ImportanceLevelEnum
 * wangweixia 2013-10-18
 */
package dyna.common.systemenum.ppms;

import dyna.common.util.StringUtils;

/**
 * @author wangweixia
 * 
 */
public enum ImportanceLevelEnum
{
	VERYHIGH("veryHigh", ""), // 极高
	HIGH("high", ""), // 高
	LOW("low", ""), // 低
	COMMON("common", ""); // 普通

	private final String	msrId;
	private final String	value;

	private ImportanceLevelEnum(String value, String msrId)
	{
		this.value = value;
		this.msrId = msrId;
	}

	/**
	 * @return the msrId
	 */
	public String getMsrId()
	{
		return msrId;
	}

	public String getValue()
	{
		return value;
	}

	/**
	 * 通过值获得此枚举
	 * 
	 * @param value
	 * @return
	 */
	public static ImportanceLevelEnum getValueOf(String value)
	{
		if (StringUtils.isNullString(value))
		{
			return null;
		}

		for (ImportanceLevelEnum enumValue : ImportanceLevelEnum.values())
		{
			if (enumValue.getValue().equals(value))
			{
				return enumValue;
			}
		}
		return null;
	}
}
