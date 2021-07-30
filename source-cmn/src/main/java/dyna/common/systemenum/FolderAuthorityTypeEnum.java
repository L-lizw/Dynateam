/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: AccessTypeEnum
 * Wanglei 2010-7-30
 */
package dyna.common.systemenum;

/**
 * @author Wanglei
 *
 */
public enum FolderAuthorityTypeEnum
{

	FOLDER("folder"), // 文件夹
	FOLDEROBJECT("folderobject"), // 文件夹对象
	SHAREFOLDER("sharefolder");// 共享文件夹

	private final String	type;

	private FolderAuthorityTypeEnum(String type)
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
