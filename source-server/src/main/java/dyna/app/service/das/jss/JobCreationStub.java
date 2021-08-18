/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: JobCreationStub
 * Wanglei 2011-11-15
 */
package dyna.app.service.das.jss;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.conf.JobDefinition;
import dyna.common.dto.Queue;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.JobStatus;
import dyna.common.util.DateFormat;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.common.DSCommonService;
import dyna.data.service.sdm.SystemDataService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Wanglei
 * 
 */
@Component
public class JobCreationStub extends AbstractServiceStub<JSSImpl>
{

	protected Queue createJob(Queue fo) throws ServiceRequestException
	{
		JobDefinition jd = this.stubService.getJobDefinition(fo.getExecutorClass());
		fo.setType(jd.getJobID());
		if (StringUtils.isNullString(fo.getIsSinglethRead()))
		{
			if (jd.isSingleThread())
			{
				fo.setIsSinglethRead("Y");
			}
			else
			{
				fo.setIsSinglethRead("N");
			}
		}
		fo.setJobStatus(JobStatus.WAITING);
		if (fo.getPriority() == null)
		{
			fo.setPriority(Integer.valueOf(jd.getPriority()));
		}
		fo.setCreateUserGuid(this.stubService.getUserSignature().getUserGuid());
		fo.setUpdateUserGuid(this.stubService.getUserSignature().getUserGuid());
		fo.setServerID(this.serviceContext.getServerContext().getServerConfig().getServerId());
		DSCommonService ds = DataServer.getDSCommonService();
		try
		{
			fo = this.stubService.getJob(ds.saveQueue(fo));
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		return fo;

	}

	protected void deleteJobs(String... jobGuids) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			for (String guid : jobGuids)
			{
				sds.delete(Queue.class, guid);
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected void deleteTimeoutJobs(String jobType, int timeOut) throws ServiceRequestException
	{
		Queue condition = new Queue();
		condition.put(Queue.TYPE, jobType);

		List<JobStatus> statuslist = this.listFinishedJobStatus();
		if (SetUtils.isNullList(statuslist) == false)
		{
			StringBuffer statusBuffer = new StringBuffer();
			for (int i = 0; i < statuslist.size(); i++)
			{
				if (i > 0)
				{
					statusBuffer.append(",");
				}
				statusBuffer.append(statuslist.get(i).getValue());
			}
			condition.put("LISTJOBSTATUS", statusBuffer.toString());
		}
		Date effTime = new Date(System.currentTimeMillis() - timeOut * 24000 * 3600);
		condition.put("CURRENTDATE", DateFormat.parse(DateFormat.format(effTime, DateFormat.PTN_YMD), DateFormat.PTN_YMD));

		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			sds.delete(Queue.class, condition, "deleteTimeOutJobByType");
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	private List<JobStatus> listFinishedJobStatus()
	{
		List<JobStatus> statuslist = new ArrayList<JobStatus>();
		statuslist.add(JobStatus.SUCCESSFUL);
		statuslist.add(JobStatus.FAILED);
		statuslist.add(JobStatus.CANCEL);

		return statuslist;
	}
}
