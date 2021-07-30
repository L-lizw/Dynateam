/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Connection
 * Wanglei 2010-4-1
 */
package dyna.net.connection;

/**
 * 网络连接接口, 提供应用服务器之间, 以及应用服务器与web服务器之间的网络通信
 * 
 * @author Wanglei
 * 
 */
public interface Connection
{

	/**
	 * 打开连接
	 */
	public void open() throws Exception;

	/**
	 * 销毁连接会话
	 * 
	 * @param sessionId
	 */
	public void destroySession(String sessionId);

	/**
	 * 关闭连接
	 */
	public void close();

	/**
	 * 测试连接是否打开
	 */
	public boolean isOpened();

	/**
	 * 测试连接是否正常
	 * 
	 * @return
	 */
	public boolean testConnection();

}
