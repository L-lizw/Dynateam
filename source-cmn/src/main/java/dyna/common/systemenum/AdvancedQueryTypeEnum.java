/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: AdvancedQueryTypeEnum
 * duanll 2014-2-18
 */
package dyna.common.systemenum;

import dyna.common.util.StringUtils;

/**
 * 高级查询的查询类型
 * 
 * @author duanll
 * 
 */
public enum AdvancedQueryTypeEnum
{
	// 快速
	QUICKLY("1"),

	// 一般
	NORMAL("2"),

	// 关联
	RELATED("3"),

	// 分类
	CLASSIFICATION("4"), ;

	private String	searchType;

	private AdvancedQueryTypeEnum(String searchType)
	{
		this.searchType = searchType;
	}

	public String getType()
	{
		return this.searchType;
	}
	
	public static AdvancedQueryTypeEnum typeof(String type)
	{
		if(StringUtils.isNullString(type))
		{
			return AdvancedQueryTypeEnum.NORMAL;
		}
		
		for(AdvancedQueryTypeEnum typeEnum : AdvancedQueryTypeEnum.values())
		{
			if(type.equals(typeEnum.getType()))
			{
				return typeEnum;
			}
		}
		
		return AdvancedQueryTypeEnum.NORMAL;
	}
}
