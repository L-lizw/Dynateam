/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 与checkIn/checkOut相关的操作分支
 * Caogc 2010-8-18
 */
package dyna.app.service.brs.bom;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.boas.BOASImpl;
import dyna.common.bean.data.foundation.BOMView;
import dyna.common.exception.ServiceRequestException;

/**
 * 与checkIn/checkOut相关的操作分支
 * 
 * @author Caogc
 * 
 */
public class BOMViewCheckInStub extends AbstractServiceStub<BOMSImpl>
{

	/**
	 * @param context
	 * @param service
	 */
	public BOMViewCheckInStub(ServiceContext context, BOMSImpl service)
	{
		super(context, service);
	}

	public BOMView checkIn(BOMView bomView, boolean isCheckAuth) throws ServiceRequestException
	{
		BOMView retBOMView = (BOMView) ((BOASImpl) this.stubService.getBOAS()).getCheckInStub().checkInNoCascade(bomView, isCheckAuth);
		// retBOMView = this.stubService.saveBOMView(retBOMView);
		return retBOMView;
	}
}
