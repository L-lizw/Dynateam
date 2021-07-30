/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: RuleReleaseEnum
 * zhanghj 2010-11-26
 */
package dyna.common.systemenum;

/**
 * @author zhanghj
 *
 */
public enum RuleReleaseEnum
{
	BOMABOUTITEM("B"), // 发布bom时关联发布item
	ITEMABOUTBOM("I");// 发布itme时关联发布bom
	private final String	type;

	private RuleReleaseEnum(String type)
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
