/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Implementation
 * WangLHB Jul 13, 2011
 */
package dyna.common.bean.model;

import java.io.Serializable;

/**
 * @author WangLHB
 *
 */
public class Implementation implements Cloneable, Serializable
{
	private static final long	serialVersionUID	= -2754389601942366975L;

	private Tool				tool				= null;

	public Implementation(String toolType)
	{
		this.tool = new Tool();
		this.tool.setType(toolType);
	}

	public Implementation(String toolType, String toolName)
	{
		this(toolType);
		this.tool.setName(toolName);
	}

	public Implementation(String toolType, String toolName, String toolUIType)
	{
		this(toolType, toolName);
		this.tool.setUiType(toolUIType);
	}

	public String getUIType()
	{
		return this.tool.getUiType();
	}

	public void setUIType(String uiType)
	{
		this.tool.setUiType(uiType);
	}
}
