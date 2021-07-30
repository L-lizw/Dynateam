/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: RuleAuthEnum
 * zhanghj 2010-11-26
 */
package dyna.common.systemenum;

/**
 * @author zhanghj
 *
 */
public enum YesOrNoEnum
{
	YES("Y"), //
	NO("N");//
	private final String	type;

	private YesOrNoEnum(String type)
	{
		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString()
	{
		return this.type;
	}

}
