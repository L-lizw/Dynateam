/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServiceStub 服务分支
 * Wanglei 2011-3-24
 */
package dyna.app.service;

import dyna.app.server.context.ServiceContext;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 服务分支
 * 
 * @author Wanglei
 * 
 */
public abstract class AbstractServiceStub<T extends DataAccessService>
{
	@Autowired
	protected ServiceContext	serviceContext	= null;
	@Autowired
	protected T					stubService		= null;
}
