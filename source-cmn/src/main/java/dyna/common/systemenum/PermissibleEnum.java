/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: PermissibleEnum
 * Wanglei 2010-12-6
 */
package dyna.common.systemenum;

import dyna.common.util.StringUtils;

/**
 * 许可枚举
 * 
 * @author Wanglei
 * 
 */
public enum PermissibleEnum
{
	/**
	 * 允许
	 */
	YES("√", "Allow;允许;允許", true, "0"), //
	/**
	 * 不允许
	 */
	NO("X", "Deny;拒绝;拒絕", false, "2"), //
	/**
	 * 授权
	 */
	GRANTED("G", "Grant;授权;授權", true, "1"), //
	/**
	 * default
	 */
	NONE("", "", false, "");//

	/*
	 * public static PermissibleEnum getPermissibleEnum(String msg)
	 * {
	 * if ("Y".equals(msg))
	 * {
	 * return YES;
	 * }
	 * else if ("N".equals(msg))
	 * {
	 * return NO;
	 * }
	 * else if ("G".equals(msg))
	 * {
	 * return GRANTED;
	 * }
	 * else
	 * {
	 * return NONE;
	 * }
	 * }
	 */

	public static PermissibleEnum getPermissibleEnum(Integer priority)
	{
		if (priority == null)
		{
			return NONE;
		}
		else if (priority == 0)
		{
			return YES;
		}
		else if (priority == 2)
		{
			return NO;
		}
		else if (priority == 1)
		{
			return GRANTED;
		}
		else
		{
			return NONE;
		}
	}

	String	msg			= null;
	String	priority	= "0";
	boolean	isPermitted	= false;
	String	value		= null;

	private PermissibleEnum(String value, String msg, boolean isPermitted, String priority)
	{
		this.msg = msg;
		this.isPermitted = isPermitted;
		this.priority = priority;
		this.value = value;
	}

	public String getMessage()
	{
		return this.msg;
	}

	public String getValue()
	{
		return this.value;
	}

	public String getMessage(LanguageEnum lang)
	{
		return StringUtils.getMsrTitle(this.msg, lang.getType());
	}

	public String getPriority()
	{
		return this.priority;
	}

	public boolean isPermitted()
	{
		return this.isPermitted;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString()
	{
		return this.value;
	}

}
