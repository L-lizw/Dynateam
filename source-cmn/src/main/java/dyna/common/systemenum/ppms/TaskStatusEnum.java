/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ProjectStatusEnum
 * WangLHB Apr 27, 2011
 */
package dyna.common.systemenum.ppms;

/**
 * 项目管理中任务状态枚举
 * 
 * @author WangLHB
 * 
 */
public enum TaskStatusEnum
{

	INI("INI", "ID_TASK_STATUS_INITIALIZATION"), // 已创建
	RUN("RUN", "ID_TASK_STATUS_RUNNING"), // 运行中
	SSP("SSP", "ID_TASK_STATUS_SUSPEND"), // 中止
	COP("COP", "ID_TASK_STATUS_COMPLETE"), // 结束
	APP("APP", "ID_TASK_STATUS_APP"), //审批
	PUS("PUS", "ID_PRJ_STATUS_PAUSE"); // 暂停

	private String	value	= null;
	private String	msrId	= null;

	TaskStatusEnum(String value, String msrId)
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

	public static TaskStatusEnum getStatusEnum(Object status)
	{
		try
		{
			if (status == null)
			{
				return null;
			}
			return TaskStatusEnum.valueOf(status.toString());
		}
		catch (Exception e)
		{
		}
		return null;
	}

	public static TaskStatusEnum[] getValidStatusEnum()
	{
		return new TaskStatusEnum[] { INI, RUN, SSP, COP, PUS };
	}
}
