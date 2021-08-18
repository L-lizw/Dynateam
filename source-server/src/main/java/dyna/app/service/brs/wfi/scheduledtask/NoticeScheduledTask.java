package dyna.app.service.brs.wfi.scheduledtask;

import dyna.app.core.sch.AbstractScheduledTask;
import dyna.app.server.context.ServiceContext;
import dyna.app.service.brs.wfi.WFIImpl;
import dyna.common.log.DynaLogger;
import dyna.net.service.brs.WFI;
import org.acegisecurity.context.SecurityContextHolder;

class NoticeScheduledTask extends AbstractScheduledTask
{
	private ServiceContext serviceContext = null;

	public NoticeScheduledTask(ServiceContext serviceContext)
	{
		this.serviceContext = serviceContext;
	}

	@Override
	public void run()
	{
		DynaLogger.info("WFE notice Scheduled [Class]NoticeScheduledTask , Scheduled Task Start...");

		WFI wfi = null;
		try
		{
			wfi = this.serviceContext.allocatService(WFI.class);
			((WFIImpl) wfi).setSignature(this.serviceContext.getServerContext().getSystemInternalSignature());
			((WFIImpl) wfi).getProcessRuntimeStub().noticeDefActrtPerf();
			((WFIImpl) wfi).getProcessRuntimeStub().noticeAdvActrtPerf();
		}
		catch (Throwable e)
		{
			DynaLogger.error("run Notice:", e);
		}
		finally
		{
			SecurityContextHolder.clearContext();

			if (wfi != null)
			{
				this.serviceContext.releaseService(wfi);
			}
		}
		DynaLogger.info("WFE notice Scheduled [Class]NoticeScheduledTask , Scheduled Task End...");

	}
}
