/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: MessageTypeEnum
 * wangweixia 2013-10-16
 */
package dyna.common.systemenum.ppms;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息类型和通知人枚举
 * 
 * @author wangweixia
 * 
 */
public enum MessageTypeEnum
{
	PROJECTSTARTNOTIFY("ID_SYS_MESSAGETYPEENUM_PROJECTSTARTNOTIFY", "ID_APP_PPMS_NOTICESTUB_STARTPROJECT", ProjectOrTaskEnum.PROJECT), // 项目启用通知

	PROJECTENDNOTIFY("ID_SYS_MESSAGETYPEENUM_PROJECTENDNOTIFY", "ID_APP_PPMS_NOTICESTUB_PROJECTENDNOTIFY", ProjectOrTaskEnum.PROJECT), // 项目取消通知

	PROJECTPAUSENOTIFY("ID_SYS_MESSAGETYPEENUM_PROJECTPAUSENOTIFY", "ID_APP_PPMS_NOTICESTUB_PROJECTPAUSE", ProjectOrTaskEnum.PROJECT), // 项目暂停通知

	PROJECTCOMPLETENOTIFY("ID_SYS_MESSAGETYPEENUM_PROJECTCOMPLETENOTIFY", "ID_APP_PPMS_NOTICESTUB_PROJECTCOMPLETE", ProjectOrTaskEnum.PROJECT), // 项目完成通知

	PROJECTCANCOMPLETENOTIFY("ID_SYS_MESSAGETYPEENUM_PROJECTCANCOMPLETENOTIFY", "ID_APP_PPMS_NOTICESTUB_PROJECTCANCOMPLETE", ProjectOrTaskEnum.PROJECT), // 项目完成通知

	PROJECTCHANGENOTIFY("ID_SYS_MESSAGETYPEENUM_PROJECTCHANGENOTIFY", "ID_APP_PPMS_NOTICESTUB_PROJECTSTARTCHANGE", ProjectOrTaskEnum.PROJECT), // 项目变更(快速)通知

	PROJECTCHANGEAFTERNOTIFY("ID_SYS_MESSAGETYPEENUM_PROJECTCHANGEAFTERNOTIFY", "ID_APP_PPMS_NOTICESTUB_PROJECTCOMPLETECHANGE", ProjectOrTaskEnum.PROJECT), // 项目变更后启动通知

	PROJECTEXECUTORCHANGENOTIFY("ID_SYS_MESSAGETYPEENUM_PROJECTEXECUTORCHANGENOTIFY", "ID_APP_PPMS_NOTICESTUB_PROJECTEXECUTORCHANGENOTIFY", ProjectOrTaskEnum.PROJECT), // 项目执行人变更后通知

	TASKASSIGNNOTIFY("ID_SYS_MESSAGETYPEENUM_TASKASSIGNNOTIFY", "ID_APP_PPMS_NOTICESTUB_TASKASSIGNNOTIFY", ProjectOrTaskEnum.TASK), // 任务分配通知

	TASKENDNOTIFY("ID_SYS_MESSAGETYPEENUM_TASKENDNOTIFY", "ID_APP_PPMS_NOTICESTUB_TASKENDNOTIFY", ProjectOrTaskEnum.TASK), // 任务取消通知

	TASKPAUSENOTIFY("ID_SYS_MESSAGETYPEENUM_TASKPAUSENOTIFY", "ID_APP_PPMS_NOTICESTUB_TASKPAUSENOTIFY", ProjectOrTaskEnum.TASK), // 任务暂停通知

	TASKCOMPLETENOTIFY("ID_SYS_MESSAGETYPEENUM_TASKCOMPLETENOTIFY", "ID_APP_PPMS_NOTICESTUB_TASKCOMPLETENOTIFY", ProjectOrTaskEnum.TASK), // 任务完成通知

	TASKAPPNOTIFY("ID_SYS_MESSAGETYPEENUM_TASKAPPNOTIFY", "ID_APP_PPMS_NOTICESTUB_TASKAPPNOTIFY", ProjectOrTaskEnum.TASK), // 任务审批通知
	
	TASKCANCELNOTIFY("ID_SYS_MESSAGETYPEENUM_TASKCANCELNOTIFY", "ID_APP_PPMS_NOTICESTUB_TASKCANCELNOTIFY", ProjectOrTaskEnum.TASK), // 任务驳回通知
	
	TASKAPPCOLNOTIFY("ID_SYS_MESSAGETYPEENUM_TASKAPPCOLNOTIFY", "ID_APP_PPMS_NOTICESTUB_TASKAPPCOLNOTIFY", ProjectOrTaskEnum.TASK), // 任务审批通过通知

	// TASKCHANGENOTIFY("ID_SYS_MESSAGETYPEENUM_TASKCHANGENOTIFY", "ID_APP_PPMS_NOTICESTUB_TASKCHANGENOTIFY",
	// ProjectOrTaskEnum.TASK), // 任务变更(快速)通知

	// TASKCHANGEAFTERNOTIFY("ID_SYS_MESSAGETYPEENUM_TASKCHANGEAFTERNOTIFY",
	// "ID_APP_PPMS_NOTICESTUB_TASKCHANGEAFTERNOTIFY", ProjectOrTaskEnum.TASK), // 任务变更后启动通知

	// TASKAUDITNOTIFY("ID_SYS_MESSAGETYPEENUM_TASKAUDITNOTIFY"), // 任务待审核通知

	// TASKCOMPLETEAUDITNOTIFY("ID_SYS_MESSAGETYPEENUM_TASKCOMPLETEAUDITNOTIFY"), // 任务完成审核通知

	PRETASKCOMPLETENOTIFY("ID_SYS_MESSAGETYPEENUM_PRETASKCOMPLETENOTIFY", "ID_APP_PPMS_NOTICESTUB_PRETASKCOMPLETENOTIFY", ProjectOrTaskEnum.TASK), // 前置任务完成通知

	WORKITEMNOTIFY("ID_SYS_MESSAGETYPEENUM_WORKITEMNOTIFY", "ID_APP_PPMS_NOTICESTUB_WORKITEMNOTIFY", ProjectOrTaskEnum.TASK), // 工作项通知

	WORKITEMCANCELNOTIFY("ID_SYS_MESSAGETYPEENUM_WORKITEMCANCELNOTIFY", "ID_APP_PPMS_NOTICESTUB_WORKITEMCANCELNOTIFY", ProjectOrTaskEnum.TASK), // 工作项取消通知
	
	TASKUPDATENOTIFY("ID_SYS_MESSAGETYPEENUM_TASKUPDATENOTIFY", "ID_APP_PPMS_NOTICESTUB_TASKUPDATENOTIFY", ProjectOrTaskEnum.TASK), // 任务最新进度说明
	WORKITEMUPDATENOTIFY("ID_SYS_MESSAGETYPEENUM_WORKITEMUPDATENOTIFY", "ID_APP_PPMS_NOTICESTUB_WORKITEMUPDATENOTIFY", ProjectOrTaskEnum.TASK), // 工作项最新进度说明

	// SCHEDULENOTIFY("ID_SYS_MESSAGETYPEENUM_SCHEDULENOTIFY"), // 日程通知

	// SCHEDULECANCELNOTIFY("ID_SYS_MESSAGETYPEENUM_SCHEDULECANCELNOTIFY"), // 日程取消通知

	// 预警消息
	// PSTARTDELAY("ID_ENUM_PM_WARNING_P_START_DELAY"), // 项目逾期启动
	//
	// PSTARTBEFORE("ID_ENUM_PM_WARNING_P_START_BEFORE"), // 项目启动前通知
	//
	// PCOMPLETEDELAY("ID_ENUM_PM_WARNING_P_COMPLETE_DELAY"), // 项目超期
	//
	// PCOMPLETEDELAYPERCENT("ID_ENUM_PM_WARNING_P_COMPLETE_DELAY_PERCENT"), // 项目超期x%
	//
	// PPROGRESSRISK("ID_ENUM_PM_WARNING_P_PROGRESS_RISK"), // 项目进度风险
	//
	// TSTARTDELAY("ID_ENUM_PM_WARNING_T_START_DELAY"), // 任务逾期启动
	//
	// TSTARTBEFORE("ID_ENUM_PM_WARNING_T_START_BEFORE"), // 任务启动前通知
	//
	// TCOMPLETEDELAY("ID_ENUM_PM_WARNING_T_COMPLETE_DELAY"), // 任务超期
	//
	// TCOMPLETEDELAYPERCENT("ID_ENUM_PM_WARNING_T_COMPLETE_DELAY_PERCENT"), // 任务超期x%
	//
	// TCOMPLETEBEFORE("ID_ENUM_PM_WARNING_T_COMPLETE_BEFORE"), // 任务完成前通知
	//
	// TPROGRESSRISK("ID_ENUM_PM_WARNING_T_PROGRESS_RISK"), // 项目进度风险

	PROJECTCREATOR("ID_SYS_MESSAGETYPEENUM_PROJECTCREATOR", "", null), // 项目创建者
	PROJECTMANAGER("ID_SYS_MESSAGETYPEENUM_PROJECTMANAGER", "", null), // 项目经理
	PROJECTMEMBER("ID_SYS_MESSAGETYPEENUM_PROJECTMEMBER", "", null), // 项目成员
	TASKRESPONSIBLE("ID_SYS_MESSAGETYPEENUM_TASKRESPONSIBLE", "", null), // 任务责任人
	TASKPARTICIPATOR("ID_SYS_MESSAGETYPEENUM_TASKPARTICIPATOR", "", null), // 任务参与者
	PRETASKRESPONSIBLE("ID_SYS_MESSAGETYPEENUM_PRETASKRESPONSIBLE", "", null), // 前置任务责任人
	POSTTASKRESPONSIBLE("ID_SYS_MESSAGETYPEENUM_POSTTASKRESPONSIBLE", "", null), // 后置任务责任人
	SUPERIORTASKRESPONSIBLE("ID_SYS_MESSAGETYPEENUM_SUPERIORTASKRESPONSIBLE", "", null), // 上一级任务责任人
	// AUDITPERSON("ID_SYS_MESSAGETYPEENUM_AUDITPERSON"), // 审核人
	WORKITEMCREATOR("ID_SYS_MESSAGETYPEENUM_WORKITEMCREATOR", "", null), // 工作项创建者
	WORKITEMRESPONSIBLE("ID_SYS_MESSAGETYPEENUM_WORKITEMRESPONSIBLE", "", null), // 工作项责任人
	WORKITEMPARTICIPATOR("ID_SYS_MESSAGETYPEENUM_WORKITEMPARTICIPATOR", "", null); // 工作项参与者
	// SCHEDULECREATOR("ID_SYS_MESSAGETYPEENUM_SCHEDULECREATOR"), // 日程创建者
	// SCHEDULERESPONSIBLE("ID_SYS_MESSAGETYPEENUM_SCHEDULERESPONSIBLE"), // 日程责任人
	// SCHEDULEAUDITOR("ID_SYS_MESSAGETYPEENUM_SCHEDULEAUDITOR"); // 日程审核人
	private final String			msrId;
	private final String			messageContentMsId;
	private final ProjectOrTaskEnum	type;

	private MessageTypeEnum(String msrId, String messageContentMsId, ProjectOrTaskEnum type)
	{
		this.msrId = msrId;
		this.messageContentMsId = messageContentMsId;
		this.type = type;
	}

	/**
	 * @return the msrId
	 */
	public String getMsrId()
	{
		return this.msrId;
	}

	/**
	 * @return the msrId
	 */
	public ProjectOrTaskEnum getType()
	{
		return this.type;
	}

	/**
	 * @return the msrId
	 */
	public String getmessageContentMsId()
	{
		return this.messageContentMsId;
	}

	public static List<MessageTypeEnum> listNotifierByType(String type)
	{
		if (MessageTypeEnum.valueOf(type) != null)
		{
			List<MessageTypeEnum> messageType = new ArrayList<MessageTypeEnum>();
			// 项目启动通知// 项目终止通知// 项目暂停// 项目完成 // 项目变更(快速)通知// 项目变更后启动通知
			if (type.equals(MessageTypeEnum.PROJECTSTARTNOTIFY.name()) || type.equals(MessageTypeEnum.PROJECTENDNOTIFY.name())
					|| type.equals(MessageTypeEnum.PROJECTPAUSENOTIFY.name()) || type.equals(MessageTypeEnum.PROJECTCOMPLETENOTIFY.name())
					|| type.equals(MessageTypeEnum.PROJECTCHANGENOTIFY.name()) || type.equals(MessageTypeEnum.PROJECTCHANGEAFTERNOTIFY.name())
					|| type.equals(MessageTypeEnum.PROJECTCANCOMPLETENOTIFY.name()))
			{
				messageType.add(MessageTypeEnum.PROJECTCREATOR);
				messageType.add(MessageTypeEnum.PROJECTMANAGER);
				messageType.add(MessageTypeEnum.PROJECTMEMBER);
			}
			// 任务分配通知// 任务取消通知// 任务暂停通知// 任务完成通知// 任务变更(快速)通知// 任务变更后启动通知
			else if (type.equals(MessageTypeEnum.TASKASSIGNNOTIFY.name()) || type.equals(MessageTypeEnum.TASKENDNOTIFY.name())
					|| type.equals(MessageTypeEnum.TASKPAUSENOTIFY.name()) || type.equals(MessageTypeEnum.TASKCOMPLETENOTIFY.name())
					|| type.equals(MessageTypeEnum.TASKUPDATENOTIFY.name())||type.equals(MessageTypeEnum.TASKAPPNOTIFY.name())
					||type.equals(MessageTypeEnum.TASKCANCELNOTIFY.name())||type.equals(MessageTypeEnum.TASKAPPCOLNOTIFY.name())
					)
			{
				messageType.add(MessageTypeEnum.PROJECTCREATOR);
				messageType.add(MessageTypeEnum.PROJECTMANAGER);
				messageType.add(MessageTypeEnum.PROJECTMEMBER);
				messageType.add(MessageTypeEnum.TASKRESPONSIBLE);
				messageType.add(MessageTypeEnum.TASKPARTICIPATOR);
				messageType.add(MessageTypeEnum.PRETASKRESPONSIBLE);
				messageType.add(MessageTypeEnum.POSTTASKRESPONSIBLE);
				// messageType.add(MessageTypeEnum.SUPERIORTASKRESPONSIBLE);
			}

			// 任务待审核通知
			// else if (type.equals(MessageTypeEnum.TASKAUDITNOTIFY.name()))
			// {
			// messageType.add(MessageTypeEnum.AUDITPERSON);
			// }
			// 任务完成审核通知
			// else if (type.equals(MessageTypeEnum.TASKCOMPLETEAUDITNOTIFY.name()))
			// {
			// messageType.add(MessageTypeEnum.TASKRESPONSIBLE);
			// messageType.add(MessageTypeEnum.TASKPARTICIPATOR);
			// }
			// 前置任务完成通知
			else if (type.equals(MessageTypeEnum.PRETASKCOMPLETENOTIFY.name()))
			{
				messageType.add(MessageTypeEnum.TASKRESPONSIBLE);
				messageType.add(MessageTypeEnum.TASKPARTICIPATOR);
				messageType.add(MessageTypeEnum.POSTTASKRESPONSIBLE);
			}
			// 工作项通知 // 工作项取消通知
			else if (type.equals(MessageTypeEnum.WORKITEMNOTIFY.name()) || type.equals(MessageTypeEnum.WORKITEMCANCELNOTIFY.name())
					|| type.equals(MessageTypeEnum.WORKITEMUPDATENOTIFY.name()))
			{
				messageType.add(MessageTypeEnum.WORKITEMCREATOR);
				messageType.add(MessageTypeEnum.WORKITEMRESPONSIBLE);
				messageType.add(MessageTypeEnum.WORKITEMPARTICIPATOR);
			}

			// 日程通知// 日程取消通知
			// else if (type.equals(MessageTypeEnum.SCHEDULENOTIFY.name()) ||
			// type.equals(MessageTypeEnum.SCHEDULECANCELNOTIFY.name()))
			// {
			// messageType.add(MessageTypeEnum.SCHEDULECREATOR);
			// messageType.add(MessageTypeEnum.SCHEDULEAUDITOR);
			// }

			// // 预警--项目启动前通知/项目逾期启动/项目超期/项目超期x%/项目进度风险预警
			// else if (type.equals(MessageTypeEnum.PSTARTBEFORE) || type.equals(MessageTypeEnum.PSTARTDELAY) ||
			// type.equals(MessageTypeEnum.PCOMPLETEDELAY)
			// || type.equals(MessageTypeEnum.PCOMPLETEDELAYPERCENT) || type.equals(MessageTypeEnum.PPROGRESSRISK))
			// {
			// messageType.add(MessageTypeEnum.PROJECTCREATOR);
			// messageType.add(MessageTypeEnum.PROJECTMANAGER);
			// }
			// // 预警--任务逾期启动/任务启动前通知/任务完成前通知
			// else if (type.equals(MessageTypeEnum.TSTARTDELAY) || type.equals(MessageTypeEnum.TSTARTBEFORE) ||
			// type.equals(MessageTypeEnum.TCOMPLETEBEFORE))
			// {
			// messageType.add(MessageTypeEnum.TASKRESPONSIBLE);
			// messageType.add(MessageTypeEnum.TASKPARTICIPATOR);
			// }
			// // 预警--任务超期/任务超期x%/任务进度风险预警
			// else if (type.equals(MessageTypeEnum.TCOMPLETEDELAY) ||
			// type.equals(MessageTypeEnum.TCOMPLETEDELAYPERCENT) || type.equals(MessageTypeEnum.TPROGRESSRISK))
			// {
			// messageType.add(MessageTypeEnum.TASKRESPONSIBLE);
			// messageType.add(MessageTypeEnum.TASKPARTICIPATOR);
			// messageType.add(MessageTypeEnum.SUPERIORTASKRESPONSIBLE);
			// }
			return messageType;
		}
		return null;
	}
}
