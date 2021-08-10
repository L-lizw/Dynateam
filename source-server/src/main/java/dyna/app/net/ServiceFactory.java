/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServiceFactory
 * Wanglei 2011-4-18
 */
package dyna.app.net;

/**
 * @author Wanglei
 *
 */
public interface ServiceFactory
{

	/**
	 * 创建服务实例
	 * 
	 * @return 服务实例对象
	 */
	public Object createService() throws Exception;
}
