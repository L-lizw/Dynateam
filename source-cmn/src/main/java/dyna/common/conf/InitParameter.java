/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: InitParameter
 * Wanglei 2010-4-13
 */
package dyna.common.conf;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * 参数初始化
 * 
 * @author Wanglei
 * 
 */
public class InitParameter
{

	private final Hashtable<String, String>	params	= new Hashtable<String, String>();

	public void setInitParameter(String name, String value)
	{
		this.params.put(name, value);
	}

	public String getInitParameter(String name)
	{
		return this.params.get(name);
	}

	public Enumeration<String> getParameters()
	{
		return this.params.elements();
	}
}
