/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServiceContext
 * Wanglei 2010-3-30
 */
package dyna.app.server.context;

import dyna.common.conf.ConfigurableServiceImpl;
import dyna.common.context.SvContext;
import dyna.common.exception.ServiceNotFoundException;
import dyna.net.service.Service;

/**
 * 服务的上下文接口
 * 
 * @author Wanglei
 * 
 */
public interface ServiceContext extends SvContext
{
	public ApplicationServerContext getServerContext();

	public ConfigurableServiceImpl getServiceConfig();

	/**
	 * 申请服务
	 * 
	 * @param <T>
	 *            服务泛型
	 * @param serviceClass
	 *            服务接口
	 * @return
	 * @throws ServiceNotFoundException
	 */
	public <T extends Service> T allocatService(Class<T> serviceClass) throws ServiceNotFoundException;

	/**
	 * 释放申请的服务
	 * 
	 * @param service
	 */
	public void releaseService(Service service);
}
