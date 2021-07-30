/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: CodeRuleFilledStratege
 * wangweixia 2013-1-30
 */
package dyna.common.systemenum.coding;

import dyna.common.util.StringUtils;

/**
 * @author wangweixia
 *         码段类型为Code和Object时的截取策略：从前向后/从后向前
 */
public enum CodeRuleCutStrategeEnum
{
	FORWARD("0", "ID_SYS_CODERULECUTSTRATEGEENUM_FORWARD"), // 从前向后
	BACKWARD("1", "ID_SYS_CODERULECUTSTRATEGEENUM_BACKWARD"); // 从后向前
	private final String	value;
	private final String	msrId;

	private CodeRuleCutStrategeEnum(String value, String msrId)
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

	public static CodeRuleCutStrategeEnum typeValueOf(String value)
	{

		if (StringUtils.isNullString(value))
		{
			return null;
		}
		for (CodeRuleCutStrategeEnum valueEnum : CodeRuleCutStrategeEnum.values())
		{
			if (value.equals(valueEnum.getValue()))
			{
				return valueEnum;
			}
		}

		return null;
	}
}
