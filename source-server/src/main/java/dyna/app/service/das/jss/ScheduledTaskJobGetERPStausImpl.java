/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ScheduledTaskJobPollingImpl
 * Wanglei 2011-11-8
 */
package dyna.app.service.das.jss;

import dyna.app.core.sch.AbstractScheduledTask;
import dyna.app.server.context.ServiceContext;
import dyna.app.service.brs.boas.BOASImpl;
import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.conf.ConfigurableKVElementImpl;
import dyna.common.conf.loader.ConfigLoaderDefaultImpl;
import dyna.common.dto.BooleanResult;
import dyna.common.dto.Queue;
import dyna.common.dto.aas.User;
import dyna.common.dto.erp.ERPServiceConfig;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.ERPServerType;
import dyna.common.systemenum.JobStatus;
import dyna.common.systemenum.MailCategoryEnum;
import dyna.common.systemenum.MailMessageType;
import dyna.common.util.EnvUtils;
import dyna.common.util.StringUtils;
import dyna.net.service.brs.AAS;
import dyna.net.service.brs.ERPI;
import dyna.net.service.brs.SMS;
import dyna.net.service.das.JSS;
import dyna.net.service.das.MSRM;

import java.io.File;
import java.util.*;

/**
 * 工作轮询任务
 * 
 * @author Wanglei
 * 
 */
public class ScheduledTaskJobGetERPStausImpl extends AbstractScheduledTask implements JobCallback
{
	private ServiceContext				serviceContext			= null;

	public ScheduledTaskJobGetERPStausImpl(ServiceContext serviceContext)
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
		DynaLogger.info("JSS Scheduled [Class]ScheduledGetERPStatusTaskJob , Scheduled Task Start...");

		JSSImpl jss = null;
		BOASImpl boas = null;
		try
		{
			jss = (JSSImpl) this.serviceContext.allocatService(JSS.class);
			jss.setSignature(this.serviceContext.getServerContext().getSystemInternalSignature());
			jss.newTransactionId();
			boas = (BOASImpl) jss.getBOAS();
			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put(Queue.SERVER_ID, this.serviceContext.getServerContext().getServerConfig().getServerId());
			condition.put(Queue.JOB_STATUS, JobStatus.ERPEXECUTING.getValue());
			List<Queue> erpExecutingJobList = jss.listJob(condition);
			MSRM msr = jss.getRefService(MSRM.class);
			Calendar xCalendar = Calendar.getInstance();
			for (Queue queuejob : erpExecutingJobList)
			{
				String serviceGuid = queuejob.getFieldc();
				if (queuejob.getType().equals("CFERP"))
				{
					serviceGuid = queuejob.getFieldb();
				}
				ERPI erpi = jss.getRefService(ERPI.class);
				ERPServiceConfig serviceConfig = erpi.getERPServiceConfigbyServiceGuid(serviceGuid);
				String jobLiveTimeStr = this.getParamVal(ERPServerType.valueOf(serviceConfig.getERPServerSelected()), "jobLiveTime");
				String jobPollingTimeStr = this.getParamVal(ERPServerType.valueOf(serviceConfig.getERPServerSelected()), "jobPollingTime");

				if (jobLiveTimeStr == null)
				{
					continue;
				}
				Integer jobLiveTime = Integer.valueOf(jobLiveTimeStr);
				Integer jobPollTime = Integer.valueOf(jobPollingTimeStr);
				String result = "The ERP processing time is more than the preset value for " + jobLiveTime + " seconds.";
				if (ERPServerType.ERPT100 == ERPServerType.valueOf(serviceConfig.getERPServerSelected()) 
						|| ERPServerType.ERPT100DB == ERPServerType.valueOf(serviceConfig.getERPServerSelected())
						|| ERPServerType.ERPTIPTOP == ERPServerType.valueOf(serviceConfig.getERPServerSelected()))
				{
					Date jobUpdateTime = queuejob.getUpdateTime();
					xCalendar.setTime(jobUpdateTime);
					xCalendar.add(Calendar.SECOND, jobPollTime);
					if(xCalendar.getTimeInMillis() < System.currentTimeMillis())
					{
						queuejob=this.getJobStatus(queuejob, erpi, jss, boas);
					}
					if(queuejob.getJobStatus()==JobStatus.ERPEXECUTING)
					{
						xCalendar.setTime(jobUpdateTime);
						xCalendar.add(Calendar.SECOND, jobLiveTime);
						if (xCalendar.getTimeInMillis() < System.currentTimeMillis())
						{
							DynaLogger.info("job process timeout");
							failJob(queuejob, result, jss, boas, msr);
						}
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
		DynaLogger.info("JSS Scheduled [Class]ScheduledGetERPStatusTaskJob , Scheduled Task End...");
	}

	private Queue getJobStatus(Queue queuejob, ERPI erpi, JSSImpl jss, BOASImpl boas) throws Exception 
	{
		StringBuffer buffer = new StringBuffer();
		BooleanResult result = erpi.getJobStatusBySeqkeyFromERP(queuejob.getGuid());
		if (result != null)
		{
			String[] splita = queuejob.getFielda().split(";");
			String[] splitb = queuejob.getFieldb().split(";");
			String foNames = "";

			try
			{
				ObjectGuid objectGuid = new ObjectGuid(splitb[0], null, splita[0], null);
				FoundationObject fo = boas.getObjectByGuid(objectGuid);
				foNames = foNames + fo.getFullName();
				if (splita.length > 1)
				{
					foNames = foNames + "  (...)";
				}
			}
			catch (Exception e)
			{

			}
			// ObjectGuid objectGuid = new ObjectGuid(queuejob.getFieldb(), null, queuejob.getFielda(), null);
			// FoundationObject fo = boas.getObjectByGuid(objectGuid);
			buffer.append(queuejob.getFieldf()).append(" : ");
			// if (fo != null)
			// {
			// buffer.append(fo.getFullName()).append(" : ").append(queuejob.getFieldd()).append("\n");
			// }
			buffer.append(foNames).append(" : ").append(queuejob.getFieldd()).append("\n");

			buffer.append("JobID: ").append(queuejob.getFieldh()).append("\n");
			buffer.append(result.getDetail());
			if (!result.getFlag())
			{
				if (result.isDataEmpty())
				{
					queuejob.setJobStatus(JobStatus.FAILED);
					queuejob.setResult("Fail:" + buffer.toString());
					this.notifyCreator(jss, queuejob, MailCategoryEnum.ERROR, "Fail:" + buffer.toString());
				}
				else
				{
					this.notifyCreator(jss, queuejob, MailCategoryEnum.ERROR, result.getDetail());
				}
			}
			else
			{
				queuejob.setJobStatus(JobStatus.SUCCESSFUL);
				queuejob.setResult("Succeed:" + buffer.toString());
				this.notifyCreator(jss, queuejob, MailCategoryEnum.INFO, "Succeed:" + buffer.toString());
			}
			queuejob = jss.saveJob(queuejob);
		}
		return queuejob;
		
	}

	private void failJob(Queue queuejob, String result, JSSImpl jss, BOASImpl boas, MSRM msr) throws ServiceRequestException 
	{
		queuejob.setJobStatus(JobStatus.FAILED);
		queuejob.setResult(result);
		queuejob = jss.saveJob(queuejob);

		ObjectGuid objectGuid = new ObjectGuid(queuejob.getFieldb(), null, queuejob.getFielda(), null);
		FoundationObject fo = boas.getObjectByGuid(objectGuid);
		String str = "";
		if (fo != null)
		{
			str += fo.getFullName() + ":" + queuejob.getFieldd() + "\n";
		}
		str += "JobID: " + queuejob.getFieldh() + "\n";
		str += "PLM:    " + msr.getMSRString("ID_APP_JSS_JOB_RUN_FAIL", this.serviceContext.getServerContext().getSystemInternalSignature().getLanguageEnum().getId());
		this.notifyCreator(jss, queuejob, MailCategoryEnum.ERROR, str + result);
		
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
		//POLLING_JOBGUID_LIST.remove(job.getGuid());
	}

	private String getFileName(ERPServerType type)
	{
		if (type == ERPServerType.ERPTIPTOP)
		{
			return "ytconf";
		}
		else if (type == ERPServerType.ERPWORKFLOW)
		{
			return "wfconf";
		}
		else if (type == ERPServerType.ERPYF)
		{
			return "yfconf";
		}
		else if (type == ERPServerType.ERPE10)
		{
			return "e10conf";
		}
		else if (type == ERPServerType.ERPSM)
		{
			return "smconf";
		}
		else if (type == ERPServerType.ERPT100)
		{
			return "T100conf";
		}
		else if (type == ERPServerType.ERPT100DB)
		{
			return "T100_DBconf";
		}

		throw new IllegalArgumentException(type + " is not supported(dyna.app.service.brs.erpi.ERPStub#getFileName)");
	}

	private ConfigurableKVElementImpl getXMLConfig(String fileName)
	{
		if (StringUtils.isNullString(fileName))
		{
			return null;
		}
		ConfigLoaderDefaultImpl configLoader = new ConfigLoaderDefaultImpl();
		configLoader.setConfigFile(new File(EnvUtils.getConfRootPath() + "conf" + File.separator + fileName + ".xml"));
		configLoader.load();
		ConfigurableKVElementImpl kv = configLoader.getConfigurable();
		return kv;
	}

	private String getParamVal(ERPServerType type, String key)
	{
		String fileName = this.getFileName(type);
		Iterator<ConfigurableKVElementImpl> it = this.getXMLConfig(fileName).iterator("root.parameters.param");
		ConfigurableKVElementImpl kv = null;
		while (it.hasNext())
		{
			kv = it.next();
			if (key.equalsIgnoreCase(kv.getAttributeValue("name")))
			{
				return kv.getAttributeValue("value");
			}
		}
		return null;
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
			jss.getServiceInstance(SMS.class).sendMailToUser(job.getType(), msg, category, null, user.getUserId(), MailMessageType.ERPNOTIFY);
		}
		catch (Exception e)
		{
			DynaLogger.error("notify job creator failed: " + e);
		}
	}
}
