/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: RoleMembers
 * wangweixia 2013-10-16
 */
package dyna.common.bean.data.ppms;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.DynaObject;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dto.aas.User;
import dyna.common.dtomapper.ppm.RoleMembersMapper;
import dyna.common.systemenum.ppms.WBSOperateEnum;

import java.math.BigDecimal;

/**
 * 项目角色成员
 * 
 * @author wangweixia
 * 
 */
@EntryMapper(RoleMembersMapper.class)
public class RoleMembers extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID	= -8686878582460005363L;

	// 项目角色guid
	public static final String	PROJECTROLEGUID		= "PROJECTROLEGUID";

	// userGuid
	public static final String	USERGUID			= "USERGUID";

	// 序号
	public static final String	SEQUENCE			= "DATASEQ";

	// User信息
	public User					user				= null;

	// 项目角色名称
	public static final String	ROLENAME			= "ROLENAME";

	public static final String	ROLEID				= "ROLEID";

	private WBSOperateEnum		operate				= null;

	/**
	 * @return the projectroleguid
	 */
	public String getProjectRoleGuid()
	{
		return (String) super.get(PROJECTROLEGUID);
	}

	public void setProjectRoleGuid(String roleGuid)
	{
		super.put(PROJECTROLEGUID, roleGuid);
	}

	/**
	 * @return the projectroleguid
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
	 * @return the projectroleguid
	 */
	public String getRoleID()
	{
		return (String) super.get(ROLEID);
	}

	public void setRoleID(String roleID)
	{
		super.put(ROLEID, roleID);
	}

	/**
	 * @return the userguid
	 */
	public String getUserGuid()
	{
		return (String) super.get(USERGUID);
	}

	public void setUserGuid(String userId)
	{
		super.put(USERGUID, userId);
	}

	/**
	 * @return the sequence
	 */
	public Integer getSequence()
	{
		Number b = (Number) this.get(SEQUENCE);
		return b == null ? 0 : b.intValue();
	}

	/**
	 * @param sequence
	 *            the sequence to set
	 */
	public void setSequence(int sequence)
	{
		super.put(SEQUENCE, new BigDecimal(sequence));
	}

	public WBSOperateEnum getOperate()
	{
		return this.operate;
	}

	public void setOperate(WBSOperateEnum operate)
	{
		this.operate = operate;
	}

	public User getUser()
	{
		return this.user;
	}

	public void setUser(User user)
	{
		this.user = user;
	}

	@Override
	public void sync(DynaObject object)
	{
		super.sync(object);
		this.setUser(((RoleMembers) object).getUser());
	}

	@Override
	public void syncValue(DynaObject object)
	{
		super.syncValue(object);
		this.setUser(((RoleMembers) object).getUser());
	}

}
