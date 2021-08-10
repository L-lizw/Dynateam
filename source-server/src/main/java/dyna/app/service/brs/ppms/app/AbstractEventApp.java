/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: AbstractEventApp
 * WangLHB Jun 28, 2012
 */
package dyna.app.service.brs.ppms.app;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.HashMap;

import dyna.app.service.brs.ppms.PPMSImpl;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.ppms.EarlyWarning;
import dyna.common.bean.data.ppms.PPMFoundationObjectUtil;
import dyna.common.bean.data.ppms.RoleMembers;
import dyna.common.bean.data.ppms.TaskMember;
import dyna.common.bean.data.ppms.TaskRelation;
import dyna.common.bean.data.ppms.WarningNotifier;
import dyna.common.dto.aas.User;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.systemenum.MailCategoryEnum;
import dyna.common.systemenum.MailMessageType;
import dyna.common.systemenum.ppms.WarningEventEnum;
import dyna.common.systemenum.ppms.WarningNotifiterEnum;
import dyna.common.util.PMConstans;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;

/**
 * @author WangLHB
 * 
 */
public abstract class AbstractEventApp implements EventApp
{
	private WarningEventEnum	eventEnum	= null;

	public AbstractEventApp(WarningEventEnum eventEnum)
	{
		this.eventEnum = eventEnum;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.servsice.brs.ppm.app.EventApp#execute(dyna.app.service.brs.ppm.PPMImpl,
	 * dyna.common.bean.data.FoundationObject, dyna.common.bean.data.FoundationObject,
	 * dyna.common.bean.data.ppm.PMWarningRule)
	 */
	@Override
	public void execute(PPMSImpl ppms, FoundationObject projectFoundationObject, FoundationObject taskFoundationObject, EarlyWarning rule) throws ServiceRequestException
	{
		if (this.isSendNotice(ppms, projectFoundationObject, taskFoundationObject, rule))
		{
			String oriContent = ppms.getMSRM().getMSRString(this.getEventEnum().getMsrMessageID(), ppms.getUserSignature().getLanguageEnum().toString());
			if (oriContent == null)
			{
				oriContent = this.getEventEnum().getMsrMessageID();
			}
			String noticeContents = MessageDecode.decodeMessage(projectFoundationObject, taskFoundationObject, oriContent, null, null);
			noticeContents = noticeContents.replace(PMConstans.SENDER_NOTICE_STRING, ppms.getUserSignature().getUserName());
			if (rule.getEventType() == WarningEventEnum.P_COMPLETE_DELAY_PERCENT || rule.getEventType() == WarningEventEnum.T_COMPLETE_DELAY_PERCENT
					|| rule.getEventType() == WarningEventEnum.P_START_DELAY || rule.getEventType() == WarningEventEnum.P_COMPLETE_DELAY
					|| rule.getEventType() == WarningEventEnum.P_PROGRESS_RISK || WarningEventEnum.P_START_BEFORE == rule.getEventType()
					|| WarningEventEnum.T_START_DELAY == rule.getEventType() || WarningEventEnum.T_START_BEFORE == rule.getEventType()
					|| WarningEventEnum.T_COMPLETE_DELAY == rule.getEventType() || WarningEventEnum.T_COMPLETE_BEFORE == rule.getEventType()
					|| rule.getEventType() == WarningEventEnum.T_PROGRESS_RISK)
			{
				Object delayPercent = this.getDelayPercent(ppms, projectFoundationObject, taskFoundationObject, rule);
				if (delayPercent != null)
				{
					noticeContents = noticeContents.replace(PMConstans.PERCENT_NOTICE_STRING, delayPercent.toString());
				}
			}
			String noticeSubject = this.getNoticeSubject(ppms, projectFoundationObject, taskFoundationObject, rule);
			List<User> listAllSendUser = this.listAllSendUser(ppms, projectFoundationObject, taskFoundationObject, rule);

			List<ObjectGuid> attachList = new ArrayList<ObjectGuid>();

			if (taskFoundationObject != null)
			{
				attachList.add(taskFoundationObject.getObjectGuid());
			}
			else if (projectFoundationObject != null)
			{
				attachList.add(projectFoundationObject.getObjectGuid());
			}

			String newNoticeContents = null;
			for (User user : listAllSendUser)
			{
				newNoticeContents = noticeContents.replace(PMConstans.RECEIVER_NOTICE_STRING, user.getName());
				ppms.getSMS().sendMailToUser(noticeSubject, newNoticeContents, MailCategoryEnum.INFO, attachList, user.getGuid(), MailMessageType.PROJECTNOTIFY);
			}
		}

	}

	protected abstract boolean isSendNotice(PPMSImpl ppms, FoundationObject projectFoundationObject, FoundationObject taskFoundationObject, EarlyWarning rule)
			throws ServiceRequestException;

	/**
	 * 取得需要发送的人,过滤重复
	 * 
	 * @param ppms
	 * @param prjFoundation
	 * @param taskFoundation
	 * @param rule
	 * @return
	 * @throws ServiceRequestException
	 */
	private List<User> listAllSendUser(PPMSImpl ppms, FoundationObject prjFoundation, FoundationObject taskFoundation, EarlyWarning rule) throws ServiceRequestException
	{
		List<User> toUserIdList = new ArrayList<User>();
		List<User> userList = this.listUserOfOrganization(ppms, rule);

		if (SetUtils.isNullList(userList))
		{
			userList = new ArrayList<User>();
		}
		// 取得任务上的通知人员
		List<WarningNotifier> noticeM = rule.getNoticeMemberList();
		if (SetUtils.isNullList(noticeM) && SetUtils.isNullList(userList))
		{
			noticeM = this.getDefaultNotifierList(rule);
		}
		if (rule.getEventType() == WarningEventEnum.P_COMPLETE_DELAY || rule.getEventType() == WarningEventEnum.P_COMPLETE_DELAY_PERCENT
				|| rule.getEventType() == WarningEventEnum.P_PROGRESS_RISK || rule.getEventType() == WarningEventEnum.P_START_BEFORE
				|| rule.getEventType() == WarningEventEnum.P_START_DELAY)
		{
			this.listUserOfProjectWarning(ppms, noticeM, prjFoundation, userList, toUserIdList);
			return toUserIdList;
		}
		this.listUserOfTaskWarning(ppms, noticeM, taskFoundation, prjFoundation, userList, toUserIdList);
		return toUserIdList;
	}

	/**
	 * 取得与任务预警相关的人
	 * 
	 * @param ppms
	 * @param noticeM
	 * @param taskFoundation
	 * @param prjFoundation
	 * @param userList
	 * @param toUserIdList
	 * @throws ServiceRequestException
	 */
	private void listUserOfTaskWarning(PPMSImpl ppms, List<WarningNotifier> noticeM, FoundationObject taskFoundation, FoundationObject prjFoundation, List<User> userList,
			List<User> toUserIdList) throws ServiceRequestException
	{
		for (WarningNotifier notifer : noticeM)
		{
			if (StringUtils.isNullString(notifer.getNotifierGuid()))
			{
				if (taskFoundation != null)
				{
					PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(taskFoundation);
					// 任务执行人
					if (notifer.getNotifierType().equals(WarningNotifiterEnum.TASKEXECUTOR))
					{
						if (!StringUtils.isNullString(util.getExecutor()))
						{
							this.getUserByGuid(ppms, util.getExecutor(), userList);
						}
					}
					// 任务参与者
					else if (notifer.getNotifierType().equals(WarningNotifiterEnum.TASKPARTICIPATOR))
					{
						List<User> ulist = ppms.getNoticeStub().getAllMenbersInTask(taskFoundation.getObjectGuid());
						if (!SetUtils.isNullList(ulist))
						{
							userList.addAll(ulist);
						}
					}
					// 任务参与者(上级)
					else if (notifer.getNotifierType().equals(WarningNotifiterEnum.SUPERIORTASKPARTICIPATOR))
					{
						FoundationObject parentTaskFo = ppms.getParentTask(taskFoundation.getObjectGuid());
						if (parentTaskFo != null)
						{
							List<User> ulist = ppms.getNoticeStub().getAllMenbersInTask(parentTaskFo.getObjectGuid());
							if (!SetUtils.isNullList(ulist))
							{
								userList.addAll(ulist);
							}
						}
					}
					// 任务执行人(上级)
					else if (notifer.getNotifierType().equals(WarningNotifiterEnum.SUPERIORTASKEXECUTOR))
					{
						FoundationObject parentTaskFo = ppms.getParentTask(taskFoundation.getObjectGuid());
						PPMFoundationObjectUtil superTaskFoun = new PPMFoundationObjectUtil(parentTaskFo);
						if (superTaskFoun != null && !StringUtils.isNullString(superTaskFoun.getExecutor()))
						{
							this.getUserByGuid(ppms, superTaskFoun.getExecutor(), userList);
						}
					}
					// 后续任务执行人
					else if (notifer.getNotifierType().equals(WarningNotifiterEnum.REARTASKEXECUTOR))
					{
						List<User> ulist = this.getPostTaskExecutors(taskFoundation.getObjectGuid(), ppms, WarningNotifiterEnum.REARTASKEXECUTOR, null);
						if (!SetUtils.isNullList(ulist))
						{
							userList.addAll(ulist);
						}
					}
					// 后续任务参与者
					else if (notifer.getNotifierType().equals(WarningNotifiterEnum.REARTASKTASKPARTICIPATOR))
					{
						List<User> ulist = this.getPostTaskExecutors(taskFoundation.getObjectGuid(), ppms, null, WarningNotifiterEnum.REARTASKTASKPARTICIPATOR);
						if (!SetUtils.isNullList(ulist))
						{
							userList.addAll(ulist);
						}

					}
					// 前置任务执行人
					else if (notifer.getNotifierType().equals(WarningNotifiterEnum.FRONTTASKEXECUTOR))
					{
						List<User> ulist = this.getPreTaskExecutors(taskFoundation.getObjectGuid(), ppms, WarningNotifiterEnum.FRONTTASKEXECUTOR, null);

						if (!SetUtils.isNullList(ulist))
						{
							userList.addAll(ulist);
						}
					}
					// 前置任务参与者
					else if (notifer.getNotifierType().equals(WarningNotifiterEnum.FRONTTASKPARTICIPATOR))
					{
						List<User> ulist = this.getPreTaskExecutors(taskFoundation.getObjectGuid(), ppms, null, WarningNotifiterEnum.FRONTTASKEXECUTOR);

						if (!SetUtils.isNullList(ulist))
						{
							userList.addAll(ulist);
						}
					}

				}
				// 项目经理(上级)
				if (notifer.getNotifierType().equals(WarningNotifiterEnum.SUPERIORPROJECTMANAGER))
				{
					PPMFoundationObjectUtil putil = new PPMFoundationObjectUtil(prjFoundation);
					if (putil.getFoundation() != null && !StringUtils.isNullString(putil.getExecutor()))
					{
						this.getUserByGuid(ppms, putil.getExecutor(), userList);
					}
				}
			}
		}
		// 过滤重复值
		this.getFilterUser(userList, toUserIdList);
	}

	/**
	 * 取得过滤后的User
	 * 
	 * @param userList
	 * @param toUserIdList
	 */
	private void getFilterUser(List<User> userList, List<User> toUserIdList)
	{
		if (!SetUtils.isNullList(userList))
		{
			Map<String, User> tempMap = new HashMap<String, User>();
			for (User u : userList)
			{
				if (tempMap.get(u.getUserId()) == null)
				{
					tempMap.put(u.getUserId(), u);
					toUserIdList.add(u);
				}
			}
		}
	}

	/**
	 * 取得与项目预警相关的人
	 * 
	 * @param ppms
	 * 
	 * @param noticeM
	 * @param prjFoundation
	 * @param userList
	 * @param toUserIdList
	 * @throws ServiceRequestException
	 */
	private void listUserOfProjectWarning(PPMSImpl ppms, List<WarningNotifier> noticeM, FoundationObject prjFoundation, List<User> userList, List<User> toUserIdList)
			throws ServiceRequestException
	{
		if (!SetUtils.isNullList(noticeM))
		{
			for (WarningNotifier notifier : noticeM)
			{
				if (notifier.getNotifierType() == WarningNotifiterEnum.SUPERIORPROJECTMANAGER)
				{
					// 项目经理
					PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(prjFoundation);
					this.getUserByGuid(ppms, util.getExecutor(), userList);
				}
				else if (notifier.getNotifierType() == WarningNotifiterEnum.TASKEXECUTOR || notifier.getNotifierType() == WarningNotifiterEnum.FRONTTASKEXECUTOR
						|| notifier.getNotifierType() == WarningNotifiterEnum.REARTASKEXECUTOR)
				{
					// 任务执行人
					List<FoundationObject> listAllSubTask = ppms.getWBSStub().listAllSubTask(prjFoundation.getObjectGuid(), true);
					if (!SetUtils.isNullList(listAllSubTask))
					{
						PPMFoundationObjectUtil util = new PPMFoundationObjectUtil(null);
						for (FoundationObject foundation : listAllSubTask)
						{
							util.setFoundation(foundation);
							if (!StringUtils.isNullString(util.getExecutor()))
							{
								this.getUserByGuid(ppms, util.getExecutor(), userList);
							}
						}
					}
				}
				else if (notifier.getNotifierType() == WarningNotifiterEnum.TASKPARTICIPATOR || notifier.getNotifierType() == WarningNotifiterEnum.FRONTTASKPARTICIPATOR
						|| notifier.getNotifierType() == WarningNotifiterEnum.REARTASKTASKPARTICIPATOR)
				{
					// 所有人
					List<RoleMembers> listUserInProject = ppms.getRoleStub().listUserInProject(prjFoundation.getGuid());
					if (listUserInProject != null)
					{
						for (RoleMembers member : listUserInProject)
						{
							this.getUserByGuid(ppms, member.getUserGuid(), userList);
						}
					}
				}
				if (notifier.getNotifierType() == WarningNotifiterEnum.PROJECTCREATOR)
				{
					this.getUserByGuid(ppms, prjFoundation.getCreateUserGuid(), userList);
				}

			}
		}
		// 过滤重复值
		this.getFilterUser(userList, toUserIdList);
	}

	/**
	 * 通过userGuid取得用户User
	 * 
	 * @param ppms
	 * 
	 * @param executor
	 * @param userList
	 * @throws ServiceRequestException
	 */
	private void getUserByGuid(PPMSImpl ppms, String userGuid, List<User> userList) throws ServiceRequestException
	{
		User u = ppms.getAAS().getUser(userGuid);
		if (u != null)
		{
			userList.add(u);
		}
	}

	/**
	 * 当类型为项目角色/group/rig/user时,取得相应的User,并过滤重复值
	 * organizationguid,用户Guid
	 * organizationid,用户id
	 * organizationname,用户name
	 * 
	 * @param ppms
	 * @param rule
	 * @return
	 */
	private List<User> listUserOfOrganization(PPMSImpl ppms, EarlyWarning rule)
	{
		List<User> value = null;
		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(WarningNotifier.WARNINGGUID, rule.getGuid());
		List<WarningNotifier> listUser = sds.query(WarningNotifier.class, filter, "selectOrganization");
		if (!SetUtils.isNullList(listUser))
		{
			value = new ArrayList<User>();
			Map<String, User> tempMap = new HashMap<String, User>();// 过滤重复值
			for (WarningNotifier organization : listUser)
			{
				User user = new User();
				if (organization.get("organizationguid") != null && organization.get("organizationguid") != "")
				{
					user.setGuid((String) organization.get("organizationguid"));
				}
				if (organization.get("organizationid") != null && organization.get("organizationid") != "")
				{
					user.setUserId((String) organization.get("organizationid"));
				}
				if (organization.get("organizationname") != null && organization.get("organizationname") != "")
				{
					user.setUserName((String) organization.get("organizationname"));
				}
				if (user != null && tempMap.get(user.getUserId()) == null)
				{
					value.add(user);
					tempMap.put(user.getUserId(), user);
				}
			}
		}
		return value;
	}

	/**
	 * 取得后续任务执行人
	 * 
	 * @param objectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	private List<User> getPostTaskExecutors(ObjectGuid taskObjectGuid, PPMSImpl ppms, WarningNotifiterEnum executor, WarningNotifiterEnum participants)
			throws ServiceRequestException
	{
		List<User> result = new ArrayList<User>();
		List<TaskRelation> taskRelation = ppms.getTaskStub().listPostTask(taskObjectGuid);
		this.listUserOfTaskMember(true, ppms, taskRelation, executor, participants, result);
		return result;
	}

	/**
	 * 
	 * @param ppms
	 * @param taskRelation
	 * @param executor
	 * @param participants
	 * @param result
	 * @throws ServiceRequestException
	 */
	private void listUserOfTaskMember(boolean isPost, PPMSImpl ppms, List<TaskRelation> taskRelation, WarningNotifiterEnum executor, WarningNotifiterEnum participants,
			List<User> result)
			throws ServiceRequestException
	{
		if (!SetUtils.isNullList(taskRelation))
		{
			List<String> value = new ArrayList<String>();
			for (TaskRelation task : taskRelation)
			{
				List<TaskMember> members = null;
				if (isPost)
				{
					members = ppms.getTaskMemberStub().listTaskMember(task.getTaskObjectGuid());
				}
				else
				{
					members = ppms.getTaskMemberStub().listTaskMember(task.getPreTaskObjectGuid());
				}
				if (!SetUtils.isNullList(members))
				{
					if (executor != null && participants != null)
					{
						for (TaskMember m : members)
						{
							if (!value.contains(m.getUserGuid()))
							{
								User user = ppms.getAAS().getUser(m.getUserGuid());
								if (user != null)
								{
									result.add(user);
									value.add(m.getUserGuid());
								}
							}
						}
					}
					else if (executor != null)
					{
						for (TaskMember m : members)
						{
							if (!value.contains(m.getUserGuid()) && m.isPersonInCharge())
							{
								User user = ppms.getAAS().getUser(m.getUserGuid());
								if (user != null)
								{
									result.add(user);
									value.add(m.getUserGuid());
								}
								break;
							}
						}
					}
					else if (participants != null)
					{
						for (TaskMember m : members)
						{
							if (!value.contains(m.getUserGuid()) && !m.isPersonInCharge())
							{
								User user = ppms.getAAS().getUser(m.getUserGuid());
								if (user != null)
								{
									result.add(user);
									value.add(m.getUserGuid());
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 取得前置任务执行人
	 * 
	 * @param objectGuid
	 * @return
	 * @throws ServiceRequestException
	 */
	private List<User> getPreTaskExecutors(ObjectGuid taskObjectGuid, PPMSImpl ppms, WarningNotifiterEnum executor, WarningNotifiterEnum participants)
			throws ServiceRequestException
	{
		List<User> result = new ArrayList<User>();
		List<TaskRelation> taskRelation = ppms.getTaskStub().listPreTask(taskObjectGuid);
		this.listUserOfTaskMember(false, ppms, taskRelation, executor, participants, result);
		return result;
	}

	/**
	 * 取得主题
	 * 
	 * @param ppm
	 * @param projectFoundationObject
	 * @param taskFoundationObject
	 * @param rule
	 * @return
	 * @throws ServiceRequestException
	 */
	protected String getNoticeSubject(PPMSImpl ppm, FoundationObject projectFoundationObject, FoundationObject taskFoundationObject, EarlyWarning rule)
			throws ServiceRequestException
	{
		String title = null;
		if (projectFoundationObject != null)
		{
			title = "[" + projectFoundationObject.getFullName() + "]";
		}

		if (taskFoundationObject != null)
		{
			title = title + "[" + taskFoundationObject.getFullName() + "]";
		}

		LanguageEnum languageEnum = ppm.getUserSignature().getLanguageEnum();
		String msrString = ppm.getMSRM().getMSRString(this.eventEnum.getMsrID(), languageEnum.toString());
		if (!StringUtils.isNullString(msrString))
		{
			title = title + "[" + msrString + "]";
		}
		return title;
	}

	/**
	 * 通过消息类型取得每种类型的默认值
	 * 
	 * @param rule
	 * @param messagetype
	 */
	private List<WarningNotifier> getDefaultNotifierList(EarlyWarning rule)
	{
		List<WarningNotifier> notifierList = new ArrayList<WarningNotifier>();
		WarningNotifier notifier = null;
		switch (rule.getEventType())
		{

		case P_START_BEFORE:
		case P_START_DELAY:
		case P_COMPLETE_DELAY:
		case P_COMPLETE_DELAY_PERCENT:
		case P_PROGRESS_RISK:
			notifier = new WarningNotifier();
			notifier.setNotifierType(WarningNotifiterEnum.SUPERIORPROJECTMANAGER);
			notifierList.add(notifier);

			notifier = new WarningNotifier();
			notifier.setNotifierType(WarningNotifiterEnum.PROJECTCREATOR);
			notifierList.add(notifier);

			break;

		case T_START_DELAY:
		case T_START_BEFORE:
		case T_COMPLETE_BEFORE:
		case T_COMPLETE_DELAY:
		case T_COMPLETE_DELAY_PERCENT:
		case T_PROGRESS_RISK:
			notifier = new WarningNotifier();
			notifier.setNotifierType(WarningNotifiterEnum.TASKEXECUTOR);
			notifierList.add(notifier);

			notifier = new WarningNotifier();
			notifier.setNotifierType(WarningNotifiterEnum.TASKPARTICIPATOR);

			notifierList.add(notifier);
			break;
		default:
			break;
		}
		return notifierList;

	}

	/**
	 * @return the eventEnum
	 */
	public WarningEventEnum getEventEnum()
	{
		return this.eventEnum;
	}

	public Object getDelayPercent(PPMSImpl ppms, FoundationObject projectFoundationObject, FoundationObject taskFoundationObject, EarlyWarning rule)
	{
		return 0D;
	}

}
