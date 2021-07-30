/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServiceProvider
 * Wanglei 2010-3-31
 */
package dyna.net.spi;

import dyna.common.exception.ServiceNotFoundException;
import dyna.net.service.Service;

/**
 * 服务提供者接口
 * 供用户请求系统提供的(远程或本地)服务时调用
 * 
 * @author Wanglei
 * 
 */
public interface ServiceProvider
{

	/**
	 * 清除所有凭证为credential的系统服务实例
	 * 
	 * @param credential
	 */
	public void clearServiceInstance(String credential);

	/**
	 * 销毁服务提供者
	 */
	public void destroy();

	/**
	 * 请求指定的系统服务(远程或本地), 使用系统连接凭证<br>
	 * 
	 * @param <T>
	 *            返回服务泛型
	 * @param serviceClass
	 *            服务接口
	 * @return
	 * @throws ServiceNotFoundException
	 */
	public <T extends Service> T getServiceInstance(Class<T> serviceClass) throws ServiceNotFoundException;

	/**
	 * 请求指定的系统服务(远程或本地), 使用凭证credential
	 * 
	 * @param <T>
	 *            返回服务泛型
	 * @param serviceClass
	 *            服务接口
	 * @param credential
	 *            凭证
	 * @return
	 * @throws ServiceNotFoundException
	 */
	public <T extends Service> T getServiceInstance(Class<T> serviceClass, String credential)
	throws ServiceNotFoundException;

	/**
	 * 释放指定的系统服务(远程或本地), 依据系统默认凭证
	 * 
	 * @param serviceClass
	 *            服务接口
	 * @throws ServiceNotFoundException
	 */
	public void releaseServiceInstance(Class<? extends Service> serviceClass) throws ServiceNotFoundException;

	/**
	 * 释放指定的系统服务(远程或本地), 依据凭证credential
	 * 
	 * @param serviceClass
	 *            服务接口
	 * @param credential
	 *            凭证
	 * @throws ServiceNotFoundException
	 */
	public void releaseServiceInstance(Class<? extends Service> serviceClass, String credential)
	throws ServiceNotFoundException;

	/**
	 * 设置会话管理凭证<br>
	 * 此方法会被系统自动调用, 程序运行时调用将无效
	 * 
	 * @param sessionMgrCredential
	 */
	public void setSessionMgrCredential(String sessionMgrCredential);

	/**
	 * 手动同步服务端状态
	 */
	public void syncServiceState();
}
