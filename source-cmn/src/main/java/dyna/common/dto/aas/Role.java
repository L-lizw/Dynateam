/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Group
 * Wanglei 2010-7-13
 */
package dyna.common.dto.aas;

import dyna.common.annotation.Cache;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;

/**
 * @author Wanglei
 *
 */
@Cache
public class Role extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID	= 4806388039916169742L;

	public static final String	ROLE_ID				= "ROLEID";
	public static final String	ROLE_NAME			= "ROLENAME";
	public static final String	ROLE_IN_GROUP_GUID	= "RIGGUID";
	public static final String	DESCRIPTION			= "DESCRIPTION";
	public static final String	IS_VALID			= "ISVALID";

	/**
	 * @return the Description
	 */
	public String getDescription()
	{
		return (String) this.get(DESCRIPTION);
	}

	public String getFullname()
	{
		return this.getRoleId() + "-" + this.getRoleName();
	}

	public String getOriginalFullname()
	{
		return this.getOriginalValue(ROLE_ID) + "-" + this.getOriginalValue(ROLE_NAME);
	}

	/**
	 * @return the roleId
	 */
	public String getRoleId()
	{
		return (String) this.get(ROLE_ID);
	}

	public String getRoleInGroupGuid()
	{
		return (String) this.get(ROLE_IN_GROUP_GUID);
	}

	/**
	 * @return the roleName
	 */
	public String getName()
	{
		return (String) this.get(ROLE_NAME);
	}

	/**
	 * @return the roleName
	 */
	public String getRoleName()
	{
		return this.getName();
	}

	public boolean isActive()
	{
		return "Y".equals(this.get("ISVALID")) ? true : false;
	}

	public void setActive(boolean isActive)
	{
		this.put("ISVALID", isActive ? "Y" : "N");
	}

	/**
	 * @param Description
	 *            the Description to set
	 */
	public void setDescription(String description)
	{
		this.put(DESCRIPTION, description);
	}

	/**
	 * @param roleId
	 *            the roleId to set
	 */
	public void setRoleId(String roleId)
	{
		this.put(ROLE_ID, roleId);
	}

	/**
	 * @param roleName
	 *            the roleName to set
	 */
	public void setName(String roleName)
	{
		this.put(ROLE_NAME, roleName);
	}

	/**
	 * @param roleName
	 *            the roleName to set
	 */
	public void setRoleName(String roleName)
	{
		this.setName(roleName);
	}
}