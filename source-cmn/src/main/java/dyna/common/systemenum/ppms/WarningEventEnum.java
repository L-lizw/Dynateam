/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WarningEventEnum
 * wangweixia 2013-10-17
 */
package dyna.common.systemenum.ppms;

import dyna.common.util.StringUtils;

/**
 * 预警事件枚举
 * 
 * @author wangweixia
 * 
 */
public enum WarningEventEnum
{
	P_START_DELAY("01", "ID_ENUM_PM_WARNING_P_START_DELAY", "PROJECT", "ID_APP_PM_WARNIG_CONTENTS_P_START_DELAY"), // 项目逾期启动

	P_START_BEFORE("02", "ID_ENUM_PM_WARNING_P_START_BEFORE", "PROJECT", "ID_APP_PM_WARNIG_CONTENTS_P_START_BEFORE"), // 项目启动前通知

	P_COMPLETE_DELAY("03", "ID_ENUM_PM_WARNING_P_COMPLETE_DELAY", "PROJECT", "ID_APP_PM_WARNIG_CONTENTS_P_COMPLETE_DELAY"), // 项目超期

	P_COMPLETE_DELAY_PERCENT("04", "ID_ENUM_PM_WARNING_P_COMPLETE_DELAY_PERCENT", "PROJECT", "ID_APP_PM_WARNIG_CONTENTS_P_COMPLETE_DELAY_PERCENT"), // 项目超期x%

	P_PROGRESS_RISK("05", "ID_ENUM_PM_WARNING_P_PROGRESS_RISK", "PROJECT", "ID_APP_PM_WARNIG_CONTENTS_P_PROGRESS_RISK"), // 项目进度风险

	T_START_DELAY("51", "ID_ENUM_PM_WARNING_T_START_DELAY", "TASK", "ID_APP_PM_WARNIG_CONTENTS_T_START_DELAY"), // 任务逾期启动

	T_START_BEFORE("52", "ID_ENUM_PM_WARNING_T_START_BEFORE", "TASK", "ID_APP_PM_WARNIG_CONTENTS_T_START_BEFORE"), // 任务启动前通知

	T_COMPLETE_DELAY("53", "ID_ENUM_PM_WARNING_T_COMPLETE_DELAY", "TASK", "ID_APP_PM_WARNIG_CONTENTS_T_COMPLETE_DELAY"), // 任务超期

	T_COMPLETE_DELAY_PERCENT("54", "ID_ENUM_PM_WARNING_T_COMPLETE_DELAY_PERCENT", "TASK", "ID_APP_PM_WARNIG_CONTENTS_T_COMPLETE_DELAY_PERCENT"), // 任务超期x%

	T_COMPLETE_BEFORE("55", "ID_ENUM_PM_WARNING_T_COMPLETE_BEFORE", "TASK", "ID_APP_PM_WARNIG_CONTENTS_T_COMPLETE_BEFORE"), // 任务完成前通知

	T_PROGRESS_RISK("56", "ID_ENUM_PM_WARNING_T_PROGRESS_RISK", "TASK", "ID_APP_PM_WARNIG_CONTENTS_T_PROGRESS_RISK");// 任务进度风险

	private String	value;

	private String	msrID;

	private String	msrMessageID;

	private String	type;

	private WarningEventEnum(String value, String msrID, String type, String msrMessageID)
	{
		this.value = value;
		this.msrID = msrID;
		this.type = type;
		this.msrMessageID = msrMessageID;
	}

	public String getMsrMessageID()
	{
		return this.msrMessageID;
	}

	public String getValue()
	{
		return this.value;
	}

	public String getMsrID()
	{
		return this.msrID;
	}

	public String getType()
	{
		return this.type;
	}

	public static WarningEventEnum typeValueOf(String type)
	{
		if (!StringUtils.isNullString(type))
		{
			for (WarningEventEnum event : WarningEventEnum.values())
			{
				if (type.equals(event.getValue()))
				{
					return event;
				}
			}
		}
		return null;
	}
}
