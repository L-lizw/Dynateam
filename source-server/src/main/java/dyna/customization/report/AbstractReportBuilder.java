/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: AbstractReportBuilder
 * Wanglei 2011-12-21
 */
package dyna.customization.report;

import java.util.ArrayList;
import java.util.List;

import dyna.app.service.brs.srs.SRSImpl;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.exception.ServiceNotFoundException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.MailCategoryEnum;
import dyna.common.systemenum.MailMessageType;
import dyna.net.service.brs.SMS;

/**
 * @author Wanglei
 * 
 */
public abstract class AbstractReportBuilder implements ReportBuilder
{
	protected String			creatorId				= null;
	protected boolean			isNotifyCreator			= false;
	protected String			notifySubject			= null;
	protected String			notifyMessage			= null;
	protected List<ObjectGuid>	notifyAttachmentList	= new ArrayList<ObjectGuid>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.customization.report.ReportBuilder#notifyCreator()
	 */
	@Override
	public void notifyCreator(SRSImpl srs)
	{
		try
		{
			SMS sms = srs.getRefService(SMS.class);
			sms.sendMailToUser(this.getNotifySubject(), this.getNotifyMessage(), MailCategoryEnum.INFO,
					this.notifyAttachmentList, this.getCreatorId(), MailMessageType.REPORTNOTIFY);
		}
		catch (ServiceNotFoundException e)
		{
			DynaLogger.error(e.getMessage());
		}
		catch (ServiceRequestException e)
		{
			DynaLogger.error(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.customization.report.ReportBuilder#isNotifyCreator()
	 */
	@Override
	public boolean isNotifyCreator()
	{
		return this.isNotifyCreator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.customization.report.ReportBuilder#getNotifySubject()
	 */
	@Override
	public String getNotifySubject()
	{
		return this.notifySubject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.customization.report.ReportBuilder#getNotifyMessage()
	 */
	@Override
	public String getNotifyMessage()
	{
		return this.notifyMessage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.customization.report.ReportBuilder#getNotifyAttachmentList()
	 */
	@Override
	public List<ObjectGuid> getNotifyAttachmentList()
	{
		return this.notifyAttachmentList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.customization.report.ReportBuilder#getCreatorId()
	 */
	@Override
	public String getCreatorId()
	{
		return this.creatorId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.customization.report.ReportBuilder#setCreatorId(java.lang.String)
	 */
	@Override
	public void setCreatorId(String creatorId)
	{
		this.creatorId = creatorId;
	}

}
