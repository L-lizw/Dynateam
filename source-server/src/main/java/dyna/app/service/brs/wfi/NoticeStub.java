/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: NoticeStub
 * WangLHB Feb 22, 2012
 */
package dyna.app.service.brs.wfi;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.dto.aas.User;
import dyna.common.dto.wf.ActivityRuntime;
import dyna.common.dto.wf.Performer;
import dyna.common.dto.wf.ProcessRuntime;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.*;
import dyna.common.util.DateFormat;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.net.service.brs.AAS;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author WangLHB
 * 
 */
@Component
public class NoticeStub extends AbstractServiceStub<WFIImpl>
{

	public void activiteCompleteNotice(ActivityRuntime activity, DecisionEnum decide) throws ServiceRequestException
	{
		List<String> comNoticeAllperList = this.stubService.getPerformerStub().listNoticeAllper(activity.getGuid(),
				"3", "Y");
		if (!SetUtils.isNullList(comNoticeAllperList))
		{
			ProcessRuntime processRuntime = this.stubService.getProcessRuntime(activity.getProcessRuntimeGuid());
			LanguageEnum languageEnum = this.stubService.getUserSignature().getLanguageEnum();
			String decideTitle = this.stubService.getMSRM()
					.getMSRString(decide.getMessageId(), languageEnum.toString());

			// "{0}({1})处理结果:{2}";
			String title = this.stubService.getMSRM().getMSRString("ID_APP_WORKFLOW_ACTIVITY_COMPLETE_TITLE",
					languageEnum.toString());
			title = MessageFormat.format(title, activity.getTitle(languageEnum),
					processRuntime.getWFTemplateTitle(languageEnum), decideTitle);

			// "{0}({1})已完成,处理结果为{2}";
			String contents = this.stubService.getMSRM().getMSRString("ID_APP_WORKFLOW_ACTIVITY_COMPLETE_CONTENTS",
					languageEnum.toString());

			contents = MessageFormat.format(contents, activity.getTitle(languageEnum),
					processRuntime.getWFTemplateTitle(languageEnum), decideTitle);

			String receiverGuid = processRuntime.getCreateUserGuid();

			this.stubService
					.getSMS()
					.getMailSentStub()
					.sendMail4WorkFlow(comNoticeAllperList, receiverGuid, activity.getProcessRuntimeGuid(),
							activity.getGuid(), contents, title, MailMessageType.WORKFLOWNOTIFY, MailCategoryEnum.INFO, activity.getStartNumber());
		}
	}

	/**
	 * 将到期工作流活动通知
	 * 
	 * @param activity
	 * @throws ServiceRequestException
	 */
	public void activiteAdvNotice(ActivityRuntime activity) throws ServiceRequestException
	{
		List<String> defNoticeAllperList = this.stubService.getPerformerStub().listNoticeAllper(activity.getGuid(),
				"1", "N");

		if (defNoticeAllperList != null)
		{
			ProcessRuntime processRuntime = this.stubService.getProcessRuntime(activity.getProcessRuntimeGuid());

			if (processRuntime.getStatus() == ProcessStatusEnum.CANCEL
					|| processRuntime.getStatus() == ProcessStatusEnum.OBSOLETE
					|| processRuntime.getStatus() == ProcessStatusEnum.CLOSED)
			{
				return;
			}

			LanguageEnum languageEnum = this.stubService.getUserSignature().getLanguageEnum();

			// "{0}({1})预警";
			String title = this.stubService.getMSRM().getMSRString("ID_APP_WORKFLOW_ACTIVITY_ADV_TITLE",
					languageEnum.toString());
			title = MessageFormat.format(title, activity.getTitle(languageEnum),
					processRuntime.getWFTemplateTitle(languageEnum));

			// "{0}({1}){2}的截止完成时间只剩{3}天";
			String contents = this.stubService.getMSRM().getMSRString("ID_APP_WORKFLOW_ACTIVITY_ADV_CONTENTS",
					languageEnum.toString());
			contents = MessageFormat.format(contents, processRuntime.getWFTemplateTitle(languageEnum),
					processRuntime.getDescription(), activity.getTitle(languageEnum),

					DateFormat.getDifferenceDay(activity.getDeadline(), this.stubService.getEMM().getSystemDate()));

			String receiverGuid = processRuntime.getCreateUserGuid();

			this.stubService
					.getSMS()
					.getMailSentStub()
					.sendMail4WorkFlow(defNoticeAllperList, receiverGuid, activity.getProcessRuntimeGuid(),
							activity.getGuid(), contents, title, MailMessageType.WORKFLOWNOTIFY, MailCategoryEnum.INFO, activity.getStartNumber());
		}

	}

	/**
	 * 超期工作流活动通知
	 * 
	 * @param activity
	 * @throws ServiceRequestException
	 */
	public void activiteDefNotice(ActivityRuntime activity) throws ServiceRequestException
	{
		List<String> defNoticeAllperList = this.stubService.getPerformerStub().listNoticeAllper(activity.getGuid(),
				"2", "N");

		if (defNoticeAllperList != null)
		{
			ProcessRuntime processRuntime = this.stubService.getProcessRuntime(activity.getProcessRuntimeGuid());

			if (processRuntime.getStatus() == ProcessStatusEnum.CANCEL
					|| processRuntime.getStatus() == ProcessStatusEnum.OBSOLETE
					|| processRuntime.getStatus() == ProcessStatusEnum.CLOSED)
			{
				return;
			}

			// {activity_title}已超期{系统时间-截止时间}天（{workflowTemplate_name}）
			LanguageEnum languageEnum = this.stubService.getUserSignature().getLanguageEnum();

			// "{0}({1})已超期";
			String title = this.stubService.getMSRM().getMSRString("ID_APP_WORKFLOW_ACTIVITY_DEF_TITLE",
					languageEnum.toString());
			title = MessageFormat.format(title, activity.getTitle(languageEnum),
					processRuntime.getWFTemplateTitle(languageEnum));
			// "{0}({1}){2}已超期{3}天";
			String contents = this.stubService.getMSRM().getMSRString("ID_APP_WORKFLOW_ACTIVITY_DEF_CONTENTS",
					languageEnum.toString());
			contents = MessageFormat.format(contents, processRuntime.getWFTemplateTitle(languageEnum),
					processRuntime.getDescription(), activity.getTitle(languageEnum),
					// DateFormat.getDate(ServerFactory.getDataService().getSystemDate())
					// - DateFormat.getDate(activity.getDeadline()));

					DateFormat.getDifferenceDay(this.stubService.getEMM().getSystemDate(), activity.getDeadline()));

			String receiverGuid = processRuntime.getCreateUserGuid();
			// 10.17 变更 流程消息的”发送者”改为流程的创建者

			this.stubService
					.getSMS()
					.getMailSentStub()
					.sendMail4WorkFlow(defNoticeAllperList, receiverGuid, activity.getProcessRuntimeGuid(),
							activity.getGuid(), contents, title, MailMessageType.WORKFLOWNOTIFY,
							MailCategoryEnum.WARNING, activity.getStartNumber());
		}
	}

	/**
	 * 
	 * 通知主题为{workflowTemplate_name}+“[”+{workflow_Description}+“]流程已启动”；
	 * 通知内容为{workflow_Owner}已于{workflow_CreateTime}启动{workflowTemplate_name}+“[”+{workflow_Description}+“]流程，点击附件，
	 * 可查看流程详细信息”。
	 * 通知附件为{workflowTemplate_name}。点击链接，打开流程。
	 * 
	 * @param procRtGuid
	 * @throws ServiceRequestException
	 */
	public void processStartUpNotice(String procRtGuid) throws ServiceRequestException
	{
		ProcessRuntime processRuntime = this.stubService.getProcessRuntime(procRtGuid);

		try
		{
			if (processRuntime != null)
			{
				List<String> openAllperList = this.stubService.getPerformerStub().listProcessNoticeAllper(procRtGuid,
						"1");

				if (!SetUtils.isNullList(openAllperList))
				{

					LanguageEnum languageEnum = this.stubService.getUserSignature().getLanguageEnum();

					String status = "";
					if (processRuntime.getStatus() != null)
					{
						status = this.stubService.getMSRM().getMSRString(processRuntime.getStatus().getMsrId(),
								languageEnum.toString());
					}

					// "{0}[{1}]流程已启动";
					String title = this.stubService.getMSRM().getMSRString("ID_APP_WORKFLOW_STARTUP_NOTICE_TITLE",
							languageEnum.toString());
					title = MessageFormat.format(title, processRuntime.getWFTemplateTitle(languageEnum),
							processRuntime.getDescription(), status);

					// "{0}已于{1}启动 {2}[{3}]流程点击附件，可查看流程详细信息";
					String contents = this.stubService.getMSRM().getMSRString(
							"ID_APP_WORKFLOW_STARTUP_NOTICE_CONTENTS", languageEnum.toString());
					contents = MessageFormat.format(contents, processRuntime.getCreateUserName(),
							DateFormat.formatYMDHMS(processRuntime.getCreateTime()),
							processRuntime.getWFTemplateTitle(languageEnum), processRuntime.getDescription());

					String receiverGuid = processRuntime.getCreateUserGuid();

					this.stubService
							.getSMS()
							.getMailSentStub()
							.sendMail4WorkFlow(openAllperList, receiverGuid, procRtGuid, null, contents.toString(),
									title.toString(), MailMessageType.WORKFLOWNOTIFY, MailCategoryEnum.INFO);
				}
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	/**
	 * 通知主题为{workflowTemplate_name}+“[”+{workflow_Description}+“]流程”+{workflow_Status}；
	 * 通知内容为{workflowTemplate_name}+“[”+{workflow_Description}+“]流程于”+{workflow_CloseTime}+{workflow_Status}，点击附件，
	 * 可查看流程详细信息”。
	 * 通知附件为{workflowTemplate_name}。点击链接，打开流程。
	 * 
	 * @param procRtGuid
	 * @throws ServiceRequestException
	 */
	public void processCompleteNotice(String procRtGuid) throws ServiceRequestException
	{

		ProcessRuntime processRuntime = this.stubService.getProcessRuntime(procRtGuid);

		try
		{
			if (processRuntime != null)
			{
				List<String> completeAllperList = this.stubService.getPerformerStub().listProcessNoticeAllper(
						procRtGuid, "2");

				if (!SetUtils.isNullList(completeAllperList))
				{
					LanguageEnum languageEnum = this.stubService.getUserSignature().getLanguageEnum();
					String status = "";
					if (processRuntime.getStatus() != null)
					{
						status = this.stubService.getMSRM().getMSRString(processRuntime.getStatus().getMsrId(),
								languageEnum.toString());
					}

					// "{0}[{1}]流程{2}";
					String title = this.stubService.getMSRM().getMSRString("ID_APP_WORKFLOW_COMPLETE_NOTICE_TITLE",
							languageEnum.toString());
					title = MessageFormat.format(title, processRuntime.getWFTemplateTitle(languageEnum),
							processRuntime.getDescription(), status);

					// "{0}[{1}]流程于{2}{3}点击附件，可查看流程详细信息";
					String contents = this.stubService.getMSRM().getMSRString(
							"ID_APP_WORKFLOW_COMPLETE_NOTICE_CONTENTS", languageEnum.toString());
					contents = MessageFormat.format(contents, processRuntime.getWFTemplateTitle(languageEnum),
							processRuntime.getDescription(), DateFormat.formatYMDHMS(processRuntime.getFinishTime()),
							status);

					String receiverGuid = processRuntime.getCreateUserGuid();
					// 10.17 变更 流程消息的”发送者”改为流程的创建者
					this.stubService
							.getSMS()
							.getMailSentStub()
							.sendMail4WorkFlow(completeAllperList, receiverGuid, procRtGuid, null, contents, title,
									MailMessageType.WORKFLOWNOTIFY, MailCategoryEnum.INFO);
				}
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	/**
	 * 发送邮件
	 * 
	 * @param listPerformer
	 * @param procGuid
	 * @param actRtGuid
	 * @throws ServiceRequestException
	 */
	public void sendPerformMail(List<Performer> listPerformer, ActivityRuntime activity, MailMessageType messageType)
			throws ServiceRequestException
	{
		Set<String> toUserGuidSet = null;
		AAS aas = this.stubService.getAAS();
		if (!SetUtils.isNullList(listPerformer))
		{
			toUserGuidSet = new HashSet<String>();
			for (Performer perf : listPerformer)
			{
				if (perf.getPerformerType() == PerformerTypeEnum.USER)
				{
					toUserGuidSet.add(perf.getPerformerGuid());
				}
				else
				{
					List<User> listUser = null;
					if (perf.getPerformerType() == PerformerTypeEnum.RIG)
					{
						listUser = aas.listUserByRoleInGroup(perf.getPerformerGuid());

					}
					else if (perf.getPerformerType() == PerformerTypeEnum.GROUP)
					{
						listUser = aas.listUserInGroupAndSubGroup(perf.getPerformerGuid());
					}
					if (!SetUtils.isNullList(listUser))
					{
						for (User user : listUser)
						{
							toUserGuidSet.add(user.getGuid());
						}
					}
				}
			}
			List<String> toUserGuidList = new ArrayList<String>();
			if (!SetUtils.isNullSet(toUserGuidSet))
			{
				toUserGuidList.addAll(toUserGuidSet);
			}

			ProcessRuntime processRuntime = this.stubService.getProcessRuntime(activity.getProcessRuntimeGuid());
			LanguageEnum languageEnum = this.stubService.getUserSignature().getLanguageEnum();

			String title = StringUtils.isNullString(processRuntime.getDescription()) ? StringUtils
					.convertNULLtoString(processRuntime.getDescription()) : processRuntime.getDescription();

			String actTitle = activity.getTitle(languageEnum);
			actTitle = actTitle == null ? "" : actTitle;
			String contents = actTitle
					+ "("
					+ (StringUtils.isNullString(processRuntime.getWFTemplateTitle(languageEnum)) ? actTitle
							: processRuntime.getWFTemplateTitle(languageEnum)) + ")";

			String receiverGuid = processRuntime.getCreateUserGuid();

			this.stubService
					.getSMS()
					.getMailSentStub()
					.sendMail4WorkFlow(toUserGuidList, receiverGuid, activity.getProcessRuntimeGuid(),
							activity.getGuid(), contents, title, messageType, MailCategoryEnum.INFO, activity.getStartNumber());

		}
	}
}
