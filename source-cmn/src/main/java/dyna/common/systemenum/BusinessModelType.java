/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: BusinessModelType
 * WangLHB Jun 17, 2011
 */
package dyna.common.systemenum;

/**
 * @author WangLHB
 *
 */
public enum BusinessModelType
{
	SHARED_MODEL("SharedModel");

	private final String	name;

	BusinessModelType(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return this.name;
	}

}
