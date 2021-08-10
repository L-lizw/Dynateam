/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: MailInboxStub
 * Wanglei 2011-4-1
 */
package dyna.app.service.brs.sms;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.boas.BOASImpl;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.app.service.helper.decorate.DecoratorFactory;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.dto.Mail;
import dyna.common.dto.MailAttachment;
import dyna.common.dto.aas.User;
import dyna.common.exception.DecorateException;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;
import dyna.net.service.brs.EMM;

/**
 * @author Wanglei
 * 
 */
public class MailStub extends AbstractServiceStub<SMSImpl>
{

	/**
	 * @param context
	 * @param service
	 */
	protected MailStub(ServiceContext context, SMSImpl service)
	{
		super(context, service);
	}

	protected Mail getMail(String mailGuid) throws ServiceRequestException
	{
		Mail mail = null;

		List<MailAttachment> mailAttachmentList = null;

		SystemDataService sds = DataServer.getSystemDataService();

		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(Mail.GUID, mailGuid);
		if (StringUtils.isNullString(mailGuid))
		{
			DynaLogger.error("MailStub getMail(): mailguid is null");
			return null;
		}

		try
		{
			mail = sds.queryObject(Mail.class, filter);
			filter.clear();
			if (mail == null)
			{
				return null;
			}

			if (StringUtils.isGuid(mail.getOrigMailGuid()))
			{
				filter.put(Mail.MAIL_GUID, mail.getOrigMailGuid());
			}
			else
			{
				filter.put(Mail.MAIL_GUID, mailGuid);
			}

			if(!StringUtils.isNullString(mail.getReceiveUser()))
			{
				User receiver = this.stubService.getAAS().getUser(mail.getReceiveUser());
				if(receiver != null)
				{
					mail.put(Mail.RECEIVE_USER_NAME, receiver.getName());
				}
					
			}
			
			if(!StringUtils.isNullString(mail.getSenderUser()))
			{
				User sender = this.stubService.getAAS().getUser(mail.getSenderUser());
				if(sender != null)
				{
					mail.put(Mail.SENDER_USER_NAME, sender.getName());
				}
					
			}
			
			mailAttachmentList = sds.query(MailAttachment.class, filter);

			mail.setMailAttachmentList(mailAttachmentList);
			if (!SetUtils.isNullList(mailAttachmentList))
			{
				EMM emm = this.stubService.getEMM();
				for (MailAttachment att : mailAttachmentList)
				{
					DecoratorFactory.decorateMailAttachment(att, emm);
				}
			}

		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (DecorateException e)
		{
			throw ServiceRequestException.createByDecorateException(e);
		}
		finally
		{
		}

		return mail;
	}

	protected FoundationObject getMailAttachment(String mailGuid, ObjectGuid objectGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			Map<String, Object> filter = new HashMap<String, Object>();

			Mail mail = this.stubService.getMail(mailGuid);
			if (mail == null)
			{
				return null;
			}

			if (StringUtils.isGuid(mail.getOrigMailGuid()))
			{
				filter.put(Mail.MAIL_GUID, mail.getOrigMailGuid());
			}
			else
			{
				filter.put(Mail.MAIL_GUID, mailGuid);
			}

			List<MailAttachment> mailAttachmentList = sds.query(MailAttachment.class, filter);
			filter.clear();
			filter = null;

			boolean isValidAttach = false;
			if (!SetUtils.isNullList(mailAttachmentList))
			{
				for (MailAttachment attach : mailAttachmentList)
				{
					if (attach.getInstanceGuid().equals(objectGuid.getGuid()))
					{
						isValidAttach = true;
						break;
					}
				}
			}

			if (!isValidAttach)
			{
				throw new ServiceRequestException("ID_APP_NO_MATCH_MAIL_ATTACHMENT", "no match mail attachment");
			}

			return ((BOASImpl) this.stubService.getBOAS()).getFoundationStub().getObject(objectGuid, false);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}
}
