/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Utils 工具类, 提供与集合相关的操作
 * Wanglei 2010-7-23
 */
package dyna.common.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 工具类, 提供与集合相关的操作
 * 
 * @author Wanglei
 * 
 */
public class SetUtils
{

	/**
	 * 判断list是否为null或者不包含任何元素(即size为0)
	 * 
	 * @param list
	 * @return
	 */
	public static boolean isNullList(List<?> list)
	{
		return list == null || list.isEmpty() ? true : false;
	}

	/**
	 * 判断set是否为null或者不包含任何元素(即size为0)
	 * 
	 * @param list
	 * @return
	 */
	public static boolean isNullSet(Set<?> set)
	{
		return set == null || set.isEmpty() ? true : false;
	}

	/**
	 * 判断map是否为null或者不包含任何元素(即size为0)
	 * 
	 * @param map
	 * @return
	 */
	public static boolean isNullMap(Map<?, ?> map)
	{
		return map == null || map.isEmpty() ? true : false;
	}

}
