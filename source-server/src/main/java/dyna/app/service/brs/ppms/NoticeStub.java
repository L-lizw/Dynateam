/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 通知管理
 * wanglhb 2013-10-21
 */

package dyna.app.service.brs.ppms;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.boas.BOASImpl;
import dyna.app.service.brs.ppms.app.MessageDecode;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.ppms.*;
import dyna.common.dto.aas.User;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.systemenum.MailCategoryEnum;
import dyna.common.systemenum.MailMessageType;
import dyna.common.systemenum.ppms.MessageTypeEnum;
import dyna.common.systemenum.ppms.ProjectOrTaskEnum;
import dyna.common.util.PMConstans;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author WangLHB
 *         通知管理
 * 
 */
public class NoticeStub extends AbstractServiceStub<PPMSImpl>
{

	private static final int MESSAGECOUNT = 13;

	protected NoticeStub(ServiceContext context, PPMSImpl service)
	{
		super(context, service);
	}

	public List<MessageRule> listProjectNotifyRule() throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> filter = new HashMap<String, Object>();
		try
		{
			List<MessageRule> messageRules = sds.query(MessageRule.class, filter);
			if (!SetUtils.isNullList(messageRules))
			{
				for (MessageRule rule : messageRules)
				{
					List<MessageTypeEnum> listnf = this.listRuleNotifiers(rule.getGuid());
					if (!SetUtils.isNullList(listnf))
					{
						rule.setNotifierList(listnf);
					}
				}
			}
			return messageRules;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

	public List<MessageRule> saveProjectNotifyRule(List<MessageRule> messageRule) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		String operatorGuid = this.stubService.getOperatorGuid();
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());
			if (!SetUtils.isNullList(messageRule))
			{
				int i = messageRule.size();
				this.initOtherMessageType(messageRule);

				for (MessageRule rule : messageRule)
				{
					rule.put(SystemObject.UPDATE_USER_GUID, operatorGuid);
					if (!StringUtils.isGuid(rule.getGuid()))
					{
						rule.put(SystemObject.CREATE_USER_GUID, operatorGuid);
						if (rule.get("@RULEINIT") != null || i == MESSAGECOUNT)
						{
							this.setDefaultNotifierList(rule, rule.getMessagetype().name());
							rule.clear("@RULEINIT");
						}
					}
					String guid = sds.save(rule);
					if (StringUtils.isGuid(guid))
					{
						rule.setGuid(guid);
					}
					// 保存通知人类型
					if (!SetUtils.isNullList(rule.getNotifierList()))
					{
						// 先删掉原先的类型
						this.deleteNotifiersByRuleGuid(rule.getGuid());
						// 保存
						this.saveRuleNotifier(rule.getNotifierList(), rule.getGuid());

					}
					else
					{
						this.deleteNotifiersByRuleGuid(rule.getGuid());
					}
				}
			}

//			DataServer.getTransactionManager().commitTransaction();
		}

		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (Exception e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}
		return this.listProjectNotifyRule();
	}

	/**
	 * 若传入参数中不含所有MessageType,则将其他MessageType放入
	 * 
	 * @param messageRule
	 * @throws ServiceRequestException
	 */
	private void initOtherMessageType(List<MessageRule> messageRule) throws ServiceRequestException
	{
		if (!SetUtils.isNullList(messageRule))
		{
			Map<MessageTypeEnum, MessageRule> saveMap = this.getSaveMessageRuleMap(messageRule);
			List<MessageTypeEnum> list = this.getSaveMessageTypeEnum();
			List<MessageRule> existList = this.listProjectNotifyRule();
			if (SetUtils.isNullList(existList))
			{
				if (messageRule.size() != MESSAGECOUNT)
				{
					for (MessageTypeEnum typeEnum : list)
					{
						if (saveMap.get(typeEnum) == null)
						{
							MessageRule rule = new MessageRule(typeEnum);
							String name = this.stubService.getMSRM().getMSRString(typeEnum.getMsrId(), this.stubService.getUserSignature().getLanguageEnum().toString());
							rule.setName(name);
							rule.setTheme(name);
							rule.put("@RULEINIT", "Y");
							messageRule.add(rule);
						}
					}
				}
			}
			else
			{
				if (existList.size() != MESSAGECOUNT && messageRule.size() != MESSAGECOUNT)
				{
					Map<MessageTypeEnum, MessageRule> existMap = this.getSaveMessageRuleMap(existList);
					for (MessageTypeEnum typeEnum : list)
					{
						if (saveMap.get(typeEnum) == null && existMap.get(typeEnum) == null)
						{
							MessageRule rule = new MessageRule(typeEnum);
							String name = this.stubService.getMSRM().getMSRString(typeEnum.getMsrId(), this.stubService.getUserSignature().getLanguageEnum().toString());
							rule.setName(name);
							rule.setTheme(name);
							rule.put("@RULEINIT", "Y");
							messageRule.add(rule);
						}
					}
				}
			}
		}
	}

	/**
	 * 需要保存的消息枚举
	 * 
	 * @return
	 */
	private List<MessageTypeEnum> getSaveMessageTypeEnum()
	{
		List<MessageTypeEnum> list = new ArrayList<MessageTypeEnum>();

		list.add(MessageTypeEnum.PROJECTSTARTNOTIFY);
		list.add(MessageTypeEnum.PROJECTENDNOTIFY);
		list.add(MessageTypeEnum.PROJECTPAUSENOTIFY);
		list.add(MessageTypeEnum.PROJECTCANCOMPLETENOTIFY);
		list.add(MessageTypeEnum.PROJECTCOMPLETENOTIFY);
		list.add(MessageTypeEnum.PROJECTCHANGENOTIFY);
		list.add(MessageTypeEnum.PROJECTCHANGEAFTERNOTIFY);
		list.add(MessageTypeEnum.TASKASSIGNNOTIFY);
		list.add(MessageTypeEnum.TASKENDNOTIFY);
		list.add(MessageTypeEnum.TASKPAUSENOTIFY);
		list.add(MessageTypeEnum.TASKCOMPLETENOTIFY);
		list.add(MessageTypeEnum.PRETASKCOMPLETENOTIFY);
		list.add(MessageTypeEnum.TASKUPDATENOTIFY);
		list.add(MessageTypeEnum.WORKITEMNOTIFY);
		list.add(MessageTypeEnum.WORKITEMCANCELNOTIFY);
		list.add(MessageTypeEnum.WORKITEMUPDATENOTIFY);
		return list;
	}

	/**
	 * @param messageRule
	 * @return
	 */
	private Map<MessageTypeEnum, MessageRule> getSaveMessageRuleMap(List<MessageRule> messageRule)
	{
		Map<MessageTypeEnum, MessageRule> tempValue = new HashMap<MessageTypeEnum, MessageRule>();
		if (!SetUtils.isNullList(messageRule))
		{
			for (MessageRule rule : messageRule)
			{
				tempValue.put(rule.getMessagetype(), rule);
			}

		}
		return tempValue;
	}

	/**
	 * @param notifierList
	 * @param guid
	 * @throws ServiceRequestException
	 */
	private void saveRuleNotifier(List<MessageTypeEnum> notifierList, String ruleGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		String operatorGuid = this.stubService.getOperatorGuid();
		try
		{
			for (MessageTypeEnum typeEnum : notifierList)
			{
				MessageRule notifier = new MessageRule();
				notifier.put("NOTIFIER", typeEnum.name());
				notifier.put("MESSAGERULEGUID", ruleGuid);
				notifier.put(SystemObject.UPDATE_USER_GUID, operatorGuid);
				notifier.put(SystemObject.CREATE_USER_GUID, operatorGuid);
				sds.save(notifier, "insertMessageNotifier");
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	/**
	 * @param guid
	 * @throws ServiceRequestException
	 */
	private void deleteNotifiersByRuleGuid(String messageRuleGuid) throws ServiceRequestException
	{
		if (StringUtils.isGuid(messageRuleGuid))
		{
			SystemDataService sds = DataServer.getSystemDataService();
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put("MESSAGERULEGUID", messageRuleGuid);
			try
			{
				List<MessageTypeEnum> notifierList = this.listRuleNotifiers(messageRuleGuid);
				if (!SetUtils.isNullList(notifierList))
				{
					sds.delete(MessageRule.class, filter, "deleteMessageNotifier");
				}
			}
			catch (DynaDataException e)
			{
				throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
			}
		}

	}

	/**
	 * @param messageRuleGuid
	 * @throws ServiceRequestException
	 */
	private List<MessageTypeEnum> listRuleNotifiers(String messageRuleGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put("MESSAGERULEGUID", messageRuleGuid);
		try
		{
			List<MessageTypeEnum> value = new ArrayList<MessageTypeEnum>();
			List<MessageRule> notifierList = sds.query(MessageRule.class, filter, "selectMessageNotifier");
			if (!SetUtils.isNullList(notifierList))
			{
				for (MessageRule notify : notifierList)
				{
					if (notify.get("NOTIFIER") != null && notify.get("NOTIFIER") != "" && MessageTypeEnum.valueOf((String) notify.get("NOTIFIER")) != null)
					{
						value.add(MessageTypeEnum.valueOf((String) notify.get("NOTIFIER")));
					}
				}
			}
			return value;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	/**
	 * 通过类型取得规则--初始化为全勾上
	 * 
	 * @param messagetype
	 * @return
	 * @throws ServiceRequestException
	 */
	public MessageRule getProjectNotifyRule(String messagetype) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(MessageRule.MESSAGETYPE, messagetype);
		try
		{
			List<MessageRule> messageRules = sds.query(MessageRule.class, filter);
			if (!SetUtils.isNullList(messageRules))
			{
				if (messageRules.get(0) != null)
				{
					messageRules.get(0).setNotifierList(this.listRuleNotifiers(messageRules.get(0).getGuid()));
				}
				return messageRules.get(0);
			}
			else
			{
				MessageTypeEnum messageEnum = MessageTypeEnum.valueOf(messagetype);
				MessageRule rule = new MessageRule(MessageTypeEnum.valueOf(messagetype));
				String name = this.stubService.getMSRM().getMSRString(messageEnum.getMsrId(), this.stubService.getUserSignature().getLanguageEnum().toString());
				rule.setName(name);
				rule.setTheme(name);
				this.setDefaultNotifierList(rule, messagetype);
				return rule;
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	/**
	 * 通过消息类型取得每种类型的默认值
	 * 
	 * @param rule
	 * @param messagetype
	 */
	private void setDefaultNotifierList(MessageRule rule, String messagetype)
	{
		if (rule != null && messagetype != null)
		{
			List<MessageTypeEnum> messageTypeList = new ArrayList<MessageTypeEnum>();
			// 项目启动通知// 项目终止通知// 项目暂停// 项目完成 // 项目变更(快速)通知// 项目变更后启动通知
			if (messagetype.equals(MessageTypeEnum.PROJECTSTARTNOTIFY.name()) || messagetype.equals(MessageTypeEnum.PROJECTENDNOTIFY.name())
					|| messagetype.equals(MessageTypeEnum.PROJECTPAUSENOTIFY.name()) || messagetype.equals(MessageTypeEnum.PROJECTCOMPLETENOTIFY.name())
					|| messagetype.equals(MessageTypeEnum.PROJECTCHANGENOTIFY.name()) || messagetype.equals(MessageTypeEnum.PROJECTCHANGEAFTERNOTIFY.name())
					|| messagetype.equals(MessageTypeEnum.PROJECTCANCOMPLETENOTIFY.name()))
			{
				messageTypeList.add(MessageTypeEnum.PROJECTCREATOR);
				messageTypeList.add(MessageTypeEnum.PROJECTMANAGER);
				messageTypeList.add(MessageTypeEnum.PROJECTMEMBER);
			}
			// 任务分配通知// 任务取消通知// 任务暂停通知//任务变更(快速)通知// 任务变更后启动通知
			else if (messagetype.equals(MessageTypeEnum.TASKASSIGNNOTIFY.name()) || messagetype.equals(MessageTypeEnum.TASKENDNOTIFY.name())
					|| messagetype.equals(MessageTypeEnum.TASKPAUSENOTIFY.name()) || messagetype.equals(MessageTypeEnum.TASKUPDATENOTIFY.name())
					|| messagetype.equals(MessageTypeEnum.TASKAPPNOTIFY.name()) || messagetype.equals(MessageTypeEnum.TASKCANCELNOTIFY.name())
					|| messagetype.equals(MessageTypeEnum.TASKAPPCOLNOTIFY.name()))
			{
				messageTypeList.add(MessageTypeEnum.TASKRESPONSIBLE);
				messageTypeList.add(MessageTypeEnum.TASKPARTICIPATOR);
			}
			// 任务完成通知// 前置任务完成通知
			else if (messagetype.equals(MessageTypeEnum.TASKCOMPLETENOTIFY.name()))
			{
				messageTypeList.add(MessageTypeEnum.TASKRESPONSIBLE);
				messageTypeList.add(MessageTypeEnum.TASKPARTICIPATOR);
				messageTypeList.add(MessageTypeEnum.POSTTASKRESPONSIBLE);
			}
			else if (messagetype.equals(MessageTypeEnum.PRETASKCOMPLETENOTIFY.name()))
			{
				messageTypeList.add(MessageTypeEnum.POSTTASKRESPONSIBLE);
			}
			else if (messagetype.equals(MessageTypeEnum.WORKITEMNOTIFY.name()) || messagetype.equals(MessageTypeEnum.WORKITEMCANCELNOTIFY.name())
					|| messagetype.equals(MessageTypeEnum.WORKITEMUPDATENOTIFY.name()))
			{
				messageTypeList.add(MessageTypeEnum.WORKITEMCREATOR);
				messageTypeList.add(MessageTypeEnum.WORKITEMRESPONSIBLE);
				messageTypeList.add(MessageTypeEnum.WORKITEMPARTICIPATOR);
			}
			rule.setNotifierList(messageTypeList);
			rule.setNoticerule(MessageTypeEnum.valueOf(messagetype).getmessageContentMsId());
		}

	}

	/**
	 * 
	 * @param util
	 * @param dealDate
	 * @param enumType
	 * @param msrId
	 * @param type
	 *            1:适用发送任务审核请求,任务审核结果通知
	 *            2:适用任务终止,任务暂停,任务完成
	 *            3:任务变更(快速),任务变更后启动,前置任务完成通知
	 *            4:任务分配
	 * @throws ServiceRequestException
	 */
	public void sendMail(FoundationObject project, FoundationObject task, MessageTypeEnum enumType, Object... params) throws ServiceRequestException
	{
		MessageRule rule = this.getProjectNotifyRule(enumType.name());
		if (rule != null && rule.isStartInstationinfo())
		{
			List<MessageTypeEnum> notifyType = rule.getNotifierList();
			if (!SetUtils.isNullList(notifyType) && notifyType.size() > 0)
			{
				Map<MessageTypeEnum, List<User>> notifierMap = null;
				// 取得发送人Map
				if (task != null)
				{
					if (MessageTypeEnum.WORKITEMNOTIFY == enumType || MessageTypeEnum.WORKITEMCANCELNOTIFY == enumType || MessageTypeEnum.WORKITEMUPDATENOTIFY == enumType)
					{
						// 工作项
						notifierMap = this.getWorkItemNotifierMap(task.getObjectGuid());
					}
					else
					{
						// 任务
						if (params != null && params.length > 0 && params[0] != null)
						{
							notifierMap = this.getTaskNotifierMap(((FoundationObject) params[0]).getObjectGuid());
						}
						else
						{
							notifierMap = this.getTaskNotifierMap(task.getObjectGuid());
						}
					}
				}
				else
				{
					notifierMap = this.getProjectNotifierMap(project.getObjectGuid());
				}

				if (!SetUtils.isNullMap(notifierMap))
				{
					// 取得发送邮件人(已经过滤掉重复值)
					List<String> sendUser = this.listSendMailUser(notifyType, notifierMap);
					// 发送邮件
					if (!SetUtils.isNullList(sendUser))
					{
						LanguageEnum languageEnum = this.stubService.getUserSignature().getLanguageEnum();

						String contents = this.stubService.getMSRM().getMSRString(enumType.getmessageContentMsId(), languageEnum.toString());

						if (StringUtils.isNullString(contents))
						{
							contents = enumType.getmessageContentMsId();
						}

						contents = MessageDecode.decodeMessage(project, task, contents, null, null);
						contents = contents.replace(PMConstans.SENDER_NOTICE_STRING, this.stubService.getUserSignature().getUserName());
						if (params != null && params.length > 0 && params[0] != null)
						{
							contents = contents.replace(PMConstans.PRETASK_NOTICE_STRING, StringUtils.convertNULLtoString(((FoundationObject) params[0]).getName()));
						}

						List<ObjectGuid> attachList = new ArrayList<ObjectGuid>();
						if (enumType != null && enumType.getType() == ProjectOrTaskEnum.PROJECT)
						{
							attachList.add(project.getObjectGuid());
						}
						else if (enumType != null && enumType.getType() == ProjectOrTaskEnum.TASK && task != null)
						{
							attachList.add(task.getObjectGuid());
						}

						for (String user : sendUser)
						{
							User user2 = this.stubService.getAAS().getUser(user);
							String userContents = contents.replace(PMConstans.RECEIVER_NOTICE_STRING, user2.getName());
							this.stubService.getSMS().sendMailToUser(rule.getTheme(), userContents, MailCategoryEnum.INFO, attachList, user, MailMessageType.TASKNOTIFY);
						}
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param util
	 * @param dealDate
	 * @param enumType
	 * @param msrId
	 * @param type
	 *            1:适用发送任务审核请求,任务审核结果通知
	 *            2:适用任务终止,任务暂停,任务完成
	 *            3:任务变更(快速),任务变更后启动,前置任务完成通知
	 *            4:任务分配
	 * @throws ServiceRequestException
	 */
	public void sendChangeProjectManagerMail(FoundationObject project) throws ServiceRequestException
	{

		LanguageEnum languageEnum = this.stubService.getUserSignature().getLanguageEnum();

		String contents = this.stubService.getMSRM().getMSRString(MessageTypeEnum.PROJECTEXECUTORCHANGENOTIFY.getmessageContentMsId(), languageEnum.toString());

		if (StringUtils.isNullString(contents))
		{
			contents = MessageTypeEnum.PROJECTEXECUTORCHANGENOTIFY.getmessageContentMsId();
		}

		String subject = this.stubService.getMSRM().getMSRString(MessageTypeEnum.PROJECTEXECUTORCHANGENOTIFY.getMsrId(), languageEnum.toString());

		if (StringUtils.isNullString(subject))
		{
			subject = MessageTypeEnum.PROJECTEXECUTORCHANGENOTIFY.getMsrId();
		}

		contents = MessageDecode.decodeMessage(project, null, contents, this.stubService.getMSRM().getMSRMap(languageEnum.toString()), languageEnum);
		contents = contents.replace(PMConstans.SENDER_NOTICE_STRING, this.stubService.getUserSignature().getUserName());

		List<ObjectGuid> attachList = new ArrayList<ObjectGuid>();

		attachList.add(project.getObjectGuid());

		PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(project);
		User user2 = this.stubService.getAAS().getUser(util.getExecutor());
		if (user2 != null)
		{
			String userContents = contents.replace(PMConstans.RECEIVER_NOTICE_STRING, user2.getName());
			this.stubService.getSMS().sendMailToUser(subject, userContents, MailCategoryEnum.INFO, attachList, user2.getGuid(), MailMessageType.PROJECTNOTIFY);
		}
	}

	/**
	 * @param notifyType
	 * @param notifierMap
	 * @return
	 */
	private List<String> listSendMailUser(List<MessageTypeEnum> notifyType, Map<MessageTypeEnum, List<User>> notifierMap)
	{
		List<String> value = new ArrayList<String>();
		for (MessageTypeEnum typeEnum : notifyType)
		{
			switch (typeEnum)
			{
			case PROJECTCREATOR:
				// 项目创建者--一人
				this.listOneUserOfNotice(notifierMap, MessageTypeEnum.PROJECTCREATOR, value);
				break;
			case PROJECTMANAGER:
				// 项目经理--一人
				this.listOneUserOfNotice(notifierMap, MessageTypeEnum.PROJECTMANAGER, value);
				break;
			case PROJECTMEMBER:
				// 项目成员--多人
				this.listMoreUserOfNotice(notifierMap, MessageTypeEnum.PROJECTMEMBER, value);
				break;
			case TASKRESPONSIBLE:
				// 任务责任人--一人
				this.listOneUserOfNotice(notifierMap, MessageTypeEnum.TASKRESPONSIBLE, value);
				break;
			case TASKPARTICIPATOR:
				// 任务参与者--多人
				this.listMoreUserOfNotice(notifierMap, MessageTypeEnum.TASKPARTICIPATOR, value);
				break;
			case PRETASKRESPONSIBLE:
				// 前置任务--多人
				this.listMoreUserOfNotice(notifierMap, MessageTypeEnum.PRETASKRESPONSIBLE, value);
				break;
			case POSTTASKRESPONSIBLE:
				// 后置任务--多人
				this.listMoreUserOfNotice(notifierMap, MessageTypeEnum.POSTTASKRESPONSIBLE, value);
				break;
			case SUPERIORTASKRESPONSIBLE:
				// 上级任务责任人--一人
				this.listOneUserOfNotice(notifierMap, MessageTypeEnum.SUPERIORTASKRESPONSIBLE, value);
				break;
			case WORKITEMPARTICIPATOR:
				// 工作项参与者
				this.listMoreUserOfNotice(notifierMap, MessageTypeEnum.WORKITEMPARTICIPATOR, value);
				break;
			case WORKITEMRESPONSIBLE:
				// 工作项责任人
				this.listOneUserOfNotice(notifierMap, MessageTypeEnum.WORKITEMRESPONSIBLE, value);
				break;
			case WORKITEMCREATOR:
				// 工作项创建者
				this.listOneUserOfNotice(notifierMap, MessageTypeEnum.WORKITEMCREATOR, value);
				break;
			default:
				break;
			}
		}
		return value;
	}

	/**
	 * 取得多个通知人
	 * 
	 * @param notifierMap
	 * @param messageType
	 * @param value
	 */
	private void listMoreUserOfNotice(Map<MessageTypeEnum, List<User>> notifierMap, MessageTypeEnum messageType, List<String> value)
	{
		List<User> userList = notifierMap.get(messageType);
		if (!SetUtils.isNullList(userList) && userList.size() > 0)
		{
			for (User u : userList)
			{
				if (!value.contains(u.getGuid()))
				{
					value.add(u.getGuid());
				}
			}
		}
	}

	/**
	 * 取得需要通知的人
	 * 
	 * @param notifierMap
	 * @param projectcreator
	 * @param value
	 */
	private void listOneUserOfNotice(Map<MessageTypeEnum, List<User>> notifierMap, MessageTypeEnum messageType, List<String> value)
	{
		List<User> userList = notifierMap.get(messageType);
		if (!SetUtils.isNullList(userList) && userList.size() > 0)
		{
			if (!value.contains(userList.get(0).getGuid()))
			{
				value.add(userList.get(0).getGuid());
			}
		}
	}

	/**
	 * @param objectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public Map<MessageTypeEnum, List<User>> getWorkItemNotifierMap(ObjectGuid workItemObjectGuid) throws ServiceRequestException
	{
		Map<MessageTypeEnum, List<User>> value = null;
		if (workItemObjectGuid != null)
		{
			value = new HashMap<MessageTypeEnum, List<User>>();
			FoundationObject foundation = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(workItemObjectGuid, false);
			PPMFoundationObjectUtil workItemFoun = new PPMFoundationObjectUtil(foundation);
			if (workItemFoun != null)
			{
				// 工作项创建者
				if (!StringUtils.isNullString(workItemFoun.getFoundation().getCreateUserGuid()))
				{
					listUserOfType(value, MessageTypeEnum.WORKITEMCREATOR, workItemFoun.getFoundation().getCreateUserGuid());
				}
				// 工作项责任人
				if (!StringUtils.isNullString(workItemFoun.getExecutor()))
				{
					listUserOfType(value, MessageTypeEnum.WORKITEMRESPONSIBLE, workItemFoun.getExecutor());
				}
				// 工作项参与者
				if (foundation.getObjectGuid() != null && foundation.getObjectGuid().getGuid() != null)
				{
					List<User> listUser = this.getAllMenbersInTask(foundation.getObjectGuid());
					if (!SetUtils.isNullList(listUser))
					{
						value.put(MessageTypeEnum.WORKITEMPARTICIPATOR, listUser);
					}
				}
			}
		}
		return value;
	}

	/**
	 * 通过类型取得具体的User
	 * 
	 * @param value
	 * @param messageType
	 * @param userGuid
	 * @throws ServiceRequestException
	 */
	private void listUserOfType(Map<MessageTypeEnum, List<User>> value, MessageTypeEnum messageType, String userGuid) throws ServiceRequestException
	{
		User user = this.stubService.getAAS().getUser(userGuid);
		if (user != null)
		{
			List<User> listUser = new ArrayList<User>();
			listUser.add(user);
			value.put(messageType, listUser);
		}
	}

	/**
	 * 项目创建者
	 * 项目经理
	 * 项目成员
	 * 任务责任人
	 * 任务参与者
	 * 前置任务责任人
	 * 后置任务责任人
	 * 上一级任务责任人
	 * 
	 * @param objectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public Map<MessageTypeEnum, List<User>> getTaskNotifierMap(ObjectGuid taskObjectGuid) throws ServiceRequestException
	{
		Map<MessageTypeEnum, List<User>> value = null;
		if (taskObjectGuid != null)
		{
			value = new HashMap<MessageTypeEnum, List<User>>();
			FoundationObject foundation = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(taskObjectGuid, false);
			PPMFoundationObjectUtil taskFoun = new PPMFoundationObjectUtil(foundation);
			if (taskFoun != null)
			{
				User managerUser = null;
				if (taskFoun.getOwnerProject() != null && taskFoun.getOwnerProject().getGuid() != null)
				{
					FoundationObject projectFo = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(taskFoun.getOwnerProject(), false);
					if (projectFo != null)
					{
						listUserOfProject(projectFo, value);
						if (value.get(MessageTypeEnum.PROJECTMANAGER) != null)
						{
							managerUser = value.get(MessageTypeEnum.PROJECTMANAGER).get(0);
						}
					}
				}
				// 任务责任人
				if (!StringUtils.isNullString(taskFoun.getExecutor()))
				{
					listUserOfType(value, MessageTypeEnum.TASKRESPONSIBLE, taskFoun.getExecutor());
				}
				// 上一级任务责任人
				if (taskFoun.getParentTask() != null && taskFoun.getParentTask().getGuid() != null)
				{
					FoundationObject foundation3 = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(taskFoun.getParentTask(), false);
					PPMFoundationObjectUtil superTaskFoun = new PPMFoundationObjectUtil(foundation3);
					if (superTaskFoun != null && !StringUtils.isNullString(superTaskFoun.getExecutor()))
					{
						listUserOfType(value, MessageTypeEnum.SUPERIORTASKRESPONSIBLE, superTaskFoun.getExecutor());
					}
					// 父级任务为空时,默认审核人为项目经理
					else
					{
						if (managerUser != null)
						{
							List<User> listUser = new ArrayList<User>();
							listUser.add(managerUser);
							value.put(MessageTypeEnum.SUPERIORTASKRESPONSIBLE, listUser);
						}
					}
				}

				// 任务参与者
				if (foundation.getObjectGuid() != null && foundation.getObjectGuid().getGuid() != null)
				{
					List<User> listUser = this.getAllMenbersInTask(foundation.getObjectGuid());
					if (!SetUtils.isNullList(listUser))
					{
						value.put(MessageTypeEnum.TASKPARTICIPATOR, listUser);
					}

				}
				// 前置任务责任人
				if (foundation.getObjectGuid() != null && foundation.getObjectGuid().getGuid() != null)
				{
					List<User> listUser = this.getPreTaskExecutor(foundation.getObjectGuid());
					if (!SetUtils.isNullList(listUser))
					{
						value.put(MessageTypeEnum.PRETASKRESPONSIBLE, listUser);
					}
				}
				// 后置任务责任人
				if (foundation.getObjectGuid() != null && foundation.getObjectGuid().getGuid() != null)
				{
					List<User> listUser = this.getSufTaskExecutor(foundation.getObjectGuid());
					if (!SetUtils.isNullList(listUser))
					{
						value.put(MessageTypeEnum.POSTTASKRESPONSIBLE, listUser);
					}
				}

			}
		}
		return value;
	}

	/**
	 * 取得项目的用户
	 * 
	 * @param projectFo
	 * @param value
	 * @throws ServiceRequestException
	 */
	private void listUserOfProject(FoundationObject projectFo, Map<MessageTypeEnum, List<User>> value) throws ServiceRequestException
	{
		if (projectFo != null)
		{
			PPMFoundationObjectUtil projectFounUtil = new PPMFoundationObjectUtil(projectFo);
			if (projectFounUtil != null)
			{
				// 项目创建者
				if (!StringUtils.isNullString(projectFo.getCreateUserGuid()))
				{
					listUserOfType(value, MessageTypeEnum.PROJECTCREATOR, projectFo.getCreateUserGuid());
				}
				// 项目经理
				if (!StringUtils.isNullString(projectFounUtil.getExecutor()))
				{
					listUserOfType(value, MessageTypeEnum.PROJECTMANAGER, projectFounUtil.getExecutor());
				}
				// 取得所有的成员
				if (!StringUtils.isNullString(projectFo.getGuid()))
				{
					List<User> listUser = this.getAllMenbersInProject(projectFo.getGuid());
					if (!SetUtils.isNullList(listUser))
					{
						value.put(MessageTypeEnum.PROJECTMEMBER, listUser);
					}
				}
			}
		}
	}

	/**
	 * @param guid
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<User> getAllMenbersInProject(String projectGuid) throws ServiceRequestException
	{
		List<User> value = new ArrayList<User>();
		List<RoleMembers> members = this.stubService.getRoleStub().listUserInProject(projectGuid);
		if (!SetUtils.isNullList(members))
		{
			for (RoleMembers m : members)
			{
				User user = this.stubService.getAAS().getUser(m.getUserGuid());
				if (user != null)
				{
					value.add(user);
				}
			}
		}
		return value;
	}

	/**
	 * 获取前置任务执行人
	 * 
	 * @param objectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	private List<User> getPreTaskExecutor(ObjectGuid taskObjectGuid) throws ServiceRequestException
	{
		List<User> value = new ArrayList<User>();
		List<TaskRelation> preTask = this.stubService.getTaskStub().listPreTask(taskObjectGuid);
		// 过滤重复的前置任务责任
		Map<String, User> tempMap = new HashMap<String, User>();
		if (!SetUtils.isNullList(preTask))
		{
			for (TaskRelation preRelation : preTask)
			{
				if (preRelation.getPreTaskObjectGuid() != null)
				{
					FoundationObject preFoun = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(preRelation.getPreTaskObjectGuid(), false);
					if (preFoun != null)
					{
						PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(preFoun);
						if (util != null && !StringUtils.isNullString(util.getExecutor()))
						{
							User user = this.stubService.getAAS().getUser(util.getExecutor());
							if (user != null)
							{
								if (tempMap.get(user.getGuid()) == null)
								{
									tempMap.put(user.getGuid(), user);
									value.add(user);
								}
							}
						}
					}
				}

			}
		}
		return value;
	}

	/**
	 * 取得任务所有成员,除去执行者
	 * 
	 * @param objectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<User> getAllMenbersInTask(ObjectGuid taskObjectGuid) throws ServiceRequestException
	{
		List<User> value = new ArrayList<User>();
		List<TaskMember> members = this.stubService.getTaskMemberStub().listTaskMember(taskObjectGuid);
		if (!SetUtils.isNullList(members))
		{
			for (TaskMember m : members)
			{
				if (!m.isPersonInCharge())
				{
					User user = this.stubService.getAAS().getUser(m.getUserGuid());
					if (user != null)
					{
						value.add(user);
					}
				}
			}
		}
		return value;
	}

	/**
	 * @param objectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	private List<User> getSufTaskExecutor(ObjectGuid taskObjectGuid) throws ServiceRequestException
	{
		List<User> value = new ArrayList<User>();
		List<TaskRelation> sufTask = this.stubService.getTaskStub().listPostTask(taskObjectGuid);
		Map<String, User> tempMap = new HashMap<String, User>();
		if (!SetUtils.isNullList(sufTask))
		{
			for (TaskRelation preRelation : sufTask)
			{
				if (preRelation.getTaskObjectGuid() != null)
				{
					FoundationObject preFoun = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(preRelation.getTaskObjectGuid(), false);
					if (preFoun != null)
					{
						PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(preFoun);
						if (util != null && !StringUtils.isNullString(util.getExecutor()))
						{
							User user = this.stubService.getAAS().getUser(util.getExecutor());
							if (user != null)
							{
								if (tempMap.get(user.getGuid()) == null)
								{
									tempMap.put(user.getGuid(), user);
									value.add(user);
								}
							}
						}
					}
				}

			}
		}
		return value;
	}

	/**
	 * 项目创建者
	 * 项目经理
	 * 项目成员
	 * 
	 * @param objectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	public Map<MessageTypeEnum, List<User>> getProjectNotifierMap(ObjectGuid projectObjectGuid) throws ServiceRequestException
	{
		Map<MessageTypeEnum, List<User>> value = null;
		if (projectObjectGuid != null)
		{
			value = new HashMap<MessageTypeEnum, List<User>>();
			FoundationObject projectFo = ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(projectObjectGuid, false);
			if (projectFo != null)
			{
				this.listUserOfProject(projectFo, value);
			}
		}
		return value;
	}
}
