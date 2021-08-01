package dyna.common.dto.acl;

import dyna.common.annotation.Cache;
import dyna.common.annotation.EntryMapper;
import dyna.common.dtomapper.acl.FolderACLItemMapper;
import dyna.common.systemenum.FolderAuthorityEnum;
import dyna.common.systemenum.PermissibleEnum;
import dyna.common.util.BooleanUtils;
import dyna.common.util.NumberUtils;
import dyna.common.util.StringUtils;

@Cache
@EntryMapper(FolderACLItemMapper.class)
public class FolderACLItem extends AbstractACLItem
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -3210094739577268312L;

	public static final String	FOLDER_GUID			= "FOLDERGUID";

	public static final String	FOLDER_NAME			= "FOLDERNAME";

	public static final String	OPER_READ			= "OPERREAD";
	public static final String	OPER_CREATE			= "OPERCREATE";
	public static final String	OPER_DELETE			= "OPERDELETE";
	public static final String	OPER_RENAME			= "OPERRENAME";
	public static final String	OPER_ADDREF			= "OPERADDREF";
	public static final String	OPER_DELREF			= "OPERDELREF";
	public static final String	IS_EXTEND			= "ISEXTEND";

	public static final String	PARENT_OPER_READ	= "PARENTOPERREAD";
	public static final String	PARENT_OPER_CREATE	= "PARENTOPERCREATE";
	public static final String	PARENT_OPER_DELETE	= "PARENTOPERDELETE";
	public static final String	PARENT_OPERRE_NAME	= "PARENTOPERRENAME";
	public static final String	PARENT_OPER_ADDREF	= "PARENTOPERADDREF";
	public static final String	PARENT_OPER_DELREF	= "PARENTOPERDELREF";

	public FolderACLItem()
	{
		super();
	}

	public FolderACLItem(PermissibleEnum allAuthority)
	{
		super();
		this.setFolderPermision(FolderAuthorityEnum.READ, allAuthority);
		this.setFolderPermision(FolderAuthorityEnum.CREATE, allAuthority);
		this.setFolderPermision(FolderAuthorityEnum.DELETE, allAuthority);
		this.setFolderPermision(FolderAuthorityEnum.RENAME, allAuthority);
		this.setFolderPermision(FolderAuthorityEnum.ADDREF, allAuthority);
		this.setFolderPermision(FolderAuthorityEnum.DELREF, allAuthority);
	}

	public FolderACLItem(String authority)
	{
		super();
		String[] values = StringUtils.splitString(authority);
		if (values == null || values.length != FolderAuthorityEnum.values().length)
		{
			return;
		}
		this.setFolderPermision(FolderAuthorityEnum.READ, PermissibleEnum.getPermissibleEnum(NumberUtils.getIneger(values[0])));
		this.setFolderPermision(FolderAuthorityEnum.CREATE, PermissibleEnum.getPermissibleEnum(NumberUtils.getIneger(values[1])));
		this.setFolderPermision(FolderAuthorityEnum.DELETE, PermissibleEnum.getPermissibleEnum(NumberUtils.getIneger(values[2])));
		this.setFolderPermision(FolderAuthorityEnum.RENAME, PermissibleEnum.getPermissibleEnum(NumberUtils.getIneger(values[3])));
		this.setFolderPermision(FolderAuthorityEnum.ADDREF, PermissibleEnum.getPermissibleEnum(NumberUtils.getIneger(values[4])));
		this.setFolderPermision(FolderAuthorityEnum.DELREF, PermissibleEnum.getPermissibleEnum(NumberUtils.getIneger(values[5])));
	}

	public String getOperRead()
	{
		return (String) this.get(OPER_READ);
	}

	public void setOperRead(String operRead)
	{
		this.put(OPER_READ, operRead);
	}

	public String getOperCreate()
	{
		return (String) this.get(OPER_CREATE);
	}

	public void setOperCreate(String operCreate)
	{
		this.put(OPER_CREATE, operCreate);
	}

	public String getOperDelete()
	{
		return (String) this.get(OPER_DELETE);
	}

	public void setOperDelete(String operDelete)
	{
		this.put(OPER_DELETE, operDelete);
	}

	public String getOperRename()
	{
		return (String) this.get(OPER_RENAME);
	}

	public void setOperRename(String operRename)
	{
		this.put(OPER_RENAME, operRename);
	}

	public String getOperAddref()
	{
		return (String) this.get(OPER_ADDREF);
	}

	public void setOperAddref(String operAddref)
	{
		this.put(OPER_ADDREF, operAddref);
	}

	public String getOperDelref()
	{
		return (String) this.get(OPER_DELREF);
	}

	public void setOperDelref(String operDelref)
	{
		this.put(OPER_DELREF, operDelref);
	}

	/**
	 * @return the isExtend
	 */
	public boolean isExtend()
	{
		if (StringUtils.isNullString((String) this.get(IS_EXTEND)))
		{
			return false;
		}
		return BooleanUtils.getBooleanByYN((String) this.get(IS_EXTEND));

	}

	/**
	 * @param isExtend
	 *            the isExtend to set
	 */
	public void setIsExtend(boolean isExtend)
	{
		this.put(IS_EXTEND, BooleanUtils.getBooleanStringYN(isExtend));
	}

	public void setFolderPermision(FolderAuthorityEnum folderAuthority, PermissibleEnum permission)
	{
		this.put(folderAuthority.getDbKey(), permission == null ? null : permission.getPriority());
	}

	public String getFolderGuid()
	{
		return (String) this.get(FOLDER_GUID);
	}

	public void setFolderGuid(String folderGuid)
	{
		this.put(FOLDER_GUID, folderGuid);
	}

	public String getFolderName()
	{
		return (String) this.get(FOLDER_NAME);
	}

	public void setFolderName(String folderName)
	{
		this.put(FOLDER_NAME, folderName);
	}
}
