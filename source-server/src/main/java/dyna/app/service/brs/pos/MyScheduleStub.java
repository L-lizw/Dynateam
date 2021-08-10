/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 与我的日程相关的操作分支
 * Caogc 2010-8-18
 */
package dyna.app.service.brs.pos;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.dto.MySchedule;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.DateFormat;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;

/**
 * 与MyScheduleStub相关的操作分支
 * 
 * @author Caogc
 * 
 */
public class MyScheduleStub extends AbstractServiceStub<POSImpl>
{

	/**
	 * @param context
	 * @param service
	 */
	public MyScheduleStub(ServiceContext context, POSImpl service)
	{
		super(context, service);
	}

	protected void deleteMySchedule(String guid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			sds.delete(MySchedule.class, guid);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(stubService, e);
		}
	}

	protected MySchedule getMySchedule(String guid) throws ServiceRequestException
	{
		MySchedule mySchedule = null;

		SystemDataService sds = DataServer.getSystemDataService();

		Map<String, Object> filter = new HashMap<String, Object>();

		filter.put(MySchedule.GUID, guid);

		try
		{
			mySchedule = sds.queryObject(MySchedule.class, filter);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(stubService, e);
		}

		return mySchedule;
	}

	protected List<MySchedule> listMySchedule(Date scheduleDate) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		Map<String, Object> filter = new HashMap<String, Object>();

		String operatorGuid = this.stubService.getOperatorGuid();

		List<MySchedule> myScheduleList = null;

		filter.put(MySchedule.OWNER_USER_GUID, operatorGuid);

		if (scheduleDate != null)
		{
			filter.put(MySchedule.SCHEDULE_DATE, DateFormat.formatYMD(scheduleDate));
		}
		try
		{
			myScheduleList = sds.query(MySchedule.class, filter);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(stubService, e);
		}

		return myScheduleList;
	}

	protected MySchedule saveMySchedule(MySchedule mySchedule) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			MySchedule retMySchedule = null;

			boolean isCreate = false;

			String myScheduleGuid = mySchedule.getGuid();

			String operatorGuid = this.stubService.getOperatorGuid();

			mySchedule.put(MySchedule.UPDATE_USER, operatorGuid);

			if (!StringUtils.isGuid(myScheduleGuid))
			{
				isCreate = true;

				mySchedule.put(MySchedule.CREATE_USER, operatorGuid);

				mySchedule.setOwnerUserGuid(operatorGuid);
			}

			String ret = sds.save(mySchedule);

			if (isCreate)
			{
				myScheduleGuid = ret;
			}

			retMySchedule = this.getMySchedule(myScheduleGuid);

			return retMySchedule;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestException.createByDynaDataException(e, mySchedule.getName());
		}
	}
}
