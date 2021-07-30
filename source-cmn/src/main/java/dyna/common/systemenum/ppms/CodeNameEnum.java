/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ProjectStatusEnum
 * WangLHB Apr 27, 2011
 */
package dyna.common.systemenum.ppms;

/**
 * 项目管理中固定的code名
 * 
 * @author WangLHB
 * 
 */
public enum CodeNameEnum
{
	EXECUTESTATUS("ExecuteStatus"), // 执行人
	TASKTYPE("TaskType"), // 任务类型
	DURATIONUNIT("DurationUnit"), // 周期单位
	TASKDEPENDENUM("TaskDepend"), // 依敕
	PROJECTTYPE("ProjectType"), //
	CHANGEMODEL("ProjectChangeModel"), // 变更模式
	ONTIMESTATE("OnTimeState"), // 准时状态
	IMPORTANCELEVEL("ImportanceLevel"), // 重要程度
	OPERATION("ChangeOperation"), // 任务变更状态
	OPERATIONSTATE("OperationState"), // 任务可操作状态
	WORKITEMSTATE("WorkItemStatus"), // 工作项状态
	TASKSTARTTYPE("TaskStartType"), // 任务执行方式
	TASKPERFORMERTYPE("TaskPerformerType"), // 执行人类型

	TRANSFORMTYPE("TransformType");// 转换类型
	private String	value	= null;

	CodeNameEnum(String value)
	{
		this.value = value;
	}

	public String getValue()
	{
		return this.value;
	}

	public static CodeNameEnum getStatusEnum(Object status)
	{
		try
		{
			if (status == null)
			{
				return null;
			}
			return CodeNameEnum.valueOf(status.toString());
		}
		catch (Exception e)
		{
		}
		return null;
	}
}
