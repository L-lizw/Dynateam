/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: FolderAcl
 * zhw 2012-6-7
 */
package dyna.common.bean.data.acl;

import java.util.HashMap;
import java.util.Map;

import dyna.common.systemenum.ShareFolderAuthorityEnum;
import dyna.common.util.StringUtils;

/**
 * @author zhw
 *
 */
public class ShareFolderACL
{

	private Map<ShareFolderAuthorityEnum, Boolean>	shareFolderAclMap	= null;

	public ShareFolderACL()
	{

	}

	/**
	 * 传入参数的格式为：2;2;2
	 * 个数和AuthorityEnum中枚举个数有关
	 * 
	 * @param aclValue
	 */
	public void setShareFoldeAcl(String aclValue)
	{
		String[] values = StringUtils.splitString(aclValue);
		int aclLength = ShareFolderAuthorityEnum.values().length;
		if (values == null || values.length != aclLength)
		{
			return;
		}
		if (shareFolderAclMap == null)
		{
			shareFolderAclMap = new HashMap<ShareFolderAuthorityEnum, Boolean>();
		}
		for (int i = 0; i < ShareFolderAuthorityEnum.values().length; i++)
		{
			String aclStr = values[i];
			if (!StringUtils.isNullString(aclStr) && aclStr.equals("2"))
			{
				shareFolderAclMap.put(ShareFolderAuthorityEnum.values()[i], false);
			}
			else
			{
				shareFolderAclMap.put(ShareFolderAuthorityEnum.values()[i], true);
			}
		}
	}

	public boolean hasShareFoldeAcl(ShareFolderAuthorityEnum ae)
	{
		if (shareFolderAclMap == null)
		{
			return false;
		}
		Boolean hasACL = shareFolderAclMap.get(ae);
		if (hasACL == null)
		{
			return false;
		}
		return hasACL;
	}

}
