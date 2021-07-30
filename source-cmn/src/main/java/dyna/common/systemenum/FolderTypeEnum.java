/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: LocationTypeEnum
 * caogc 2010-12-08
 */
package dyna.common.systemenum;

/**
 * Location类型枚举
 * 
 * @author caogc
 * 
 */
public enum FolderTypeEnum
{
	PRIVATE("1"), LIB_FOLDER("2"), LIBRARY("4");

	public static FolderTypeEnum typeValueOf(String type)
	{
		for (FolderTypeEnum locationTypeEnum : FolderTypeEnum.values())
		{
			if (type.equals(locationTypeEnum.toString()))
			{
				return locationTypeEnum;
			}
		}

		return null;
	}

	private String type = null;

	private FolderTypeEnum(String type)
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

	public int toValue()
	{
		return Integer.parseInt(this.type);
	}
}
