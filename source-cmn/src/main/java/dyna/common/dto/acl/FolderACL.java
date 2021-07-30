/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: FolderAcl
 * zhw 2012-6-7
 */
package dyna.common.dto.acl;

import java.util.HashMap;
import java.util.Map;

import dyna.common.systemenum.FolderAuthorityEnum;
import dyna.common.util.StringUtils;

/**
 * @author zhw
 *
 */
public class FolderACL
{

	private Map<FolderAuthorityEnum, Boolean>	folderAclMap	= null;

	public FolderACL()
	{

	}

	/**
	 * 传入参数的格式为：2;2;2
	 * 个数和AuthorityEnum中枚举个数有关
	 * 
	 * @param aclValue
	 */
	public void setFolderAcl(String aclValue)
	{
		String[] values = StringUtils.splitString(aclValue);
		int aclLength = FolderAuthorityEnum.values().length;
		if (values == null || values.length != aclLength)
		{
			return;
		}
		if (folderAclMap == null)
		{
			folderAclMap = new HashMap<FolderAuthorityEnum, Boolean>();
		}
		for (int i = 0; i < FolderAuthorityEnum.values().length; i++)
		{
			String aclStr = values[i];
			if (!StringUtils.isNullString(aclStr) && aclStr.equals("2"))
			{
				folderAclMap.put(FolderAuthorityEnum.values()[i], false);
			}
			else
			{
				folderAclMap.put(FolderAuthorityEnum.values()[i], true);
			}
		}
	}

	public boolean hasFolderAcl(FolderAuthorityEnum ae)
	{
		if (folderAclMap == null)
		{
			return false;
		}
		Boolean hasACL = folderAclMap.get(ae);
		if (hasACL == null)
		{
			return false;
		}
		return hasACL;
	}

}
