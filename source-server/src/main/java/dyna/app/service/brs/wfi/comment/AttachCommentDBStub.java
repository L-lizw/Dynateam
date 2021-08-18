package dyna.app.service.brs.wfi.comment;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.wfi.WFIImpl;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.bean.data.SystemObject;
import dyna.common.dto.wf.ProcTrackAttach;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AttachCommentDBStub extends AbstractServiceStub<WFIImpl>
{

	protected List<ProcTrackAttach> listProcAttachComment(String procRtGuid, String actRtGuid, String attachGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(ProcTrackAttach.PROCRT_GUID, procRtGuid);
			filter.put(ProcTrackAttach.ATTACH_GUID, attachGuid);

			List<ProcTrackAttach> opinionList = sds.query(ProcTrackAttach.class, filter, "select");

			return opinionList;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	protected ProcTrackAttach getProcAttachComment(String attachOpinion) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(ProcTrackAttach.GUID, attachOpinion);
			ProcTrackAttach opinion = sds.queryObject(ProcTrackAttach.class, filter, "select");
			return opinion;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	/**
	 * 添加附件意见
	 * 
	 * @param attachOpinion
	 * @throws ServiceRequestException
	 */
	protected void addAttachOpinion(ProcTrackAttach attachOpinion) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			sds.save(attachOpinion);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	/**
	 * 修改附件意见
	 * 
	 * @param attachOpinion
	 * @throws ServiceRequestException
	 */
	protected void updateAttachOpinion(ProcTrackAttach attachOpinion) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(ProcTrackAttach.COMMENTS, attachOpinion.getComments());
			filter.put(ProcTrackAttach.QUOTE_COMMENTS, attachOpinion.getQuoteComments());
			filter.put(SystemObject.GUID, attachOpinion.getGuid());

			sds.update(ProcTrackAttach.class, filter, "update");
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

	/**
	 * 删除附件意见
	 * 
	 * @param attachOpinion
	 * @throws ServiceRequestException
	 */
	protected void deleteAttachOpinion(String attachOpinion) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(ProcTrackAttach.GUID, attachOpinion);
			sds.delete(ProcTrackAttach.class, filter, "deleteGuid");
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

}
