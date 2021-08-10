package dyna.app.service.brs.ppms.timertask;

import org.acegisecurity.context.SecurityContextHolder;

import dyna.app.core.sch.AbstractScheduledTask;
import dyna.app.server.context.ServiceContext;
import dyna.app.service.brs.ppms.PPMSImpl;
import dyna.common.log.DynaLogger;
import dyna.net.service.brs.PPMS;

public class WarningScheduledTask extends AbstractScheduledTask
{
	private ServiceContext	serviceContext	= null;

	public WarningScheduledTask(ServiceContext serviceContext)
	{
		this.serviceContext = serviceContext;
	}

	@Override
	public void run()
	{
		DynaLogger.info("PPMS Warning Scheduled [Class]WarningScheduledTask , Scheduled Task Start...");

		PPMS ppms = null;
		try
		{
			ppms = this.serviceContext.allocatService(PPMS.class);
			((PPMSImpl) ppms).setSignature(this.serviceContext.getServerContext().getSystemInternalSignature());
			((PPMSImpl) ppms).dispatchProjectWarningRule();
		}
		catch (Throwable e)
		{
			DynaLogger.error("run runWarningRule:", e);
		}
		finally
		{
			SecurityContextHolder.clearContext();

			if (ppms != null)
			{
				this.serviceContext.releaseService(ppms);
			}
		}
		DynaLogger.info("PPMS Warning Scheduled [Class]WarningScheduledTask , Scheduled Task End...");

	}

}

