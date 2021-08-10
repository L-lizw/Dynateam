/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: CheckOutStub
 * Wanglei 2011-3-30
 */
package dyna.app.service.brs.bom;

import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.boas.BOASImpl;
import dyna.common.bean.data.foundation.BOMView;
import dyna.common.exception.ServiceRequestException;

/**
 * @author Wanglei
 *
 */
public class BOMViewCheckOutStub extends AbstractServiceStub<BOMSImpl>
{

	/**
	 * @param context
	 * @param service
	 */
	public BOMViewCheckOutStub(ServiceContext context, BOMSImpl service)
	{
		super(context, service);
	}

	public BOMView checkOut(BOMView bomView, String checkOutUserGuid, boolean isCheckAuth) throws ServiceRequestException
	{
		BOMView retBOMView = (BOMView) ((BOASImpl) this.stubService.getBOAS()).getCheckOutStub().checkOutNoCascade(bomView, checkOutUserGuid, isCheckAuth);
		return retBOMView;
	}

}
