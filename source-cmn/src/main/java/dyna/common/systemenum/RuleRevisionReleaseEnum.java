/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: RuleRevisionEnum
 * zhanghj 2010-11-26
 */
package dyna.common.systemenum;

/**
 * @author zhanghj
 * 
 */
public enum RuleRevisionReleaseEnum
{
	NOTCREATEREVISE("0"), // 不发布也允许创建修订版
	CREATEREVISE("1"); // 仅发布后允许创建修订版
	private final String	type;

	public static RuleRevisionReleaseEnum typeValueOf(String type)
	{
		for (RuleRevisionReleaseEnum reviseRevisionRuleEnum : RuleRevisionReleaseEnum.values())
		{
			if (type.equals(reviseRevisionRuleEnum.toString()))
			{
				return reviseRevisionRuleEnum;
			}
		}

		return null;
	}

	private RuleRevisionReleaseEnum(String type)
	{
		this.type = type;
	}

	@Override
	public String toString()
	{
		return this.type;
	}
}
