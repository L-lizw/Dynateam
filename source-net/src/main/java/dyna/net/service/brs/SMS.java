/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SNS System Mail Service
 * caogc 2010-8-20
 */
package dyna.net.service.brs;

import java.util.List;

import dyna.common.SearchCondition;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.dto.DSSFileInfo;
import dyna.common.dto.DSSFileTrans;
import dyna.common.dto.EmailServer;
import dyna.common.dto.Mail;
import dyna.common.dto.MailWorkFlow;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.MailCategoryEnum;
import dyna.common.systemenum.MailMessageType;
import dyna.net.service.Service;

/**
 * System Mail Service 系统邮件相关服务
 * 
 * @author caogc
 * 
 */
public interface SMS extends Service
{
	/**
	 * 获取邮件附件
	 * 
	 * @param mailGuid
	 *            邮件guid
	 * @param objectGuid
	 *            附件objectguid
	 * @return
	 */
	public FoundationObject getMailAttachment(String mailGuid, ObjectGuid objectGuid) throws ServiceRequestException;

	/**
	 * 清空垃圾箱
	 * 
	 * @throws ServiceRequestException
	 */
	public void clearTrash() throws ServiceRequestException;

	/**
	 * 删除收件箱中的信件集,将其转移至垃圾箱
	 * 
	 * @param mailGuidList
	 *            要删除的信件的guid的List
	 * @throws ServiceRequestException
	 */
	public void deleteInbox(List<String> mailGuidList) throws ServiceRequestException;

	/**
	 * 删除发件箱中的信件集,将其转移至垃圾箱
	 * 
	 * @param mailGuidList
	 *            要删除的信件的guid的List
	 * @throws ServiceRequestException
	 */
	public void deleteSent(List<String> mailGuidList) throws ServiceRequestException;

	/**
	 * 删除垃圾箱中的信件集,数据库中彻底删除
	 * 
	 * @param mailGuidList
	 *            要删除的垃圾信件的guid的List
	 * @throws ServiceRequestException
	 */
	public void deleteTrash(List<String> mailGuidList) throws ServiceRequestException;

	/**
	 * 读取一条指定的信件
	 * 
	 * @param mailGuid
	 *            要读取信件的guid
	 * @return 信件对象
	 * @throws ServiceRequestException
	 */
	public Mail getMail(String mailGuid) throws ServiceRequestException;

	/**
	 * 按照条件检索 本人收到的信件列表
	 * 
	 * @param searchCondition
	 *            指定的检索条件
	 * @return 本人收到的信件列表
	 * @throws ServiceRequestException
	 */
	public List<Mail> listInbox(SearchCondition searchCondition) throws ServiceRequestException;

	/**
	 * 按照条件检索 已发送的信件列表
	 * 
	 * @param searchCondition
	 *            指定的检索条件
	 * @return 已发送的信件列表
	 * @throws ServiceRequestException
	 */
	public List<Mail> listSent(SearchCondition searchCondition) throws ServiceRequestException;

	/**
	 * 按照条件检索本人垃圾箱信件列表
	 * 
	 * @param searchCondition
	 *            指定的检索条件
	 * @return 垃圾箱信件列表
	 * @throws ServiceRequestException
	 */
	public List<Mail> listTrash(SearchCondition searchCondition) throws ServiceRequestException;

	/**
	 * 从垃圾箱中还原信件到收件箱或者发件箱
	 * 
	 * @param mailGuidList
	 *            要还原的信件的guid的List
	 * @throws ServiceRequestException
	 */
	public void restoreFromTrash(List<String> mailGuidList) throws ServiceRequestException;

	// /**
	// * 给指定的Group列表发送信件
	// *
	// * @param subject
	// * 要发送的信件的标题
	// * @param content
	// * 要发送邮件的内容
	// * @param mailCategoryEnum
	// * 要发送的邮件的所属类别
	// * @param objectGuidList
	// * 要发送附件的对象的ObjectGuid的列表
	// * @param toGroupIdList
	// * 要发送给的Group的Id的List
	// * @throws ServiceRequestException
	// */
	// public void sendMailToGroup(String subject, String content, MailCategoryEnum mailCategoryEnum,
	// List<ObjectGuid> objectGuidList, List<String> toGroupIdList) throws ServiceRequestException;

	// /**
	// * 给指定的Role In Group列表发送信件
	// *
	// * @param subject
	// * 要发送的信件的标题
	// * @param content
	// * 要发送邮件的内容
	// * @param mailCategoryEnum
	// * 要发送的邮件的所属类别
	// * @param objectGuidList
	// * 要发送附件的对象的ObjectGuid的列表
	// * @param toRigList
	// * rig IdList
	// * 要发送给的Group的Id的List
	// * @throws ServiceRequestException
	// */
	// public void sendMailToRIG(String subject, String content, MailCategoryEnum mailCategoryEnum,
	// List<ObjectGuid> objectGuidList, List<String> toRigList) throws ServiceRequestException;

	// /**
	// * 给指定的人列表发送信件
	// * 包含group,rig,user
	// *
	// * @param subject
	// * 要发送的信件的标题
	// * @param content
	// * 要发送邮件的内容
	// * @param mailCategoryEnum
	// * 要发送的邮件的所属类别
	// * @param objectGuidList
	// * 要发送附件的对象的ObjectGuid的列表
	// * @param notifierList
	// * notifierList
	// * 要发送给的Group,rig,user
	// * @throws ServiceRequestException
	// */
	// public void sendMailToNotifier(String subject, String content, MailCategoryEnum mailCategoryEnum,
	// List<ObjectGuid> objectGuidList, List<Notifier> notifierList) throws ServiceRequestException;

	/**
	 * 给指定的用户列表发送信件
	 * 
	 * @param subject
	 *            要发送的信件的标题
	 * @param content
	 *            要发送邮件的内容
	 * @param mailCategoryEnum
	 *            要发送的邮件的所属类别
	 * @param objectGuidList
	 *            要发送附件的对象的ObjectGuid的列表
	 * @param toUserIdList
	 *            要发送给的用户的Id的List,或者用户的guid列表
	 * @throws ServiceRequestException
	 */
	public void sendMailToUsers(String subject, String content, MailCategoryEnum mailCategoryEnum, List<ObjectGuid> objectGuidList, List<String> toUserIdList,
			MailMessageType messageType) throws ServiceRequestException;

	/**
	 * 给指定的用户发送信件
	 * 
	 * @param subject
	 *            要发送的信件的标题
	 * @param content
	 *            要发送邮件的内容
	 * @param mailCategoryEnum
	 *            要发送的邮件的所属类别
	 * @param objectGuidList
	 *            要发送附件的对象的ObjectGuid的列表
	 * @param toUserId
	 *            要发送给的用户的Id 或者是用户的Guid
	 * @throws ServiceRequestException
	 */
	public void sendMailToUser(String subject, String content, MailCategoryEnum mailCategoryEnum, List<ObjectGuid> objectGuidList, String toUserId, MailMessageType messageType)
			throws ServiceRequestException;

	/**
	 * 给指定的用户发送信件,附件为物理文件,调用此方法之后,需要再次调用物理文件上传接口将文件上传到服务器
	 * 
	 * @param subject
	 *            要发送的信件的标题
	 * @param content
	 *            要发送邮件的内容
	 * @param mailCategoryEnum
	 *            要发送的邮件的所属类别
	 * @param toUserId
	 *            要发送给的用户的Id 或者是用户的Guid
	 * @param fileList
	 *            要发送文件附件的列表
	 * @return 文件传输对象列表
	 * @throws ServiceRequestException
	 */
	public List<DSSFileTrans> sendMail4Report(String subject, String content, MailCategoryEnum mailCategoryEnum, String toUserId, List<DSSFileInfo> fileList)
			throws ServiceRequestException;

	/**
	 * 将指定信件列表 设置为已处理
	 * 
	 * @param mailGuidList
	 * @throws ServiceRequestException
	 */
	public void setAsProcessed(List<String> mailGuidList) throws ServiceRequestException;

	/**
	 * 将指定信件列表 设置已读
	 * 
	 * @param mailGuidList
	 *            要设置的信件的guid的List
	 * @throws ServiceRequestException
	 */
	public void setAsRead(List<String> mailGuidList) throws ServiceRequestException;

	/**
	 * 将指定信件列表 设置未读
	 * 
	 * @param mailGuidList
	 *            要设置的信件的guid的List
	 * @throws ServiceRequestException
	 */
	public void setAsNotRead(List<String> mailGuidList) throws ServiceRequestException;

	// /**
	// * 设置信件类别
	// *
	// * @param mailGuidList
	// * 要设置的信件的guid的List
	// * @param mailCategoryEnum
	// * 要设置的类别
	// * 如果该参数为null 那么表示设置无任何分类
	// * @throws ServiceRequestException
	// */
	// public void setInboxCategory(List<String> mailGuidList, MailCategoryEnum mailCategoryEnum)
	// throws ServiceRequestException;

	/**
	 * 保存Email服务信息
	 * 
	 * @param emailServer
	 * @throws ServiceRequestException
	 */
	public EmailServer saveEmailServer(EmailServer emailServer) throws ServiceRequestException;

	/**
	 * 取得Email服务信息
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public EmailServer getEmailServer() throws ServiceRequestException;

	/**
	 * 取得未读信息条数
	 * 
	 * @return 未读信息条数
	 * @throws ServiceRequestException
	 */
	public int getCountOfUnreadNotice() throws ServiceRequestException;

	/**
	 * 按照条件检索 本人收到的跟工作流相关信件列表
	 * 
	 * @param searchCondition
	 *            指定的检索条件
	 * @return 本人收到的信件列表
	 * @throws ServiceRequestException
	 */
	public List<MailWorkFlow> listMailOfWorkFlow(SearchCondition searchCondition) throws ServiceRequestException;

	/**
	 * 通过“个人偏好”进行删除消息
	 * 
	 * @throws ServiceRequestException
	 */
	public void clearMailByConfig() throws ServiceRequestException;

}
