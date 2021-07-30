/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServiceStub 服务分支
 * Wanglei 2011-3-24
 */
package dyna.data.service;

import dyna.data.context.DataServerContext;

/**
 * 服务分支
 * 
 * @author Wanglei
 * 
 */
public abstract class DataAbstractServiceStub<T extends DataRuleService>
{
	protected DataServerContext	serviceContext	= null;
	protected T					stubService		= null;

	protected DataAbstractServiceStub(DataServerContext context, T service)
	{
		this.serviceContext = context;
		this.stubService = service;
	}
}
