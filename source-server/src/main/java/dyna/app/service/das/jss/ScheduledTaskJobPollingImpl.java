/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ScheduledTaskJobPollingImpl
 * Wanglei 2011-11-8
 */
package dyna.app.service.das.jss;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import dyna.app.core.sch.AbstractScheduledTask;
import dyna.app.core.sch.ScheduledTask;
import dyna.app.server.context.ServiceContext;
import dyna.common.dto.Queue;
import dyna.common.log.DynaLogger;
import dyna.common.util.SetUtils;
import dyna.net.service.das.JSS;

/**
 * 工作轮询任务
 * 
 * @author Wanglei
 * 
 */
public class ScheduledTaskJobPollingImpl extends AbstractScheduledTask implements JobCallback
{
	private static final List<String>	POLLING_JOBGUID_LIST	= Collections.synchronizedList(new ArrayList<String>());

	private ServiceContext				serviceContext			= null;

	public ScheduledTaskJobPollingImpl(ServiceContext serviceContext)
	{
		this.serviceContext = serviceContext;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.TimerTask#run()
	 */
	@Override
	public void run()
	{
		// DynaLogger.info("JSS Scheduled [Class]ScheduledTaskJobPollingImpl , Scheduled Task Start...");

		JSSImpl jss = null;
		try
		{
			jss = (JSSImpl) this.serviceContext.allocatService(JSS.class);
			jss.setSignature(this.serviceContext.getServerContext().getSystemInternalSignature());
			jss.newTransactionId();
			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put(Queue.SERVER_ID, this.serviceContext.getServerContext().getServerConfig().getServerId());
			List<Queue> waitingJobList = jss.listWaitingJob(condition);
			if (SetUtils.isNullList(waitingJobList) == false)
			{
				String jobObjectGuid = null;
				for (Queue job : waitingJobList)
				{
					jobObjectGuid = job.getGuid();
					if (POLLING_JOBGUID_LIST.contains(jobObjectGuid))
					{
						continue;
					}

					POLLING_JOBGUID_LIST.add(jobObjectGuid);
					String sid = jss.getJobDefinition(job.getExecutorClass()).getSchedulerID();
					if (this.serviceContext.getServerContext().getSchedulerManager().getScheduler(sid) != null)
					{
						ScheduledTask task = new ScheduledTaskRunJobImpl(this.serviceContext, job, this);
						task.setPriority(job.getPriority().intValue());
						task.setCreateTime(job.getCreateTime());
						this.serviceContext.getServerContext().getSchedulerManager().getScheduler(sid).addTask(task);
					}
				}
			}
		}
		catch (Exception e)
		{
			DynaLogger.error("polling job error: " + e, e);
		}
		finally
		{
			if (jss != null)
			{
				this.serviceContext.releaseService(jss);
			}
		}
		// DynaLogger.info("JSS Scheduled [Class]ScheduledTaskJobPollingImpl , Scheduled Task End...");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.service.brs.jss.JobCallback#beforePerform(dyna.common.bean.data.FoundationObject)
	 */
	@Override
	public void beforePerform(Queue job)
	{
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.service.brs.jss.JobCallback#afterPerform(dyna.common.bean.data.FoundationObject)
	 */
	@Override
	public void afterPerform(Queue job)
	{
		POLLING_JOBGUID_LIST.remove(job.getGuid());
	}

}
