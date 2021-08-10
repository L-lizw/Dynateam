package dyna.app.service.das.jss;

import dyna.app.core.sch.AbstractScheduledTask;
import dyna.app.server.context.ServiceContext;
import dyna.common.conf.JobDefinition;
import dyna.common.log.DynaLogger;
import dyna.net.service.das.JSS;

/**
 * 按照类型删除超期对列
 * 
 * @author duanll
 * 
 */
public class ScheduledTaskDeleteJobByTypeImpl extends AbstractScheduledTask
{
	private ServiceContext	serviceContext	= null;

	private String			jobType			= null;

	public ScheduledTaskDeleteJobByTypeImpl(ServiceContext serviceContext, String jobType)
	{
		this.serviceContext = serviceContext;
		this.jobType = jobType;
	}

	@Override
	public void run()
	{
		JSSImpl jss = null;
		DynaLogger.info("JSS Scheduled [Class]ScheduledTaskDeleteJobByTypeImpl[" + this.jobType + "], Scheduled Task Start...");
		try
		{
			jss = (JSSImpl) this.serviceContext.allocatService(JSS.class);
			jss.setSignature(this.serviceContext.getServerContext().getSystemInternalSignature());
			jss.newTransactionId();

			JobDefinition jobDefinition = jss.getJobDefinitionByType(this.jobType);
			int timeOut = jobDefinition.getTimeOut();
			if (timeOut > 0)
			{
				jss.deleteTimeoutJobs(this.jobType, timeOut);
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
			DynaLogger.info("JSS Scheduled [Class]ScheduledTaskDeleteJobByTypeImpl[" + this.jobType + "], Scheduled Task End...");
		}
	}
}
