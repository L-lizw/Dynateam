/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: AccessConditionEnum
 * Wanglei 2010-7-30
 */
package dyna.common.systemenum;

import dyna.common.util.StringUtils;

/**
 * @author Wanglei
 * 
 */
public enum AccessFunctionConditionEnum
{
	USER(0, "user;用户;用戶"),
	ROLE(1, "role;角色;角色"),
	GROUP(2, "group;组;組");

	private int		precedence	= 0;
	private String	title		= null;

	private AccessFunctionConditionEnum(int precedence, String title)
	{
		this.precedence = precedence;
		this.title = title;
	}

	public int getPrecedence()
	{
		return this.precedence;
	}

	public String getTitle()
	{
		return this.title;
	}

	public String getTitle(LanguageEnum lang)
	{
		return StringUtils.getMsrTitle(this.title, lang.getType());
	}
}
