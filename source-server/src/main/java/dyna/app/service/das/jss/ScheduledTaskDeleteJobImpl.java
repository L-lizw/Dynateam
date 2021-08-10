package dyna.app.service.das.jss;

import java.util.List;

import dyna.app.core.sch.AbstractScheduledTask;
import dyna.app.server.context.ServiceContext;
import dyna.common.conf.JobDefinition;
import dyna.common.dto.Queue;
import dyna.common.log.DynaLogger;
import dyna.common.util.SetUtils;
import dyna.net.service.das.JSS;

public class ScheduledTaskDeleteJobImpl extends AbstractScheduledTask implements JobCallback
{
	private ServiceContext	serviceContext	= null;

	public ScheduledTaskDeleteJobImpl(ServiceContext serviceContext)
	{
		this.serviceContext = serviceContext;
	}

	@Override
	public void run()
	{
		JSSImpl jss = null;
		DynaLogger.info("JSS Scheduled [Class]ScheduledTaskDeleteJobImpl , Scheduled Task Start...");
		try
		{
			jss = (JSSImpl) this.serviceContext.allocatService(JSS.class);
			jss.setSignature(this.serviceContext.getServerContext().getSystemInternalSignature());
			jss.newTransactionId();

			List<JobDefinition> jobQueueTypeList = jss.getJobQueueTypeList();
			if (!SetUtils.isNullList(jobQueueTypeList))
			{
				for (JobDefinition jobDefinition : jobQueueTypeList)
				{
					ScheduledTaskDeleteJobByTypeImpl deleteJob = new ScheduledTaskDeleteJobByTypeImpl(this.serviceContext, jobDefinition.getJobID());
					serviceContext.getServerContext().getSchedulerManager().getMultiThreadQueuedTaskScheduler().addTask(deleteJob);
				}
			}
		}
		catch (Exception e)
		{
			DynaLogger.error("delete job error: " + e, e);
		}
		finally
		{
			if (jss != null)
			{
				this.serviceContext.releaseService(jss);
			}
			DynaLogger.info("JSS Scheduled [Class]ScheduledTaskDeleteJobImpl , Scheduled Task End...");
		}
	}

	@Override
	public void beforePerform(Queue job)
	{
		// do nothing
	}

	@Override
	public void afterPerform(Queue job)
	{
		// do nothing
	}

}
