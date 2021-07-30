/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ProjectStatusEnum
 * WangLHB Apr 27, 2011
 */
package dyna.common.systemenum.ppms;

import dyna.common.util.StringUtils;

/**
 * 项目管理中项目状态枚举
 * 
 * @author WangLHB
 * 
 */
public enum RoleTypeEnum
{
	OBSERVER("O"), // 观察者
	MANAGER("M"), // 经理
	PARTICIPATE("P"), // 参与者
	CHARGE("C"), // 负责人

	// 项目经理
	PARENTCHARGE("PC");// 父级任务责任人

	private String	role;

	private RoleTypeEnum(String role)
	{
		this.role = role;
	}

	public String getRole()
	{
		return this.role;
	}

	public static RoleTypeEnum typeValueOf(String role)
	{
		if (StringUtils.isNullString(role))
		{
			return null;
		}

		for (RoleTypeEnum roleType : RoleTypeEnum.values())
		{
			if (roleType.getRole().equals(role))
			{
				return roleType;
			}
		}

		return null;
	}
}
