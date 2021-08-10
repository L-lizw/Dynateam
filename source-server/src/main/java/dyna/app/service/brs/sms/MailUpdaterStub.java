/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: MailUpdaterStub
 * Wanglei 2011-4-1
 */
package dyna.app.service.brs.sms;

import java.util.List;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.dto.Mail;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.MailCategoryEnum;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;

/**
 * @author Wanglei
 * 
 */
public class MailUpdaterStub extends AbstractServiceStub<SMSImpl>
{

	/**
	 * @param context
	 * @param service
	 */
	protected MailUpdaterStub(ServiceContext context, SMSImpl service)
	{
		super(context, service);
	}

	/**
	 * 把指定邮件列表从数据库中删除
	 * 
	 * @param mailGuidList
	 *            要删除的邮件guid的列表
	 * @throws ServiceRequestException
	 */
	protected void deleteMail(List<String> mailGuidList) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		if (SetUtils.isNullList(mailGuidList))
		{
			return;
		}

		try
		{
			for (String mailGuid : mailGuidList)
			{
				sds.delete(Mail.class, mailGuid);
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	/**
	 * 把指定的邮件列表放入垃圾箱
	 * 
	 * @param mailGuidList
	 *            要修改的邮件的guid的列表
	 * @throws ServiceRequestException
	 */
	protected void moveToTrash(List<String> mailGuidList) throws ServiceRequestException
	{
		for (String mailGuid : mailGuidList)
		{
			Mail mail = this.stubService.getMail(mailGuid);

			mail.put(Mail.IS_IN_TRASH, "Y");
			this.updateMail(mail);
		}
	}

	public void setAsRead(List<String> mailGuidList) throws ServiceRequestException
	{
		for (String mailGuid : mailGuidList)
		{
			Mail mail = this.stubService.getMail(mailGuid);
			// if (!StringUtils.isNullString(mail.getOrigMailGuid()))
			// {
			// Mail oriMail = this.stubService.getMail(mail.getOrigMailGuid());
			// if(oriMail!=null)
			// {
			// oriMail.put(Mail.IS_READ, "Y");
			// this.updateMail(oriMail);
			// }
			// }
			mail.put(Mail.IS_READ, "Y");
			this.updateMail(mail);
		}
	}

	public void setAsNotRead(List<String> mailGuidList) throws ServiceRequestException
	{
		for (String mailGuid : mailGuidList)
		{
			Mail mail = this.stubService.getMail(mailGuid);
			// if (!StringUtils.isNullString(mail.getOrigMailGuid()))
			// {
			// Mail oriMail = this.stubService.getMail(mail.getOrigMailGuid());
			// if (oriMail != null)
			// {
			// oriMail.put(Mail.IS_READ, "N");
			// this.updateMail(oriMail);
			// }
			// }
			mail.put(Mail.IS_READ, "N");
			this.updateMail(mail);
		}
	}

	public void setAsProcessed(List<String> mailGuidList) throws ServiceRequestException
	{
		for (String mailGuid : mailGuidList)
		{
			if (StringUtils.isNullString(mailGuid))
			{
				continue;
			}
			Mail mail = this.stubService.getMail(mailGuid);
			if (mail == null)
			{
				continue;
			}
			mail.put(Mail.IS_PROCESS, "Y");
			this.updateMail(mail);

			// // 同时处理已发邮件
			// mail = this.stubService.getMail(mail.getOrigMailGuid());
			// if (mail == null)
			// {
			// continue;
			// }
			// mail.put(Mail.IS_PROCESS, "Y");
			// this.updateMail(mail);
		}
	}

	public void setInboxCategory(List<String> mailGuidList, MailCategoryEnum mailCategoryEnum)
			throws ServiceRequestException
	{
		int category = 0;

		if (mailCategoryEnum != null)
		{
			category = mailCategoryEnum.ordinal() + 1;
		}

		for (String mailGuid : mailGuidList)
		{
			Mail mail = this.stubService.getMail(mailGuid);

			mail.put(Mail.CATEGORY, category);
			this.updateMail(mail);
		}

	}

	private void updateMail(Mail mail) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			mail.setUpdateUserGuid(this.stubService.getOperatorGuid());
			sds.update(Mail.class, mail, "update");
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

}
