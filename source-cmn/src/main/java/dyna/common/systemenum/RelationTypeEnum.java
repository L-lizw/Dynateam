/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Relation类型枚举
 * Jiagang 2010-7-13
 */
package dyna.common.systemenum;

/**
 * Relation类型枚举
 * 
 * @author Jiagang
 * 
 */
public enum RelationTypeEnum
{
	ASSOCIATION("Association", "2"), COMPOSITION("Composition", "0"), AGGREGATION("Aggregation", "1"), USE("Use", "3");

	private final String	type;
	private String			value;
	@Override
	public String toString()
	{
		return this.type;
	}

	private RelationTypeEnum(String type, String value)
	{
		this.type = type;
		this.value = value;
	}

	public String getValue()
	{
		return this.value;
	}
}
