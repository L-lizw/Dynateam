/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 与checkIn/checkOut相关的操作分支
 * Caogc 2010-8-18
 */
package dyna.app.service.brs.bom;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.boas.BOASImpl;
import dyna.common.bean.data.foundation.BOMView;
import dyna.common.exception.ServiceRequestException;
import org.springframework.stereotype.Component;

/**
 * 与checkIn/checkOut相关的操作分支
 * 
 * @author Caogc
 * 
 */
@Component
public class BOMViewCheckInStub extends AbstractServiceStub<BOMSImpl>
{

	public BOMView checkIn(BOMView bomView, boolean isCheckAuth) throws ServiceRequestException
	{
		BOMView retBOMView = (BOMView) ((BOASImpl) this.stubService.getBOAS()).getCheckInStub().checkInNoCascade(bomView, isCheckAuth);
		// retBOMView = this.stubService.saveBOMView(retBOMView);
		return retBOMView;
	}
}
