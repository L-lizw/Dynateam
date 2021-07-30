/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: LocationTypeEnum
 * caogc 2010-12-08
 */
package dyna.common.systemenum;

/**
 * Location类型枚举
 * 
 * @author caogc
 * 
 */
public enum EffectiveRuleEnum
{
	// 1，新建即生效。2，用户指定生效时间。3，需要工作流生效
	CREATE("1"), EFFECTIVE("2"), WORKFLOW("3");

	public static EffectiveRuleEnum typeValueOf(String type)
	{
		for (EffectiveRuleEnum locationTypeEnum : EffectiveRuleEnum.values())
		{
			if (type.equals(locationTypeEnum.toString()))
			{
				return locationTypeEnum;
			}
		}

		return null;
	}

	private String	type	= null;

	private EffectiveRuleEnum(String type)
	{
		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString()
	{
		return this.type;
	}

	public int toValue()
	{
		return Integer.parseInt(this.type);
	}
}
