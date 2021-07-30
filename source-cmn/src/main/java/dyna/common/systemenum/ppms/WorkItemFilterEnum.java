/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkItemFilterEnum
 * wangweixia 2013-10-30
 */
package dyna.common.systemenum.ppms;

import dyna.common.util.StringUtils;

/**
 * 工作项过滤器
 * 
 * @author wangweixia
 * 
 */
public enum WorkItemFilterEnum
{
	RESPONSIBLEWORKITEM("ID_SYS_WORKITEMFILTERENUM_RESPONSIBLE"), // 我负责的工作项
	PARTICIPATEDWORKITEM("ID_SYS_WORKITEMFILTERENUM_PARTICIPATED"), // 我参与的工作项
	CREATEDWORKITEM("ID_SYS_WORKITEMFILTERENUM_CREATED"); // 我创建的工作项
	private final String	msrId;

	private WorkItemFilterEnum(String msrId)
	{
		this.msrId = msrId;
	}

	/**
	 * @return the msrId
	 */
	public String getMsrId()
	{
		return msrId;
	}

	public static WorkItemFilterEnum getWorkItemEnumByName(String name)
	{
		if (StringUtils.isNullString(name))
		{
			return null;
		}
		WorkItemFilterEnum[] workItems = WorkItemFilterEnum.values();
		if (workItems != null && workItems.length > 0)
		{
			for (WorkItemFilterEnum enumValue : workItems)
			{
				if (enumValue.name().equalsIgnoreCase(name))
				{
					return enumValue;
				}
			}
		}
		return null;
	}
}
