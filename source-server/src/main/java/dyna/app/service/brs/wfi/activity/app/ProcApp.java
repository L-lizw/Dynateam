/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ProcApp
 * Wanglei 2010-11-11
 */
package dyna.app.service.brs.wfi.activity.app;

import dyna.app.service.brs.wfi.WFIImpl;
import dyna.common.dto.wf.ActivityRuntime;
import dyna.common.exception.ServiceRequestException;

/**
 * @author Wanglei
 * 
 */
public interface ProcApp
{

	public Object execute(WFIImpl wfeImpl, ActivityRuntime activity) throws ServiceRequestException;
}
