/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ProjectRole
 * wangweixia 2013-10-16
 */
package dyna.common.bean.data.ppms;

import java.util.List;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.systemenum.ppms.WBSOperateEnum;
import dyna.common.util.PMConstans;
import dyna.common.util.SetUtils;

/**
 * 项目角色设置
 * 
 * @author wangweixia
 * 
 */
public class ProjectRole extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID	= 8502074347981484907L;

	// 项目类型的guid或者项目guid,项目模板guid,
	public static final String	TYPEGUID			= "TYPEGUID";

	public static final String	TYPECLASSGUID		= "TYPECLASSGUID";

	// type=1:项目类型guid; type=2:除项目类型以外的
	public static final String	TYPE				= "OWNERTYPE";

	// 角色编号
	public static final String	ROLEID				= "ROLEID";

	// 角色名称
	public static final String	ROLENAME			= "ROLENAME";

	// 说明
	public static final String	DESCRIPTION			= "DESCRIPTION";

	// 项目角色成员
	public List<RoleMembers>	roleMembersList		= null;

	private WBSOperateEnum		operate				= null;

	/**
	 * @return the typeguid
	 */
	public String getTypeGuid()
	{
		return (String) super.get(TYPEGUID);
	}

	public void setTypeGuid(String typeGuid)
	{
		super.put(TYPEGUID, typeGuid);
	}

	/**
	 * @return the typeclassguid
	 */
	public String getTypeClassGuid()
	{
		return (String) super.get(TYPECLASSGUID);
	}

	public void setTypeClassGuid(String typeClassGuid)
	{
		super.put(TYPECLASSGUID, typeClassGuid);
	}

	/**
	 * @return the type
	 */
	public String getType()
	{
		return (String) super.get(TYPE);
	}

	public void setType(String type)
	{
		super.put(TYPE, type);
	}

	/**
	 * @return the roleid
	 */
	public String getRoleId()
	{
		return (String) super.get(ROLEID);
	}

	public void setRoleId(String roleId)
	{
		super.put(ROLEID, roleId);
	}

	/**
	 * @return the rolename
	 */
	public String getRoleName()
	{
		return (String) super.get(ROLENAME);
	}

	public void setRoleName(String roleName)
	{
		super.put(ROLENAME, roleName);
	}

	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return (String) super.get(DESCRIPTION);
	}

	public void setDescription(String description)
	{
		super.put(DESCRIPTION, description);
	}

	/**
	 * @return the roleMembersList
	 */
	public List<RoleMembers> getRoleMembersList()
	{
		return this.roleMembersList;
	}

	/**
	 * @param roleMembersList
	 *            the roleMembersList to set
	 */
	public void setRoleMembersList(List<RoleMembers> roleMembersList)
	{
		if (!SetUtils.isNullList(roleMembersList))
		{
			this.roleMembersList = roleMembersList;
		}
	}

	public WBSOperateEnum getOperate()
	{
		return this.operate;
	}

	public void setOperate(WBSOperateEnum operate)
	{
		this.operate = operate;
	}

	public boolean isManger()
	{
		return PMConstans.PROJECT_MANAGER_ROLE.equalsIgnoreCase(this.getRoleId());
	}

}
