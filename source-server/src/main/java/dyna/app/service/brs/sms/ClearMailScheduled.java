/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ClearMailScheduled
 * wangweixia 2015-3-2
 */
package dyna.app.service.brs.sms;

import org.acegisecurity.context.SecurityContextHolder;

import dyna.app.core.sch.AbstractScheduledTask;
import dyna.app.server.context.ServiceContext;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.net.service.brs.SMS;

/**
 * @author wangweixia
 *         邮件清除：按照每个人设置的时间进行清除
 */
public class ClearMailScheduled extends AbstractScheduledTask
{

	private ServiceContext	serviceContext	= null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.TimerTask#run()
	 */
	@Override
	public void run()
	{
		DynaLogger.info("Mail clear Scheduled [Class]ClearMailScheduled , Scheduled Task Start...");

		SMS sms = null;
		try
		{
			sms = this.serviceContext.allocatService(SMS.class);
			((SMSImpl) sms).setSignature(this.serviceContext.getServerContext().getSystemInternalSignature());
			this.clearMailByConfig(sms);
		}
		catch (Throwable e)
		{
			DynaLogger.error("run ClearMailScheduled:", e);
		}
		finally
		{
			SecurityContextHolder.clearContext();

			if (sms != null)
			{
				this.serviceContext.releaseService(sms);
			}
		}
		DynaLogger.info("Mail clear Scheduled [Class]ClearMailScheduled , Scheduled Task End...");

	}

	/**
	 * 通过“个人偏好”清除邮件(消息和流程消息)
	 * 
	 * @param sms
	 * @throws ServiceRequestException
	 */
	private void clearMailByConfig(SMS sms) throws ServiceRequestException
	{
		sms.clearMailByConfig();
	}

	/**
	 * 
	 */
	public ClearMailScheduled(ServiceContext serviceContext)
	{
		this.serviceContext = serviceContext;
	}
}
