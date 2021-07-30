/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: FolderAcl
 * zhw 2012-6-7
 */
package dyna.common.dto.acl;

import java.util.HashMap;
import java.util.Map;

import dyna.common.systemenum.PublicSearchAuthorityEnum;
import dyna.common.util.StringUtils;

/**
 * @author zhw
 *
 */
public class PublicSearchACL
{

	private Map<PublicSearchAuthorityEnum, Boolean>	publicSearchAclMap	= null;

	public PublicSearchACL()
	{

	}

	/**
	 * 传入参数的格式为：2;2;2
	 * 个数和AuthorityEnum中枚举个数有关
	 * 
	 * @param aclValue
	 */
	public void setPublicSearchAcl(String aclValue)
	{
		String[] values = StringUtils.splitString(aclValue);
		int aclLength = PublicSearchAuthorityEnum.values().length;
		if (values == null || values.length != aclLength)
		{
			return;
		}
		if (publicSearchAclMap == null)
		{
			publicSearchAclMap = new HashMap<PublicSearchAuthorityEnum, Boolean>();
		}
		for (int i = 0; i < PublicSearchAuthorityEnum.values().length; i++)
		{
			String aclStr = values[i];
			if (!StringUtils.isNullString(aclStr) && aclStr.equals("2"))
			{
				publicSearchAclMap.put(PublicSearchAuthorityEnum.values()[i], false);
			}
			else
			{
				publicSearchAclMap.put(PublicSearchAuthorityEnum.values()[i], true);
			}
		}
	}

	public boolean hasPublicSearchAcl(PublicSearchAuthorityEnum ae)
	{
		if (publicSearchAclMap == null)
		{
			return false;
		}
		Boolean hasACL = publicSearchAclMap.get(ae);
		if (hasACL == null)
		{
			return false;
		}
		return hasACL;
	}

}
