/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SvContext
 * Wanglei 2011-7-12
 */
package dyna.common.context;

import dyna.common.systemenum.ServiceStateEnum;

/**
 * 服务上下文
 * 
 * @author Wanglei
 * 
 */
public interface SvContext extends Context
{
	public void init() throws Exception;

	public String getContextDescription();

	public ServiceStateEnum getServiceState();

	public ServiceStateEnum setServiceState(ServiceStateEnum serviceState);

}
