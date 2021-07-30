/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: BooleanUtils 工具类, 提供布尔类型与其他类型(例如字符串)的相互转换
 * Wanglei 2010-8-17
 */
package dyna.common.util;

/**
 * 工具类, 提供布尔类型与其他类型(例如字符串)的相互转换
 * 
 * @author Wanglei
 * 
 */
public class BooleanUtils
{
	/**
	 * 如果没有给定值，那么返回默认值，有给定值则返回给定值
	 * 
	 * @param value
	 * @param defaultValue
	 * @return
	 */
	public static boolean getBoolean(Boolean value, boolean defaultValue)
	{
		if (value != null)
		{
			return value.booleanValue();
		}

		return defaultValue;
	}

	/**
	 * 根据'Y'/'N'字符串返回true/false
	 * 
	 * @param value
	 * @return
	 */
	public static Boolean getBooleanByYN(String value)
	{
		if (StringUtils.isNullString(value))
		{
			return null;
		}
		return "Y".equals(value);
	}

	/**
	 * 根据'0'/'1'字符串返回true/false,
	 * 
	 * @param value
	 * @return
	 */
	@Deprecated
	public static Boolean getBooleanBy01(String value)
	{
		if (StringUtils.isNullString(value))
		{
			return null;
		}

		return "0".equals(value);
	}

	/**
	 * 根据'0'/'1'字符串返回false/true,
	 * 
	 * @param value
	 * @return
	 */
	public static Boolean getBooleanBy10(String value)
	{
		if (StringUtils.isNullString(value))
		{
			return null;
		}

		return !"0".equals(value);
	}

	/**
	 * 根据false/true字符串返回false/true,
	 * 
	 * @param value
	 * @return
	 */
	public static Boolean getBooleanByValue(String value)
	{
		if (StringUtils.isNullString(value))
		{
			return null;
		}

		return "true".equalsIgnoreCase(value);
	}

	/**
	 * 根据boolean值返回指定字符串
	 * 
	 * @param value
	 *            boolean值
	 * @param trueStr
	 *            值为true时的字符串
	 * @param falseStr
	 *            值为false时的字符串
	 * @param nullStr
	 *            值为null时的字符串
	 * @return
	 */
	public static String getBooleanString(Boolean value, String trueStr, String falseStr, String nullStr)
	{
		if (value == null)
		{
			return nullStr;
		}
		return value ? trueStr : falseStr;
	}

	/**
	 * 根据boolean值true/false返回'0'/'1'
	 * 
	 * @param value
	 * @return
	 */
	@Deprecated
	public static String getBooleanString01(Boolean value)
	{
		return getBooleanString(value, "0", "1", null);
	}

	/**
	 * 根据boolean值true/false返回'1'/'0'
	 * 
	 * @param value
	 * @return
	 */
	public static String getBooleanString10(Boolean value)
	{
		return getBooleanString(value, "1", "0", null);
	}

	/**
	 * 根据boolean值true/false返回'Y'/'N'
	 * 
	 * @param value
	 * @return
	 */
	public static String getBooleanStringYN(Boolean value)
	{
		return getBooleanString(value, "Y", "N", null);
	}

	/**
	 * 根据boolean值true/false返回true/false
	 * 
	 * @param value
	 * @return
	 */
	public static String getBooleanStringValue(Boolean value)
	{
		return getBooleanString(value, "true", "false", null);
	}
}
