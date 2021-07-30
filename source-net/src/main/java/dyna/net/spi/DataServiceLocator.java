/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DataServiceLocator
 * Wanglei 2010-12-7
 */
package dyna.net.spi;

import dyna.common.exception.ServiceNotFoundException;
import dyna.net.service.Service;

/**
 * @author Wanglei
 *
 */
public interface DataServiceLocator extends ServiceLocator
{

	/**
	 * 初始化方法
	 */
	public void init();

	/**
	 * 搜索定位指定数据服务
	 * 
	 * @param <T>
	 *            返回服务泛型
	 * @param serviceClass
	 *            服务接口
	 * @return
	 * @throws ServiceNotFoundException
	 */
	public <T extends Service> T lookup(Class<T> serviceClass) throws ServiceNotFoundException;
}
