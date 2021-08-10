/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ATSImpl
 * Wanglei 2011-11-18
 */
package dyna.app.service.das;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dyna.app.service.DataAccessService;
import dyna.app.service.helper.FilterBuilder;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.SearchCondition;
import dyna.common.dto.SysTrack;
import dyna.common.dto.aas.User;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;
import dyna.net.service.brs.AAS;
import dyna.net.service.das.ATS;

/**
 * Action Track Service implementation 用户操作跟踪服务(系统日志)实现
 * 
 * @author Wanglei
 * 
 */
public class ATSImpl extends DataAccessService implements ATS
{

	protected synchronized AAS getAAS() throws ServiceRequestException
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.das.ATS#listTrack(dyna.common.SearchCondition)
	 */
	@Override
	public List<SysTrack> listTrack(SearchCondition searchCondition) throws ServiceRequestException
	{
		Map<String, Object> filter = FilterBuilder.buildFilterBySearchCondition(searchCondition);
		if (filter.containsKey(SysTrack.CREATE_TIME))
		{
			Date ct = (Date) filter.get(SysTrack.CREATE_TIME);
			if (ct != null)
			{
				filter.put(SysTrack.SEARCH_FROM, ct);
				filter.put(SysTrack.SEARCH_TO, ct);
			}
		}
		Date to = (Date) filter.get(SysTrack.SEARCH_TO);
		if (to != null)
		{
			Calendar currentDate = Calendar.getInstance();
			currentDate.setTime(to);
			currentDate.set(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH), 0, 0, 1);

			filter.put(SysTrack.SEARCH_TO, new Date(currentDate.getTimeInMillis() + 24 * 3600000));
		}
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			return sds.query(SysTrack.class, filter);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this, e);
		}
	}

	private void deleteTrack(Map<String, Object> filter, String sqlId) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		Date to = (Date) filter.get(SysTrack.SEARCH_TO);
		if (to != null)
		{
			Calendar currentDate = Calendar.getInstance();
			currentDate.setTime(to);
			currentDate.set(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH), 0, 0, 1);

			filter.put(SysTrack.SEARCH_TO, new Date(currentDate.getTimeInMillis() + 24 * 3600000));
		}

		try
		{
			sds.delete(SysTrack.class, filter, sqlId);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this, e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.das.ATS#deleteTrack(java.lang.String[])
	 */
	@Override
	public void deleteTrack(String... trackGuids) throws ServiceRequestException
	{
		if (trackGuids == null || trackGuids.length == 0)
		{
			return;
		}

		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(SysTrack.GUID, Arrays.asList(trackGuids));

		this.deleteTrack(filter, "deleteByGuids");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.das.ATS#deleteTrackByDate(java.lang.String, java.lang.String, java.util.Date,
	 * java.util.Date)
	 */
	@Override
	public void deleteTrackByDate(String sessiondId, String userId, Date fromDate, Date toDate) throws ServiceRequestException
	{
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(SysTrack.SEARCH_FROM, fromDate);
		filter.put(SysTrack.SEARCH_TO, toDate);
		Date to = (Date) filter.get(SysTrack.SEARCH_TO);
		if (to != null)
		{
			Calendar currentDate = Calendar.getInstance();
			currentDate.setTime(to);
			currentDate.set(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH), 0, 0, 1);

			filter.put(SysTrack.SEARCH_TO, new Date(currentDate.getTimeInMillis() + 24 * 3600000));
		}

		if (!StringUtils.isNullString(sessiondId) && !StringUtils.isNullString(userId))
		{
			filter.put(SysTrack.SID, sessiondId);
			User user = this.getAAS().getUserById(userId);
			if (user == null)
			{
				throw new ServiceRequestException("not found user: " + userId);
			}
			filter.put(SysTrack.CREATE_USER_GUID, user.getGuid());

			this.deleteTrack(filter, "deleteByAll");
		}
		else if (!StringUtils.isNullString(sessiondId))
		{
			filter.put(SysTrack.SID, sessiondId);
			this.deleteTrack(filter, "deleteBySID");
		}
		else if (!StringUtils.isNullString(userId))
		{
			User user = this.getAAS().getUserById(userId);
			if (user == null)
			{
				throw new ServiceRequestException("not found user: " + userId);
			}
			filter.put(SysTrack.CREATE_USER_GUID, user.getGuid());
			this.deleteTrack(filter, "deleteByUserGuid");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.service.das.ATS#clearTrack()
	 */
	@Override
	public void clearTrack() throws ServiceRequestException
	{
		this.deleteTrack(new HashMap<String, Object>(), "deleteALL");
	}

}
