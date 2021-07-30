/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 项目管理使用，实例类型:项目或者任务。
 * duanll 2012-5-29
 */
package dyna.common.systemenum.ppms;

import dyna.common.util.StringUtils;

public enum ProjectOrTaskEnum
{
	// 项目
	PROJECT("1"),
	// 任务
	TASK("2"),
	// 共有
	ALL("3");

	private String	type;

	private ProjectOrTaskEnum(String type)
	{
		this.type = type;
	}

	public String getType()
	{
		return this.type;
	}

	public static ProjectOrTaskEnum typeValueOf(String type)
	{
		if (StringUtils.isNullString(type))
		{
			return null;
		}

		for (ProjectOrTaskEnum projectOrTaskEnum : ProjectOrTaskEnum.values())
		{
			if (projectOrTaskEnum.getType().equals(type))
			{
				return projectOrTaskEnum;
			}
		}

		return null;
	}
}
