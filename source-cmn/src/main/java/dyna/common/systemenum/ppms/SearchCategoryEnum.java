/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SearchConditionType
 * WangLHB May 21, 2012
 */
package dyna.common.systemenum.ppms;

import dyna.common.util.StringUtils;

/**
 * @author WangLHB
 * 
 */
public enum SearchCategoryEnum
{
	// 默认过滤器：作为项目团队成员或创建者可见的项目
	PROJ_DEFAULT(""),
	// 目前所有：项目“执行状态”为”未启动”、“进行中”、“暂停”的我是创建者，项目团队的成员的项目
	PROJ_ALL_UP_TO_NOW("ID_SYS_SEARCHCATEGORYENUM_PROJALLUPTONOW"),
	// 初始化的项目：项目状态为”未启动”的我是创建者，项目团队的成员的项目
	PROJ_INIT("ID_SYS_SEARCHCATEGORYENUM_PROJINIT"),
	// 进行中的项目：项目状态为“进行中”的我是创建者，项目团队的成员的项目
	PROJ_IN_PROCESS("ID_SYS_SEARCHCATEGORYENUM_PROJINPROCESS"),
	// 已完成的项目：项目状态为“已完成”的我是创建者，项目团队的成员的项目
	PROJ_COMPLETE("ID_SYS_SEARCHCATEGORYENUM_PROJCOMPLETE"),
	// 暂停的项目：项目状态为“暂停”的我是创建者，项目团队的成员的项目
	PROJ_SUSPEND("ID_SYS_SEARCHCATEGORYENUM_PROJSUSPEND"),
	// 终止的项目：项目状态为“中止”的我是创建者，项目团队的成员的项目
	PROJ_STOP("ID_SYS_SEARCHCATEGORYENUM_PROJSTOP"),
	// 参与的项目：我是项目团队的成员的所有状态的项目
	PROJ_PARTICIPATE("ID_SYS_SEARCHCATEGORYENUM_PROJPARTICIPATE"),
	// 创建的项目：我是创建者的所有状态的项目
	PROJ_CREATE("ID_SYS_SEARCHCATEGORYENUM_PROJCREATE"),
	// 管理的项目：我是项目经理的所有状态的项目
	PROJ_MANAGE("ID_SYS_SEARCHCATEGORYENUM_PROJMANAGE"),
	// 跟踪的项目：我是观察者的所有状态的项目
	PROJ_OBSERVE("ID_SYS_SEARCHCATEGORYENUM_PROJOBSERVE"),
	// 我是组管理员时，所有组成员的项目
	PROJ_GROUP_MANAGER("ID_SYS_SEARCHCATEGORYENUM_GROUPMANAGER"),

	// 默认过滤器：作为任务资源的任务
	TASK_DEFAULT(""),
	// 默认过滤器：待办任务
	TASK_WAIT_TODO("ID_SYS_SEARCHCATEGORYENUM_TASKWAITTODO"),
	// 目前所有：目前所有项目状态为”未启动”、“进行中”、“暂停”的我是任务资源的角色的任务
	TASK_ALL_UP_TO_NOW("ID_SYS_SEARCHCATEGORYENUM_TASKALLUPTONOW"),
	// 进行中的任务：任务状态为“进行中”的我是任务资源的角色的任务
	TASK_IN_PROCESS("ID_SYS_SEARCHCATEGORYENUM_TASKINPROCESS"),
	// 已完成的任务：任务状态为“已完成”的我是任务资源的角色的任务
	TASK_COMPLETE("ID_SYS_SEARCHCATEGORYENUM_TASKCOMPLETE"),
	// 暂停的任务：任务状态为“暂停”的我是任务资源的角色的任务
	TASK_SUSPEND("ID_SYS_SEARCHCATEGORYENUM_TASKSUSPEND"),
	// 取消的任务：任务状态为“中止”的我是任务资源的角色的任务
	TASK_STOP("ID_SYS_SEARCHCATEGORYENUM_TASKSTOP"),
	// 我参与的任务：我是任务资源的所有状态的任务
	TASK_PARTICIPATE("ID_SYS_SEARCHCATEGORYENUM_TASKPARTICIPATE"),
	// 未启动的任务:任务“执行状态”为“未启动”的我是任务资源的角色的任务
	TASK_NOT_START("ID_SYS_SEARCHCATEGORYENUM_TASKNOTSTART"),
	// 我负责的任务:我是执行者的所有状态的任务
	TASK_RESPONSIBLE("ID_SYS_SEARCHCATEGORYENUM_TASKRESPONSIBLE"),
	// 可启动的任务:任务“操作状态”为“可启动”的我是执行者的角色的任务
	TASK_CAN_START("ID_SYS_SEARCHCATEGORYENUM_TASKCANSTART"),
	// 可完成的任务:任务“操作状态”为“可完成”的我是执行者的角色的任务
	TASK_CAN_COMPLETE("ID_SYS_SEARCHCATEGORYENUM_TASKCANCOMPLETE"),

	// 待审批的任务:任务为“审批状态”,我是执行者的角色
	TASK_APP("ID_SYS_SEARCHCATEGORYENUM_TASKAPP"),
	// 待审批的任务:任务为“审批状态”,我是审批者的角色
	TASK_PROJECT_APP("ID_SYS_SEARCHCATEGORYENUM_PROJECTAPP"),

	// 资源工作量中:我参与的任务
	WORKLOAD_PARTICIPATE_TASK("ID_SYS_SEARCHCATEGORYENUM_WORKLOADPARTICIPATETASK"),
	// 我参与的工作项:包含所有状态
	WORKLOAD_PARTICIPATE_WORKITEM("ID_SYS_SEARCHCATEGORYENUM_WORKLOADPARTICIPATEWORKITEM"),

	// 工作项:默认过滤器
	WORKITEM_DEFAULT(""),
	// 待办工作项
	WORKITEM_WAIT_TODO("ID_SYS_SEARCHCATEGORYENUM_WORKITEMWAITTODO"),
	// 目前所有：任务“执行状态”为“未启动”、“进行中”、“暂停”的我是责任人或参与者的工作项
	WORKITEM_ALL_UP_TO_NOW("ID_SYS_SEARCHCATEGORYENUM_WORKITEMALLUPTONOW"),
	// 进行中的工作项：任务状态为“进行中”的我是责任人或参与者的工作项
	WORKITEM_IN_PROCESS("ID_SYS_SEARCHCATEGORYENUM_WORKITEMINPROCESS"),
	// 已完成的工作项：任务状态为“已完成”的我是责任人或参与者的工作项
	WORKITEM_COMPLETE("ID_SYS_SEARCHCATEGORYENUM_WORKITEMCOMPLETE"),
	// 未启动的工作项:任务“执行状态”为“未启动”的我是责任人或参与者的工作项
	WORKITEM_NOT_START("ID_SYS_SEARCHCATEGORYENUM_WORKITEMNOTSTART"),
	// 暂停的工作项：任务状态为“暂停”的我是创建者，项目经理，或资源的角色的工作项
	WORKITEM_SUSPEND("ID_SYS_SEARCHCATEGORYENUM_WORKITEM_SUSPEND"),
	// 取消的工作项：任务状态为“中止”的我是创建者，项目经理，或资源的角色的工作项
	WORKITEM_STOP("ID_SYS_SEARCHCATEGORYENUM_WORKITEM_STOP"),
	// 我负责的任务:责任人是当前用户的除“未指派”之外所有状态的工作项
	WORKITEM_RESPONSIBLE("ID_SYS_WORKITEMFILTERENUM_RESPONSIBLE"),
	// 我参与的任务：参与人包含当前用户的除“未指派”之外所有状态的工作项
	WORKITEM_PARTICIPATE("ID_SYS_WORKITEMFILTERENUM_PARTICIPATED"),
	// 创建的项目：我是创建者的所有状态的工作项
	WORKITEM_CREATE("ID_SYS_WORKITEMFILTERENUM_CREATED"),

	// 所有待处理
	WORKFLOW_WAIT_PROCESS("ID_CLIENT_WORKFLOWMAIL_SEARCH_WAITPROCESS"),
	// 逾期未处理
	WORKFLOW_NOT_PROCESS("ID_CLIENT_WORKFLOWMAIL_SEARCH_NOTPROCESS"),
	// 所有已处理
	WORKFLOW_ALREADY_PROCESS("ID_CLIENT_WORKFLOWMAIL_SEARCH_ALLPROCESS"),
	// 所有
	WORKFLOW_ALL("ID_CLIENT_WORKFLOWMAIL_SEARCH_ALL"),
	// 所有我代理的
	WORKFLOW_AGENT_PROCESS("ID_CLIENT_WORKFLOWMAIL_SEARCH_AGENTPROCESS"),
	// 所有非我代理的
	WORKFLOW_NOT_AGENT_PROCESS("ID_CLIENT_WORKFLOWMAIL_SEARCH_NOT_AGENTPROCESS"),
	;

	private final String	msrId;

	/**
	 * 
	 */
	private SearchCategoryEnum(String msrId)
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

	public static SearchCategoryEnum getSearchEnumByName(String name)
	{
		if (StringUtils.isNullString(name))
		{
			return null;
		}
		SearchCategoryEnum[] searchEnums = SearchCategoryEnum.values();
		if (searchEnums != null && searchEnums.length > 0)
		{
			for (SearchCategoryEnum enumValue : searchEnums)
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
