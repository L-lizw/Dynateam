/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SubProcessTypeEnum
 * WangLHB 2011-4-19
 */
package dyna.common.systemenum;

/**
 * @author WangLHB
 *
 */
public enum SubProcessTypeEnum
{
	AUTOBLOCK("AutoBlock"), //
	BLOCK("Block"), //
	UNBLOCK("UnBlock");

	private final String	title;

	private SubProcessTypeEnum(String title)
	{
		this.title = title;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString()
	{
		return this.title;
	}
}
