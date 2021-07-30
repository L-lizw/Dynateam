/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: AssociatedTypeEnum
 * duanll 2012-7-19
 */
package dyna.common.systemenum;

import dyna.common.util.StringUtils;

/**
 * @author duanll
 *         取/替代基本设置 主/元件关联
 * 
 */
public enum AssociatedTypeEnum
{
	// 主对象
	MASTER("1"),
	// 版本
	REVISION("2"), ;

	private String	type;

	private AssociatedTypeEnum(String type)
	{
		this.type = type;
	}

	public String getType()
	{
		return this.type;
	}

	public static AssociatedTypeEnum typeValueOf(String type)
	{
		if (StringUtils.isNullString(type))
		{
			return null;
		}
		for (AssociatedTypeEnum associatedType : AssociatedTypeEnum.values())
		{
			if (type.equals(associatedType.getType()))
			{
				return associatedType;
			}
		}
		return null;
	}
}
