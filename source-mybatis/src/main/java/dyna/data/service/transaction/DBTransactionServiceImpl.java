/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DBTransactionServiceImpl
 * Qiuxq 2012-11-15
 */
package dyna.data.service.transaction;

import dyna.common.conf.ServiceDefinition;
import dyna.data.context.DataServerContext;
import dyna.data.service.DataRuleService;

/**
 * @author Qiuxq
 * 
 */
public class DBTransactionServiceImpl extends DataRuleService implements DBTransactionService
{
	private TransactionManagerStub transactionManagerStubStub = null;

	public DBTransactionServiceImpl(DataServerContext context, ServiceDefinition sd)
	{
		super(context, sd);
	}

	public TransactionManagerStub getTransactionManagerStub()
	{
		if (this.transactionManagerStubStub == null)
		{
			this.transactionManagerStubStub = new TransactionManagerStub(this.serviceContext, this);
		}
		return this.transactionManagerStubStub;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.data.transaction.DBTransactionService#commitTransaction()
	 */
	@Override
	public void commitTransaction(String sessionId)
	{
		this.getTransactionManagerStub().commitTransaction(sessionId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * dyna.data.transaction.DBTransactionService#startTransaction(java.lang
	 * .String)
	 */
	@Override
	public void startTransaction(String sessionId)
	{
		this.getTransactionManagerStub().startTransaction(sessionId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.data.transaction.DBTransactionService#rollbackTransaction()
	 */
	@Override
	public void rollbackTransaction(String sessionId)
	{
		this.getTransactionManagerStub().rollbackTransaction(sessionId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * dyna.data.transaction.DBTransactionService#commitTransactionImmediately
	 * (java.lang.String)
	 */
	@Override
	public void commitTransactionImmediately(String sessionId)
	{
		this.getTransactionManagerStub().commitTransactionImmediately(sessionId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * dyna.data.transaction.DBTransactionService#rollbackTransactionImmediately
	 * (java.lang.String)
	 */
	@Override
	public void rollbackTransactionImmediately(String sessionId)
	{
		this.getTransactionManagerStub().rollbackTransactionImmediately(sessionId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * dyna.data.transaction.DBTransactionService#resumeTransaction(java.lang
	 * .String)
	 */
	@Override
	public void resumeTransaction(String sessionId)
	{
		this.getTransactionManagerStub().resumeTransaction(sessionId);
	}
}
