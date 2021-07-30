/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 显示语言枚举
 * WangLHB, 2010-08-13
 */
package dyna.common.systemenum;

/**
 * 显示语言枚举
 * 
 * @author WangLHB
 * 
 */
public enum FilePreferenceEnum
{
	OPEN("1"), OPEN_ONLY_CHECKOUT("2"), NOT_OPEN("3");

	private String	value;

	private FilePreferenceEnum(String value)
	{
		this.value = value;
	}

	public String getValue()
	{
		return this.value;
	}

	public static FilePreferenceEnum getByValue(String value)
	{
		for (FilePreferenceEnum lang : FilePreferenceEnum.values())
		{
			if (lang.value.equals(value))
			{
				return lang;
			}
		}
		return NOT_OPEN;
	}

}
