/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ConnectionManager
 * Wanglei 2011-1-17
 */
package dyna.net.connection;

import java.util.List;

import dyna.net.security.signature.ModuleSignature;

/**
 * 连接管理器
 * 
 * @author Wanglei
 * 
 */
public interface ConnectionManager
{

	/**
	 * 查询已连接的客户凭证
	 * 
	 * @return
	 */
	public List<String> listConnectionCredential();

	/**
	 * 获取指定客户凭证的连接的签名
	 * 
	 * @param clientCredential
	 *            客户凭证
	 * @return
	 */
	public ModuleSignature getConnectionSignature(String clientCredential);

	/**
	 * 新建连接, 模块名称为moduleName
	 * 
	 * @param moduleName
	 *            模块名称
	 * @return 连接凭证
	 */
	public String newConnection(String moduleName);

	/**
	 * 删除指定客户凭证的连接
	 * 
	 * @param clientCredential
	 */
	public void removeConnection(String clientCredential);

	/**
	 * 清除所有连接的凭证
	 */
	public void clearConnection();
}
