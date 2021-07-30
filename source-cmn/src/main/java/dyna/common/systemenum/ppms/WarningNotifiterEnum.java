/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WarningNotifiterEnum
 * wangweixia 2013-10-17
 */
package dyna.common.systemenum.ppms;

/**
 * 预警通知人枚举
 * 
 * @author wangweixia
 * 
 */
public enum WarningNotifiterEnum
{
	TASKEXECUTOR("ID_CLIENT_PM_TASK_EXECUTOR"), // 任务执行人
	TASKPARTICIPATOR("ID_CLIENT_PM_TASK_PARTICIPANTS"), // 任务参与者
	SUPERIORTASKEXECUTOR("ID_CLIENT_PM_TASK_EXECUTOR_SUPERIOR"), // 任务执行人(上级)
	SUPERIORTASKPARTICIPATOR("ID_CLIENT_PM_TASK_PARTICIPANTS_SUPERIOR"), // 任务参与者(上级)
	FRONTTASKEXECUTOR("ID_CLIENT_PM_TASK_FRONT_EXECUTOR"), // 前置任务执行人
	FRONTTASKPARTICIPATOR("ID_CLIENT_PM_TASK_FRONT_PARTICIPANTS"), // 前置任务参与者
	REARTASKEXECUTOR("ID_CLIENT_PM_SUBTASK_MISSION_EXECUTOR"), // 后置任务执行人
	REARTASKTASKPARTICIPATOR("ID_CLIENT_PM_TASK_SUBSEQUENT_PARTICIPANTS"), // 后置任务参与者
	SUPERIORPROJECTMANAGER("ID_CLIENT_PM_TASK_PM_SUPERIOR"), // 项目经理(上级)
	PROJECTCREATOR("ID_CLIENT_PM_TASK_PM_CREATOR"), // 项目创建者
	PROJECTROLE(""), // 项目角色
	GROUP(""), // 组
	RIG(""), // rig (role in group)
	USER(""); // user
	private final String	msrId;

	private WarningNotifiterEnum(String msrId)
	{
		this.msrId = msrId;
	}

	/**
	 * @return the msrId
	 */
	public String getMsrId()
	{
		return this.msrId;
	}

}
