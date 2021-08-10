/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: TimeTaskServerContextListener 定时生效监听器
 * Qiuxq 2012-06-04
 */
package dyna.app.service.brs.ppms.timertask;

import java.util.Calendar;
import java.util.Date;

import dyna.app.server.context.ApplicationServerContext;
import dyna.app.server.context.ServerContextListener;
import dyna.app.server.context.ServiceContext;

public class TimeTaskServerContextListener implements ServerContextListener
{

	@Override
	public void contextInitialized(ApplicationServerContext serverContext, ServiceContext serviceContext)
	{
		Date firstTime = null;
		firstTime = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(firstTime);

		Calendar firstTimeCalendar = Calendar.getInstance();
		firstTimeCalendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH), 2, 0, 0);
		firstTimeCalendar.add(Calendar.DAY_OF_MONTH, 1);
		firstTime = firstTimeCalendar.getTime();
		long period = 24 * 60 * 60 * 1000;

		serviceContext.getServerContext().getSchedulerManager().getScheduledTaskScheduler()
				.scheduleAtFixedRate(new ProjectCalculateScheduled(serviceContext), firstTime, period);

		firstTimeCalendar = Calendar.getInstance();
		firstTimeCalendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH), 3, 0, 0);
		firstTimeCalendar.add(Calendar.DAY_OF_MONTH, 1);
		firstTime = firstTimeCalendar.getTime();

		serviceContext.getServerContext().getSchedulerManager().getScheduledTaskScheduler()
				.scheduleAtFixedRate(new WarningScheduledTask(serviceContext), firstTime, period);

	}

}
