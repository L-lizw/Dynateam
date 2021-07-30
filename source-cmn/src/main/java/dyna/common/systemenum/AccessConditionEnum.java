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
public enum AccessConditionEnum
{
	INLIFECYCLEPHASE(5, "lifecycle;生命周期;生命週期"), //
	HASCLASS(4, "class;类;類"), //
	HASSTATUS(1, "status;状态;狀態"), //
	OWNINGGROUP(2, "ownergroup;所有组;所有組"), //
	HASCLASSFICATION(3, "classfication;分类;分類"), //
	HASREVISION(6, "revision;版本;版本");//

	private int		precedence	= 0;
	private String	title		= null;

	private AccessConditionEnum(int precedence, String title)
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
