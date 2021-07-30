/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Tool
 * WangLHB Jul 13, 2011
 */
package dyna.common.bean.model;

import java.io.Serializable;

import org.simpleframework.xml.Attribute;

/**
 * @author WangLHB
 *
 */
class Tool implements Cloneable, Serializable
{
	private static final long	serialVersionUID	= -6784056078591858755L;

	@Attribute(name = "name", required = false)
	private String				name				= null;

	@Attribute(name = "type", required = false)
	private String				type				= null;

	@Attribute(name = "uitype", required = false)
	private String				uiType				= null;

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 * @return the type
	 */
	public String getType()
	{
		return this.type;
	}

	/**
	 * @param uitype
	 *            the uitype to set
	 */
	public void setUiType(String uiType)
	{
		this.uiType = uiType;
	}

	/**
	 * @return the uitype
	 */
	public String getUiType()
	{
		return this.uiType;
	}

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
}