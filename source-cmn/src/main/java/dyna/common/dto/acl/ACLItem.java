/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ACLItem
 * Wanglei 2010-7-30
 */
package dyna.common.dto.acl;

import dyna.common.annotation.Cache;
import dyna.common.annotation.EntryMapper;
import dyna.common.dtomapper.acl.ACLItemMapper;
import dyna.common.systemenum.AuthorityEnum;
import dyna.common.systemenum.FolderAuthorityEnum;
import dyna.common.systemenum.PermissibleEnum;
import dyna.common.util.NumberUtils;
import dyna.common.util.StringUtils;

/**
 * @author Wanglei
 * 
 */
@Cache
@EntryMapper(ACLItemMapper.class)
public class ACLItem extends AbstractACLItem
{

	private static final long	serialVersionUID		= -5256245302334123561L;

	// 系统权限
	public static final String	OPER_SELECT				= "OPERSELECT";			// 查询权限
	public static final String	OPER_INSERT				= "OPERINSERT";			// 创建权限
	public static final String	OPER_REVISE				= "OPERREVISE";			// 修订权限
	public static final String	OPER_DELETE				= "OPERDELETE";			// 删除权限
	public static final String	OPER_EFFECTIVE			= "OPEREFFECTIVE";			// 生效

	public static final String	OPER_OBSOLETE			= "OPEROBSOLETE";			// 废弃权限
	public static final String	OPER_RLS				= "OPERRLS";				// 发布
	public static final String	OPER_UNOBSOLETE			= "OPERUNOBSOLETE";		// 重新使用
	public static final String	OPER_CHECKOUT			= "OPERCHECKOUT";			// 检出权限
	public static final String	OPER_CANCELCHKOUT		= "OPERCANCELCHKOUT";		// 取消组检出权限
	public static final String	OPER_CHGOWNER			= "OPERCHGOWNER";			// 更改所有者权限

	public static final String	OPER_CHGID				= "OPERCHGID";				// 更改ID权限
	public static final String	OPER_CHANGEMASTERNAME	= "OPERCHANGEMASTERNAME";	// 更改主名称权限
	public static final String	OPER_EXPORT				= "OPEREXPORT";			// 导出权限
	public static final String	OPER_IMPORT				= "OPERIMPORT";			// 导入权限
	public static final String	OPER_PREVIEW_FILE		= "OPERPREVIEWFILE";		// 预览文件

	public static final String	OPER_VIEWFILE			= "OPERVIEWFILE";			// 查看文件权限
	public static final String	OPER_DOWNLOADFILE		= "OPERDOWNLOADFILE";		// 下载文件权限
	public static final String	OPER_ADDFILE			= "OPERADDFILE";			// 上传文件
	public static final String	OPER_EDITFILE			= "OPEREDITFILE";			// 编辑文件权限
	public static final String	OPER_DELETEFILE			= "OPERDELETEFILE";		// 删除文件

	public static final String	OPER_LINK				= "OPERLINK";				// 添加子阶（end2）
	public static final String	OPER_EDITLINK			= "OPEREDITLINK";			// 编辑结构（structure）
	public static final String	OPER_UNLINK				= "OPERUNLINK";			// 移除子阶（end2）

	public ACLItem()
	{
		super();
	}

	public ACLItem(PermissibleEnum allAuthority)
	{
		super();
		this.setPermision(AuthorityEnum.READ, allAuthority);
		this.setPermision(AuthorityEnum.CREATE, allAuthority);
		this.setPermision(AuthorityEnum.REVISE, allAuthority);
		this.setPermision(AuthorityEnum.DELETE, allAuthority);
		this.setPermision(AuthorityEnum.EFFECTIVE, allAuthority);

		this.setPermision(AuthorityEnum.OBSOLETE, allAuthority);
		this.setPermision(AuthorityEnum.RELEASE, allAuthority);
		this.setPermision(AuthorityEnum.UNOBSOLETE, allAuthority);
		this.setPermision(AuthorityEnum.CHECKOUT, allAuthority);
		this.setPermision(AuthorityEnum.CANCELCHECKOUT, allAuthority);
		this.setPermision(AuthorityEnum.CHANGOWNER, allAuthority);

		this.setPermision(AuthorityEnum.EXPORT, allAuthority);
		this.setPermision(AuthorityEnum.IMPORT, allAuthority);
		this.setPermision(AuthorityEnum.PREVIEW, allAuthority);

		this.setPermision(AuthorityEnum.VIEWFILE, allAuthority);
		this.setPermision(AuthorityEnum.DOWNLOADFILE, allAuthority);
		this.setPermision(AuthorityEnum.ADDFILE, allAuthority);
		this.setPermision(AuthorityEnum.EDITFILE, allAuthority);
		this.setPermision(AuthorityEnum.DELETEFILE, allAuthority);

		this.setPermision(AuthorityEnum.LINK, allAuthority);
		this.setPermision(AuthorityEnum.EDITLINK, allAuthority);
		this.setPermision(AuthorityEnum.UNLINK, allAuthority);
	}

	public ACLItem(String authority)
	{
		super();
		String[] values = StringUtils.splitString(authority);
		if (values == null || values.length != AuthorityEnum.values().length)
		{
			return;
		}

		this.setPermision(AuthorityEnum.READ, PermissibleEnum.getPermissibleEnum(NumberUtils.getIneger(values[0])));
		this.setPermision(AuthorityEnum.CREATE, PermissibleEnum.getPermissibleEnum(NumberUtils.getIneger(values[1])));
		this.setPermision(AuthorityEnum.REVISE, PermissibleEnum.getPermissibleEnum(NumberUtils.getIneger(values[2])));
		this.setPermision(AuthorityEnum.DELETE, PermissibleEnum.getPermissibleEnum(NumberUtils.getIneger(values[3])));
		this.setPermision(AuthorityEnum.EFFECTIVE, PermissibleEnum.getPermissibleEnum(NumberUtils.getIneger(values[4])));

		this.setPermision(AuthorityEnum.OBSOLETE, PermissibleEnum.getPermissibleEnum(NumberUtils.getIneger(values[5])));
		this.setPermision(AuthorityEnum.RELEASE, PermissibleEnum.getPermissibleEnum(NumberUtils.getIneger(values[6])));
		this.setPermision(AuthorityEnum.CHECKOUT, PermissibleEnum.getPermissibleEnum(NumberUtils.getIneger(values[7])));
		this.setPermision(AuthorityEnum.CANCELCHECKOUT, PermissibleEnum.getPermissibleEnum(NumberUtils.getIneger(values[8])));
		this.setPermision(AuthorityEnum.CHANGOWNER, PermissibleEnum.getPermissibleEnum(NumberUtils.getIneger(values[9])));

		this.setPermision(AuthorityEnum.EXPORT, PermissibleEnum.getPermissibleEnum(NumberUtils.getIneger(values[12])));
		this.setPermision(AuthorityEnum.IMPORT, PermissibleEnum.getPermissibleEnum(NumberUtils.getIneger(values[13])));
		this.setPermision(AuthorityEnum.PREVIEW, PermissibleEnum.getPermissibleEnum(NumberUtils.getIneger(values[14])));

		this.setPermision(AuthorityEnum.VIEWFILE, PermissibleEnum.getPermissibleEnum(NumberUtils.getIneger(values[15])));
		this.setPermision(AuthorityEnum.DOWNLOADFILE, PermissibleEnum.getPermissibleEnum(NumberUtils.getIneger(values[16])));
		this.setPermision(AuthorityEnum.ADDFILE, PermissibleEnum.getPermissibleEnum(NumberUtils.getIneger(values[17])));
		this.setPermision(AuthorityEnum.EDITFILE, PermissibleEnum.getPermissibleEnum(NumberUtils.getIneger(values[18])));
		this.setPermision(AuthorityEnum.DELETEFILE, PermissibleEnum.getPermissibleEnum(NumberUtils.getIneger(values[19])));

		this.setPermision(AuthorityEnum.LINK, PermissibleEnum.getPermissibleEnum(NumberUtils.getIneger(values[20])));
		this.setPermision(AuthorityEnum.EDITLINK, PermissibleEnum.getPermissibleEnum(NumberUtils.getIneger(values[21])));
		this.setPermision(AuthorityEnum.UNLINK, PermissibleEnum.getPermissibleEnum(NumberUtils.getIneger(values[22])));
		this.setPermision(AuthorityEnum.UNOBSOLETE, PermissibleEnum.getPermissibleEnum(NumberUtils.getIneger(values[23])));
	}

	/**
	 * @return the operSelect
	 */
	public String getOperSelect()
	{
		return (String) this.get(OPER_SELECT);
	}

	public void setOperSelect(String operSelect)
	{
		this.put(OPER_SELECT, operSelect);
	}

	/**
	 * @return the operInsert
	 */
	public String getOperInsert()
	{
		return (String) this.get(OPER_INSERT);
	}

	public void setOperInsert(String operInsert)
	{
		this.put(OPER_INSERT, operInsert);
	}

	/**
	 * @return the operRevise
	 */
	public String getOperRevise()
	{
		return (String) this.get(OPER_REVISE);
	}

	public void setOperRevise(String operRevise)
	{
		this.put(OPER_REVISE, operRevise);
	}

	public String getOperDelete()
	{
		return (String) this.get(OPER_DELETE);
	}

	public void setOperDelete(String operDelete)
	{
		this.put(OPER_DELETE, operDelete);
	}

	public String getOperEffective()
	{
		return (String) this.get(OPER_EFFECTIVE);
	}

	public void setOperEffective(String operEffective)
	{
		this.put(OPER_EFFECTIVE, operEffective);
	}

	public String getOperObsolete()
	{
		return (String) this.get(OPER_OBSOLETE);
	}

	public void setOperObsolete(String operObsolete)
	{
		this.put(OPER_OBSOLETE, operObsolete);
	}

	public String getOperRelease()
	{
		return (String) this.get(OPER_RLS);
	}

	public void setOperRelease(String operRelease)
	{
		this.put(OPER_RLS, operRelease);
	}

	public String getOperUnobsolete()
	{
		return (String) this.get(OPER_UNOBSOLETE);
	}

	public void setOperUnbsolete(String operUnobsolete)
	{
		this.put(OPER_UNOBSOLETE, operUnobsolete);
	}

	public String getOperCheckout()
	{
		return (String) this.get(OPER_CHECKOUT);
	}

	public void setOperCheckout(String operCheckout)
	{
		this.put(OPER_CHECKOUT, operCheckout);
	}

	public String getOperCancelCheckout()
	{
		return (String) this.get(OPER_CANCELCHKOUT);
	}

	public void setOperCancelCheckout(String operCancelCheckout)
	{
		this.put(OPER_CANCELCHKOUT, operCancelCheckout);
	}

	public String getOperChangeOwner()
	{
		return (String) this.get(OPER_CHGOWNER);
	}

	public void setOperChangeOwner(String operChangeOwner)
	{
		this.put(OPER_CHGOWNER, operChangeOwner);
	}

	public String getOperExport()
	{
		return (String) this.get(OPER_EXPORT);
	}

	public void setOperExport(String operExport)
	{
		this.put(OPER_EXPORT, operExport);
	}

	public String getOperImport()
	{
		return (String) this.get(OPER_IMPORT);
	}

	public void setOperImport(String operImport)
	{
		this.put(OPER_IMPORT, operImport);
	}

	public String getOperPreviewFile()
	{
		return (String) this.get(OPER_PREVIEW_FILE);
	}

	public void setOperPreviewFile(String operPreviewFile)
	{
		this.put(OPER_PREVIEW_FILE, operPreviewFile);
	}

	public String getOperViewfile()
	{
		return (String) this.get(OPER_VIEWFILE);
	}

	public void setOperViewfile(String operViewfile)
	{
		this.put(OPER_VIEWFILE, operViewfile);
	}

	public String getOperDownloadFile()
	{
		return (String) this.get(OPER_DOWNLOADFILE);
	}

	public void setOperDownloadFile(String operDownloadFile)
	{
		this.put(OPER_DOWNLOADFILE, operDownloadFile);
	}

	public String getOperAddFile()
	{
		return (String) this.get(OPER_ADDFILE);
	}

	public void setOperAddFile(String operAddFile)
	{
		this.put(OPER_ADDFILE, operAddFile);
	}

	public String getOperEditFile()
	{
		return (String) this.get(OPER_EDITFILE);
	}

	public void setOperEditFile(String operEditFile)
	{
		this.put(OPER_EDITFILE, operEditFile);
	}

	/**
	 * 
	 * @return
	 */
	public String getOperDeleteFile()
	{
		return (String) this.get(OPER_DELETEFILE);
	}

	/**
	 * 
	 * @param operDeleteFile
	 */
	public void setOperDeleteFile(String operDeleteFile)
	{
		this.put(OPER_DELETEFILE, operDeleteFile);
	}

	/**
	 * 
	 * @return
	 */
	public String getOperLink()
	{
		return (String) this.get(OPER_LINK);
	}

	/**
	 * 
	 * @param operLink
	 */
	public void setOperLink(String operLink)
	{
		this.put(OPER_LINK, operLink);
	}

	/**
	 * 
	 * @return
	 */
	public String getOperEditLink()
	{
		return (String) this.get(OPER_EDITLINK);
	}

	/**
	 * 
	 * @param operEditLink
	 */
	public void setOperEditLink(String operEditLink)
	{
		this.put(OPER_EDITLINK, operEditLink);
	}

	/**
	 * 
	 * @return
	 */
	public String getOperUnLink()
	{
		return (String) this.get(OPER_UNLINK);
	}

	/**
	 * 
	 * @param operUnLink
	 */
	public void setOperUnLink(String operUnLink)
	{
		this.put(OPER_UNLINK, operUnLink);
	}

	public PermissibleEnum checkFolderPermission(FolderAuthorityEnum folderauthority)
	{
		String enable = (String) this.get(folderauthority.getDbKey());
		return PermissibleEnum.getPermissibleEnum(NumberUtils.getIneger(enable));
	}

	public PermissibleEnum checkPermission(AuthorityEnum authority)
	{
		String enable = (String) this.get(authority.getDbKey());
		return PermissibleEnum.getPermissibleEnum(NumberUtils.getIneger(enable));
	}

	private Boolean hasAuthority(AuthorityEnum authority)
	{
		return this.checkPermission(authority).isPermitted();
	}

	public Boolean isChangowner()
	{
		return this.hasAuthority(AuthorityEnum.CHANGOWNER);
	}

	/**
	 * @return the checkout
	 */
	public Boolean isCheckout()
	{
		return this.hasAuthority(AuthorityEnum.CHECKOUT);
	}

	/**
	 * @return the create
	 */
	public Boolean isCreate()
	{
		return this.hasAuthority(AuthorityEnum.CREATE);
	}

	/**
	 * @return the delete
	 */
	public Boolean isDelete()
	{
		return this.hasAuthority(AuthorityEnum.DELETE);
	}

	/**
	 * @return the demote
	 */
	public Boolean isRELEASE()
	{
		return this.hasAuthority(AuthorityEnum.RELEASE);
	}

	public Boolean isDownloadFile()
	{
		return this.hasAuthority(AuthorityEnum.DOWNLOADFILE);
	}

	public Boolean isEditFile()
	{
		return this.hasAuthority(AuthorityEnum.EDITFILE);
	}

	public Boolean isAddFile()
	{
		return this.hasAuthority(AuthorityEnum.ADDFILE);
	}

	public Boolean isDeleteFile()
	{
		return this.hasAuthority(AuthorityEnum.DELETEFILE);
	}

	public Boolean isLink()
	{
		return this.hasAuthority(AuthorityEnum.LINK);
	}

	public Boolean isUnlink()
	{
		return this.hasAuthority(AuthorityEnum.UNLINK);
	}

	public Boolean isEditLink()
	{
		return this.hasAuthority(AuthorityEnum.EDITLINK);
	}

	/**
	 * @return the exportable
	 */
	public Boolean isExportable()
	{
		return this.hasAuthority(AuthorityEnum.EXPORT);
	}

	/**
	 * @return the importable
	 */
	public Boolean isImportable()
	{
		return this.hasAuthority(AuthorityEnum.IMPORT);
	}

	/**
	 * @return the obsolete
	 */
	public Boolean isObsolete()
	{
		return this.hasAuthority(AuthorityEnum.OBSOLETE);
	}

	/**
	 * @return the read
	 */
	public Boolean isRead()
	{
		return this.hasAuthority(AuthorityEnum.READ);
	}

	/**
	 * @return the revice
	 */
	public Boolean isRevise()
	{
		return this.hasAuthority(AuthorityEnum.REVISE);
	}

	/**
	 * @return the transfercheckout
	 */
	public Boolean isCancleCheckout()
	{
		return this.hasAuthority(AuthorityEnum.CANCELCHECKOUT);
	}

	public Boolean isViewFile()
	{
		return this.hasAuthority(AuthorityEnum.VIEWFILE);
	}

	public Boolean isPreviewFile()
	{
		return this.hasAuthority(AuthorityEnum.PREVIEW);
	}

	public void setPermision(AuthorityEnum authority, PermissibleEnum permission)
	{
		this.put(authority.getDbKey(), permission == null ? null : permission.getPriority());
	}
}
