/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 通知的类别枚举
 * caogc, 2010-08-20
 */
package dyna.common.systemenum;

import dyna.common.util.StringUtils;

/**
 * 邮件的类别枚举
 * 
 * @author caogc
 * 
 */
public enum MailCategoryEnum
{
	INFO("1"), WARNING("2"), ERROR("3"), @Deprecated
	REPORT("4");

	public static MailCategoryEnum typeValueOf(String type)
	{
		if (!StringUtils.isNullString(type))
		{
			for (MailCategoryEnum mailCategoryEnum : MailCategoryEnum.values())
			{
				if (type.equals(mailCategoryEnum.toString()))
				{
					return mailCategoryEnum;
				}
			}
		}

		return null;
	}

	private String	type	= null;

	private MailCategoryEnum(String type)
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

	public int toValue()
	{
		return Integer.parseInt(this.type);
	}
}
