/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 流程变迁处理分支
 * Wanglei 2010-11-8
 */
package dyna.app.service.brs.wfi.routrestriction;


import dyna.app.server.context.ServiceContext;
import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.wfi.WFIImpl;
import dyna.common.dto.wf.TransRestriction;
import dyna.common.exception.ServiceRequestException;

/**
 * 流程变迁处理分支
 * 
 * @author Wanglei
 * 
 */
public class RoutRestrictionStub extends AbstractServiceStub<WFIImpl>
{

	private RoutRestrictionDBStub dbStub = null;

	/**
	 * @param context
	 * @param service
	 */
	public RoutRestrictionStub(ServiceContext context, WFIImpl service)
	{
		super(context, service);
		this.dbStub = new RoutRestrictionDBStub(context, service);
	}

	public TransRestriction getRoutRestriction(String actRtGuid) throws ServiceRequestException
	{
		return this.dbStub.getRoutRestriction(actRtGuid);
	}
}
