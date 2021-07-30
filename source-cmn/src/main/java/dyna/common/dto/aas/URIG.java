/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: URIG 管理组, 角色, 用户关系
 * Wanglei 2010-7-27
 */
package dyna.common.dto.aas;

import dyna.common.annotation.Cache;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;

/**
 * 管理组, 角色, 用户关系的工具类, 为应用层和数据层提供操作数据库之用途, 不带具体方法
 * 
 * @author Wanglei
 * 
 */
@Cache
public final class URIG extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID	= -787117931550975456L;

	public static final String	ROLE_GROUP_GUID		= "ROLEGROUPGUID";
	public static final String	USER_GUID			= "USERGUID";
	public static final String	ROLE_GUID			= "ROLEGUID";
	public static final String	GROUP_GUID			= "GROUPGUID";
	public static final String	IS_VALID			= "ISVALID";

	public String getRoleGuid()
	{
		return (String) this.get(ROLE_GUID);
	}

	public String getGroupGuid()
	{
		return (String) this.get(GROUP_GUID);
	}

	public String getUserGuid()
	{
		return (String) this.get(USER_GUID);
	}

	public void setUserGuid(String userGuid)
	{
		this.put(USER_GUID, userGuid);
	}

	public String getRoleGroupGuid()
	{
		return (String) this.get(ROLE_GROUP_GUID);
	}

	public void setRoleGroupGuid(String rigGuid)
	{
		this.put(ROLE_GROUP_GUID, rigGuid);
	}

	public boolean isValid()
	{
		return this.get(IS_VALID) == null || "Y".equals(this.get(IS_VALID)) ? true : false;
	}
}
