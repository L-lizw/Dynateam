/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: OnTimeStateEnum
 * wangweixia 2013-10-18
 */
package dyna.common.systemenum.ppms;

import dyna.common.util.StringUtils;

/**
 * @author wangweixia
 * 
 */
public enum OnTimeStateEnum
{
	POTENTIALRISK("potentialRisk", "ID_CLIENT_WBS_WARN_WARN"), // 潜在进度风险
	NOTSTARTED("notStarted", "ID_CLIENT_WBS_NOTSTART"), // 未启动
	DELAY("delay", "ID_CLIENT_WBS_WARN_DELAY"), // 延迟
	PROGRESSONTIME("progressonTime", "ID_CLIENT_WBS_WARN_ONTIME");// 进度准时
	private final String	msrId;
	private final String	value;

	private OnTimeStateEnum(String value, String msrId)
	{
		this.value = value;
		this.msrId = msrId;
	}

	/**
	 * @return the msrId
	 */
	public String getMsrId()
	{
		return this.msrId;
	}

	public String getValue()
	{
		return this.value;
	}

	public static OnTimeStateEnum getValueOf(String value)
	{
		if (StringUtils.isNullString(value))
		{
			return null;
		}

		for (OnTimeStateEnum enumValue : OnTimeStateEnum.values())
		{
			if (enumValue.getValue().equals(value))
			{
				return enumValue;
			}
		}
		return null;
	}
}
