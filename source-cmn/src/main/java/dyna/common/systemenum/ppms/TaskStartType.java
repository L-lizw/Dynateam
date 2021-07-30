/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ProjectStatusEnum
 * WangLHB Apr 27, 2011
 */
package dyna.common.systemenum.ppms;

/**
 * 任务启动类型枚举
 * 
 * @author WangLHB
 * 
 */
public enum TaskStartType
{

	Manual("Manual", "ID_TASK_START_TYPE_MANUAL"), // 手工启动
	FrontDrive("FrontDrive", "ID_TASK_START_TYPE_FRONT"); // 前置任务驱动

	private String	value	= null;
	private String	msrId	= null;

	TaskStartType(String value, String msrId)
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

	public static TaskStartType getStatusEnum(String status)
	{
		for (TaskStartType type : TaskStartType.values())
		{
			if (type.value.equalsIgnoreCase(status))
			{
				return type;
			}
		}
		return null;
	}
}
