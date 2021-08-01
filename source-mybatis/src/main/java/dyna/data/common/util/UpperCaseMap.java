/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 不区分大小写key的Map（都UpperCase)
 * xiasheng 2010-7-7
 */
package dyna.data.common.util;

import java.util.HashMap;


public class UpperCaseMap extends HashMap<String, String>
{
	private static final long	serialVersionUID	= 7634361077797248782L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#get(java.lang.Object)
	 */
	@Override
	public String get(Object key)
	{
		return super.get(key.toString().toUpperCase());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public String put(String key, String value)
	{
		if (value != null)
		{
			value = value.toUpperCase();
		}

		return super.put(key.toUpperCase(), value);
	}

	@Override
	public boolean containsKey(Object key)
	{
		return super.containsKey(key.toString().toUpperCase());
	}
}
