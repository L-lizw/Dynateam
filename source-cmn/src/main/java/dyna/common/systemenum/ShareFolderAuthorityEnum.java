/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ShareFolderAuthorityEnum
 * zhanghj 2011-9-2
 */
package dyna.common.systemenum;

import dyna.common.dto.acl.ShareFolderACLItem;

/**
 * 共享文件夹权限
 * 
 * @author zhanghj
 * 
 */
public enum ShareFolderAuthorityEnum
{
	/**
	 * 读
	 */
	READ("ID_SHAREFOLDER_ACL_READ", ShareFolderACLItem.OPER_SELECT, "view.png"),
	/**
	 * 添加
	 */
	ADD("ID_SHAREFOLDER_ACL_ADD", ShareFolderACLItem.OPER_ADD, "add.png"),
	/**
	 * 删除
	 */
	DELETE("ID_SHAREFOLDER_ACL_DELETE", ShareFolderACLItem.OPER_REMOVE, "delete.png");

	private String	msrId	= null;
	private String	dbKey	= null;
	private String	picName	= null;

	private ShareFolderAuthorityEnum(String msrId, String dbKey, String picName)
	{
		this.msrId = msrId;
		this.dbKey = dbKey;
		this.picName = picName;
	}

	public String getDbKey()
	{
		return this.dbKey;
	}

	public String getMsrId()
	{
		return this.msrId;
	}

	public String getPicName()
	{
		return this.picName;
	}
}
