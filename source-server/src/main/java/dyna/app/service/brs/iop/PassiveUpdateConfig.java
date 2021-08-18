package dyna.app.service.brs.iop;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ModelInterfaceEnum;
import dyna.data.DataServer;
import dyna.data.service.config.ConfigManagerService;
import org.springframework.stereotype.Component;

/**
 * 配置参数的被动修改
 * 发布时
 * 
 * @author wwx
 * 
 */
@Component
public class PassiveUpdateConfig extends AbstractServiceStub<IOPImpl>
{

	public void release(String masterGuid, String foundationId) throws ServiceRequestException
	{
		try
		{
//			DataServer.getTransactionManager().startTransaction(this.stubService.getFixedTransactionId());

			ConfigManagerService ds = DataServer.getConfigManagerService();
			ds.releaseConfigTable(masterGuid, foundationId, ModelInterfaceEnum.IOption, this.stubService.getUserSignature().getUserGuid());

//			DataServer.getTransactionManager().commitTransaction();
		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
		catch (Exception e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
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
