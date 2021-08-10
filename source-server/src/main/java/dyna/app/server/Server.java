/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Server接口,
 * 定义应用服务器的基本行为: 开始运行, 停止运行, 关闭销毁
 * Wanglei 2010-3-22
 */
package dyna.app.server;

import dyna.app.server.context.ServerContextListener;

import java.util.List;

/**
 * Server接口<br>
 * 定义应用服务器的基本行为: 开始运行, 停止运行, 关闭销毁等
 * 
 * @author Wanglei
 * 
 */
public interface Server
{

	/**
	 * 关闭销毁
	 */
	public void shutdown() throws Exception;

	/**
	 * 开始运行
	 */
	public void start() throws Exception;

	/**
	 * 停止运行
	 */
	public void stop() throws Exception;

	/**
	 * 检测服务器是否在正常运行
	 * 
	 * @return <code>true</code> or <code>false</code>
	 */
	public boolean isAlive();

	/**
	 * 添加服务器上下文监听器
	 */
	public void addServerContextListener(ServerContextListener listener);

	/**
	 * 删除服务器上下文监听器
	 *
	 * @param listener
	 */
	public void removeServerContextListener(ServerContextListener listener);

	/**
	 * 查询服务器上下文监听器
	 */
	public List<ServerContextListener> getServerContextListeners();

}
