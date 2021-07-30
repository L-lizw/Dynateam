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
public enum RuleAuthEnum
{
	EDITBOMHASCHECKOUT("X"), // 对BOM进行编辑必须拥有对象的检出权限
	EDITBOMNOTCHECKOUT("Y");// 对bom进行编辑可以没有item的检出权限
	private final String	type;

	private RuleAuthEnum(String type)
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
