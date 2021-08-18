/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: MailSendStub
 * Wanglei 2011-4-1
 */
package dyna.app.service.brs.sms;

import dyna.app.core.sch.Scheduler;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.boas.BOASImpl;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.SearchCondition;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.SystemObject;
import dyna.common.dto.*;
import dyna.common.dto.aas.User;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.MailCategoryEnum;
import dyna.common.systemenum.MailMessageType;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;
import dyna.net.service.brs.DSS;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * @author Wanglei
 * 
 */
@Component
public class MailSentStub extends AbstractServiceStub<SMSImpl>
{

	protected List<Mail> listSent(SearchCondition searchCondition) throws ServiceRequestException
	{
		List<Mail> mailList = null;
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			Map<String, Object> filter = this.stubService.getMailInboxStub().buildFilterBySearchCondition(searchCondition, null);
			String operatorGuid = this.stubService.getOperatorGuid();
			filter.put(Mail.USER_GUID, operatorGuid);

			mailList = sds.query(Mail.class, filter, "getSent");
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		return mailList;
	}

	/**
	 * 工作流通知
	 * 
	 * @param toUserGuidList
	 * @param fromUseGuid
	 * @param processGuid
	 * @param activityGuid
	 * @param contents
	 * @param title
	 * @param isWFNotice
	 * @param category
	 * @throws ServiceRequestException
	 */
	public void sendMail4WorkFlow(List<String> toUserGuidList, String fromUseGuid, String processGuid, String activityGuid, String contents, String title,
			MailMessageType messageType, MailCategoryEnum category) throws ServiceRequestException
	{
		this.sendMailToUser(title, contents, category, processGuid, activityGuid, messageType, null, null, toUserGuidList, fromUseGuid, null);
	}

	public void sendMail4WorkFlow(List<String> toUserGuidList, String fromUseGuid, String processGuid, String activityGuid, String contents, String title,
			MailMessageType messageType, MailCategoryEnum category, Integer startNumber) throws ServiceRequestException
	{
		this.sendMailToUser(title, contents, category, processGuid, activityGuid, messageType, null, null, toUserGuidList, fromUseGuid, startNumber);
	}

	/**
	 * 多个用户通知
	 * 
	 * @param subject
	 * @param content
	 * @param mailCategoryEnum
	 * @param objectGuidList
	 * @param toUserIdList
	 * @throws ServiceRequestException
	 */
	protected void sendMailToUsers(String subject, String content, MailCategoryEnum mailCategoryEnum, List<ObjectGuid> objectGuidList, List<String> toUserIdList,
			MailMessageType messageType) throws ServiceRequestException
	{
		this.sendMailToUser(subject, content, mailCategoryEnum, null, null, messageType, objectGuidList, null, toUserIdList, this.stubService.getOperatorGuid(), null);
	}

	/**
	 * 单个用户发送通知
	 * 
	 * @param subject
	 * @param content
	 * @param mailCategoryEnum
	 * @param objectGuidList
	 * @param toUserId
	 * @throws ServiceRequestException
	 */
	protected void sendMailToUser(String subject, String content, MailCategoryEnum mailCategoryEnum, List<ObjectGuid> objectGuidList, String toUserId, MailMessageType messageType)
			throws ServiceRequestException
	{
		// this.sendMailToUser(subject, content, mailCategoryEnum, objectGuidList, null, toUserId,
		// this.stubService.getOperatorGuid());
		this.sendMailToUser(subject, content, mailCategoryEnum, null, null, messageType, objectGuidList, null, Arrays.asList(toUserId), this.stubService.getOperatorGuid(), null);

	}

	/**
	 * 报表通知
	 * 
	 * @param subject
	 * @param content
	 * @param mailCategoryEnum
	 * @param toUserId
	 * @param fileList
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<DSSFileTrans> sendMail4Report(String subject, String content, MailCategoryEnum mailCategoryEnum, String toUserId, List<DSSFileInfo> fileList)
			throws ServiceRequestException
	{
		return this.sendMailToUser(subject, content, mailCategoryEnum, null, null, MailMessageType.REPORTNOTIFY, null, fileList, Arrays.asList(toUserId),
				this.stubService.getOperatorGuid(), null);
	}

	/**
	 * 发送方发送邮件给接收方
	 * 
	 * @param subject
	 *            邮件标题
	 * @param content
	 *            邮件内容
	 * @param mailCategoryEnum
	 *            邮件的类别
	 * @param processGuid
	 *            流程guid
	 * @param activityGuid
	 *            流程活动节点guid
	 * @param isWFNotice
	 *            是否为工作流通知
	 * @param fileList
	 *            文件列表
	 * @param objectGuidList
	 *            附件的ObjectGuid的列表
	 * @param toUserIdList
	 *            邮件接收者的Id或者Guid
	 * @param formUserId
	 *            邮件发送者的Id或者Guid
	 * @throws ServiceRequestException
	 */
	private List<DSSFileTrans> sendMailToUser(String subject, String content, MailCategoryEnum mailCategoryEnum, String processGuid, String activityGuid,
			MailMessageType messageType, List<ObjectGuid> objectGuidList, List<DSSFileInfo> fileList, List<String> toUserIdList, String formUserId, Integer startNumber)
			throws ServiceRequestException
	{
		List<DSSFileTrans> fileTransList = new ArrayList<DSSFileTrans>();
		if (SetUtils.isNullList(toUserIdList))
		{
			return fileTransList;
		}

		SystemDataService sds = DataServer.getSystemDataService();

		// 添加接收人列表
		List<String> toUserGuidList = new ArrayList<String>();

		String receiveUserMasterGuid = UUID.randomUUID().toString();
		receiveUserMasterGuid = receiveUserMasterGuid.replaceAll("-", "");

		for (String toUser : toUserIdList)
		{
			MailReceiveUser mailReceiveUser = new MailReceiveUser();
			mailReceiveUser.setMasterGuid(receiveUserMasterGuid);
			if (!StringUtils.isGuid(toUser))
			{
				User user = this.stubService.getAAS().getUserById(toUser);
				if (user != null)
				{
					toUser = user.getGuid();
				}
			}

			if (!toUserGuidList.contains(toUser))
			{
				mailReceiveUser.setReceiveUser(toUser);
				sds.save(mailReceiveUser);
				toUserGuidList.add(toUser);
			}
		}

		String fromUserGuid = null;

		// 转换发件人guid
		if (StringUtils.isGuid(formUserId))
		{
			fromUserGuid = formUserId;
		}
		else
		{
			User fromUser = this.stubService.getAAS().getUserById(formUserId);
			fromUserGuid = fromUser.getGuid();
		}

		String fromMailGuid = null;
		Mail mail = new Mail();
		// 如果有附件则把是否有附件的字段设置为Y,同时组织MailAttachment对象并存入mailAttachmentList中
		boolean isFoAtt = !SetUtils.isNullList(objectGuidList);
		boolean isFileAtt = !SetUtils.isNullList(fileList);
		boolean hasAttachment = isFoAtt || isFileAtt;

		// 设置mail信息
		mail.setHasAttachment(hasAttachment);

		// if (content != null && content.length() > 300)
		// {
		// String substring = content.substring(0, 300);
		// content = substring + "......";
		// }
		//
		// if (subject != null && subject.length() > 80)
		// {
		// String substring = subject.substring(0, 80);
		// subject = substring + "......";
		// }

		// [外部问题单] O20203/A.2-项目经理点击要求成员更新进度报错“联系系统管理员”
		// 内容超出4000
		try
		{
			if (content.getBytes("UTF-8").length > 4000)
			{
				for (int j = 1300; j < content.length(); j++)
				{
					if (content.substring(0, j).getBytes("UTF-8").length + 3 > 4000)
					{
						content = content.substring(0, j - 1) + "...";
						break;
					}
				}
			}
		}
		catch (UnsupportedEncodingException e)
		{

		}

		mail.setTitle(subject);
		mail.setContents(content);
		mail.setCategory(mailCategoryEnum);

		mail.setProcessRuntimeGuid(processGuid);
		mail.setActRuntimeGuid(activityGuid);
		// if (isWFNotice == null)
		// {
		// mail.setWFNotice(false);
		// }
		// else
		// {
		// mail.setWFNotice(isWFNotice);
		// }
		mail.setModuleType(messageType.getValue());
		mail.put(SystemObject.CREATE_USER_GUID, fromUserGuid);
		mail.put(SystemObject.UPDATE_USER_GUID, fromUserGuid);
		mail.setSenderUser(fromUserGuid);
		mail.setReceiveUser(fromUserGuid);
		mail.setRUMasterGuid(receiveUserMasterGuid);
		mail.setStartNumber(startNumber == null ? 0 : startNumber);

		// 添加发送方邮件的处理
		fromMailGuid = sds.save(mail);

		DSSFileTrans fileTrans = null;
		List<MailAttachment> mailAttachmentList = new ArrayList<MailAttachment>();
		if (isFoAtt)
		{
			for (ObjectGuid objectGuid : objectGuidList)
			{
				MailAttachment mailAttachment = new MailAttachment();
				mailAttachment.setInstanceClass(objectGuid.getClassGuid());
				mailAttachment.setInstanceGuid(objectGuid.getGuid());
				FoundationObject foundationObject = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(objectGuid, false);
				mailAttachment.setInstanceTitle(foundationObject.getFullName());
				mailAttachmentList.add(mailAttachment);
			}
		}
		else if (isFileAtt)
		{

			DSS dss = this.stubService.getDSS();
			String filePath = null;
			List<String> fileGuidList = new ArrayList<String>();
			List<String> filePathList = new ArrayList<String>();
			for (DSSFileInfo fileInfo : fileList)
			{
				filePath = fileInfo.getFilePath();

				fileInfo = dss.attachFile4Tab("BI_MAIL", fromMailGuid, fileInfo);
				fileGuidList.add(fileInfo.getGuid());
				filePathList.add(filePath);

				MailAttachment mailAttachment = new MailAttachment();
				mailAttachment.setFileGuid(fileInfo.getGuid());
				mailAttachmentList.add(mailAttachment);
			}

			fileTrans = dss.batchUploadFile(fileGuidList, filePathList);
		}

		// 如果关联了附件，那么添加附件列表
		for (MailAttachment mailAttachment : mailAttachmentList)
		{
			mailAttachment.setMailGuid(fromMailGuid);
			mailAttachment.setCreateUserGuid(fromUserGuid);

			sds.save(mailAttachment);
		}

		if (fileTrans != null)
		{
			fileTransList.add(fileTrans);
		}

		for (String toUserGuid : toUserGuidList)
		{

			// 添加接收邮件的处理
			mail.remove(Mail.GUID);
			mail.setReceiveUser(toUserGuid);
			mail.setOrigMailGuid(fromMailGuid);
			sds.save(mail);

		}

		getMailScheduler()
				.addTask(new MailScheduledTask(this.serviceContext, mail, toUserGuidList, this.stubService.getUserSignature().getLanguageEnum()));

		return fileTransList;
	}

	private Scheduler getMailScheduler()
	{
		Scheduler scheduler=this.serviceContext.getServerContext().getSchedulerManager().getScheduler("MAIL_SEND");
		if (scheduler==null)
		{
			this.serviceContext.getServerContext().getSchedulerManager().createScheduler("MAIL_SEND", 4);
			scheduler=this.serviceContext.getServerContext().getSchedulerManager().getScheduler("MAIL_SEND");
		}
		return scheduler;
	}

	// @Deprecated
	// protected void sendMailToRIG(String subject, String content, MailCategoryEnum mailCategoryEnum,
	// List<ObjectGuid> objectGuidList, List<String> toRIGList) throws ServiceRequestException
	// {
	// if (!SetUtils.isNullList(toRIGList))
	// {
	// for (String rigKey : toRIGList)
	// {
	// String rigGuid = null;
	// if (StringUtils.isGuid(rigKey))
	// {
	// rigGuid = rigKey;
	// }
	// else
	// {
	// RIG rig = this.stubService.getAAS().getRIG(rigGuid);
	// if (rig != null)
	// {
	// rigGuid = rig.getGuid();
	// }
	// }
	//
	// List<User> toUserIdList = this.stubService.getAAS().listUserByRoleInGroup(rigGuid);
	//
	// if (!SetUtils.isNullList(toUserIdList))
	// {
	// for (User user : toUserIdList)
	// {
	// this.sendMailToUser(subject, content, mailCategoryEnum, objectGuidList, user.getGuid());
	// }
	// }
	//
	// }
	// }
	// }

	// @Deprecated
	// protected void sendMailToGroup(String subject, String content, MailCategoryEnum mailCategoryEnum,
	// List<ObjectGuid> objectGuidList, List<String> toGroupIdList) throws ServiceRequestException
	// {
	// if (!SetUtils.isNullList(toGroupIdList))
	// {
	// for (String groupKey : toGroupIdList)
	// {
	// String groupGuid = null;
	// if (StringUtils.isGuid(groupKey))
	// {
	// groupGuid = groupKey;
	// }
	// else
	// {
	// Group group = this.stubService.getAAS().getGroupById(groupKey);
	// if (group != null)
	// {
	// groupGuid = group.getGuid();
	// }
	// }
	//
	// List<User> toUserIdList = this.stubService.getAAS().listUserInGroup(groupGuid);
	//
	// if (!SetUtils.isNullList(toUserIdList))
	// {
	// for (User user : toUserIdList)
	// {
	// this.sendMailToUser(subject, content, mailCategoryEnum, objectGuidList, user.getGuid());
	// }
	// }
	//
	// }
	// }
	// }

	// /**
	// * 客户端主页发送通知
	// *
	// * @param subject
	// * @param content
	// * @param mailCategoryEnum
	// * @param objectGuidList
	// * @param notifierList
	// * @throws ServiceRequestException
	// */
	// protected void sendMailToNotifier(String subject, String content, MailCategoryEnum mailCategoryEnum,
	// List<ObjectGuid> objectGuidList, List<Notifier> notifierList) throws ServiceRequestException
	// {
	// if (!SetUtils.isNullList(notifierList))
	// {
	// List<String> userGuidList = new ArrayList<String>();
	// for (Notifier notifier : notifierList)
	// {
	// if (notifier.getPerfType() == PerformerTypeEnum.GROUP)
	// {
	// List<User> toUserList = ((AASImpl) this.stubService.getAAS()).getUserStub().listUserInGroup(
	// notifier.getPerfGuid(), true);
	// if (!SetUtils.isNullList(toUserList))
	// {
	// for (User user : toUserList)
	// {
	// if (!userGuidList.contains(user.getGuid()))
	// {
	// userGuidList.add(user.getGuid());
	// }
	// }
	// }
	// }
	// else if (notifier.getPerfType() == PerformerTypeEnum.RIG)
	// {
	// List<User> toUserList = null;
	// if (StringUtils.isGuid(notifier.getPerfGuid()))
	// {
	// toUserList = this.stubService.getAAS().listUserByRoleInGroup(notifier.getPerfGuid());
	// }
	// else
	// {
	// toUserList = ((AASImpl) this.stubService.getAAS()).getUserStub().listUserByRoleInGroupGuid(
	// notifier.getPerfGroupGuid(), notifier.getPerfRoleGuid(), true);
	// }
	//
	// if (!SetUtils.isNullList(toUserList))
	// {
	// for (User user : toUserList)
	// {
	// if (!userGuidList.contains(user.getGuid()))
	// {
	// userGuidList.add(user.getGuid());
	// }
	// }
	// }
	// }
	// else if (notifier.getPerfType() == PerformerTypeEnum.USER)
	// {
	// if (!userGuidList.contains(notifier.getPerfGuid()))
	// {
	// userGuidList.add(notifier.getPerfGuid());
	// }
	// }
	// }
	//
	// this.sendMailToUsers(subject, content, mailCategoryEnum, objectGuidList, userGuidList);
	//
	// }
	// }
}
