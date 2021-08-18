/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: TimeClearServerContextListener
 * wangweixia 2015-3-2
 */
package dyna.app.service.brs.sms;

import dyna.app.server.context.ServerContextListener;

import java.util.Calendar;
import java.util.Date;

/**
 * @author wangweixia
 *         定时清除邮件服务(依据：消息清除规则设置、流程清除规则设置)
 */
public class TimeClearServerContextListener implements ServerContextListener
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.server.context.ServerContextListener#contextInitialized(dyna.app.server.context.ServerContext,
	 * dyna.app.server.context.ServiceContext)
	 */
	@Override
	public void contextInitialized()
	{
		Date firstTime = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(firstTime);

		Calendar firstTimeCalendar = Calendar.getInstance();
		firstTimeCalendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 1, 0, 0);
		firstTimeCalendar.add(Calendar.DAY_OF_MONTH, 1);
		firstTime = firstTimeCalendar.getTime();
		long period = 24 * 60 * 60 * 1000;

		serviceContext.getServerContext().getSchedulerManager().getScheduledTaskScheduler().scheduleAtFixedRate(new ClearMailScheduled(serviceContext), firstTime, period);
	}

}
