/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ChangPhaseAppImpl
 * Wanglei 2010-11-11
 */
package dyna.app.service.brs.wfi.activity.app.impl;

import dyna.app.service.brs.wfi.WFIImpl;
import dyna.app.service.brs.wfi.activity.app.ProcApp;
import dyna.common.dto.wf.ActivityRuntime;
import dyna.common.exception.ServiceRequestException;

/**
 * @author Wanglei
 * 
 */
public class ChangStatusAppImpl implements ProcApp
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.service.brs.wfi.app.ProcApp#invoke(dyna.common.bean.data.system.ActivityRuntime)
	 */
	@Override
	public Object execute(WFIImpl wfeImpl, ActivityRuntime activity) throws ServiceRequestException
	{
		wfeImpl.getAttachStub().changeAttachStatus(activity);
		return null;
	}

}
