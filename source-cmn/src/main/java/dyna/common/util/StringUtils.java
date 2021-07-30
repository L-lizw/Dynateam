/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Utils 工具类, 提供静态工具方法 操作字符串
 * Wanglei 2010-4-9
 */
package dyna.common.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 工具类, 提供静态工具方法 操作字符串
 * 
 * @author Wanglei
 * 
 */
public class StringUtils
{
	public static final String	EMPTY_STRING	= "";
	public static final String	NO_HASAUTH		= "-";
	public static final String	PAI_DEFAULT_VAL	= "3.14";

	/**
	 * 解析多语言标题, 并获取指定的标题
	 * 
	 * @param msrTitle
	 * @param idx
	 * @return
	 */
	public static String getMsrTitle(String msrTitle, int idx)
	{
		String title = EMPTY_STRING;
		if (!StringUtils.isNullString(msrTitle))
		{
			String[] ss = StringUtils.splitString(msrTitle);
			if (idx < ss.length && ss[idx] != null)
			{
				title = ss[idx];
			}
		}
		return title;
	}

	/**
	 * 使用分号(;)拼接字符串
	 * 
	 * @param strs
	 * @return
	 */
	public static String concatString(final String... strs)
	{
		return concatStringWithDelimiter(";", strs);
	}

	/**
	 * 使用delimiter连接字符串
	 * 
	 * @param delimiter
	 * @param strs
	 * @return
	 */
	public static String concatStringWithDelimiter(final String delimiter, final String... strs)
	{
		StringBuffer retStr = new StringBuffer();
		boolean isNull = false;
		int size = strs.length;
		for (int i = 0; i < size; i++)
		{
			if (isNullString(strs[i]))
			{
				strs[i] = "";
				isNull = true;
			}
			retStr.append(strs[i]);

			// 最后一个字符串为非空串, 则不需要再加上delimiter
			if (!isNull && i == size - 1)
			{
				break;
			}

			retStr.append(delimiter);
			isNull = false;
		}
		return retStr.toString();
	}

	/**
	 * 将'null'/'NULL'字符串格式化为空
	 * 
	 * @param str
	 * @return
	 */
	public static String convertNULL(String str)
	{
		return isNullString(str) || "null".equalsIgnoreCase(str) ? null : str;
	}

	/**
	 * 将null对象格式化为""
	 * 
	 * @param str
	 * @return
	 */
	public static String convertNULLtoString(Object object)
	{
		return object == null ? "" : object.toString();
	}

	public static String converNULLtoSign(String str, String signbom)
	{
		if (isNullString(str))
		{
			return "\t";
		}
		str = str.trim();
		str = str.replaceAll("\\s", signbom);
		str = str.replaceAll("\n", signbom);
		str = str.replaceAll("\t", signbom);
		str = str.replaceAll("\r", signbom);
		return str;
	}

	/**
	 * 产生随机字符串
	 * 
	 * @param length
	 *            长度, 最长32位
	 * @return
	 */
	public static String generateRandomUID(int length)
	{
		String uuid = UUID.randomUUID().toString().replace("-", "");
		if (length < 1)
		{
			return uuid;
		}
		return uuid.substring(0, length);
	}

	/**
	 * 如果所给字符串str为null或者空字符串, 则返回指定的字符串defaults, 否则返回str;<br>
	 * 
	 * @param str
	 * @param defaults
	 * @return
	 */
	public static String getString(final String str, final String defaults)
	{
		return isNullString(str) ? defaults : str;
	}

	/**
	 * 判断字符串是否为GUID表示法(32位16进制大写字符串)
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isGuid(final String str)
	{
		if (isNullString(str))
		{
			return false;
		}
		return str.matches("^[0-9A-F]{32}$");
	}

	/**
	 * 判断字符串是否为null或者空字符串(即长度为0的字符串)
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNullString(final String str)
	{
		return (str == null || str.isEmpty());
	}

	/**
	 * 判断字符串为空，或者全部为*或%,如果是则非法
	 * 
	 * @param keyStr
	 * @return 如果是则返回true,否则为false
	 */
	public static boolean isIllegalString(String keyStr)
	{
		if (keyStr == null)
		{
			return true;
		}
		keyStr = keyStr.trim();
		if (isNullString(keyStr))
		{
			return true;
		}

		for (int i = 0; i < keyStr.length(); i++)
		{
			if (keyStr.charAt(i) != '*')
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * 使用分号(;)分割字符串strs
	 * 
	 * @param strs
	 *            字符串数组
	 * @return
	 */
	public static String[] splitString(final String strs)
	{
		return splitStringWithDelimiter(";", strs);
	}

	/**
	 * 使用delimiter分割字符串strs
	 * <实例：>",a,,b," 分割后->{null,a,null,b}
	 * 
	 * @param delimiter
	 *            分割符
	 * @param strs
	 *            字符串数组
	 * @return
	 */
	public static String[] splitStringWithDelimiter(final String delimiter, String strs)
	{
		if (isNullString(strs))
		{
			return null;
		}

		List<String> strList = new ArrayList<String>();

		int index = 0;
		String strValue = null;
		while (strs.contains(delimiter))
		{
			strValue = null;
			index = strs.indexOf(delimiter);
			if (index != 0)
			{
				strValue = strs.substring(0, index);
			}

			strList.add(strValue);
			strs = strs.substring(index + delimiter.length());
		}

		if (!isNullString(strs))
		{
			strList.add(strs);
		}

		return strList.toArray(new String[strList.size()]);
	}

	/**
	 * 使用delimiter分割字符串strs
	 * <实例：>",a,,b," 分割后->{null,a,null,b,null}
	 * 
	 * @param delimiter
	 *            分割符
	 * @param strs
	 *            字符串数组
	 * @return
	 */
	public static String[] splitStringWithDelimiterHavEnd(final String delimiter, String strs)
	{
		if (isNullString(strs))
		{
			return null;
		}

		List<String> strList = new ArrayList<String>();

		int index = 0;
		String strValue = null;
		while (strs.contains(delimiter))
		{
			strValue = null;
			index = strs.indexOf(delimiter);
			if (index != 0)
			{
				strValue = strs.substring(0, index);
			}

			strList.add(strValue);
			strs = strs.substring(index + delimiter.length());
		}

		if ("".equals(strs))
		{
			strs = null;
		}
		strList.add(strs);

		return strList.toArray(new String[strList.size()]);
	}

	/**
	 * 如果字符串中包含“'”转换成“''” 保存此字符串到数据库时使用
	 * 为空返回""
	 * 
	 * @param value
	 */
	public static String translateSpecialChar(String value)
	{
		if (!isNullString(value))
		{
			return value.replaceAll("'", "''");
		}
		else
		{
			return "";
		}

	}

	public static String getSQLOrderString(String sortField, String defaultField, boolean isASC)
	{
		if (StringUtils.isNullString(sortField))
		{
			return defaultField;
		}
		else
		{
			if (isASC)
			{
				return sortField;
			}
			else
			{
				return sortField + " DESC";
			}
		}
	}

	public static List<String> splitToListByStrs(String message, String delimiter)
	{
		LinkedList<String> list = new LinkedList<String>();
		int pos1 = 0, pos2 = 0;

		while (pos2 >= 0)
		{
			pos2 = message.indexOf(delimiter, pos1);

			if (pos2 >= 0)
			{
				list.add(message.substring(pos1, pos2));

				pos1 = pos2 + delimiter.length();
			}
			else
			{
				list.add(message.substring(pos1));
			}
		}

		return list;
	}

	/**
	 * 制作由给定字符组成的固定长度的字符串
	 * 
	 * @param fixedCharacter
	 * @param length
	 * @return
	 */
	public static String makeFixedLengthString(char fixedCharacter, int length)
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++)
		{
			sb.append(fixedCharacter);
		}
		return sb.toString();
	}

	/**
	 * 在给定字符串左侧使用指定字符补足指定位数
	 * 
	 * @param s
	 * @param length
	 * @param pad
	 * @return
	 */
	public static String lpad(String s, int length, char pad)
	{
		if (StringUtils.isNullString(s) || s.length() >= length)
		{
			return s;
		}

		int padLength = length - s.length();
		String padString = makeFixedLengthString(pad, padLength);
		return padString + s;
	}

	/**
	 * 通过特殊的分隔符获得键值对,并保证顺序正确
	 * 
	 * key1=value1
	 * key2=value2
	 * 
	 * @param value
	 *            属性名+换行符+结果+回车符...
	 * @return
	 */
	public static Map<String, String> getKeyOfValueByDelimiter(String value)
	{
		Map<String, String> map = new LinkedHashMap<String, String>();
		if (!StringUtils.isNullString(value))
		{
			String s1 = "\n";// 换行符
			String s2 = "\t";// Tab键
			String[] v = splitStringWithDelimiter(s2, value);
			if (v != null && v.length > 0)
			{
				for (String v1 : v)
				{
					String[] v2 = splitStringWithDelimiterHavEnd(s1, v1);
					if (v2 != null && v2.length > 1)
					{
						map.put(v2[0], v2[1]);
					}
				}
			}
		}
		return map;
	}

	/*
	 * c 要填充的字符
	 * length 填充后字符串的总长度
	 * content 要格式化的字符串
	 * 格式化字符串，左对齐
	 */
	public static String flushLeft(char c, long length, String content)
	{
		String str = "";
		String cs = "";
		content = StringUtils.convertNULLtoString(content);
		if (content.length() > length)
		{
			str = content;
		}
		else
		{
			for (int i = 0; i < length - content.length(); i++)
			{
				cs = cs + c;
			}
		}
		str = cs + content;
		return str;
	}

	/**
	 * 判断传进来的字符串，是否大于指定的字节，如果大于递归调用
	 * 直到小于指定字节数，一定要指定字符编码，因为各个系统字符编码都不一样，字节数也不一样
	 * 
	 * @param str
	 *            原始字符串
	 * @param limitLength
	 *            传进来指定字节数
	 * @return String 截取后的字符串
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public static String substr(String str, int limitLength)
	{
		if (str == null || str.length() == 0)
		{
			return str;
		}

		int length = 0;
		try
		{
			length = str.getBytes("UTF-8").length;
			if (length > limitLength)
			{
				str = str.substring(0, str.length() - 1);
				str = substr(str, limitLength);
			}
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * 按照指定长度分割字符串,最小指定长度为3
	 * 
	 * @param str
	 *            源字符串
	 * @param limitLength
	 *            指定长度
	 * @return
	 * @throws Exception
	 */
	public static List<String> splitByLength(String str, int limitLength)
	{
		if (str == null || str.length() == 0)
		{
			return null;
		}

		List<String> list = new ArrayList<String>();

		String dest = substr(str, limitLength);
		list.add(dest);

		while (dest.length() < str.length())
		{
			str = str.substring(dest.length());
			dest = substr(str, limitLength);
			list.add(dest);
		}

		return list;
	}

	public static String generateMd5(String value)
	{
		String retString = null;
		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] buffer = value.getBytes();
			md.update(buffer, 0, buffer.length);
			retString = EncryptUtils.toHexString(md.digest());
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		return retString;
	}

	/**
	 * 转义字符串
	 * 
	 * @return
	 */
	public static String formatEscapeString(String value)
	{
		if (value == null)
		{
			return null;
		}

		char content[] = new char[value.length()];
		value.getChars(0, value.length(), content, 0);
		StringBuffer result = new StringBuffer(content.length + 50);
		for (int i = 0; i < content.length; i++)
			switch (content[i])
			{
			case 60: // '<'
				result.append("&lt;");
				break;

			case 62: // '>'
				result.append("&gt;");
				break;

			case 38: // '&'
				result.append("&amp;");
				break;

			case 34: // '"'
				result.append("&quot;");
				break;

			case 39: // '\''
				result.append("&#39;");
				break;

			default:
				result.append(content[i]);
				break;
			}

		return result.toString();
	}
	
	/**
	 * 是否为null或者空(即长度为0的字符串)
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNull(final Object str)
	{
		return (str == null || "NULL".equalsIgnoreCase(str.toString()));
	}
	
	/**
	 * 替换字符串中的回车为空
	 * 
	 * @param value
	 * @return
	 */
	public static String subStringR(String value)
	{
		if (value == null)
		{
			return null;
		}
		if (value.contains("\r\n"))
		{
			value = value.replaceAll("\\r", "");
		}
		return value.toString();
	}

	public static String formatMarkStr(String value)
	{
		if (value == null)
		{
			return null;
		}

		char content[] = new char[value.length()];
		value.getChars(0, value.length(), content, 0);
		StringBuffer result = new StringBuffer(content.length + 50);
		for (int i = 0; i < content.length; i++)
			switch (content[i])
			{
			case 34: // '"'
				result.append("&quot;");
				break;
			default:
				result.append(content[i]);
				break;
			}

		return result.toString();
	}

	/**
	 * 解析公式,替换公式中的变量
	 * 
	 * @return
	 */
	public static String analyzeFormula(String formula, Map<String, Object> varValMap)
	{
		if (StringUtils.isNullString(formula))
		{
			return null;
		}

		if (SetUtils.isNullMap(varValMap))
		{
			return formula;
		}

		// 变量替换
		for (String var : varValMap.keySet())
		{
			Object val = varValMap.get(var);
			formula = formula.replaceAll(var, val == null ? "" : val.toString());
		}

		// 替换圆周率
		formula = formula.replaceAll("([1-9])π", "$1*" + PAI_DEFAULT_VAL);
		formula = formula.replace("π", PAI_DEFAULT_VAL);

		// 幂运算
		while (formula.contains("^"))
		{
			int index = formula.indexOf("^");
			int start = -1;
			int end = -1;

			// 底数
			String baseNumber = null;
			// 指数
			String exponent = null;

			// 往前取数值,直到运算符终止
			for (int i = index; i >= 0; i--)
			{
				if (i == 0)
				{
					break;
				}

				String s = formula.substring(i - 1, i);
				if (!NumberUtils.isNumeric(s))
				{
					start = i;
					baseNumber = formula.substring(i, index);
					break;
				}
			}

			// 往后取数值,直到运算符终止
			for (int i = index + 1; i < formula.length(); i++)
			{
				if (i == formula.length())
				{
					break;
				}

				String s = formula.substring(i, i + 1);
				if (!NumberUtils.isNumeric(s))
				{
					end = i;
					exponent = formula.substring(index + 1, i);
					break;
				}
			}

			// 幂运算在公式头部
			if (StringUtils.isNullString(baseNumber))
			{
				baseNumber = formula.substring(0, index);
			}

			// 幂运算在公式尾部
			if (StringUtils.isNullString(exponent))
			{
				exponent = formula.substring(index + 1);
			}

			String prefix = start == -1 ? "" : formula.substring(0, start);
			String subffix = end == -1 ? "" : formula.substring(end);

			formula = prefix + "Math.pow(" + baseNumber + "," + exponent + ")" + subffix;
		}

		return formula;
	}

	/**
	 * 检查公式是否正确
	 * 
	 * @return
	 */
	public static boolean formulaCheck(String formula)
	{
		// 剔除空白符
		formula = formula == null ? "" : formula.replace(" ", "");
		if (isNullString(formula))
		{
			return false;
		}

		// 替换π
		formula = formula.replace("π", PAI_DEFAULT_VAL);

		// 连续的"()"
		if (formula.contains("()"))
		{
			return false;
		}

		// 括号跟着幂运算
		if (formula.contains("(^") || formula.contains("^(") || formula.contains(")^") || formula.contains("^)"))
		{
			return false;
		}

		// 括号是否成对出现
		if (formula.replace("(", "").length() != formula.replace(")", "").length())
		{
			return false;
		}

		// 是否只包含允许的字符.+-*/(),数字,字母
		Pattern p = Pattern.compile("[^\\+\\-\\*\\/\\^\\(\\)\\.0-9a-zA-Z]");
		Matcher m = p.matcher(formula);
		if (m.find())
		{
			return false;
		}

		// 运算符连续
		p = Pattern.compile("[\\+\\-\\*\\/\\^]{2,}");
		m = p.matcher(formula);
		if (m.find())
		{
			return false;
		}

		// 左括号后面出现+-*/符号
		p = Pattern.compile("\\([\\+\\-\\*\\/]+");
		m = p.matcher(formula);
		if (m.find())
		{
			return false;
		}

		// 右括前后面出现+-*/符号
		p = Pattern.compile("[\\+\\-\\*\\/]\\)");
		m = p.matcher(formula);
		if (m.find())
		{
			return false;
		}

		// 右括号后面跟着数字和字母
		p = Pattern.compile("\\)[0-9a-zA-Z]+");
		m = p.matcher(formula);
		if (m.find())
		{
			return false;
		}

		// 左括号前面跟着数字和字母
		p = Pattern.compile("[0-9a-zA-Z]\\(");
		m = p.matcher(formula);
		if (m.find())
		{
			return false;
		}

		return true;
	}

	/**
	 * 把分号分割，等号连接的键值对转换为map
	 * 
	 * @param str
	 * @return
	 */
	public static Map<String, String> transferStrToMap(String str)
	{
		Map<String, String> map = new HashMap<String, String>();
		if (!StringUtils.isNullString(str))
		{
			if (str.contains(";"))
			{
				String[] tempArr = str.split(";");
				for (String s : tempArr)
				{
					if (s.contains("="))
					{
						String[] tempArr_ = s.split("=");
						map.put(tempArr_[0], tempArr_.length == 2 ? tempArr_[1] : null);
					}
				}
			}
			else if (str.contains("="))
			{
				String[] tempArr = str.split("=");
				map.put(tempArr[0], tempArr.length == 2 ? tempArr[1] : null);
			}
		}
		return map;
	}

	public static String genericGuid()
	{
		return StringUtils.generateRandomUID(32).toUpperCase();
	}
}
