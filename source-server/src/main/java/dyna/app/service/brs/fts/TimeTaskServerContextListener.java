package dyna.app.service.brs.fts;

import dyna.app.server.context.ServerContextListener;
import dyna.common.exception.DynaDataException;
import dyna.common.log.DynaLogger;

import java.util.Calendar;
import java.util.Date;

public class TimeTaskServerContextListener implements ServerContextListener
{

	@Override
	public void contextInitialized()
	{
		Date firstTime = null;
		try
		{
			firstTime = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(firstTime);

			Calendar firstTimeCalendar = Calendar.getInstance();
			firstTimeCalendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), 0, 0);
			firstTimeCalendar.add(Calendar.MINUTE, 1);
			firstTime = firstTimeCalendar.getTime();
		}
		catch (DynaDataException e)
		{
			DynaLogger.error("FTS TimeTaskServerContextListener:", e);
		}

		long period =  5 * 60 * 1000;
		
		serviceContext.getServerContext().getSchedulerManager().getScheduledTaskScheduler().scheduleAtFixedRate(new TransformQueueScheduledTask(serviceContext), firstTime, period);

	}
}