/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: workItemStateEnum
 * wangweixia 2013-10-17
 */
package dyna.common.systemenum.ppms;

import dyna.common.util.StringUtils;

/**
 * @author wangweixia
 * 
 */
public enum WorkItemStateEnum
{
	NOTASSIGNED("notAssigned", "ID_CLIENT_PM_CT_WORKITEM_NOTASSIGNED"), // 未指派
	NOTSTART("notStart", "ID_CLIENT_PM_CT_WORKITEM_NOTSTART"), // 未启动
	INPROGRESS("inProgress", "ID_CLIENT_PM_CT_WORKITEM_INPROGRESS"), // 进行中
	COMPLETED("completed", "ID_CLIENT_PM_CT_WORKITEM_COMPLETED"), // 已完成
	CANCEL("cancel", "ID_CLIENT_PM_CT_WORKITEM_CANCEL"), // 已取消
	APP("APP", "ID_SYS_PROGRESSRATEENUM_APP"), //审批中
	PAUSE("pause", "ID_CLIENT_PM_CT_WORKITEM_PAUSE"), ;// 暂停

	private final String	msrId;
	private final String	value;

	private WorkItemStateEnum(String value, String msrId)
	{
		this.value = value;
		this.msrId = msrId;
	}

	/**
	 * @return the msrId
	 */
	public String getMsrId()
	{
		return msrId;
	}

	public String getValue()
	{

		return value;
	}

	/**
	 * 通过值获得此枚举
	 * 
	 * @param value
	 * @return
	 */
	public static WorkItemStateEnum getValueOf(String value)
	{
		if (StringUtils.isNullString(value))
		{
			return null;
		}

		for (WorkItemStateEnum enumValue : WorkItemStateEnum.values())
		{
			if (enumValue.getValue().equals(value))
			{
				return enumValue;
			}
		}
		return null;
	}
}
