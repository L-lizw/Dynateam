package dyna.app.service.brs.wfi.favoritecomment;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.wfi.WFIImpl;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.bean.data.SystemObject;
import dyna.common.dto.wf.ProcTrackComm;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 常用意见数据操作支持
 * 
 * @author lizw
 *
 */

@Component
public class FavoriteCommentDBStub extends AbstractServiceStub<WFIImpl>
{

	protected ProcTrackComm getTrackComm(String guid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(SystemObject.GUID, guid);
			ProcTrackComm procTrackComm = sds.queryObject(ProcTrackComm.class, filter, "select");
			return procTrackComm;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(stubService, e);
		}
	}

	protected String saveTrackComm(ProcTrackComm procComm) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			String guid = sds.save(procComm);
			return guid;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(stubService, e);
		}
	}

	protected void deleteTrackComm(String guid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(SystemObject.GUID, guid);
			sds.delete(ProcTrackComm.class, filter, "delete");
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(stubService, e);
		}
	}

	protected List<ProcTrackComm> listProcTrackComm() throws ServiceRequestException
	{
		List<ProcTrackComm> listProcComm = null;
		try
		{
			SystemDataService sds = DataServer.getSystemDataService();

			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(SystemObject.CREATE_USER_GUID, this.stubService.getOperatorGuid());

			listProcComm = sds.query(ProcTrackComm.class, filter, "select");
			if (listProcComm != null)
			{
				for (ProcTrackComm info : listProcComm)
				{
					info.setCreateUserName(this.stubService.getAAS().getUser(info.getCreateUserGuid()).getName());
					info.setUpdateUserName(this.stubService.getAAS().getUser(info.getUpdateUserGuid()).getName());
				}
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(stubService, e);
		}
		return listProcComm;
	}

}
