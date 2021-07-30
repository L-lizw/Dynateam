/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: BomProcessType
 * WangLHB May 20, 2011
 */
package dyna.common.systemenum;

/**
 * @author WangLHB
 * 
 */
public enum BomPreciseType
{
	PRECISE("1"), NONPRECISE("2"), USERSPECIFIED("3");

	private final String	value;

	private BomPreciseType(String value)
	{
		this.value = value;
	}

	public String getValue()
	{
		return this.value;
	}

	public static BomPreciseType getEnum(String value)
	{
		for (BomPreciseType type : BomPreciseType.values())
		{
			if (type.getValue().equalsIgnoreCase(value))
			{
				return type;
			}
		}
		return null;
	}
}
