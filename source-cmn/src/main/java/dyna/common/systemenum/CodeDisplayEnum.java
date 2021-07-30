/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: CodeDisplayEnum
 * WangLHB 2011-4-11
 */
package dyna.common.systemenum;

/**
 * @author WangLHB
 * 
 */
public enum CodeDisplayEnum
{
	DROPDOWN("DropDown", "1"), POPUP("Popup", "2");

	private String	type;

	private String	value;

	@Override
	public String toString()
	{
		return this.type;
	}

	private CodeDisplayEnum(String type, String value)
	{
		this.type = type;
		this.value = value;
	}

	public String getValue()
	{
		return this.value;
	}

	public static CodeDisplayEnum getEnum(String value)
	{
		for (CodeDisplayEnum showEnum : CodeDisplayEnum.values())
		{
			if (showEnum.value.equals(value))
			{
				return showEnum;
			}
		}
		return null;
	}

	public static CodeDisplayEnum getEnumByType(String type)
	{
		for (CodeDisplayEnum showEnum : CodeDisplayEnum.values())
		{
			if (showEnum.type.equalsIgnoreCase(type))
			{
				return showEnum;
			}
		}
		return null;
	}
}
