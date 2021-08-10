package dyna.app.service.brs.wfi.track;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.wfi.WFIImpl;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.dto.wf.ProcTrack;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;

public class TrackDBStub extends AbstractServiceStub<WFIImpl>
{

	protected TrackDBStub(ServiceContext context, WFIImpl service)
	{
		super(context, service);
		// TODO Auto-generated constructor stub
	}

	protected List<ProcTrack> listComment(String procRtGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(ProcTrack.PROCRT_GUID, procRtGuid);

			List<ProcTrack> trackList = sds.query(ProcTrack.class, filter);

			return trackList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	/**
	 * 查询活动节点的意见
	 * 
	 * @param actrtGuid
	 * @param startNumber
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<ProcTrack> listActivityComment(String actrtGuid, String startNumber) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(ProcTrack.ACTRT_GUID, actrtGuid);
			if(!StringUtils.isNullString(startNumber))
			{
				filter.put(ProcTrack.START_NUMBER, startNumber);
			}

			List<ProcTrack> trackList = sds.query(ProcTrack.class, filter);

			return trackList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected List<ProcTrack> listActivityComment(Map<String, Object> filter) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			List<ProcTrack> trackList = sds.query(ProcTrack.class, filter);

			return trackList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

}
