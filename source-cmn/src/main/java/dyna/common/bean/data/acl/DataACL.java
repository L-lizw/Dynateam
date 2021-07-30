/*
 * Copyright (C) PLM 版权所有
 * 功能描述: DataACL
 * ZhangHW 2012-6-6
 */

package dyna.common.bean.data.acl;

import java.util.HashMap;
import java.util.Map;

import dyna.common.systemenum.AuthorityEnum;
import dyna.common.util.StringUtils;

/**
 * @author ZhangHW
 * 
 */
public class DataACL
{
	private Map<AuthorityEnum, Boolean>	dataAclMap			= null;

	public DataACL()
	{

	}

	/**
	 * 传入参数的格式为：2;2;2
	 * 个数和AuthorityEnum中枚举个数有关
	 * 
	 * @param aclValue
	 */
	public void setDataAcl(String aclValue)
	{
		String[] values = StringUtils.splitString(aclValue);
		int aclLength = AuthorityEnum.values().length;
		if (values == null || values.length != aclLength)
		{
			return;
		}
		if (dataAclMap == null)
		{
			dataAclMap = new HashMap<AuthorityEnum, Boolean>();
		}
		for (int i = 0; i < AuthorityEnum.values().length; i++)
		{
			String aclStr = values[i];
			if (!StringUtils.isNullString(aclStr) && aclStr.equals("2"))
			{
				dataAclMap.put(AuthorityEnum.values()[i], false);
			}
			else
			{
				dataAclMap.put(AuthorityEnum.values()[i], true);
			}
		}
	}

	public boolean hasDataAcl(AuthorityEnum ae)
	{
		if (dataAclMap == null)
		{
			return false;
		}
		Boolean hasACL = dataAclMap.get(ae);
		if (hasACL == null)
		{
			return false;
		}
		return hasACL;
	}
}
