/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DataServerTransactionManager
 * Wanglei 2012-11-15
 */
package dyna.data.service.transaction;

/**
 * @author qiuxq
 * 
 */
public interface DataServerTransactionManager 
{
	public void commitTransaction();

	public void rollbackTransaction();;

	public void startTransaction(String sessionId);

	public void commitTransactionImmediately(String sessionId);;

	public void rollbackTransactionImmediately(String sessionId);;

	public String getTransactionId();;

	/**
	 * 取得没有提交的事务总数
	 * @return
	 */
	public int getCountOfNotCommitTranscation();
}
