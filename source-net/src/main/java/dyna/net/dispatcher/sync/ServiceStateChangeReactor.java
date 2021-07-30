/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServiceStateChangeReactor
 * Wanglei 2011-1-14
 */
package dyna.net.dispatcher.sync;

import dyna.common.systemenum.ServiceStateEnum;

/**
 * 服务状态改变反应器
 * @author Wanglei
 *
 */
public interface ServiceStateChangeReactor
{

	/**
	 * 状态改变后执行的动作
	 * 
	 * @param serviceState
	 *            当前服务的状态
	 * @return
	 */
	public ServiceStateEnum stateChanged(ServiceStateEnum serviceState);
}
