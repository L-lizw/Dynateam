/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: OperationState
 * wangweixia 2014-11-14
 */
package dyna.common.systemenum.ppms;

import dyna.common.util.StringUtils;

/**
 * @author wangweixia
 *         任务可操作状态
 */
public enum OperationStateEnum
{
	START("start", ""), // 启动
	CANNOTSTART("cannotStart", ""), // 不启动
	COMPLETE("complete", ""), // 完成
	CANNOTCOMPLETE("cannotComplete", "");// 不可完成
	private final String	value;
	private final String	msrId;

	private OperationStateEnum(String value, String msrId)
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

	public static OperationStateEnum getValueOf(String value)
	{
		if (StringUtils.isNullString(value))
		{
			return null;
		}

		for (OperationStateEnum enumValue : OperationStateEnum.values())
		{
			if (enumValue.getValue().equals(value))
			{
				return enumValue;
			}
		}
		return null;
	}
}
