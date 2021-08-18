package dyna.app.service.brs.wfi.routrestriction;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.wfi.WFIImpl;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.dto.wf.TransRestriction;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.data.DataServer;
import dyna.data.service.sdm.SystemDataService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 流程变迁数据层交互支持
 * @author lizw
 *
 */
@Component
public class RoutRestrictionDBStub extends AbstractServiceStub<WFIImpl>
{
	
	protected TransRestriction getRoutRestriction(String actRtGuid) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			Map<String, Object> filter = new HashMap<String, Object>();
			filter.put(TransRestriction.ACTRT_GUID, actRtGuid);

			TransRestriction tr = sds.queryObject(TransRestriction.class, filter);

			return tr;
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(stubService, e);
		}
	}

}
