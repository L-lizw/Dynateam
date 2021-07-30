/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: authority Enum constants
 * xiasheng May 7, 2010
 */
package dyna.common.systemenum;

import java.util.HashMap;
import java.util.Map;

import dyna.common.dto.acl.ACLItem;

/**
 * 系统权限枚举
 * 
 * @author xiasheng
 */
public enum AuthorityEnum
{
	/**
	 * 查询
	 */
	READ("ID_SYS_ACL_READ", ACLItem.OPER_SELECT, "view.png", 0),

	/**
	 * 新建
	 */
	CREATE("ID_SYS_ACL_CREATE", ACLItem.OPER_INSERT, "new.png", 1),

	/**
	 * 修订
	 */
	REVISE("ID_SYS_ACL_REVISE", ACLItem.OPER_REVISE, "revise.png", 2),

	/**
	 * 删除
	 */
	DELETE("ID_SYS_ACL_DELETE", ACLItem.OPER_DELETE, "delete.png", 3),

	/**
	 * 生效
	 */
	EFFECTIVE("ID_SYS_ACL_EFFECTIVE", ACLItem.OPER_EFFECTIVE, "effective.png", 4),

	/**
	 * 作废
	 */
	OBSOLETE("ID_SYS_ACL_OBSOLETE", ACLItem.OPER_OBSOLETE, "obsolete.png", 5),

	/**
	 * 检出
	 */
	CHECKOUT("ID_SYS_ACL_CHECKOUT", ACLItem.OPER_CHECKOUT, "checkout.png", 6),

	/**
	 * 发布(release)
	 */
	RELEASE("ID_SYS_ACL_RELEASE", ACLItem.OPER_RLS, "release.png", 7),

	/**
	 * 取消组检出
	 */
	CANCELCHECKOUT("ID_SYS_ACL_CANCELCHECKOUT", ACLItem.OPER_CANCELCHKOUT, "cancel_checkout.png", 8),

	/**
	 * 更改id
	 */
	CHANGID("ID_SYS_ACL_CHANGIDE", ACLItem.OPER_CHGID, "changeid.png", 9),

	/**
	 * 更改主名称
	 */
	CHANGEMASTERNAME("ID_SYS_ACL_CHANGEMASTERNAME", ACLItem.OPER_CHANGEMASTERNAME, "changemastername.png", 10),

	/**
	 * 更改所有者
	 */
	CHANGOWNER("ID_SYS_ACL_CHANGOWNER", ACLItem.OPER_CHGOWNER, "changeowner.png", 11),

	/**
	 * 预览文件
	 */
	PREVIEW("ID_SYS_ACL_PREVIEW", ACLItem.OPER_PREVIEW_FILE, "preview.png", 12),

	/**
	 * 查看文件
	 */
	VIEWFILE("ID_SYS_ACL_VIEWFILE", ACLItem.OPER_VIEWFILE, "viewfile.png", 13),

	/**
	 * 上传文件
	 */
	ADDFILE("ID_SYS_ACL_ADDFILE", ACLItem.OPER_ADDFILE, "upload.png", 14),

	/**
	 * 下载文件
	 */
	DOWNLOADFILE("ID_SYS_ACL_DOWNLOADFILE", ACLItem.OPER_DOWNLOADFILE, "download.png", 15),

	/**
	 * 管理(编辑)文件
	 */
	EDITFILE("ID_SYS_ACL_EDITFILE", ACLItem.OPER_EDITFILE, "file_edit.png", 16),

	/**
	 * 删除文件
	 */
	DELETEFILE("ID_SYS_ACL_DELETEFILE", ACLItem.OPER_DELETEFILE, "file_delete.png", 17),

	/**
	 * 添加子阶（end2）
	 */
	LINK("ID_SYS_ACL_LINK", ACLItem.OPER_LINK, "link_add.png", 18),

	/**
	 * 移除子阶（end2）
	 */
	UNLINK("ID_SYS_ACL_UNLINK", ACLItem.OPER_UNLINK, "link_delete.png", 19),

	/**
	 * 编辑结构（structure）
	 */
	EDITLINK("ID_SYS_ACL_EDITLINK", ACLItem.OPER_EDITLINK, "link_edit.png", 20),

	/**
	 * 导入
	 */
	IMPORT("ID_SYS_ACL_IMPORT", ACLItem.OPER_IMPORT, "import.png", 21),

	/**
	 * 导出
	 */
	EXPORT("ID_SYS_ACL_EXPORT", ACLItem.OPER_EXPORT, "export.png", 22),

	/**
	 * 重新使用
	 */
	UNOBSOLETE("ID_SYS_ACL_RERELEASE", ACLItem.OPER_UNOBSOLETE, "accept.png", 23);

	private String								msrId				= null;
	private String								dbKey				= null;
	private String								picName				= null;
	private int									position			= -1;
	public static Map<Integer, AuthorityEnum>	authorityEnumMap	= new HashMap<Integer, AuthorityEnum>();

	static
	{
		for (int i = 0; i < AuthorityEnum.values().length; i++)
		{
			authorityEnumMap.put(AuthorityEnum.values()[i].getPosition(), AuthorityEnum.values()[i]);
		}
	}

	private AuthorityEnum(String msrId, String dbKey, String picName, int position)
	{
		this.msrId = msrId;
		this.dbKey = dbKey;
		this.picName = picName;
		this.position = position;
	}

	public String getMsrId()
	{
		return this.msrId;
	}

	public String getDbKey()
	{
		return this.dbKey;
	}

	public String getPicName()
	{
		return this.picName;
	}

	public int getPosition()
	{
		return this.position;
	}

}
