/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ScheduledTaskUdSessionImpl
 * Wanglei 2011-4-22
 */
package dyna.app.core.sch;

import dyna.common.log.DynaLogger;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;

/**
 * 刷新 会话最后更新时间
 * 
 * @author Wanglei
 * 
 */
public class ScheduledTaskUdSessionImpl extends AbstractScheduledTask
{

	// private Session session = null;
	private String	sessionId		= null;
	private boolean	isUserAccess	= true;

	public ScheduledTaskUdSessionImpl(String sessionId, boolean isUserAccess)
	{
		this.sessionId = sessionId;
		this.isUserAccess = isUserAccess;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		try
		{
			String transactionId = StringUtils.generateRandomUID(32);
//			DataServer.getTransactionManager().startTransaction(transactionId);

			if (isUserAccess)
			{
				DataServer.getDSCommonService().updateSession(this.sessionId);
			}
			else
			{
				DataServer.getDSCommonService().updateSessionActiveTime(this.sessionId);
			}
//			DataServer.getTransactionManager().commitTransaction();
		}
		catch (Exception e)
		{
//			DataServer.getTransactionManager().rollbackTransaction();
			DynaLogger.error(e.getMessage());
		}
	}

}
