/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Transactional
 * Wanglei 2011-11-29
 */
package dyna.app.core;

/**
 * 可事务化接口
 * 
 * @author Wanglei
 * 
 */
public interface Transactional
{

	/**
	 * 获取序列事务ID
	 * 
	 * @return
	 */
	public String getFixedTransactionId();

	/**
	 * 设置序列事务ID,如事务ID已存在,则不处理
	 * 
	 * @param transactionId
	 */
	public void setFixedTransactionId(String transactionId);

	/**
	 * 产生新的调用数据库时使用的事务ID, 用于开启新事务<br>
	 * <br>
	 * Note. <strong>此方法每次调用都将产生不同的事务ID,因此应该在事务开启和关闭之前取得该ID</strong>
	 * 
	 * @return
	 */
	public String newTransactionId();

}
