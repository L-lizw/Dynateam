/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: CalendarStub
 * WangLHB May 7, 2012
 */
package dyna.app.service.brs.ppms;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.ppms.PMCalendar;
import dyna.common.bean.data.ppms.PMCalendarSpecialDate;
import dyna.common.bean.data.ppms.PMCalendarSpecialDateTime;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.DataExceptionEnum;
import dyna.common.util.BooleanUtils;
import dyna.common.util.PMConstans;
import dyna.common.util.SetUtils;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 项目日历相关方法
 * 
 * @author WangLHB
 * 
 */
@Component
public class CalendarStub extends AbstractServiceStub<PPMSImpl>
{

	protected PMCalendar obsoleteCalendar(String pmCalendarGuid) throws ServiceRequestException
	{
		try
		{
			PMCalendar workCalendarBaseInfo = this.getWorkCalendarBaseInfo(pmCalendarGuid);
			if (workCalendarBaseInfo != null)
			{

				if (PMConstans.CALENDAR_STANDARD_ID.equalsIgnoreCase(workCalendarBaseInfo.getId()))
				{
					throw new ServiceRequestException("ID_APP_PM_CALENDAR_STANDARD_NOT_OBSOLETE", "standard calendar is not be obsolete");
				}

				String operatorGuid = this.stubService.getOperatorGuid();
				workCalendarBaseInfo.put(SystemObject.UPDATE_USER_GUID, operatorGuid);

				if (StringUtils.isNullString(workCalendarBaseInfo.getGuid()))
				{
					workCalendarBaseInfo.put(SystemObject.CREATE_USER_GUID, operatorGuid);
				}
				workCalendarBaseInfo.setValid(false);

				SystemDataService sds = DataServer.getSystemDataService();

				sds.save(workCalendarBaseInfo);

			}

			return this.getWorkCalendar(pmCalendarGuid);

		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected PMCalendar saveCalendar(PMCalendar pmCalendar) throws ServiceRequestException
	{

		String operatorGuid = this.stubService.getOperatorGuid();
		pmCalendar.put(SystemObject.UPDATE_USER_GUID, operatorGuid);

		if (StringUtils.isNullString(pmCalendar.getGuid()))
		{
			pmCalendar.put(SystemObject.CREATE_USER_GUID, operatorGuid);
		}

		SystemDataService sds = DataServer.getSystemDataService();
		String calendarGuid = null;
		try
		{
			String result = sds.save(pmCalendar);
			if (StringUtils.isGuid(result))
			{
				calendarGuid = result;
			}
			else
			{
				calendarGuid = pmCalendar.getGuid();
			}

			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(PMCalendarSpecialDate.CALENDAR_GUID, calendarGuid);
			sds.delete(PMCalendarSpecialDate.class, filter, "deleteSpecialDateByCal");

			if (!SetUtils.isNullList(pmCalendar.getSpecialDateList()))
			{
				for (PMCalendarSpecialDate specialDate : pmCalendar.getSpecialDateList())
				{
					specialDate.put(SystemObject.UPDATE_USER_GUID, operatorGuid);

					if (StringUtils.isNullString(specialDate.getGuid()))
					{
						specialDate.put(SystemObject.CREATE_USER_GUID, operatorGuid);
					}

					specialDate.setCalendarGuid(calendarGuid);
					specialDate.setGuid(null);
					String calendarSpecialGuid = sds.save(specialDate);

					filter.clear();
					filter.put(PMCalendarSpecialDateTime.SPECIAL_DATE_GUID, calendarSpecialGuid);
					sds.delete(PMCalendarSpecialDateTime.class, filter, "deleteSpecialDateTimeByDate");

					if (!SetUtils.isNullList(specialDate.getSpecialDateTimeList()))
					{
						for (PMCalendarSpecialDateTime specialDateTime : specialDate.getSpecialDateTimeList())
						{
							specialDateTime.put(SystemObject.UPDATE_USER_GUID, operatorGuid);

							if (StringUtils.isNullString(specialDateTime.getGuid()))
							{
								specialDateTime.put(SystemObject.CREATE_USER_GUID, operatorGuid);
							}

							specialDateTime.setSpecialDateGuid(calendarSpecialGuid);
							specialDateTime.setGuid(null);
							sds.save(specialDateTime);
						}
					}
				}
			}
		}
		catch (DynaDataException e)
		{
			if (e.getDataExceptionEnum() == DataExceptionEnum.DS_UNIQUE)
			{
				e.setDataExceptionEnum(DataExceptionEnum.DS_UNIQUE_ID);
			}

			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (Exception e)
		{
			if (e instanceof ServiceRequestException)
			{
				throw (ServiceRequestException) e;
			}
			else
			{
				throw ServiceRequestException.createByException("ID_APP_SERVER_EXCEPTION", e);
			}
		}
		finally
		{
		}

		return this.getWorkCalendar(calendarGuid);

	}

	protected PMCalendar getStandardWorkCalendar() throws ServiceRequestException
	{
		PMCalendar standardCal = this.stubService.getCalendarStub().getWorkCalendarBaseInfoById(PMConstans.CALENDAR_STANDARD_ID);
		if (standardCal == null)
		{
			standardCal = new PMCalendar();
			standardCal.setId(PMConstans.CALENDAR_STANDARD_ID);
			standardCal.setName(PMConstans.CALENDAR_STANDARD_NAME);
			standardCal = this.saveCalendar(standardCal);
		}
		return standardCal;
	}

	protected PMCalendar getCalendarById(String calendarId) throws ServiceRequestException
	{
		PMCalendar calendar = this.getWorkCalendarBaseInfoById(calendarId);
		if (calendar.isValid())
		{
			return this.getWorkCalendar(calendar.getGuid());
		}
		return null;
	}

	protected PMCalendar getWorkCalendarBaseInfoById(String calendarId) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(PMCalendar.ID, calendarId);
		try
		{
			PMCalendar calendar = sds.queryObject(PMCalendar.class, filter);
			return calendar;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

	protected PMCalendar getWorkCalendar(String calendarGuid) throws ServiceRequestException
	{
		PMCalendar workCalendarBaseInfo = this.getWorkCalendarBaseInfo(calendarGuid);
		if (workCalendarBaseInfo != null)
		{
			workCalendarBaseInfo.setSpecialDateList(this.listSpecialDate(calendarGuid));
		}
		return workCalendarBaseInfo;
	}

	protected PMCalendar getWorkCalendarBaseInfo(String calendarGuid) throws ServiceRequestException
	{

		SystemDataService sds = DataServer.getSystemDataService();

		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(SystemObject.GUID, calendarGuid);
		try
		{
			PMCalendar calendar = sds.queryObject(PMCalendar.class, filter);
			return calendar;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

	protected PMCalendar saveASCalendar(String oriCalendarGuid, PMCalendar pmCalendar) throws ServiceRequestException
	{
		PMCalendar workCalendar = this.getWorkCalendar(oriCalendarGuid);
		if (workCalendar != null)
		{
			workCalendar.setId(pmCalendar.getId());
			workCalendar.setName(pmCalendar.getName());
			workCalendar.setGuid(null);
			if (!SetUtils.isNullList(workCalendar.getSpecialDateList()))
			{
				for (PMCalendarSpecialDate specialDateList : workCalendar.getSpecialDateList())
				{
					specialDateList.setGuid(null);
					if (!SetUtils.isNullList(specialDateList.getSpecialDateTimeList()))
					{
						specialDateList.getSpecialDateTimeList();
						for (PMCalendarSpecialDateTime time : specialDateList.getSpecialDateTimeList())
						{
							time.setGuid(null);
						}
					}
				}
			}

		}

		PMCalendar saveCalendar = this.saveCalendar(workCalendar);
		return saveCalendar;
	}

	/**
	 * 取得公有的日历
	 * 
	 * @param isContainObsolete
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<PMCalendar> listCalendarBaseInfo(Boolean isContainObsolete) throws ServiceRequestException
	{
		List<PMCalendar> calendarList = null;
		Map<String, Object> filter = new HashMap<String, Object>();
		SystemDataService sds = DataServer.getSystemDataService();

		filter.put(PMCalendar.PROJECT_OBJECTGUID, null);

		if (!isContainObsolete)
		{
			filter.put(PMCalendar.IS_VALID, BooleanUtils.getBooleanString10(true));
		}

		calendarList = sds.query(PMCalendar.class, filter);

		return calendarList;
	}

	protected List<PMCalendarSpecialDate> listSpecialDate(String calendarGuid)
	{

		List<PMCalendarSpecialDate> specialDateList = null;

		SystemDataService sds = DataServer.getSystemDataService();

		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(PMCalendarSpecialDate.CALENDAR_GUID, calendarGuid);
		specialDateList = sds.query(PMCalendarSpecialDate.class, filter);

		if (!SetUtils.isNullList(specialDateList))
		{
			for (PMCalendarSpecialDate specialDate : specialDateList)
			{
				specialDate.setSpecialDateTimeList(this.listSpecialDateTime(specialDate.getGuid()));
			}
		}
		return specialDateList;

	}

	protected List<PMCalendarSpecialDateTime> listSpecialDateTime(String sepcialDateGuid)
	{

		List<PMCalendarSpecialDateTime> specialDateTimeList = null;

		SystemDataService sds = DataServer.getSystemDataService();

		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(PMCalendarSpecialDateTime.SPECIAL_DATE_GUID, sepcialDateGuid);
		specialDateTimeList = sds.query(PMCalendarSpecialDateTime.class, filter);

		return specialDateTimeList;

	}
}
