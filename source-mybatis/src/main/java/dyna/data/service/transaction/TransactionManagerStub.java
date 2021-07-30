package dyna.data.service.transaction;

import dyna.data.context.DataServerContext;
import dyna.data.service.DSAbstractServiceStub;
import org.apache.ibatis.session.SqlSession;

import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class TransactionManagerStub extends DSAbstractServiceStub<DBTransactionServiceImpl>
{
	private final Map<String, SqlSession>       sessionConnectionMap  = new HashMap<>();
	private final Map<String, Stack<Savepoint>> sessionSavepointStack = new HashMap<>();

	protected TransactionManagerStub(DataServerContext context, DBTransactionServiceImpl service)
	{
		super(context, service);
	}

	public void commitTransaction(String sessionId)
	{
		try
		{
			SqlSession sqlSession = this.sessionConnectionMap.get(sessionId);
			Stack<Savepoint> spStack = this.sessionSavepointStack.get(sessionId);

			if (spStack != null && !spStack.isEmpty())
			{
				spStack.pop();
			}
			else if (sqlSession != null)
			{
				try
				{
					sqlSession.commit();
					sqlSession.close();
					this.sessionSavepointStack.remove(sessionId);
					this.sessionConnectionMap.remove(sessionId);
				}
				catch (Exception e)
				{
					e.printStackTrace();
					sqlSession = null;
				}
				finally
				{
					if (sqlSession != null)
					{
						sqlSession.close();
					}

				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public void startTransaction(String sessionId)
	{
		try
		{
			SqlSession sqlSession = this.sessionConnectionMap.get(sessionId);

			if (sqlSession == null)
			{
				sqlSession = this.sqlSessionFactory.openSession(false);
				this.sessionConnectionMap.put(sessionId, sqlSession);
			}
			else
			{
				Savepoint savepoint = sqlSession.getConnection().setSavepoint();
				Stack<Savepoint> spStack = this.sessionSavepointStack.get(sessionId);
				if (spStack == null)
				{
					spStack = new Stack<Savepoint>();
					this.sessionSavepointStack.put(sessionId, spStack);
				}
				spStack.push(savepoint);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

	}

	public void rollbackTransaction(String sessionId)
	{
		try
		{
			SqlSession session = this.sessionConnectionMap.get(sessionId);
			Stack<Savepoint> spStack = this.sessionSavepointStack.get(sessionId);

			if (spStack != null && !spStack.isEmpty())
			{
				try
				{
					session.getConnection().rollback(spStack.pop());
					session.rollback(true);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			else if (session != null)
			{
				try
				{
					session.rollback();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				finally
				{
					this.sessionSavepointStack.remove(sessionId);
					this.sessionConnectionMap.remove(sessionId);
					if (session != null)
					{
						session.close();
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public void commitTransactionImmediately(String sessionId)
	{
		try
		{
			SqlSession session = this.sessionConnectionMap.get(sessionId);
			if (session == null)
			{
				return;
			}
			try
			{
				// this.mapperper.commitTransaction();
				session.commit();
				session.close();
				this.sessionConnectionMap.remove(sessionId);
				this.sessionSavepointStack.remove(sessionId);
			}
			catch (Exception e)
			{
				session = null;
				e.printStackTrace();
			}
			finally
			{
				if (session != null)
				{
					session.close();
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void rollbackTransactionImmediately(String sessionId)
	{
		try
		{
			SqlSession session = this.sessionConnectionMap.get(sessionId);
			if (session == null)
			{
				return;
			}
			try
			{
				if (session.getConnection().isClosed() == false)
				{
					session.getConnection().rollback();
				}
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			finally
			{
				this.sessionConnectionMap.remove(sessionId);
				this.sessionSavepointStack.remove(sessionId);
				if (session != null)
				{
					session.close();
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public void resumeTransaction(String sessionId)
	{
		try
		{
			//TODO lizw
//			if (StringUtils.isNullString(sessionId) == false)
//			{
//				SqlSession session = this.sessionConnectionMap.get(sessionId);
//				if (session != null)
//				{
//					this.sqlSessionFactory.setCurrentSession(session);
//				}
//				else
//				{
//					this.sqlSessionFactory.setCurrentSession(null);
//				}
//			}
//			else
//			{
//				this.sqlSessionFactory.setCurrentSession(null);
//			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}
}
