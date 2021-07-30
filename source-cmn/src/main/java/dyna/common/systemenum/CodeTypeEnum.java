/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: CodeTypeEnum
 * Jiagang 2010-8-13
 */
package dyna.common.systemenum;

/**
 * @author Jiagang
 *
 */
public enum CodeTypeEnum
{
	TREE("Tree", "1"), LIST("List", "2");

	private final String	type;

	private String			value;

	@Override
	public String toString()
	{
		return this.type;
	}

	public String getValue()
	{
		return this.value;
	}

	private CodeTypeEnum(String type, String value)
	{
		this.value = value;
		this.type = type;
	}

	public static CodeTypeEnum getEnum(String value)
	{
		for (CodeTypeEnum showEnum : CodeTypeEnum.values())
		{
			if (showEnum.value.equals(value))
			{
				return showEnum;
			}
		}
		return null;
	}

	public static CodeTypeEnum getEnumByType(String type)
	{
		for (CodeTypeEnum showEnum : CodeTypeEnum.values())
		{
			if (showEnum.type.equalsIgnoreCase(type))
			{
				return showEnum;
			}
		}
		return null;
	}
}
