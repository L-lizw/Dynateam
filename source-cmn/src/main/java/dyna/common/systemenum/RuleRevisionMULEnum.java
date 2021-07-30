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
public enum RuleRevisionMULEnum
{
	NOTMUL("0"), // 不允许多个工作版本同时存在
	MUL("1"); // 允许多个工作版本同时存在
	private final String	type;

	public static RuleRevisionMULEnum typeValueOf(String type)
	{
		for (RuleRevisionMULEnum reviseRevisionRuleEnum : RuleRevisionMULEnum.values())
		{
			if (reviseRevisionRuleEnum.toString().equals(type))
			{
				return reviseRevisionRuleEnum;
			}
		}

		return null;
	}

	private RuleRevisionMULEnum(String type)
	{
		this.type = type;
	}

	@Override
	public String toString()
	{
		return this.type;
	}
}
