/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Business Model类型枚举
 * Jiagang 2010-7-13
 */
package dyna.common.systemenum;

/**
 * Business Model类型枚举
 * 
 * @author Jiagang
 * 
 */
public enum BusinessModelTypeEnum
{
	PACKAGE("Package", "1"), CLASS("Class", "0");

	private final String	type;

	private final String	value;

	@Override
	public String toString()
	{
		return this.type;
	}

	private BusinessModelTypeEnum(String type, String value)
	{
		this.type = type;
		this.value = value;
	}

	public String getValue()
	{
		return this.value;
	}

	public static BusinessModelTypeEnum getEnum(String value)
	{
		if ("0".equals(value))
		{
			return BusinessModelTypeEnum.CLASS;
		}
		else if ("1".equals(value))
		{
			return BusinessModelTypeEnum.PACKAGE;
		}
		return null;
	}
}
