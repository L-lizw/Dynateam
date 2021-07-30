package dyna.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dyna.common.bean.data.configparamter.ConfigVariable;
import dyna.common.bean.data.configparamter.DynamicOfColumn;
import dyna.common.bean.data.configparamter.TableOfInputVariable;
import dyna.common.bean.data.configparamter.TableOfParameter;
import dyna.common.exception.ServiceRequestException;

public class ConfigUtil
{
	/**
	 * 是否是指定表的图面变量
	 * 
	 * @param str
	 * @param table
	 * @return
	 */
	public static boolean isVariableOfTable(String str, String table)
	{
		if (StringUtils.isNullString(table))
		{
			return false;
		}
		return isDrawVariable(str) && str.endsWith(table);
	}

	/**
	 * 判断字符串是不是图面变量
	 * 
	 * @return
	 */
	public static boolean isVariable(String str, Map<String, String> inptMap)
	{
		if (inptMap.containsKey(str))
		{
			return true;
		}

		return isDrawVariable(str);
	}

	/**
	 * 判断字符串是不是图面变量
	 * 
	 * @return
	 */
	public static boolean isDrawVariable(String str)
	{
		if (StringUtils.isNullString(str))
		{
			return false;
		}

		String regex = "^[A-Z]{1}\\d{1,2}[ABCDEGRQPLF]$";
		Pattern p = Pattern.compile(regex);
		return p.matcher(str).find();
	}

	public static boolean isEndWithLetter(String value)
	{
		if (StringUtils.isNullString(value))
		{
			return false;
		}
		Pattern pattern = Pattern.compile("[a-zA-Z]+");
		Matcher m = pattern.matcher(value.subSequence(value.length() - 1, value.length()));
		return m.matches();
	}

	public static boolean isEndWithNumber(String value)
	{
		if (StringUtils.isNullString(value))
		{
			return false;
		}
		Pattern pattern = Pattern.compile("[0-9]+");
		Matcher m = pattern.matcher(value.subSequence(value.length() - 1, value.length()));
		return m.matches();
	}

	public static boolean isStartWithLetter(String value)
	{
		if (StringUtils.isNullString(value))
		{
			return false;
		}
		char[] char_ = value.toCharArray();
		return Character.isLetter(char_[0]);
	}

	/**
	 * 分割L变量,支持两种格式:1:A1AB12AB13AC4A
	 * 2:L01L11L23L45
	 * 
	 * @param lNumbers
	 * @return
	 */
	public static List<String> transferLNumberStrToList(String lNumbers)
	{
		if (StringUtils.isNullString(lNumbers))
		{
			return null;
		}

		char[] tmpArr = lNumbers.toUpperCase().toCharArray();
		List<String> lNumberList = new ArrayList<String>();

		// 以数字结尾,说明L变量都为确定的值,而不是变量组合
		if (ConfigUtil.isEndWithNumber(lNumbers))
		{
			boolean isEnd = false;
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i <= tmpArr.length; i++)
			{
				if (i == tmpArr.length)
				{
					lNumberList.add(buffer.toString());
				}
				else
				{
					if (Character.isLetter(tmpArr[i]) && buffer.length() > 0)
					{
						isEnd = true;
					}
					if (isEnd && buffer.length() > 0)
					{
						lNumberList.add(buffer.toString());
						buffer.setLength(0);
						isEnd = false;
					}

					buffer.append(tmpArr[i]);
				}
			}
		}
		else
		{
			// 说明L变量都为变量组合
			boolean isEnd = false;
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < tmpArr.length; i++)
			{
				buffer.append(tmpArr[i]);
				if (i == tmpArr.length - 1)
				{
					lNumberList.add(buffer.toString());
				}
				else
				{
					if (Character.isLetter(tmpArr[i]) && buffer.length() > 1)
					{
						isEnd = true;
					}
					if (isEnd && buffer.length() > 1)
					{
						lNumberList.add(buffer.toString());
						buffer.setLength(0);
						isEnd = false;
					}
				}
			}
		}
		return lNumberList;
	}
	
	public static String[] splitUnique(String uniqueNo)
	{
		String id = uniqueNo.substring(0, uniqueNo.indexOf("^"));

		uniqueNo = uniqueNo.substring(id.length() + 1);
		String gNumber = uniqueNo.substring(0, uniqueNo.indexOf("^"));

		uniqueNo = uniqueNo.substring(gNumber.length() + 1);
		String lNumbers = uniqueNo.substring(0, uniqueNo.indexOf("^"));

		String inptVarriables = uniqueNo.substring(lNumbers.length() + 1);

		String[] vals = { id, gNumber, lNumbers, inptVarriables };
		return vals;
	}

	public static Map<String, String> transferInputVarStrToMap(String inputVariables)
	{
		Map<String, String> inptMap = new HashMap<String, String>();
		if (!StringUtils.isNullString(inputVariables))
		{
			if (inputVariables.contains(";"))
			{
				String[] tmpInptVarArr = inputVariables.split(";");
				for (String tmpInptVar : tmpInptVarArr)
				{
					if (tmpInptVar.contains("="))
					{
						String[] tmpArr = tmpInptVar.split("=");
						inptMap.put(tmpArr[0], tmpArr.length == 2 ? tmpArr[1] : null);
					}
					else if (tmpInptVar.contains("@"))
					{
						inptMap.put(tmpInptVar.substring(1), tmpInptVar);
					}
					else
					{
						inptMap.put(tmpInptVar, null);
					}
				}
			}
			else
			{
				if (inputVariables.contains("="))
				{
					String[] tmpArr = inputVariables.split("=");
					inptMap.put(tmpArr[0], tmpArr.length == 2 ? tmpArr[1] : null);
				}
				else if (inputVariables.contains("@"))
				{
					inptMap.put(inputVariables.substring(1), inputVariables);
				}
				else
				{
					inptMap.put(inputVariables, null);
				}
			}
		}
		return inptMap;
	}

	public static String resetInputVariables(ConfigVariable configVariable, String gNumber, String inputVariables) throws ServiceRequestException
	{
		// 输入变量为空,根据P表补充输入变量
		if (StringUtils.isNullString(inputVariables))
		{
			StringBuffer buffer = new StringBuffer();
			TableOfParameter parameter = configVariable.getParameter(gNumber);
			if (parameter != null)
			{
				List<DynamicOfColumn> columns = parameter.getColumns();
				if (!SetUtils.isNullList(columns))
				{
					for (DynamicOfColumn column : columns)
					{
						if (StringUtils.isNullString(column.getValue()))
						{
							continue;
						}

						if (buffer.length() > 0)
						{
							buffer.append(";");
						}
						buffer.append(column.getValue() + "=");
					}
				}
			}
			return buffer.toString();
		}

		TableOfParameter parameter = configVariable.getParameter(gNumber);
		List<String> inputVarOfPList = new ArrayList<String>();
		if (parameter != null)
		{
			List<DynamicOfColumn> columns = parameter.getColumns();
			if (!SetUtils.isNullList(columns))
			{
				for (DynamicOfColumn column : columns)
				{
					if (StringUtils.isNullString(column.getValue()))
					{
						continue;
					}
					
					inputVarOfPList.add(column.getValue());
				}
			}
		}

		Map<String, String> inputVarMap = transferInputVarStrToMap(inputVariables);
		// 如果P表不为空,需要删除在P表中不存在的变量
		if (!SetUtils.isNullList(inputVarOfPList))
		{
			Iterator<String> iterator = inputVarMap.keySet().iterator();
			while (iterator.hasNext())
			{
				String var = iterator.next();
				if (!inputVarOfPList.contains(var))
				{
					iterator.remove();
				}
			}

			// 增加P表中有,但是输入变量没有的
			for (String var : inputVarOfPList)
			{
				if (!inputVarMap.containsKey(var))
				{
					inputVarMap.put(var, null);
				}
			}
		}

		StringBuffer buffer = new StringBuffer();
		if (!SetUtils.isNullList(configVariable.getInptVarList()))
		{
			for (TableOfInputVariable inputVar : configVariable.getInptVarList())
			{
				String varName = inputVar.getName();
				if (inputVarMap.containsKey(varName))
				{
					if (buffer.length() > 0)
					{
						buffer.append(";");
					}
					buffer.append(varName).append("=").append(StringUtils.convertNULLtoString(inputVarMap.get(varName)));
				}
			}
		}
		return buffer.toString();
	}

	public static String resetInputVariablesForOrder(ConfigVariable configVariable, String gNumber, String inputVariables) throws ServiceRequestException
	{
		TableOfParameter parameter = configVariable.getParameter(gNumber);
		List<String> inputVarOfPList = new ArrayList<String>();
		if (parameter != null)
		{
			List<DynamicOfColumn> columns = parameter.getColumns();
			if (!SetUtils.isNullList(columns))
			{
				for (DynamicOfColumn column : columns)
				{
					if (StringUtils.isNullString(column.getValue()))
					{
						continue;
					}
					
					inputVarOfPList.add(column.getValue());
				}
			}
		}

		Map<String, String> inputVarMap = transferInputVarStrToMap(inputVariables);
		// 如果P表不为空,需要删除在P表中不存在的变量
		if (!SetUtils.isNullList(inputVarOfPList))
		{
			Iterator<String> iterator = inputVarMap.keySet().iterator();
			while (iterator.hasNext())
			{
				String var = iterator.next();
				if (!inputVarOfPList.contains(var))
				{
					iterator.remove();
				}
			}
		}

		StringBuffer buffer = new StringBuffer();
		if (!SetUtils.isNullList(configVariable.getInptVarList()))
		{
			for (TableOfInputVariable inputVar : configVariable.getInptVarList())
			{
				String varName = inputVar.getName();
				if (inputVarMap.containsKey(varName))
				{
					if (buffer.length() > 0)
					{
						buffer.append(";");
					}
					buffer.append(varName).append("=").append(StringUtils.convertNULLtoString(inputVarMap.get(varName)));
				}
			}
		}
		return buffer.toString();
	}

	public static String resetLNumbers(String lNumbers)
	{
		if (StringUtils.isNullString(lNumbers))
		{
			return lNumbers;
		}
		List<String> lNumberList = ConfigUtil.transferLNumberStrToList(lNumbers);
		Collections.sort(lNumberList, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2)
			{
				String o1n = o1.substring(1);
				String o2n = o2.substring(1);

				return Integer.valueOf(o1n).compareTo(Integer.valueOf(o2n));
			}
		});

		StringBuffer lNumberBuffer = new StringBuffer();
		for (String lNumber : lNumberList)
		{
			lNumberBuffer.append(lNumber);
		}
		return lNumberBuffer.toString();
	}

	/**
	 * MAK字符串解析（格式如：B2,B1,1~2,12A,14~18）
	 * 
	 * @param makVal
	 * @return
	 */
	public static List<String> resolveMakStr(String makVal)
	{
		if (StringUtils.isNullString(makVal))
		{
			return null;
		}

		List<String> makKeyList = new ArrayList<String>();
		if (!StringUtils.isNullString(makVal))
		{
			if (makVal.contains(","))
			{
				String[] tmpArr = makVal.split(",");
				for (String s : tmpArr)
				{
					if (s.contains("~"))
					{
						makKeyList.addAll(getValFromOnlyWithVolna(s));
					}
					else
					{
						makKeyList.add(s);
					}
				}
			}
			else
			{
				if (makVal.contains("~"))
				{
					makKeyList.addAll(getValFromOnlyWithVolna(makVal));
				}
				else
				{
					makKeyList.add(makVal);
				}
			}
		}

		return makKeyList;
	}

	/**
	 * 根据波浪号分解字符串
	 * 
	 * @param val
	 * @param makValMap
	 * @return
	 */
	private static List<String> getValFromOnlyWithVolna(String val)
	{
		List<String> makKeyList = new ArrayList<String>();

		if (!val.contains("~"))
		{
			return makKeyList;
		}

		String[] tmpArr = val.split("~");
		if (Character.isLetter(tmpArr[0].toCharArray()[0]) && Character.isLetter(tmpArr[0].toCharArray()[1]) && tmpArr[0].toCharArray()[0] == tmpArr[0].toCharArray()[1])
		{
			String prefix = tmpArr[0].substring(0, 1);
			String fromStr = tmpArr[0].substring(1);
			String toStr = tmpArr[1].substring(1);
			if (fromStr.matches("[0-9]+") && toStr.matches("[0-9]+"))
			{
				Integer from = Integer.valueOf(fromStr);
				Integer to = Integer.valueOf(toStr);
				if (from > to)
				{
					for (int i = from; i <= to; i--)
					{
						makKeyList.add(prefix + i);
					}
				}
				else
				{
					for (int i = from; i <= to; i++)
					{
						makKeyList.add(prefix + i);
					}
				}
			}
		}
		else if (tmpArr[0].matches("[0-9]+") && tmpArr[1].matches("[0-9]+"))
		{
			Integer from = Integer.valueOf(tmpArr[0]);
			Integer to = Integer.valueOf(tmpArr[1]);
			if (from > to)
			{
				for (int i = from; i <= to; i--)
				{
					makKeyList.add(String.valueOf(i));
				}
			}
			else
			{
				for (int i = from; i <= to; i++)
				{
					makKeyList.add(String.valueOf(i));
				}
			}
		}

		return makKeyList;
	}
}
