/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: TimeTaskServerContextListener 定时生效监听器
 * WangLHB 2012-1-13
 */
package dyna.app.service.brs.wfi.scheduledtask;

import java.util.Calendar;
import java.util.Date;

import dyna.app.server.context.ApplicationServerContext;
import dyna.app.server.context.ServerContextListener;
import dyna.app.server.context.ServiceContext;
import dyna.common.exception.DynaDataException;
import dyna.common.log.DynaLogger;

public class TimeTaskServerContextListener implements ServerContextListener
{

	@Override
	public void contextInitialized(ApplicationServerContext serverContext, ServiceContext serviceContext)
	{
		Date firstTime = null;
		try
		{
			firstTime = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(firstTime);

			Calendar firstTimeCalendar = Calendar.getInstance();
			firstTimeCalendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH), 1, 0, 0);
			firstTimeCalendar.add(Calendar.DAY_OF_MONTH, 1);
			firstTime = firstTimeCalendar.getTime();
		}
		catch (DynaDataException e)
		{
			DynaLogger.error("WFE TimeTaskServerContextListener:", e);
		}

		long period = 24 * 60 * 60 * 1000;

		serviceContext.getServerContext().getSchedulerManager().getScheduledTaskScheduler()
				.scheduleAtFixedRate(new NoticeScheduledTask(serviceContext), firstTime, period);

		serviceContext.getServerContext().getSchedulerManager().getScheduledTaskScheduler()
				.scheduleAtFixedRate(new OverTimeActionScheduledTask(serviceContext), firstTime, period);

		serviceContext.getServerContext().getSchedulerManager().getScheduledTaskScheduler()
		        .scheduleAtFixedRate(new OverTimeAgentScheduledTask(serviceContext), firstTime, period);
	}

}
