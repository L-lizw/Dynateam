/*
 * Copyright (C) DCIS 版权所有
 * 功能描述:数据库辅助工具集
 * xiasheng 2010-7-16
 */
package dyna.dbcommon.util;

import dyna.common.util.DateFormat;

import java.util.Date;

public class DBUtil
{
	/**
	 * 构造调用数据库函数所需要的参数字符串（第一个参数为sessionId）
	 * 
	 * @param function
	 * @param sessionId
	 * @param params
	 * @return
	 */
	public static String buildFunctionContext(String function, String sessionId, Object... params)
	{
		StringBuilder functionContext = new StringBuilder(function + "('" + sessionId + "'");
		for (Object param : params)
		{
			functionContext.append(",");

			if (param == null)
			{
				functionContext.append("NULL");
			}
			else if (param instanceof String)
			{
				functionContext.append("'" + ((String) param).replace("'", "''") + "'");
			}
			else if (param instanceof Date)
			{
				functionContext.append("TO_DATE('" + DateFormat.formatYMDHMS((Date) param) + "','"
						+ "yyyy-MM-dd HH24:mi:ss" + "')");
			}
			else if (param instanceof Date)
			{
				functionContext.append("TO_DATE(''" + DateFormat.formatYMD((Date) param) + "'',''" + DateFormat.getSDFYMD()
						+ "'')");
			}
			else if (param instanceof Boolean)
			{
				if (((Boolean) param).booleanValue())
				{
					functionContext.append("'Y'");
				}
				else
				{
					functionContext.append("'N'");
				}
			}
			else
			{
				functionContext.append(param);
			}
		}

		functionContext.append(")");
		return functionContext.toString();
	}

	/**
	 * 构造调用数据库函数所需要的参数字符串（可变参数）
	 *
	 * @param function
	 * @param params
	 * @return
	 */
	public static String buildFunctionContextForChangeArg(String function, Object... params)
	{
		StringBuilder functionContext = new StringBuilder(function + "(");
		// 没有参数
		if (params != null && params.length == 0)
		{
			;
		}
		// 只有一个参数并且为null
		else if (params == null)
		{
			functionContext.append("NULL");
		}
		else
		{
			for (Object param : params)
			{
				String symbol = functionContext.substring(functionContext.length() - 1, functionContext.length());
				if (!"(".equals(symbol))
				{
					functionContext.append(",");
				}
				if (param == null)
				{
					functionContext.append("NULL");
				}
				else if (param instanceof String)
				{
					functionContext.append("'" + ((String) param).replace("'", "''") + "'");
				}
				else if (param instanceof Date)
				{
					functionContext.append("TO_DATE('" + DateFormat.formatYMDHMS((Date) param) + "','"
							+ "yyyy-MM-dd HH24:mi:ss" + "')");
				}
				else if (param instanceof Date)
				{
					functionContext.append("TO_DATE(''" + DateFormat.formatYMD((Date) param) + "'',''"
							+ DateFormat.getSDFYMD() + "'')");
				}
				else if (param instanceof Boolean)
				{
					if (((Boolean) param).booleanValue())
					{
						functionContext.append("'Y'");
					}
					else
					{
						functionContext.append("'N'");
					}
				}
				else
				{
					functionContext.append(param);
				}
			}
		}
		functionContext.append(")");
		return functionContext.toString();
	}

}
