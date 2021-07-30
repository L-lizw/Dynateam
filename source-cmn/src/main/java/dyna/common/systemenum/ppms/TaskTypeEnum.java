/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ProjectStatusEnum
 * WangLHB Apr 27, 2011
 */
package dyna.common.systemenum.ppms;

/**
 * 任务类型枚举
 * 
 * @author WangLHB
 * 
 */
public enum TaskTypeEnum
{
	// 普通任务、摘要任务、里程碑、子项目
	GENERAL("GENERAL", "ID_TASK_TYPE_GENERAL"), // 普通任务
	SUMMARY("SUMMARY", "ID_TASK_TYPE_SUMMARY"), // 摘要任务
	MILESTONE("MILESTONE", "ID_TASK_TYPE_MILESTONE");// 里程碑
	// SUBPRJ("SUBPRJ", "ID_TASK_TYPE_SUBPRJ"); // 子项目

	private String	value	= null;
	private String	msrId	= null;

	TaskTypeEnum(String value, String msrId)
	{
		this.value = value;
		this.msrId = msrId;
	}

	public String getMsrId()
	{
		return this.msrId;
	}

	public String getValue()
	{
		return this.value;
	}

	public static TaskTypeEnum getStatusEnum(Object status)
	{
		for (TaskTypeEnum type : TaskTypeEnum.values())
		{
			if (type.value.equals(status))
			{
				return type;
			}
		}
		return null;
	}
}
