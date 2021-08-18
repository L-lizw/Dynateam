/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServerContextListenerJobPollingImpl
 * Wanglei 2011-11-9
 */
package dyna.app.service.das.jss;

import dyna.app.server.context.ApplicationServerContext;
import dyna.app.server.context.ServerContextListener;
import dyna.app.server.context.ServiceContext;
import dyna.common.conf.JobDefinition;
import dyna.common.conf.ServiceDefinition;
import dyna.common.dto.Queue;
import dyna.common.dto.aas.User;
import dyna.common.exception.ServiceNotFoundException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.JobStatus;
import dyna.common.systemenum.MailCategoryEnum;
import dyna.common.systemenum.MailMessageType;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.net.service.brs.AAS;
import dyna.net.service.brs.SMS;
import dyna.net.service.das.JSS;
import dyna.net.service.das.MSRM;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 工作任务轮询监听器, 添加轮询机制
 *
 * @author Wanglei
 */
public class ServerContextListenerJobPollingImpl implements ServerContextListener
{
	private final static Long DEFAULT_JOB_POLLING_TIME = Long.valueOf(30000);

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.app.server.context.ServerContextListener#contextInitialized(dyna.app.server.context.ServerContext,
	 * dyna.app.server.context.ServiceContext)
	 */
	@Override
	public void contextInitialized()
	{
		ServiceDefinition definition = serviceContext.getServiceConfig().getServiceDefinition("JSS");
		String jobPollingTimeStr = definition.getInitParameter("jobPollingTime");
		String jobERPPollingTimeStr = definition.getInitParameter("jobERPStatusPollingTime");

		ServiceDefinition erpServiceDefinition = serviceContext.getServiceConfig().getServiceDefinition("ERPI");
		String hasERPExceutingStatus = erpServiceDefinition.getInitParameter("hasERPExceutingStatus");

		long jobPollingTime = DEFAULT_JOB_POLLING_TIME;
		long jobERPPollingTime = DEFAULT_JOB_POLLING_TIME * 4;
		if (jobPollingTimeStr != null)
		{
			jobPollingTime = Long.valueOf(jobPollingTimeStr) * 1000;
		}
		if (jobERPPollingTimeStr != null)
		{
			jobERPPollingTime = Long.valueOf(jobERPPollingTimeStr) * 1000;
		}

		ScheduledTaskJobPollingImpl jobPollingTask = new ScheduledTaskJobPollingImpl(serviceContext);
		ScheduledTaskJobGetERPStausImpl jobGetErpStatusTask = new ScheduledTaskJobGetERPStausImpl(serviceContext);
		JSSImpl jss = null;
		try
		{
			jss = (JSSImpl) serviceContext.allocatService(JSS.class);
			MSRM msrm = jss.getRefService(MSRM.class);
			jss.setSignature(serverContext.getSystemInternalSignature());
			List<JobStatus> statuslist = new ArrayList<JobStatus>();
			statuslist.add(JobStatus.RUNNING);
			statuslist.add(JobStatus.ERPEXECUTING);
			List<Queue> finishJobList = jss.listJob(null, false, statuslist);
			if (!SetUtils.isNullList(finishJobList))
			{
				String str = msrm.getMSRString("ID_APP_JSS_JOB_RUN_FAIL", serverContext.getSystemInternalSignature().getLanguageEnum().getId());
				String msg = msrm.getMSRString("ID_APP_JSS_JOB_FAIL_SERVER_RESTART", serverContext.getSystemInternalSignature().getLanguageEnum().getId());
				for (Queue job : finishJobList)
				{
					job.setJobStatus(JobStatus.FAILED);
					job.setResult(msg);
					jss.saveJob(job);
					try
					{
						String clsName = job.getExecutorClass();
						if (StringUtils.isNullString(clsName))
						{
							this.notifyCreator(jss, job, MailCategoryEnum.ERROR, str + msg);
						}
						else
						{
							Class<?> jobExecutorClass = Class.forName(clsName);
							Object newInstance = jobExecutorClass.newInstance();
							if (newInstance instanceof JobExecutor)
							{
								JobExecutor jobExecutor = (JobExecutor) newInstance;
								job.setResult(jobExecutor.serverPerformFail(jss, job).getMessage());
								jss.saveJob(job);
							}
							else
							{
								this.notifyCreator(jss, job, MailCategoryEnum.ERROR, str + msg);
							}
						}
					}
					catch (Exception e)
					{
						this.notifyCreator(jss, job, MailCategoryEnum.ERROR, str + msg);
					}
				}
			}
		}
		catch (ServiceNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (ServiceRequestException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(jss != null)
			{
				serviceContext.releaseService(jss);
			}
		}
		serverContext.getSchedulerManager().getScheduledTaskScheduler().scheduleAtFixedRate(jobPollingTask, 5000, jobPollingTime);
		if ("true".equalsIgnoreCase(hasERPExceutingStatus))
		{
			serverContext.getSchedulerManager().getScheduledTaskScheduler().scheduleAtFixedRate(jobGetErpStatusTask, 60000, jobERPPollingTime);
		}
		addDeleteTask(serverContext, serviceContext);
	}

	private void addDeleteTask(ApplicationServerContext serverContext, ServiceContext serviceContext)
	{
		Date firstTime = null;
		firstTime = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(firstTime);

		Calendar firstTimeCalendar = Calendar.getInstance();
		firstTimeCalendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 2, 0, 0);
		firstTimeCalendar.add(Calendar.DAY_OF_MONTH, 1);
		firstTime = firstTimeCalendar.getTime();
		long period = 24 * 60 * 60 * 1000;
		serverContext.getSchedulerManager().getScheduledTaskScheduler().scheduleAtFixedRate(new ScheduledTaskDeleteJobImpl(serviceContext), firstTime, period);
	}

	private void notifyCreator(JSSImpl jss, Queue job, MailCategoryEnum category, String msg)
	{
		if (StringUtils.isNullString(msg))
		{
			return;
		}

		try
		{
			User user = jss.getServiceInstance(AAS.class).getUser(job.getCreateUserGuid());
			MSRM msr = jss.getServiceInstance(MSRM.class);
			String title = "";
			JobDefinition jobDefinition = jss.getJobDefinition(job.getExecutorClass());
			if (StringUtils.isNullString(jobDefinition.getMsrId()))
			{
				title = msr.getMSRString(jobDefinition.getMsrId(), jss.getUserSignature().getLanguageEnum().toString());
			}
			if (StringUtils.isNullString(title))
			{
				title = jobDefinition.getJobName();
			}
			if (StringUtils.isNullString(title))
			{
				title = job.getType();
			}
			jss.getServiceInstance(SMS.class).sendMailToUser(job.getType(), msg, category, null, user.getUserId(), MailMessageType.JOBNOTIFY);
		}
		catch (Exception e)
		{
			DynaLogger.error("notify job creator failed: " + e);
		}
	}

}
