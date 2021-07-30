/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DayOfMonthEnum
 * Administrator 2012-5-9
 */
package dyna.common.systemenum.ppms;

import dyna.common.util.StringUtils;

/**
 * @author Administrator
 * 
 */
public enum PMAuthorityEnum
{
	// 共有权限
	START("1", ProjectOrTaskEnum.ALL), // 启动
	PAUSE("2", ProjectOrTaskEnum.ALL), // 暂停任务
	SUSPEND("3", ProjectOrTaskEnum.ALL), // 中止项目
	COMPLETE("4", ProjectOrTaskEnum.ALL), // 完成项目
	// PLANTIME("5", ProjectOrTaskEnum.ALL), // 修改计划时间

	// 项目权限
	PROJECT_TEAMRESOURCES("11", ProjectOrTaskEnum.PROJECT), // 添加团队成员(添加角色/角色加人/从角色移除人/替换)
	// PROJECT_PUBLICDELIVERY("12", ProjectOrTaskEnum.PROJECT), // 公开交付物
	// PROJECT_WARNINGSET("13", ProjectOrTaskEnum.PROJECT), // 预警设置
	// PROJECT_IMPORT("12", ProjectOrTaskEnum.PROJECT), // 项目导入
	PROJECT_WBS("14", ProjectOrTaskEnum.ALL), // WBS分解
	// PROJECT_WORKITEM("15", ProjectOrTaskEnum.PROJECT), // 新建项目工作项
	// PROJECT_CHANGE("16", ProjectOrTaskEnum.PROJECT), // 项目变更
	// PROJECT_EXPORT("17", ProjectOrTaskEnum.PROJECT), // 项目导出
	// PROJECT_SAVEASTEMPLATE("17", ProjectOrTaskEnum.PROJECT), // 保存为模板

	// PROJECT_TYPE("14", ProjectOrTaskEnum.PROJECT), // 项目类型
	// PROJECT_WBS_ADD("15", ProjectOrTaskEnum.PROJECT), // 允许添加任务(项目WBS分解)
	// PROJECT_WBS_REMOVE("16", ProjectOrTaskEnum.PROJECT), // 允许删除任务(项目WBS分解)//
	// PROJECT_CHANGE_SET("17", ProjectOrTaskEnum.PROJECT), // 设置变更方式
	// PROJECT_TYPE_SET("18", ProjectOrTaskEnum.PROJECT), // 设置项目类型
	// PROJECT_TYPE_UI("19", ProjectOrTaskEnum.PROJECT), // 设置一般属性
	// PUBLIC_DELIVERY_test("20", ProjectOrTaskEnum.PROJECT), // 公开交付物属性

	// 任务权限

	TASK_TEAMRESOURCES("21", ProjectOrTaskEnum.TASK), // 任务团队资源
	// TASK_CHARGEPERSON("22", ProjectOrTaskEnum.TASK), // 修改任务责任人
	TASK_MANAGEDELIVERY("23", ProjectOrTaskEnum.TASK), // 交付项管理
	//TASK_COMMITDELIVERY("24", ProjectOrTaskEnum.TASK), // 提交交付物
	TASK_CHANGETOPROJECT("25", ProjectOrTaskEnum.TASK); // 任务转子项目
	//TASK_UPDATESTATUS("26", ProjectOrTaskEnum.TASK), // 更新任务状态
	//TASK_REMINDUPDATE("27", ProjectOrTaskEnum.TASK), // 提醒更新状态
	//TASK_WORKITEM("28", ProjectOrTaskEnum.TASK); // 任务工作项

	// TASK_WBS("24", ProjectOrTaskEnum.TASK), // 任务分解(总成型)
	// TASK_BASELINE("25", ProjectOrTaskEnum.TASK), // 设置里程碑

	// TASK_COMPLETION_RATE("26", ProjectOrTaskEnum.TASK), // 完成率
	// COMPLETION_RATE("25", ProjectOrTaskEnum.TASK),// 设置变更方式
	// TASK_MODIFY_DELIVERY_RELEASE("25", ProjectOrTaskEnum.TASK), // 允许修改交付物是否发布(交付项管理)
	// TASK_MODIFY_DELIVERY_MANDATORY("27", ProjectOrTaskEnum.TASK), // 允许修改交付物是否必须(交付项管理)
	// TASK_ADD_DELIVERY_ITEM("28", ProjectOrTaskEnum.TASK), // 允许添加交付项(交付项管理)
	// TASK_REMOVE_DELIVERY_ITEM("29", ProjectOrTaskEnum.TASK), // 允许删除交付项(交付项管理)
	// TASK_TYPE_UI("30", ProjectOrTaskEnum.TASK); // 设置一般属性

	private String				value;
	private ProjectOrTaskEnum	type;

	private PMAuthorityEnum(String value, ProjectOrTaskEnum type)
	{
		this.value = value;
		this.type = type;
	}

	public String getValue()
	{
		return this.value;
	}

	public ProjectOrTaskEnum getType()
	{
		return this.type;
	}

	public static PMAuthorityEnum typeValueOf(String value)
	{
		if (StringUtils.isNullString(value))
		{
			return null;
		}

		for (PMAuthorityEnum authorityEnum : PMAuthorityEnum.values())
		{
			if (authorityEnum.getValue().equals(value))
			{
				return authorityEnum;
			}
		}

		return null;
	}
}
