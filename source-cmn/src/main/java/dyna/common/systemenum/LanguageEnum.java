/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 显示语言枚举
 * WangLHB, 2010-08-13
 */
package dyna.common.systemenum;

import java.util.Locale;

/**
 * 显示语言枚举
 * 
 * @author WangLHB
 * 
 */
public enum LanguageEnum
{
	EN(0, "en", "English", Locale.ENGLISH), //
	ZH_CN(1, "zh_CN", "中文简体", Locale.PRC), // 名字和ID相同，大小写不同
	ZH_TW(2, "zh_TW", "中文繁體", Locale.TAIWAN);

	private int		type	= 0;
	private String	id		= null;
	private String	display	= null;
	private Locale	local	= null;

	private LanguageEnum(int idx, String id, String display, Locale local)
	{
		this.type = idx;
		this.id = id;
		this.display = display;
		this.local = local;
	}

	public static LanguageEnum getById(String id)
	{
		for (LanguageEnum lang : LanguageEnum.values())
		{
			if (lang.id.equals(id))
			{
				return lang;
			}
		}
		return ZH_CN;
	}

	public String getId()
	{
		return this.id;
	}

	public String getDisplay()
	{
		return this.display;
	}

	public Locale getLocal()
	{
		return this.local;
	}

	public int getType()
	{
		return this.type;
	}

	@Override
	public String toString()
	{
		return this.id;
	}

}
