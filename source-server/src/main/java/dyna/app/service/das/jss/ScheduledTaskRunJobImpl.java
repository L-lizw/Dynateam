/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ScheduledTaskRunJobImpl
 * Wanglei 2011-11-8
 */
package dyna.app.service.das.jss;

import dyna.app.core.sch.AbstractScheduledTask;
import dyna.app.server.context.ServiceContext;
import dyna.common.conf.JobDefinition;
import dyna.common.dto.Queue;
import dyna.common.dto.aas.User;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.JobStatus;
import dyna.common.systemenum.MailCategoryEnum;
import dyna.common.systemenum.MailMessageType;
import dyna.common.util.DateFormat;
import dyna.common.util.StringUtils;
import dyna.net.service.brs.AAS;
import dyna.net.service.brs.SMS;
import dyna.net.service.das.JSS;
import dyna.net.service.das.MSRM;

import java.util.Date;

/**
 * 队列工作任务执行线程
 * 
 * @author Wanglei
 * 
 */
public class ScheduledTaskRunJobImpl extends AbstractScheduledTask
{
	private ServiceContext	serviceContext	= null;
	private Queue			job				= null;
	private JobCallback		callback		= null;

	public ScheduledTaskRunJobImpl(ServiceContext serviceContext, Queue job, JobCallback callback) throws Exception
	{
		this.serviceContext = serviceContext;
		this.job = job;
		this.callback = callback;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.TimerTask#run()
	 */
	@Override
	public void run()
	{
		DynaLogger.info("JSS Scheduled [Class]ScheduledTaskRunJobImpl , Scheduled Task Start...");

		JobExecutor jobExecutor = null;
		JSSImpl jss = null;
		MSRM msr = null;
		String credential = null;
		String tranId = null;
		long time = 0;
		// FoundationObject job = null;
		try
		{
			time = new java.util.Date().getTime();
			jss = (JSSImpl) this.serviceContext.allocatService(JSS.class);
			jss.setSignature(this.serviceContext.getServerContext().getSystemInternalSignature());
			msr = jss.getRefService(MSRM.class);
			credential = this.serviceContext.getServerContext().getSystemInternalSignature().getCredential();
			tranId = jss.newTransactionId();

			// job = jss.getJob(this.jobObjectGuid);
			Object temp = this.job.getJobStatus();
			JobStatus status = temp == null ? JobStatus.WAITING : (JobStatus) temp;
			if (JobStatus.WAITING != status)
			{
				return;
			}

			String clsName = this.job.getExecutorClass();
			if (StringUtils.isNullString(clsName))
			{
				throw new Exception("undefined executor for job");
			}
			Class<?> jobExecutorClass = Class.forName(clsName);
			Object newInstance = jobExecutorClass.newInstance();
			if (newInstance instanceof JobExecutor)
			{
				jobExecutor = (JobExecutor) newInstance;
			}
			else
			{
				throw new Exception("executor initializes failed");
			}

			if (this.callback != null)
			{
				this.callback.beforePerform(this.job);
			}

			this.job = jss.getJob(this.job.getGuid());
			if (this.job.getJobStatus() != JobStatus.WAITING)
			{
				return;
			}
			this.job = jss.setJobStatus(this.job, JobStatus.RUNNING);

			JobResult result = jobExecutor.perform(jss, this.job);

			if (result == null)
			{
				result = JobResult.succeed(null);
			}

			this.job = jss.getJob(this.job.getGuid());
			this.job.setResult(result.getMessage());
			this.job.setJobStatus(result.getStatus());
			this.job = jss.saveJob(this.job);
			if (result.isSendNotify())
			{
				this.notifyCreator(jss, this.job, MailCategoryEnum.INFO, result.getMessage());
			}

			//TODO
//			if (StringUtils.isNullString(tranId) == false && DataServer.getTransactionManager().getCountOfNotCommitTranscation() > 0)
			{
				DynaLogger.info("APP Transation Not Commit");
				DynaLogger.info("APP Start:" + DateFormat.format(new Date(time), "HH:mm:ss,SSS") + "\tcredential:" + credential + "\tmethod:ScheduledTaskRunJobImpl#run");
//				DataServer.getTransactionService().commitTransactionImmediately(tranId);
			}
		}
		catch (Throwable e)
		{
			if (this.job != null && jss != null)
			{
				try
				{
					String result = StringUtils.convertNULLtoString(e.getMessage());

					this.job = jss.getJob(this.job.getGuid());
					this.job.setResult(result);
					this.job.setJobStatus(JobStatus.FAILED);
					this.job = jss.saveJob(this.job);
					String str = msr.getMSRString("ID_APP_JSS_JOB_RUN_FAIL", this.serviceContext.getServerContext().getSystemInternalSignature().getLanguageEnum().getId());
					this.notifyCreator(jss, this.job, MailCategoryEnum.ERROR, str + result);
				}
				catch (ServiceRequestException e1)
				{
				}
			}
			DynaLogger.error("RunJob failed : " + this.job, e);
		}
		finally
		{
			//TODO
//			if (StringUtils.isNullString(tranId) == false && DataServer.getTransactionManager().getCountOfNotCommitTranscation() > 0)
			{
				DynaLogger.info("APP Transation Not Commit");
				DynaLogger.info("APP Start:" + DateFormat.format(new Date(time), "HH:mm:ss,SSS") + "\tcredential:" + credential + "\tmethod:ScheduledTaskRunJobImpl#run");
//				DataServer.getTransactionService().rollbackTransactionImmediately(tranId);
			}

			if (jss != null)
			{
				this.serviceContext.releaseService(jss);
			}

			if (this.callback != null)
			{
				this.callback.afterPerform(this.job);
			}
			jobExecutor = null;
			DynaLogger.info("JSS Scheduled [Class]ScheduledTaskRunJobImpl , Scheduled Task End...");
		}

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
