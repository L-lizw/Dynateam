/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServerContextListener
 * Wanglei 2011-9-26
 */
package dyna.app.server.context;

/**
 * 应用服务器上下文环境监听器
 * 
 * @author Wanglei
 * 
 */
public interface ServerContextListener
{
	/**
	 * 服务器启动初始化完成之后, 执行此方法
	 * 
	 * @param serverContext
	 *            服务器上下文
	 * @param serviceContext
	 *            服务上下文
	 */
	public void contextInitialized(ApplicationServerContext serverContext, ServiceContext serviceContext);

}
