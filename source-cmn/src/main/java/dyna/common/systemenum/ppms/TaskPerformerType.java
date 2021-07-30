/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ProjectStatusEnum
 * WangLHB Apr 27, 2011
 */
package dyna.common.systemenum.ppms;

/**
 * 任务执行类型枚举
 * 
 * @author WangLHB
 * 
 */
public enum TaskPerformerType
{

	Owner("Owner", "ID_TASK_PERFORMER_OWNER_TYPE"), // 只能由任务执行人执行
	Superior("Superior", "ID_TASK_PERFORMER_SUPERIOR_TYPE"); // 允许任务执行人及上级执行

	private String	value	= null;
	private String	msrId	= null;

	TaskPerformerType(String value, String msrId)
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

	public static TaskPerformerType getStatusEnum(String status)
	{
		for (TaskPerformerType type : TaskPerformerType.values())
		{
			if (type.value.equalsIgnoreCase(status))
			{
				return type;
			}
		}
		return null;
	}
}
