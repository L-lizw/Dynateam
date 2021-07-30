/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServiceLocator
 * Wanglei 2010-4-6
 */
package dyna.net.spi;

import java.util.List;

import dyna.common.bean.serv.ServiceBean;
import dyna.common.exception.ServiceNotFoundException;
import dyna.net.dispatcher.sync.ServiceStateSync;
import dyna.net.service.Service;

/**
 * 服务定位器, 用于搜索服务(本地或远程)
 * 
 * @author Wanglei
 * 
 */
public interface ServiceLocator
{

	/**
	 * 关闭服务定位
	 */
	public void shutdown();

	/**
	 * 清除服务
	 * 
	 * @param credential
	 */
	public void clear(String credential);

	/**
	 * 清除所有服务
	 */
	public void clearAll();

	/**
	 * 获取服务定义bean
	 * 
	 * @param serviceClass
	 *            服务接口类
	 * @return 服务定义bean
	 */
	public ServiceBean getServiceBean(Class<? extends Service> serviceClass);

	/**
	 * 获取服务状态同步机
	 * 
	 * @param exchanger
	 */
	public ServiceStateSync getServiceStateSync();

	/**
	 * 获取当前客户端所保存的凭证列表
	 * 
	 * @return
	 */
	public List<String> listCredential();

	/**
	 * 获取服务列表
	 * 
	 * @return
	 */
	public List<ServiceBean> listServiceBean();

	/**
	 * 搜索指定服务
	 * 
	 * @param <T>
	 *            返回参数泛型
	 * @param serviceClass
	 *            服务接口
	 * @param credential
	 *            凭证
	 * @return
	 * @throws ServiceNotFoundException
	 */
	public <T extends Service> T lookup(Class<T> serviceClass, String credential) throws ServiceNotFoundException;

	/**
	 * 释放指定服务
	 * 
	 * @param serviceClass
	 *            服务接口
	 * @param credential
	 *            凭证
	 */
	public void release(Class<? extends Service> serviceClass, String credential);

	/**
	 * 定位器是否可用
	 * 
	 * @return
	 */
	public boolean isAvailable();
}
