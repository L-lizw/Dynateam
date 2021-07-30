/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: CodeRuleFilledStratege
 * wangweixia 2013-1-30
 */
package dyna.common.systemenum.coding;

import dyna.common.util.StringUtils;

/**
 * @author wangweixia
 *         码段类型为Code和Object时的补齐策略：向前/向后补齐
 */
public enum CodeRuleFilledStrategeEnum
{
	FORWARD("forward", "ID_SYS_CODERULEFILLEDSTRATEGEENUM_FORWARD"), // 向前
	BACKWARD("backward", "ID_SYS_CODERULEFILLEDSTRATEGEENUM_BACKWARD"); // 向后
	private final String	value;
	private final String	msrId;

	private CodeRuleFilledStrategeEnum(String value, String msrId)
	{
		this.value = value;
		this.msrId = msrId;
	}

	public String getValue()
	{
		return this.value;
	}

	/**
	 * @return the msrId
	 */
	public String getMsrId()
	{
		return this.msrId;
	}

	public static CodeRuleFilledStrategeEnum typeValueOf(String value)
	{

		if (StringUtils.isNullString(value))
		{
			return null;
		}
		for (CodeRuleFilledStrategeEnum valueEnum : CodeRuleFilledStrategeEnum.values())
		{
			if (value.equals(valueEnum.getValue()))
			{
				return valueEnum;
			}
		}

		return null;
	}
}
