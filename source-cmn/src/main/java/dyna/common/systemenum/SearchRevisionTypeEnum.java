/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SearchRevisionTypeEnum
 * duanll 2013-7-18
 */
package dyna.common.systemenum;

/**
 * @author duanll
 *         高级检索用检索版本类型：仅查询最新版本，查询历史版本，查询最新发布版本，查询最新版本为发布版本
 */
public enum SearchRevisionTypeEnum
{
	ISLATESTONLY("1"), ISHISTORYREVISION("2"), ISLATESTRLSONLY("3"), ISLATESTRLSWIP("4"), ;

	private String	type;

	private SearchRevisionTypeEnum(String type)
	{
		this.type = type;
	}

	public String getType()
	{
		return this.type;
	}

	public static SearchRevisionTypeEnum typeValueOf(String type)
	{
		if (type == null)
		{
			return ISLATESTONLY;
		}

		for (SearchRevisionTypeEnum searchRevisionTypeEnum : SearchRevisionTypeEnum.values())
		{
			if (type.equals(searchRevisionTypeEnum.getType()))
			{
				return searchRevisionTypeEnum;
			}
		}

		return ISLATESTONLY;
	}
}
