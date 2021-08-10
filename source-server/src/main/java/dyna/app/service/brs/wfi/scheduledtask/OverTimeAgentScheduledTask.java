package dyna.app.service.brs.wfi.scheduledtask;

import org.acegisecurity.context.SecurityContextHolder;

import dyna.app.core.sch.AbstractScheduledTask;
import dyna.app.server.context.ServiceContext;
import dyna.app.service.brs.wfi.WFIImpl;
import dyna.common.log.DynaLogger;
import dyna.net.service.brs.WFI;

class OverTimeAgentScheduledTask extends AbstractScheduledTask
{
	private ServiceContext serviceContext = null;

	public OverTimeAgentScheduledTask(ServiceContext serviceContext)
	{
		this.serviceContext = serviceContext;
	}

	@Override
	public void run()
	{
		DynaLogger.info("WFE OverTime Scheduled [Class]OverTimeAgentScheduledTask , Scheduled Task Start...");

		WFI wfe = null;
		try
		{
			wfe = this.serviceContext.allocatService(WFI.class);
			((WFIImpl) wfe).setSignature(this.serviceContext.getServerContext().getSystemInternalSignature());
			((WFIImpl) wfe).getProcessRuntimeStub().runOverTimeAgent();
		}
		catch (Throwable e)
		{
			DynaLogger.error("run OverTimeAction:", e);
		}
		finally
		{
			SecurityContextHolder.clearContext();

			if (wfe != null)
			{
				this.serviceContext.releaseService(wfe);
			}
		}
		DynaLogger.info("WFE OverTime Scheduled [Class]OverTimeAgentScheduledTask , Scheduled Task End...");
	}
}
