/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: CodeRuleLifeCycle
 * WangLHB Sep 25, 2012
 */
package dyna.common.systemenum.coding;

import dyna.common.util.StringUtils;

/**
 * @author WangLHB
 * 
 */
public enum CodeRuleLifeCycleEnum
{
	ENABLED("0", "ID_SYS_CODERULELIFECYCLEENUM_ENABLED"), // 启用
	PAUSE("1", "ID_SYS_CODERULELIFECYCLEENUM_PAUSE"), // 暂停
	DISABLE("2", "ID_SYS_CODERULELIFECYCLEENUM_DISABLE");// 废弃

	private String	value;
	private String	msrId;

	private CodeRuleLifeCycleEnum(String value, String msrId)
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

	public static CodeRuleLifeCycleEnum typeValueOf(String value)
	{

		if (StringUtils.isNullString(value))
		{
			return null;
		}
		for (CodeRuleLifeCycleEnum codeRuleLifeCycleEnum : CodeRuleLifeCycleEnum.values())
		{
			if (value.equals(codeRuleLifeCycleEnum.getValue()))
			{
				return codeRuleLifeCycleEnum;
			}
		}

		return null;
	}
}
