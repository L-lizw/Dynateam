/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServiceDispatcher 服务分发器接口定义
 * Wanglei 2010-11-26
 */
package dyna.net.dispatcher;

import java.util.List;

import dyna.common.bean.serv.ServiceBean;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.ServiceStateEnum;

/**
 * 服务分发器接口定义
 * 
 * @author Wanglei
 * 
 */
public interface ServiceDispatcher
{
	/**
	 * 客户端完成与服务同步
	 * 
	 * @throws ServiceRequestException
	 */
	public void synchronizedService() throws ServiceRequestException;

	/**
	 * 测试服务是否正常提供服务
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public ServiceStateEnum getServiceState() throws ServiceRequestException;

	/**
	 * 查询服务列表
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public List<ServiceBean> listService() throws ServiceRequestException;

	/**
	 * 获取内部认证
	 * 
	 * @param moduleName
	 * 
	 * @return
	 * @throws ServiceRequestException
	 */
	public String getConnectionCredential(String moduleName) throws ServiceRequestException;

	/**
	 * 断开连接
	 * 
	 * @param moduleName
	 * @throws ServiceRequestException
	 */
	public void disconnect(String moduleName) throws ServiceRequestException;

	/**
	 * 测试连接是否正常
	 * 
	 * @throws Exception
	 */
	public boolean isConnected();
}
