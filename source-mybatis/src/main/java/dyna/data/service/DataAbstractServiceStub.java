/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServiceStub 服务分支
 * Wanglei 2011-3-24
 */
package dyna.data.service;

import dyna.data.context.DataServerContext;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 服务分支
 * 
 * @author Wanglei
 * 
 */
public abstract class DataAbstractServiceStub<T extends DataRuleService>
{
	@Autowired
	protected DataServerContext	serviceContext	= null;
	@Autowired
	protected T					stubService		= null;
}
