/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: folder authority Enum constants
 * caogc January 19, 2011
 */
package dyna.common.systemenum;

import dyna.common.dto.acl.FolderACLItem;

/**
 * 文件夾权限枚举
 * 
 * @author caogc
 */
public enum FolderAuthorityEnum
{
	/**
	 * 读
	 */
	READ("ID_FOLDER_ACL_READ", FolderACLItem.OPER_READ, "view.png"),
	/**
	 * 创建
	 */
	CREATE("ID_FOLDER_ACL_CREATE", FolderACLItem.OPER_CREATE, "new.png"),
	/**
	 * 删除
	 */
	DELETE("ID_FOLDER_ACL_DELETE", FolderACLItem.OPER_DELETE, "delete.png"),
	/**
	 * 重命名
	 */
	RENAME("ID_FOLDER_ACL_RENAME", FolderACLItem.OPER_RENAME, "edit.png"),
	/**
	 * 添加关系
	 */
	ADDREF("ID_FOLDER_ACL_ADDREF", FolderACLItem.OPER_ADDREF, "add.png"),
	/**
	 * 删除关系
	 */
	DELREF("ID_FOLDER_ACL_DELREF", FolderACLItem.OPER_DELREF, "remove.png");

	private String	msrId	= null;
	private String	dbKey	= null;
	private String	picName	= null;

	private FolderAuthorityEnum(String msrId, String dbKey, String picName)
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
