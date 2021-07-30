package dyna.common.dto.acl;

import dyna.common.annotation.Cache;
import dyna.common.systemenum.PermissibleEnum;
import dyna.common.systemenum.ShareFolderAuthorityEnum;

@Cache
public class ShareFolderACLItem extends AbstractACLItem
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -4667041765641719579L;

	public static final String	FOLDER_GUID			= "FOLDERGUID";

	public static final String	FOLDER_NAME			= "FOLDERNAME";

	public static final String	OPER_SELECT			= "OPERSELECT";			// 查询权限

	public static final String	OPER_ADD			= "OPERADD";			// 添加

	public static final String	OPER_REMOVE			= "OPERREMOVE";			// 删除

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

	public String getOperAdd()
	{
		return (String) this.get(OPER_ADD);
	}

	public void setOperAdd(String operAdd)
	{
		this.put(OPER_ADD, operAdd);
	}

	public String getOperRemove()
	{
		return (String) this.get(OPER_REMOVE);
	}

	public void setOperRemove(String operRemove)
	{
		this.put(OPER_REMOVE, operRemove);
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

	public void setFolderPermision(ShareFolderAuthorityEnum folderAuthority, PermissibleEnum permission)
	{
		this.put(folderAuthority.getDbKey(), permission == null ? null : permission.getPriority());
	}
}
