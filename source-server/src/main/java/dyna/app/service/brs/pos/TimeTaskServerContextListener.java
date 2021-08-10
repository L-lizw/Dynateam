package dyna.app.service.brs.pos;

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
		long period = 10 * 60 * 1000;

		serviceContext.getServerContext().getSchedulerManager().getScheduledTaskScheduler().scheduleAtFixedRate(new HistoryDeleteScheduledTask(serviceContext), firstTime,
				period);

	}

}
