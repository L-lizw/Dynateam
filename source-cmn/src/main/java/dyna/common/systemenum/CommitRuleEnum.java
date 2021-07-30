/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: CommitRuleEnum 入库规则枚举
 * caogc 2010-12-08
 */
package dyna.common.systemenum;

/**
 * CommitRuleEnum 入库规则枚举
 * 
 * @author caogc
 * 
 */
public enum CommitRuleEnum
{
	// 1，生效入库。2，发布时入库 3，生效时入库并强制使用生效规则 4，发布时入库并强制使用生效规则
	EFFECTIVECOMMIT("1"), RLSCOMMIT("2"), EFFECTIVECOMMITFORCE("3"), RLSCOMMITFORCE("4");

	public static CommitRuleEnum typeValueOf(String type)
	{
		for (CommitRuleEnum locationTypeEnum : CommitRuleEnum.values())
		{
			if (type.equals(locationTypeEnum.toString()))
			{
				return locationTypeEnum;
			}
		}
		return null;
	}

	private String	type	= null;

	private CommitRuleEnum(String type)
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
