/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DBTransactionService
 * Qiuxq 2012-11-15
 */
package dyna.data.service.transaction;

import dyna.data.service.OrmService;

/**
 * @author Qiuxq
 *
 */
public interface DBTransactionService extends OrmService
{

	/**
	 * 服务器内部使用
	 * 提交事务
	 * 如果当前事务嵌套多个事务
	 * 那么此方法可能只提交某一嵌套事务
	 * 只有所有的嵌套事务都提交了才会真正提交事务
	 * 此方法应和public void startTransaction(String sessionId)配合使用
	 * 
	 * @param sessionId
	 */
	public void commitTransaction(String sessionId);

	/**
	 * 服务器内部使用
	 * 开始一个事务
	 * 如果sessionId所在的connection已经存在
	 * 那么开始一个嵌套事务
	 * 
	 * @param sessionId
	 */
	public void startTransaction(String sessionId);

	/**
	 * 服务器内部使用
	 * 回滚一个数据库事务
	 * 如果当前事务嵌套多个事务
	 * 那么此方法可能只回滚某一嵌套事务
	 * 只有所有的嵌套事务都回滚了才会真正回滚事务
	 * 此方法应和
	 * public void startTransaction(String sessionId)、
	 * public void commitTransaction(String sessionId)
	 * 配合使用
	 * 
	 * @param sessionId
	 */
	public void rollbackTransaction(String sessionId);

	/**
	 * 服务器内部使用
	 * 立即提交事务
	 * 如果当前事务嵌套多个事务将一起提交
	 * 
	 * @param sessionId
	 */
	public void commitTransactionImmediately(String sessionId);

	/**
	 * 服务器内部使用
	 * 立即回滚事务
	 * 如果当前事务嵌套多个事务将一起回滚
	 * 
	 * @param sessionId
	 */
	public void rollbackTransactionImmediately(String sessionId);

	/**
	 * 服务器内部使用
	 * 恢复当前线程的事务
	 * 如果当前事务嵌套多个事务将一起回滚
	 * 
	 * @param sessionId
	 */
	public void resumeTransaction(String sessionId);
}
