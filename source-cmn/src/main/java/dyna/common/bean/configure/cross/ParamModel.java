/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: CrossParam
 * WangLHB Sep 9, 2011
 */
package dyna.common.bean.configure.cross;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

/**
 * @author WangLHB
 * 
 */
@Root(name = "param")
public class ParamModel extends CrossModel
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 2831520039511642535L;

	@Attribute(name = "key", required = false)
	private String				key					= null;

	@Attribute(name = "type", required = false)
	private String				type				= null;

	@Text(required = false)
	private String				value				= null;

	/**
	 * @return the key
	 */
	public String getKey()
	{
		return this.key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(String key)
	{
		this.key = key;
	}

	/**
	 * @return the type
	 */
	public String getType()
	{
		return this.type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value)
	{
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public String getValue()
	{
		return this.value;
	}
}
