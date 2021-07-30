/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: NumberUtils 工具类, 提供与数字操作相关的操作
 * Wanglei 2010-4-16
 */
package dyna.common.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.regex.Pattern;

/**
 * 工具类, 提供与数字操作相关的操作
 * 
 * @author Wanglei
 * 
 */
public class NumberUtils
{

	/**
	 * 将obj转换为int类型的数值, 如果obj为null, 则返回指定的defaults值
	 * 
	 * @param obj
	 * @param defaults
	 * @return
	 */
	public static int intOf(final Object obj, final int defaults)
	{
		try
		{
			return (obj == null) ? defaults : Integer.parseInt(obj.toString());
		}
		catch (NumberFormatException e)
		{
			return defaults;
		}
	}

	public static Integer getIneger(final String integerStr)
	{
		return StringUtils.isNullString(integerStr) ? null : Integer.valueOf(integerStr);
	}

	/**
	 * 将obj format
	 * 
	 * @param value
	 * @param maximumFraction
	 * @return
	 */
	public static String format(Object value, int maximumFraction)
	{
		if (value == null)
		{
			return null;
		}
		NumberFormat instance = DecimalFormat.getNumberInstance();
		instance.setMaximumFractionDigits(maximumFraction);

		String strvalue = null;
		if (value instanceof String)
		{
			strvalue = instance.format(new BigDecimal((String) value));
		}
		else if (value instanceof BigDecimal)
		{
			strvalue = instance.format(value);
		}
		else if (value instanceof Float)
		{
			strvalue = instance.format(value);
		}

		return strvalue;
	}

	// 取出float类型字段的数据
	public static Double getFloat(Object value, boolean defaultOne)
	{
		Double doubleValue = null;

		if (value == null)
		{
			if (defaultOne)
			{
				doubleValue = 1.000D;
			}
		}
		else if (value instanceof String)
		{
			if (!StringUtils.isNullString((String) value))
			{
				doubleValue = new BigDecimal((String) value).doubleValue();
			}
			else
			{
				if (defaultOne)
				{
					doubleValue = 1.000D;
				}
			}
		}
		else if (value instanceof Double)
		{
			doubleValue = (Double) value;
		}
		else if (value instanceof Float)
		{
			doubleValue = new BigDecimal(String.valueOf(value)).doubleValue();
		}
		else if (value instanceof Integer)
		{
			doubleValue = new BigDecimal((Integer) value).doubleValue();
		}
		else
		{
			doubleValue = ((Number) value).doubleValue();
		}
		return doubleValue;
	}

	/**
	 * 显示Float值
	 * 通过classField设置的长度和精度进行正确显示Float字段
	 * 
	 * @param classfieldSize
	 *            classField的长度
	 * @param value
	 *            Float类型的值
	 * @return
	 */
	public static String getDecimalValueOfClassFieldSize(String classfieldSize, Object value)
	{
		if (value != null)
		{
			Double _value = getFloat(value, false);
			DecimalFormat decimalFormat = getDecimalFormat(classfieldSize);
			if (decimalFormat != null && _value != null)
			{
				return decimalFormat.format(_value);
			}
		}
		return null;
	}

	public static DecimalFormat getDecimalFormat(String classfieldSize)
	{
		DecimalFormat decimalFormat = null;
		String formatString = getDecimalFormatString(classfieldSize);
		decimalFormat = new DecimalFormat(formatString);
		return decimalFormat;
	}

	public static String getDecimalFormatString(String classFieldSize)
	{
		String formatString = "";
		if (!StringUtils.isNullString(classFieldSize))
		{
			int size = 0;
			int accuracy = 0;
			if (classFieldSize.contains(","))
			{
				String[] strs = StringUtils.splitStringWithDelimiter(",", classFieldSize);
				if (strs != null && strs.length == 1)
				{
					size = Integer.parseInt(strs[0]);
					if (size > 2)
					{
						while (size - 2 > 1)
						{
							formatString += "#";
							size--;
						}
						formatString += "0.0#";
					}
				}
				else if (strs != null && strs.length > 1)
				{
					if (!StringUtils.isNullString(strs[0]) && !StringUtils.isNullString(strs[1]))
					{
						size = Integer.parseInt(strs[0]);
						accuracy = Integer.parseInt(strs[1]);
						formatString = getformatString(size, accuracy);

					}
					else if (StringUtils.isNullString(strs[0]) && !StringUtils.isNullString(strs[1]))
					{
						accuracy = Integer.parseInt(strs[1]);
						int s = 18;
						formatString = getformatString(s, accuracy);
					}
				}
			}
		}

		if (StringUtils.isNullString(formatString))
		{
			formatString = "###############0.0#";
		}
		return formatString;
	}

	private static String getformatString(int size, int accuracy)
	{
		String formatString = "";
		if (size > accuracy)
		{
			while (size - accuracy > 1)
			{
				formatString += "#";
				size--;
			}
			formatString += "0";
			if (accuracy > 0)
			{
				formatString += ".";
				while (accuracy >= 1)
				{
					formatString += "0";
					accuracy--;
				}
			}
		}
		return formatString;
	}

	public static boolean isNumeric(String str)
	{
		try
		{
			if (str == null)
			{
				return false;
			}

			BigDecimal num = new BigDecimal(str);
			num.doubleValue();
			return true;
		}
		catch (NumberFormatException e)
		{
			return false;
		}
	}

	public static boolean isPositiveInteger(String value)
	{
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(value).matches();
	}

}
