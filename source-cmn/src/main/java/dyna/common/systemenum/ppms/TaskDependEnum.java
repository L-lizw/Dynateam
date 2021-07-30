/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: TaskDependEnum
 * Qiuxq 2012-5-15
 */
package dyna.common.systemenum.ppms;

/**
 * @author Qiuxq
 * 
 */
public enum TaskDependEnum
{
	FINISH_START("FS", "ID_TASK_DEPEND_TYPE_FINISH_START"), // 完成-开始
	START_START("SS", "ID_TASK_DEPEND_TYPE_START_START"), // 开始-开始
	FINISH_FINISH("FF", "ID_TASK_DEPEND_TYPE_FINISH_FINISH"), // 完成-完成
	START_FINISH("SF", "ID_TASK_DEPEND_TYPE_START_FINISH");// 开始-完成

	private String	value	= null;
	private String	msrId	= null;

	private TaskDependEnum(String value, String msrId)
	{
		this.value = value;
		this.msrId = msrId;
	}

	public String getMsrId()
	{
		return this.msrId;
	}

	@Override
	public String toString()
	{
		return this.value;
	}

	public String getValue()
	{
		return this.value;
	}

	public static TaskDependEnum getEnum(String value)
	{
		for (TaskDependEnum type : TaskDependEnum.values())
		{
			if (type.getValue().equalsIgnoreCase(value))
			{
				return type;
			}
		}
		return null;
	}

}
