/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: RIG 管理组, 角色, 用户关系
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
public final class RIG extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID	= -787117931550975456L;

	public static final String	ROLE_GUID			= "ROLEGUID";
	public static final String	GROUP_GUID			= "GROUPGUID";

	public static final String	ROLE_ID				= "ROLEID";
	public static final String	ROLE_NAME			= "ROLENAME";
	public static final String	GROUP_ID			= "GROUPID";
	public static final String	GROUP_NAME			= "GROUPNAME";
	public static final String	IS_VALID			= "ISVALID";

	/**
	 * @return the roleGuid
	 */
	public String getRoleGuid()
	{
		return (String) this.get(ROLE_GUID);
	}

	/**
	 * @param roleGuid
	 *            the roleGuid to set
	 */
	public void setRoleGuid(String roleGuid)
	{
		this.put(ROLE_GUID, roleGuid);
	}

	/**
	 * @return the groupGuid
	 */
	public String getGroupGuid()
	{
		return (String) this.get(GROUP_GUID);
	}

	/**
	 * @param groupGuid
	 *            the groupGuid to set
	 */
	public void setGroupGuid(String groupGuid)
	{
		this.put(GROUP_GUID, groupGuid);
	}

	/**
	 * @return the roleId
	 */
	public String getRoleId()
	{
		return (String) this.get(ROLE_ID);
	}

	/**
	 * @return the roleName
	 */
	public String getRoleName()
	{
		return (String) this.get(ROLE_NAME);
	}

	/**
	 * @return the groupId
	 */
	public String getGroupId()
	{
		return (String) this.get(GROUP_ID);
	}

	/**
	 * @return the groupName
	 */
	public String getGroupName()
	{
		return (String) this.get(GROUP_NAME);
	}

	public boolean isValid()
	{
		return this.get(IS_VALID) == null || "Y".equals(this.get(IS_VALID)) ? true : false;
	}

	@Override
	public String toString()
	{
		return this.getGroupId() + "-" + this.getGroupName() + "/" + this.getRoleId() + "-" + this.getRoleName();
	}

}
