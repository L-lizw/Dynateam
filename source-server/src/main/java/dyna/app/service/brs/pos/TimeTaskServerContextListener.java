package dyna.app.service.brs.pos;

import dyna.app.server.context.ServerContextListener;

import java.util.Calendar;
import java.util.Date;

public class TimeTaskServerContextListener implements ServerContextListener
{

	@Override
	public void contextInitialized()
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
