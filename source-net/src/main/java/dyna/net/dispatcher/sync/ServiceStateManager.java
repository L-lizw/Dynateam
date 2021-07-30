/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServiceStateManager
 * Wanglei 2011-1-17
 */
package dyna.net.dispatcher.sync;

import dyna.common.systemenum.ServiceStateEnum;

/**
 * 服务状态管理器
 * 
 * @author Wanglei
 * 
 */
public interface ServiceStateManager
{

	/**
	 * 设置所有连接的服务状态为state
	 * 
	 * @param state
	 */
	public void setServiceState(ServiceStateEnum state);

	/**
	 * 设置指定连接的服务状态为state
	 * 
	 * @param clientCredeantial
	 *            连接凭证
	 * @param state
	 */
	public void setServiceState(String clientCredeantial, ServiceStateEnum state);

	/**
	 * 获取指定连接的服务状态
	 * 
	 * @param clientCredential
	 *            连接凭证
	 * @return
	 */
	public ServiceStateEnum getServiceState(String clientCredential);
}
