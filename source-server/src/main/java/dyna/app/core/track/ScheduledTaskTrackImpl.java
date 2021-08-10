/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ScheduledTaskTrackImpl
 * Wanglei 2011-11-11
 */
package dyna.app.core.track;

import java.lang.reflect.Method;

import dyna.app.core.sch.AbstractScheduledTask;
import dyna.common.log.DynaLogger;
import dyna.net.security.signature.Signature;

/**
 * @author Wanglei
 *
 */
public class ScheduledTaskTrackImpl extends AbstractScheduledTask
{
	private TrackerBuilder	builder		= null;
	private Signature		signature	= null;
	private Method			method		= null;
	private Object[]		parameters	= null;
	private Object			result		= null;

	public ScheduledTaskTrackImpl(TrackerBuilder builder, Signature signature, Method method, Object[] parameters, Object result)
	{
		this.builder = builder;
		this.signature = signature;
		this.method = method;
		this.parameters = parameters;
		this.result = result;
		this.setPriority(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.TimerTask#run()
	 */
	@Override
	public void run()
	{
		Tracker tracker = this.builder.make(this.signature, this.method, this.result, this.parameters);
		if (tracker == null)
		{
			return;
		}
		try
		{
			tracker.persist();
			// 将日志写入远程服务器
			tracker.getSyslogString();
		}
		catch (Exception e)
		{
			DynaLogger.error("[" + this.method.getName() + "] tracker persistence error: " + e);
		}
		finally
		{
			tracker = null;
		}
	}

}
