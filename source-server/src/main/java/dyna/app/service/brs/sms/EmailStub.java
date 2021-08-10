/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: EmailStub
 * WangLHB Feb 23, 2012
 */
package dyna.app.service.brs.sms;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import org.acegisecurity.context.SecurityContextHolder;

import dyna.app.core.sch.AbstractScheduledTask;
import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.aas.AASImpl;
import dyna.app.service.brs.pos.POSImpl;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.dto.EmailServer;
import dyna.common.dto.Mail;
import dyna.common.dto.aas.User;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;
import dyna.net.service.brs.AAS;
import dyna.net.service.brs.POS;
import dyna.net.service.brs.SMS;
import dyna.net.service.das.MSRM;

/**
 * @author WangLHB
 *
 */
public class EmailStub extends AbstractServiceStub<SMSImpl>
{

	/**
	 * @param context
	 * @param service
	 */
	protected EmailStub(ServiceContext context, SMSImpl service)
	{
		super(context, service);
	}

	protected EmailServer saveEmailServer(EmailServer emailServer) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			emailServer.put(EmailServer.CREATE_USER_GUID, this.stubService.getOperatorGuid());
			emailServer.put(EmailServer.UPDATE_USER_GUID, this.stubService.getOperatorGuid());

			sds.save(emailServer);
			return this.getEmailServer();
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected EmailServer getEmailServer() throws ServiceRequestException
	{
		EmailServer server = null;
		SystemDataService sds = DataServer.getSystemDataService();
		Map<String, Object> filter = new HashMap<String, Object>();
		try
		{
			List<EmailServer> serverList = sds.query(EmailServer.class, filter);
			if (!SetUtils.isNullList(serverList))
			{
				server = serverList.get(0);
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

		return server;
	}

}

class MailScheduledTask extends AbstractScheduledTask
{
	private ServiceContext	serviceContext	= null;
	// List<Mail> mailList = null;
	List<String>			toUserGuidList	= null;
	Mail					mail			= null;
	LanguageEnum			languageEnum	= LanguageEnum.ZH_CN;
	SMS						sms				= null;
	AAS						aas				= null;
	POS						pos				= null;
	MSRM					msrm			= null;
	EmailServer				currentServer;
	String					sessionId		= null;

	public MailScheduledTask(ServiceContext serviceContext, Mail mail, List<String> toUserGuidList, LanguageEnum languageEnum)
	{
		this.serviceContext = serviceContext;
		this.mail = mail;
		this.languageEnum = languageEnum;
		this.toUserGuidList = toUserGuidList;
	}

	@Override
	public void run()
	{
		DynaLogger.debug("SMS MultiThreadQueued Scheduled [Class]MailScheduledTask , Scheduled Task Start...");
		try
		{
			this.sms = this.serviceContext.allocatService(SMS.class);
			this.aas = this.serviceContext.allocatService(AAS.class);
			this.pos = this.serviceContext.allocatService(POS.class);

			this.msrm = this.serviceContext.allocatService(MSRM.class);
			this.currentServer = this.sms.getEmailServer();

			if (this.currentServer == null)
			{
				return;
			}

			((SMSImpl) this.sms).setSignature(this.serviceContext.getServerContext().getSystemInternalSignature());
			((AASImpl) this.aas).setSignature(this.serviceContext.getServerContext().getSystemInternalSignature());
			((POSImpl) this.pos).setSignature(this.serviceContext.getServerContext().getSystemInternalSignature());

			String warningText = null;
			if (this.currentServer.isShowWarn())
			{
				warningText = msrm.getMSRString("ID_APP_MAIL_WARNING_CONTENT", this.languageEnum.toString());
			}

			if (!SetUtils.isNullList(this.toUserGuidList))
			{
				for (String toUserGuid : this.toUserGuidList)
				{
					if (toUserGuid != null)
					{
						this.mail.setReceiveUser(toUserGuid);
						this.sendMail(this.mail, warningText);
					}
				}
			}

		}
		catch (Throwable e)
		{
			DynaLogger.error("run send mail:", e);
		}
		finally
		{
			SecurityContextHolder.clearContext();

			if (this.aas != null)
			{
				this.serviceContext.releaseService(this.aas);
			}
			if (this.pos != null)
			{
				this.serviceContext.releaseService(this.pos);
			}
			if (this.sms != null)
			{
				this.serviceContext.releaseService(this.sms);
			}
			if (this.msrm != null)
			{
				this.serviceContext.releaseService(this.msrm);
			}
		}

		DynaLogger.debug("SMS MultiThreadQueued Scheduled [Class]MailScheduledTask , Scheduled Task End...");
	}

	public void sendMail(Mail mail, String warningText)
	{

		Properties properties = null;
		Session mailSession = null;
		MimeMessage mailMessage = null;
		String subject = (String) mail.get(Mail.TITLE);
		if (subject == null)
		{
			subject = "";
		}
		String content = (String) mail.get(Mail.CONTENTS);
		if (content == null)
		{
			content = "";
		}

		String toUserGuid = mail.getReceiveUser();

		try
		{

			User user = this.aas.getUser(toUserGuid);
			if (this.currentServer == null || !this.pos.isReceiveEmail(user.getGuid()) || StringUtils.isNullString(user.getEmail()))
			{
				return;
			}
			if (this.currentServer.isShowWarn() && !StringUtils.isNullString(warningText))
			{
				content = content + "\r\n" + warningText;
			}

			properties = new Properties();
			// 设置邮件服务器
			properties.put("mail.smtp.host", this.currentServer.getSMTP());
			if (StringUtils.isNullString(MailScheduledTask.this.currentServer.getPassword()))
			{
				// 不验证密码
				mailSession = Session.getInstance(properties, null);
			}
			else
			{
				// 验证密码
				properties.put("mail.smtp.auth", "true");
				// 根据属性新建一个邮件会话
				mailSession = Session.getInstance(properties, new Authenticator()
				{
					@Override
					public PasswordAuthentication getPasswordAuthentication()
					{
						return new PasswordAuthentication(MailScheduledTask.this.currentServer.getUserName(), MailScheduledTask.this.currentServer.getPassword());
					}
				});
			}
			// mailSession.setDebug(true);
			// 建立消息对象
			mailMessage = new MimeMessage(mailSession);
			// 发件人
			mailMessage.setFrom(new InternetAddress(this.currentServer.getFromAddress()));
			// 收件人
			mailMessage.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(user.getEmail()));
			// 主题
			// MimeUtility.encodeText(subject, "UTF-8", "B");
			mailMessage.setSubject(MimeUtility.encodeText(subject, "UTF-8", "B"));
			// 内容
			mailMessage.setText(content, "UTF-8");
			// 发信时间
			mailMessage.setSentDate(new Date());
			// 存储信息
			mailMessage.saveChanges();

			// 发送
			// Thread sendThread = new Thread(new SendEmailThread(mailMessage));
			// sendThread.start();

			try
			{
				Transport.send(mailMessage);
				DynaLogger.debug("Send e-mail successful, user:" + user.getUserName() + " subject:" + subject + " content:" + content);
			}
			catch (MessagingException e)
			{
				DynaLogger.error("Send e-mail error: " + e + ", " + e.getMessage());
			}

		}
		catch (Exception e)
		{
			DynaLogger.error("Send e-mail error: " + e + ", " + e.getMessage());
		}
		finally
		{
		}
	}
}
