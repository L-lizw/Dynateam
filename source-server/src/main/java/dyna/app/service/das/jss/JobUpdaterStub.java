/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: JobUpdaterStub
 * Wanglei 2011-11-16
 */
package dyna.app.service.das.jss;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.dto.Queue;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.JobStatus;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Wanglei
 * 
 */
@Component
public class JobUpdaterStub extends AbstractServiceStub<JSSImpl>
{

	public Queue saveJob(Queue fo, boolean isERPNotify) throws ServiceRequestException
	{
		if (fo.isChanged(Queue.JOB_STATUS))
		{
			Object object = fo.get(Queue.JOB_STATUS);
			int sv = JobStatus.WAITING.getValue();
			if (object instanceof BigDecimal)
			{
				sv = ((BigDecimal) object).intValue();
			}
			else if (object instanceof Integer)
			{
				sv = (Integer) object;
			}
			JobStatus jobStatus = JobStatus.getStatus(sv);
			this.handleStatus(fo, jobStatus, isERPNotify);
		}
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			String guid = fo.getGuid();
			fo.setCreateUserGuid(fo.getCreateUserGuid());
			if (!StringUtils.isNullString(fo.getResult()))
			{
				fo.setResult(this.cutString(fo.getResult(), 4000));
			}
			sds.save(fo);
			fo = this.stubService.getJob(guid);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		return fo;
	}

	public Queue setJobStatus(Queue fo, JobStatus jobStatus) throws ServiceRequestException
	{
		this.handleStatus(fo, jobStatus, false);

		if (!fo.isChanged(Queue.JOB_STATUS))
		{
			throw new ServiceRequestException("status no changed");
		}
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			String guid = fo.getGuid();
			fo.setCreateUserGuid(fo.getCreateUserGuid());
			if (!StringUtils.isNullString(fo.getResult()))
			{
				fo.setResult(this.cutString(fo.getResult(), 4000));
			}
			sds.save(fo);
			fo = this.stubService.getJob(guid);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		return fo;
	}

	private String cutString(String result, int maxLength)
	{
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m = p.matcher(result);
		if (m.find())
		{
			maxLength = maxLength / 3;
		}
		if (maxLength < result.length())
		{
			return result.substring(0, maxLength);
		}
		return result;
	}

	private void handleStatus(Queue fo, JobStatus jobStatus, boolean isERPNotify)
	{
		Object object = fo.getOriginalValue(Queue.JOB_STATUS);
		int sv = JobStatus.WAITING.getValue();
		if (object instanceof BigDecimal)
		{
			sv = ((BigDecimal) object).intValue();
		}
		else if (object instanceof Integer)
		{
			sv = (Integer) object;
		}
		JobStatus sourceStatus = JobStatus.getStatus(sv);
		fo.put(Queue.JOB_STATUS, this.convertStatus(sourceStatus, jobStatus, isERPNotify).getValue());
	}

	private JobStatus convertStatus(JobStatus sourceStatus, JobStatus destStatus, boolean isERPNotify)
	{
		JobStatus retStatus = sourceStatus;
		switch (sourceStatus)
		{
		case WAITING:
			retStatus = (destStatus == JobStatus.RUNNING || destStatus == JobStatus.CANCEL) ? destStatus : sourceStatus;
			break;

		case RUNNING:
			retStatus = (destStatus == JobStatus.SUCCESSFUL || destStatus == JobStatus.FAILED || destStatus == JobStatus.ERPEXECUTING) ? destStatus : sourceStatus;
			break;

		case ERPEXECUTING:
			retStatus = (destStatus == JobStatus.SUCCESSFUL || destStatus == JobStatus.FAILED) ? destStatus : sourceStatus;
			break;

		case CANCEL:
		case SUCCESSFUL:
		case FAILED:
			if (isERPNotify)
			{
				retStatus = (destStatus == JobStatus.WAITING || destStatus == JobStatus.SUCCESSFUL) ? destStatus : sourceStatus;
			}
			else
			{
				retStatus = destStatus == JobStatus.WAITING ? destStatus : sourceStatus;
			}
			break;

		default:
			break;
		}
		return retStatus;
	}
}
