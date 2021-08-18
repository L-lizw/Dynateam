/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: JSSImpl Job Serialized Service 实现
 * Wanglei 2011-11-7
 */
package dyna.app.service.das.jss;

import dyna.app.core.sch.SchedulerQueuedTaskImpl;
import dyna.app.server.GenericServer;
import dyna.app.service.DataAccessService;
import dyna.common.conf.ConfigurableJSSImpl;
import dyna.common.conf.JobDefinition;
import dyna.common.conf.SchedulerDefinition;
import dyna.common.conf.loader.ConfigLoaderFactory;
import dyna.common.conf.loader.ConfigLoaderJSSImpl;
import dyna.common.dto.Queue;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.JobGroupEnum;
import dyna.common.systemenum.JobStatus;
import dyna.net.service.brs.AAS;
import dyna.net.service.brs.BOAS;
import dyna.net.service.brs.EMM;
import dyna.net.service.brs.LIC;
import dyna.net.service.das.JSS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Job Serialized Service 实现
 * 
 * @author Wanglei
 * 
 */
@Service
public class JSSImpl extends DataAccessService implements JSS
{

	private static boolean		initialized		= false;
	private static boolean		runJobQuery		= true;
	@Autowired
	private JobQueryStub		jobQueryStub	= null;
	@Autowired
	private JobCreationStub		jobCreationStub	= null;
	@Autowired
	private JobUpdaterStub		jobUpdaterStub	= null;
	private ConfigurableJSSImpl	configurableJSS	= null;

	private synchronized void syncInit()
	{
		if (initialized)
		{
			return;
		}
		initialized = true;
		if (runJobQuery)
		{
			server.addServerContextListener(new ServerContextListenerJobPollingImpl());
			if (configurableJSS.getSchedulermap() != null)
			{
				Iterator<SchedulerDefinition> itr = configurableJSS.getSchedulermap().values().iterator();
				while (itr.hasNext())
				{
					SchedulerDefinition sd = itr.next();
					if (serviceContext.getServerContext().getSchedulerManager().getScheduler(sd.getId()) == null)
					{
						serviceContext.getServerContext().getSchedulerManager().addScheduler(sd.getId(), new SchedulerQueuedTaskImpl(sd.getThreadCount()));
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.service.DataAccessService#init()
	 */
	@Override
	public void init()
	{
		runJobQuery = !"false".equalsIgnoreCase(this.getServiceDefinition().getInitParameter("enable"));
		ConfigLoaderJSSImpl configLoader = GenericServer.getServiceBean(ConfigLoaderFactory.class).getLoader4JSS();
		this.configurableJSS = configLoader.getConfigurable();
		syncInit();
	}

	/**
	 * @return the jobQueryStub
	 */
	protected JobQueryStub getJobQueryStub() throws ServiceRequestException
	{
		return this.jobQueryStub;
	}

	protected JobCreationStub getJobCreationStub()
	{
		return this.jobCreationStub;
	}

	/**
	 * @return the jobUpdaterStub
	 */
	protected JobUpdaterStub getJobUpdaterStub()
	{
		return this.jobUpdaterStub;
	}

	protected synchronized BOAS getBOAS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(BOAS.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	public synchronized AAS getAAS() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(AAS.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	public synchronized LIC getLIC() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(LIC.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	protected synchronized EMM getEMM() throws ServiceRequestException
	{
		try
		{
			return this.getRefService(EMM.class);
		}
		catch (Exception e)
		{
			throw new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.JSS#getJob(java.lang.String)
	 */
	@Override
	public Queue getJob(String jobObjectGuid) throws ServiceRequestException
	{
		return this.getJobQueryStub().getJob(jobObjectGuid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.JSS#listJob(dyna.common.SearchCondition)
	 */
	@Override
	public List<Queue> listJob(Map<String, Object> condition) throws ServiceRequestException
	{
		return this.getJobQueryStub().listJob(condition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.JSS#listWaitingJob(dyna.common.SearchCondition)
	 */
	@Override
	public List<Queue> listWaitingJob(Map<String, Object> condition) throws ServiceRequestException
	{
		return this.getJobQueryStub().listWaitingJob(condition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.JSS#listRunningJob(dyna.common.SearchCondition)
	 */
	@Override
	public List<Queue> listRunningJob(Map<String, Object> condition) throws ServiceRequestException
	{
		return this.getJobQueryStub().listRunningJob(condition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.JSS#listCancelJob(dyna.common.SearchCondition)
	 */
	@Override
	public List<Queue> listCancelJob(Map<String, Object> condition) throws ServiceRequestException
	{
		return this.getJobQueryStub().listCancelJob(condition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.JSS#listSuccessfulJob(dyna.common.SearchCondition)
	 */
	@Override
	public List<Queue> listSuccessfulJob(Map<String, Object> condition) throws ServiceRequestException
	{
		return this.getJobQueryStub().listSuccessfulJob(condition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.JSS#listFailedJob(dyna.common.SearchCondition)
	 */
	@Override
	public List<Queue> listFailedJob(Map<String, Object> condition) throws ServiceRequestException
	{
		return this.getJobQueryStub().listFailedJob(condition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.JSS#createJob(dyna.common.bean.data.FoundationObject)
	 */
	@Override
	public Queue createJob(Queue fo) throws ServiceRequestException
	{

		return this.getJobCreationStub().createJob(fo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.JSS#saveJob(dyna.common.bean.data.FoundationObject)
	 */
	@Override
	public Queue saveJob(Queue fo) throws ServiceRequestException
	{
		return this.getJobUpdaterStub().saveJob(fo, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.JSS#setJobStatus(dyna.common.bean.data.FoundationObject,
	 * dyna.common.systemenum.JobStatus)
	 */
	@Override
	public Queue setJobStatus(Queue fo, JobStatus jobStatus) throws ServiceRequestException
	{
		return this.getJobUpdaterStub().setJobStatus(fo, jobStatus);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.JSS#deleteJobs(java.lang.String[])
	 */
	@Override
	public void deleteJobs(String... jobGuids) throws ServiceRequestException
	{
		this.getJobCreationStub().deleteJobs(jobGuids);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.brs.JSS#deleteTimeoutJobs(java.lang.String, int)
	 */
	@Override
	public void deleteTimeoutJobs(String jobType, int timeOut) throws ServiceRequestException
	{
		this.getJobCreationStub().deleteTimeoutJobs(jobType, timeOut);
	}

	/**
	 * @param executorClass
	 * @return
	 */
	public JobDefinition getJobDefinition(String executorClass)
	{
		return this.configurableJSS.getJobDelfinitionWithClassName(executorClass);
	}

	public JobDefinition getJobDefinitionByType(String jobType)
	{
		return this.configurableJSS.getJobDefinitionByType(jobType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.das.JSS#getJobQueueTypeList()
	 */
	@Override
	public List<JobDefinition> getJobQueueTypeList() throws ServiceRequestException
	{
		return configurableJSS.getJobDefinitionList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.das.JSS#listNotFinishJobQueue(java.lang.String)
	 */
	@Override
	public List<Queue> listJob(String typeId, boolean isSearchOwner, List<JobStatus> statuslist) throws ServiceRequestException
	{
		return this.getJobQueryStub().listJob(typeId, isSearchOwner, statuslist);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.das.JSS#listJobQueue(java.util.Map, int, int, boolean)
	 */
	@Override
	public List<Queue> listJobQueue(Map<String, Object> searchCondition, int pageNum, int pageSize) throws ServiceRequestException
	{
		return this.getJobQueryStub().listJobQueue(searchCondition, pageNum, pageSize);
	}

	@Override
	public Queue saveJob4ERPNotify(Queue fo) throws ServiceRequestException
	{
		return this.getJobUpdaterStub().saveJob(fo, true);
	}

	@Override
	public void reStartQueue(Queue queue) throws ServiceRequestException
	{
		Queue newQueue = new Queue();
		queue.setGuid(null);
		newQueue = (Queue) queue.clone();
		if (queue.getJobGroup().equals(JobGroupEnum.ERP))
		{
			newQueue.setFieldh(String.valueOf(System.nanoTime()));
			newQueue.setFieldl(null);
		}
		this.createJob(newQueue);
	}

}
