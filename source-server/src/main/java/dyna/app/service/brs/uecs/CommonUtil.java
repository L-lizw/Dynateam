/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 公共常用变量/方法
 * liuzt 2011-3-17
 */
package dyna.app.service.brs.uecs;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import dyna.common.bean.data.FoundationObject;
import dyna.common.dto.model.cls.ClassField;
import dyna.common.util.StringUtils;

/**
 * 公共常用变量/方法
 * 
 * @author liuWei
 * 
 */
public class CommonUtil
{

	// 取出float类型字段的数据
	public static Double getFloat(String filedName, FoundationObject changeItemObject)
	{
		Double floatValue = null;
		Object value0 = changeItemObject.get(filedName.toUpperCase());
		if (value0 == null)
		{
			floatValue = null;
		}
		else if (value0 instanceof String)
		{
			floatValue = new BigDecimal((String) value0).doubleValue();
		}
		else if (value0 instanceof Double)
		{
			floatValue = (Double) value0;
		}
		else if (value0 instanceof Float)
		{
			floatValue = new BigDecimal(String.valueOf(value0)).doubleValue();
		}
		else
		{
			floatValue = ((Number) value0).doubleValue();
		}
		return floatValue;
	}

	public static String formatString(ClassField classField, FoundationObject bomStructure)
	{
		DecimalFormat decimalFormat = getDecimalFormat(classField);
		if (CommonUtil.getFloat(classField.getName(), bomStructure) == null)
		{
			return null;
		}
		return decimalFormat.format(CommonUtil.getFloat(classField.getName(), bomStructure));
	}

	public static DecimalFormat getDecimalFormat(ClassField classField)
	{
		DecimalFormat decimalFormat = null;
		String formatString = getDecimalFormatString(classField);

		decimalFormat = new DecimalFormat(formatString);
		return decimalFormat;
	}

	public static String getDecimalFormatString(ClassField classField)
	{
		String formatString = "";
		if (classField != null)
		{
			if (!StringUtils.isNullString(classField.getFieldSize()))
			{
				int size = 0;
				int accuracy = 0;
				if (classField.getFieldSize().contains(","))
				{
					String[] strs = StringUtils.splitStringWithDelimiter(",", classField.getFieldSize());
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
							if (size > accuracy)
							{
								while (size - accuracy > 1)
								{
									formatString += "#";
									size--;
								}
								formatString += "0.";
								while (accuracy > 1)
								{
									formatString += "0";
									accuracy--;
								}
								formatString += "#";
							}
						}
						else if (StringUtils.isNullString(strs[0]) && !StringUtils.isNullString(strs[1]))
						{
							accuracy = Integer.parseInt(strs[0]);
							int s = 18;
							while (s - accuracy > 1)
							{
								formatString += "#";
								s--;
							}
							formatString += "0.";
							while (accuracy > 1)
							{
								formatString += "0";
								accuracy--;
							}
							formatString += "#";
						}
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
}
