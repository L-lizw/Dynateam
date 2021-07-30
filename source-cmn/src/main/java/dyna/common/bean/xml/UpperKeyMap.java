/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 不区分大小写key的Map（都UpperCase)
 * xiasheng 2010-7-7
 */
package dyna.common.bean.xml;

import java.util.HashMap;

public class UpperKeyMap<T> extends HashMap<String, T>
{
	private static final long serialVersionUID = 7634361077797248782L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#get(java.lang.Object)
	 */
	@Override
	public T get(Object key)
	{
		return super.get(key.toString().toUpperCase());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public T put(String key, T value)
	{
		return super.put(key.toUpperCase(), value);
	}

}
