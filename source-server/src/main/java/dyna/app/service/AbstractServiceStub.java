/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServiceStub 服务分支
 * Wanglei 2011-3-24
 */
package dyna.app.service;

import dyna.app.server.context.ServiceContext;

/**
 * 服务分支
 * 
 * @author Wanglei
 * 
 */
public abstract class AbstractServiceStub<T extends DataAccessService>
{
	protected ServiceContext	serviceContext	= null;
	protected T					stubService		= null;

	protected AbstractServiceStub(ServiceContext context, T service)
	{
		this.serviceContext = context;
		this.stubService = service;
	}

}
