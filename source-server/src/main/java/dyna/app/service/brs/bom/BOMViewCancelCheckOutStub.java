/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: BOMViewCancelCheckOutStub
 * caogc 2011-7-30
 */
package dyna.app.service.brs.bom;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.helper.Constants;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.bean.data.foundation.BOMView;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.data.DataServer;
import dyna.data.service.ins.InstanceService;

/**
 * @author caogc
 * 
 */
public class BOMViewCancelCheckOutStub extends AbstractServiceStub<BOMSImpl>
{

	/**
	 * @param context
	 * @param service
	 */
	public BOMViewCancelCheckOutStub(ServiceContext context, BOMSImpl service)
	{
		super(context, service);
	}

	protected BOMView cancelCheckOut(BOMView bomView) throws ServiceRequestException
	{
		InstanceService ds = DataServer.getInstanceService();

		BOMView retBOMView = null;

		String sessionId = this.stubService.getSignature().getCredential();

		try
		{
			// 执行取消检出操作
			ds.cancelCheckout(bomView, Constants.isSupervisor(true, this.stubService), sessionId, this.stubService.getFixedTransactionId());

			// 获取取消检出后的对象
			retBOMView = this.stubService.getBOMView(bomView.getObjectGuid());

		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

		return retBOMView;
	}
}
