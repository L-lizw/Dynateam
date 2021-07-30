/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DataKeyModel
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
@Root(name = "key")
public class KeyModel extends CrossModel
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -1423575437566565050L;

	@Attribute(name = "name", required = false)
	private String				name				= null;

	@Text(required = false)
	private String				value				= null;

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return this.name;
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
