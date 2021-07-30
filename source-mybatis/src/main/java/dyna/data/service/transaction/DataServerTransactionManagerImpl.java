/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DataServerRMITransactionManager
 * Wanglei 2012-11-15
 */
package dyna.data.service.transaction;

import java.util.Stack;

import dyna.common.exception.DynaDataException;
import dyna.data.DataServer;

/**
 * @author Wanglei
 * 
 */
public class DataServerTransactionManagerImpl implements DataServerTransactionManager
{
	private ThreadLocal<Stack<String>> localSession = null;

	public DataServerTransactionManagerImpl() throws DynaDataException
	{

		localSession = new ThreadLocal<Stack<String>>();
	}

	@Override
	public void commitTransaction()
	{
		Stack<String> sessionStack = this.localSession.get();
		if (sessionStack != null && sessionStack.isEmpty() == false)
		{
			DataServer.getTransactionService().commitTransaction(sessionStack.peek());
			sessionStack.pop();
			if (sessionStack.isEmpty() == false)
			{
				String preSessionId = sessionStack.peek();
				DataServer.getTransactionService().resumeTransaction(preSessionId);
			}
		}
		else
		{
			System.out.println("No tran");
		}

		if (this.getCountOfNotCommitTranscation() == 0)
		{
			DataServer.getSystemDataService().notifyRefreshCacheListeners();
		}
	}

	public void rollbackTransaction()
	{
		Stack<String> sessionStack = this.localSession.get();
		if (sessionStack != null && sessionStack.isEmpty() == false)
		{
			DataServer.getTransactionService().rollbackTransaction(sessionStack.pop());
			if (sessionStack.isEmpty() == false)
			{
				String preSessionId = sessionStack.peek();
				DataServer.getTransactionService().resumeTransaction(preSessionId);
			}
		}

		if (this.getCountOfNotCommitTranscation() == 0)
		{
			DataServer.getSystemDataService().clearRefreshCacheListeners();
		}
	}

	public void startTransaction(String sessionId)
	{
		DataServer.getTransactionService().startTransaction(sessionId);
		Stack<String> sessionStack = this.localSession.get();
		if (sessionStack == null)
		{
			sessionStack = new Stack<String>();
			sessionStack.push(sessionId);
			this.localSession.set(sessionStack);
		}
		else
		{
			sessionStack.push(sessionId);
		}
	}

	public void commitTransactionImmediately(String sessionId)
	{
		Stack<String> sessionStack = this.localSession.get();
		if (sessionStack != null && sessionStack.contains(sessionId))
		{
			DataServer.getTransactionService().commitTransactionImmediately(sessionId);
			for (int i = sessionStack.size() - 1; i > -1; i--)
			{
				String id = sessionStack.elementAt(i);
				if (id.equals(sessionId))
				{
					sessionStack.removeElementAt(i);
				}
			}
			if (sessionStack.isEmpty() == false)
			{
				String preSessionId = sessionStack.peek();
				DataServer.getTransactionService().resumeTransaction(preSessionId);
			}
		}
		if (this.getCountOfNotCommitTranscation() == 0)
		{
			DataServer.getSystemDataService().notifyRefreshCacheListeners();
		}
	}

	public void rollbackTransactionImmediately(String sessionId)
	{
		Stack<String> sessionStack = this.localSession.get();
		if (sessionStack != null && sessionStack.contains(sessionId))
		{
			DataServer.getTransactionService().rollbackTransactionImmediately(sessionId);
			for (int i = sessionStack.size() - 1; i > -1; i--)
			{
				String id = sessionStack.elementAt(i);
				if (id.equals(sessionId))
				{
					sessionStack.removeElementAt(i);
				}
			}
			if (sessionStack.isEmpty() == false)
			{
				String preSessionId = sessionStack.peek();
				DataServer.getTransactionService().resumeTransaction(preSessionId);
			}
		}
		if (this.getCountOfNotCommitTranscation() == 0)
		{
			DataServer.getSystemDataService().clearRefreshCacheListeners();
		}
	}

	public String getTransactionId()
	{
		Stack<String> sessionStack = this.localSession.get();
		if (sessionStack != null)
		{
			if (sessionStack.isEmpty() == false)
			{
				String preSessionId = sessionStack.peek();
				return preSessionId;
			}
		}
		return null;
	}

	public int getCountOfNotCommitTranscation()
	{
		Stack<String> sessionStack = this.localSession.get();
		if (sessionStack == null || sessionStack.isEmpty())
		{
			return 0;
		}

		return sessionStack.size();
	}
}
