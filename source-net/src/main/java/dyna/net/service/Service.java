/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Service
 * Wanglei 2010-3-22
 */
package dyna.net.service;

import dyna.common.conf.ServiceDefinition;

/**
 * 服务接口, 不提供具体实现, 仅作服务声明标签
 * 
 * @author Lizw
 * 
 */
public interface Service
{
	/**
	 * 设置服务定义
	 * @param serviceDefinition
	 */
	void setServiceDefinition(ServiceDefinition serviceDefinition);

	/**
	 * 初始化服务
	 */
	void init();
}
