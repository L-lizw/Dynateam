/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: BOMViewTransferCheckOutStub
 * caogc 2011-3-30
 */
package dyna.app.service.brs.bom;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.helper.Constants;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.bean.data.foundation.BOMView;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.data.DataServer;
import dyna.data.service.ins.InstanceService;
import org.springframework.stereotype.Component;

/**
 * @author caogc
 *
 */
@Component
public class BOMViewTransferCheckOutStub extends AbstractServiceStub<BOMSImpl>
{

	public BOMView transferCheckout(BOMView bomView, String toUserGuid, boolean isCheckAuth) throws ServiceRequestException
	{
		InstanceService ds = DataServer.getInstanceService();

		BOMView retBOMView = null;

		String sessionId = this.stubService.getSignature().getCredential();

		try
		{
			// 执行迁移检出操作
			ds.transferCheckout(bomView, toUserGuid, Constants.isSupervisor(isCheckAuth, this.stubService), Constants.IS_OWN_ONLY, sessionId,
					this.stubService.getFixedTransactionId());

			// 获取迁移检出后的对象
			retBOMView = this.stubService.getBOMView(bomView.getObjectGuid());

		}
		catch (DynaDataException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw ServiceRequestExceptionWrap.createByDynaDataException(stubService, e);
		}
		catch (ServiceRequestException e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			throw e;
		}
		finally
		{
		}

		return retBOMView;
	}
}
