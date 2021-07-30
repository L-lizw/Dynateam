/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: BusinessModelType
 * WangLHB Jun 17, 2011
 */
package dyna.common.systemenum;

/**
 * @author duanll
 *
 */
public enum NewProjectTypeEnum
{
	TEMPLATE("1"),
	PROJECT("2"),
	SUBPROJECT("3");

	private final String	newProjectType;

	NewProjectTypeEnum(String newProjectType)
	{
		this.newProjectType = newProjectType;
	}

	public String getNewProjectType()
	{
		return this.newProjectType;
	}
}
