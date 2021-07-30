/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: PublicSearchAuthorityEnum
 * ZhangHW 2012年2月6日
 */
package dyna.common.systemenum;

import dyna.common.dto.acl.PublicSearchACLItem;

/**
 * @author ZhangHW
 *
 */
public enum PublicSearchAuthorityEnum
{
	/**
	 * 读取
	 */
	READ("ID_PUBLICSEARCH_ACL_READ", PublicSearchACLItem.PUBLIC_OPER_READ, "view.png"),
	/**
	 * 修改
	 */
	UPDATE("ID_PUBLICSEARCH_ACL_UPDATE", PublicSearchACLItem.PUBLIC_OPER_UPDATE, "edit.png"),
	/**
	 * 删除
	 */
	DELETE("ID_PUBLICSEARCH_ACL_DELETE", PublicSearchACLItem.PUBLIC_OPER_DELETE, "delete.png");

	private String	msrId	= null;
	private String	dbKey	= null;
	private String	picName	= null;

	private PublicSearchAuthorityEnum(String msrId, String dbKey, String picName)
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
