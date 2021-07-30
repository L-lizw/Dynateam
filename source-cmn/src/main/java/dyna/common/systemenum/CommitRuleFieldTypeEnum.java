/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Field类型枚举
 * Jiagang 2010-7-13
 */
package dyna.common.systemenum;

/**
 * Field类型枚举
 * 
 * @author Jiagang
 * 
 */
public enum CommitRuleFieldTypeEnum
{
	INTERFACE("interface"), BUSINESSOBJECT("businessobject"), CLASSIFICATION("classification"), STATUS("status"), GROUP(
			"group");

	public static CommitRuleFieldTypeEnum typeValueOf(String type)
	{
		for (CommitRuleFieldTypeEnum commitRuleFieldTypeEnum : CommitRuleFieldTypeEnum.values())
		{
			if (type.equals(commitRuleFieldTypeEnum.toString()))
			{
				return commitRuleFieldTypeEnum;
			}
		}

		return null;
	}

	private String	type	= null;

	private CommitRuleFieldTypeEnum(String type)
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

}
