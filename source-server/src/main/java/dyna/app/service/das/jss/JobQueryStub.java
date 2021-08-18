/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: JobQueryStub
 * Wanglei 2011-11-15
 */
package dyna.app.service.das.jss;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.bean.data.SystemObject;
import dyna.common.dto.Queue;
import dyna.common.dto.aas.User;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.JobStatus;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Wanglei
 * 
 */
@Component
public class JobQueryStub extends AbstractServiceStub<JSSImpl>
{

	protected Queue getJob(String jobObjectGuid) throws ServiceRequestException
	{
		Queue queue = null;

		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			Map<String, Object> searchConditionMap = new HashMap<String, Object>();
			searchConditionMap.put("GUID", jobObjectGuid);
			searchConditionMap.put(Queue.CURRENTUSERGUID, this.stubService.getUserSignature().getUserGuid());
			queue = sds.queryObject(Queue.class, searchConditionMap);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		return queue;
	}

	protected List<Queue> listJob(Map<String, Object> condition) throws ServiceRequestException
	{
		List<Queue> results = null;
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			condition.put(Queue.CURRENTUSERGUID, this.stubService.getUserSignature().getUserGuid());
			results = sds.query(Queue.class, condition);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

		return results;
	}

	protected List<Queue> listWaitingJob(Map<String, Object> condition) throws ServiceRequestException
	{
		if (condition == null)
		{
			condition = new HashMap<String, Object>();
		}
		condition.put(Queue.CURRENTUSERGUID, this.stubService.getUserSignature().getUserGuid());
		condition.put(Queue.JOB_STATUS, JobStatus.WAITING.getValue());
		return this.listJob(condition);
	}

	protected List<Queue> listRunningJob(Map<String, Object> condition) throws ServiceRequestException
	{
		if (condition == null)
		{
			condition = new HashMap<String, Object>();
		}
		condition.put(Queue.JOB_STATUS, JobStatus.RUNNING.getValue());
		condition.put(Queue.CURRENTUSERGUID, this.stubService.getUserSignature().getUserGuid());
		return this.listJob(condition);
	}

	protected List<Queue> listCancelJob(Map<String, Object> condition) throws ServiceRequestException
	{
		if (condition == null)
		{
			condition = new HashMap<String, Object>();
		}
		condition.put(Queue.JOB_STATUS, JobStatus.CANCEL.getValue());
		condition.put(Queue.CURRENTUSERGUID, this.stubService.getUserSignature().getUserGuid());
		return this.listJob(condition);
	}

	protected List<Queue> listSuccessfulJob(Map<String, Object> condition) throws ServiceRequestException
	{
		if (condition == null)
		{
			condition = new HashMap<String, Object>();
		}
		condition.put(Queue.JOB_STATUS, JobStatus.SUCCESSFUL.getValue());
		condition.put(Queue.CURRENTUSERGUID, this.stubService.getUserSignature().getUserGuid());
		return this.listJob(condition);
	}

	protected List<Queue> listFailedJob(Map<String, Object> condition) throws ServiceRequestException
	{
		if (condition == null)
		{
			condition = new HashMap<String, Object>();
		}
		condition.put(Queue.JOB_STATUS, JobStatus.FAILED.getValue());
		condition.put(Queue.CURRENTUSERGUID, this.stubService.getUserSignature().getUserGuid());
		return this.listJob(condition);
	}

	/**
	 * @param typeId
	 * @param isSearchOwner
	 * @param starindex
	 * @param pageSize
	 * @param searchnext
	 * @param statuslist
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<Queue> listJob(String typeId, boolean isSearchOwner, List<JobStatus> statuslist) throws ServiceRequestException
	{
		List<Queue> results = null;
		Map<String, Object> condition = new HashMap<String, Object>();
		if (!StringUtils.isNullString(typeId))
		{
			condition.put(Queue.TYPE, typeId);
		}
		if (isSearchOwner)
		{
			condition.put(Queue.CREATE_USER_GUID, this.stubService.getUserSignature().getUserGuid());
		}
		if (SetUtils.isNullList(statuslist) == false)
		{
			String statusStr = Queue.JOB_STATUS + " = " + String.valueOf(statuslist.get(0).getValue());
			for (int i = 1; i < statuslist.size(); i++)
			{
				statusStr = statusStr + " OR " + Queue.JOB_STATUS + "=" + String.valueOf(statuslist.get(i).getValue());
			}
			statusStr = "( " + statusStr + " )";
			condition.put("LISTJOBSTATUS", statusStr);
		}
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			results = sds.query(Queue.class, condition, "selectvip");
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		return results;
	}

	/**
	 * @param searchCondition
	 * @param starindex
	 * @param pageSize
	 * @param searchnext
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<Queue> listJobQueue(Map<String, Object> searchCondition, int pagenum, int pageSize) throws ServiceRequestException
	{
		List<Queue> results = null;
		searchCondition.put(Queue.CURRENTUSERGUID, this.stubService.getUserSignature().getUserGuid());
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			int count = 0;
			if (pagenum < 2)
			{
				results = sds.query(Queue.class, searchCondition, "selectForCount");
				count = SetUtils.isNullList(results) ? 0 : ((Number) results.get(0).get("CN")).intValue();
			}
			if (pagenum > 1 || count > 0)
			{
				searchCondition.put("CURRENTPAGE", pagenum);
				searchCondition.put("ROWSPERPAGE", pageSize);
				results = sds.query(Queue.class, searchCondition, "selectFuzzy");
				if (!SetUtils.isNullList(results))
				{
					for (Queue job : results)
					{
						job.put("ROWCOUNT$", count);
						if (!StringUtils.isNullString(job.getCreateUserGuid()))
						{
							User user = this.stubService.getAAS().getUser(job.getCreateUserGuid());
							if (user!=null)
							{
								job.put(SystemObject.CREATE_USER_NAME, user.getUserName());
							}
						}
					}
				}
			}
			else
			{
				return null;
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		return results;
	}
}
