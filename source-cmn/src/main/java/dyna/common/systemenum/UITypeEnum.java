/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: UI类型枚举
 * Jiagang 2010-7-13
 */
package dyna.common.systemenum;

/**
 * UI类型枚举
 * 
 * @author Jiagang
 * 
 */
public enum UITypeEnum
{
	FORM("Form"), LIST("List"), FORM_MOBILE("Form_Mobile"),LIST_MOBILE("List_Mobile"), REPORT("Report"), SECTION("Section");

	private final String	type;

	@Override
	public String toString()
	{
		return this.type;
	}

	private UITypeEnum(String type)
	{
		this.type = type;
	}

	public static UITypeEnum[] listBaseUIType()
	{
		return new UITypeEnum[] { FORM, LIST };
	}
}
