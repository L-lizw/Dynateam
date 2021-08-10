package dyna.app.service.brs.wfi.attach;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.wfi.WFIImpl;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.dto.wf.ProcAttach;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;

/**
 * 流程附件操作支持
 * 
 * @author lizw
 *
 */

public class AttachDBStub extends AbstractServiceStub<WFIImpl>
{

	protected AttachDBStub(ServiceContext context, WFIImpl service)
	{
		super(context, service);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 查詢流程附件
	 * 
	 * @param filter
	 * @param sqlId
	 * @return
	 * @throws ServiceRequestException
	 */
	protected List<ProcAttach> listProcAttach(Map<String, Object> filter, String sqlId) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			return sds.query(ProcAttach.class, filter, sqlId);
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	/**
	 * 添加流程附件，已存在的不合法则更新其信息，已存在且合法的进行记录
	 * 
	 * @param attach
	 * @param iterationId
	 * @throws ServiceRequestException
	 */
	protected ProcAttach addAttachment(ProcAttach attach, Integer iterationId) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			if (StringUtils.isNullString(attach.getGuid()))
			{
				attach.put("ITERATIONID", String.valueOf(iterationId));
			}
			else if (!attach.isInvalid())
			{
				return attach;
			}
			else
			{
				attach.setInvalid(false);
				attach.setCalculated(false);
				attach.setMain(true);
				attach.setUpdateUserGuid(this.stubService.getOperatorGuid());
			}
			sds.save(attach);
			Map<String,Object> param = new HashMap<String,Object>();
			param.put(ProcAttach.PROCRT_GUID, attach.getProcessRuntimeGuid());
			param.put(ProcAttach.INSTANCE_GUID, attach.getInstanceGuid());
			return sds.queryObject(ProcAttach.class, param, "selectDirectly");
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

	/**
	 * 根据条件移除流程附件
	 * 
	 * @param filter
	 * @param procAttachGuidList
	 * @param isCheckAcl
	 * @throws ServiceRequestException
	 */
	public void removeAttachment(Map<String, Object> filter, List<String> procAttachGuidList, boolean isCheckAcl) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			for (String attachInstanceGuid : procAttachGuidList)
			{
				filter.put(ProcAttach.INSTANCE_GUID, attachInstanceGuid);
				sds.delete(ProcAttach.class, filter, "delete");
			}
		}
		catch (DynaDataException e)
		{
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
	}

}
