/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DataServiceDelegator
 * Wanglei 2010-12-20
 */
package dyna.data.dispatch;

import dyna.common.exception.ServiceNotAvailableException;
import dyna.common.invocationlog.InvocationLogger;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.ServiceStateEnum;
import dyna.data.context.DataServerContext;
import dyna.data.service.model.ModelService;
import dyna.dbcommon.exception.DynaDataGenericException;
import dyna.net.syncfile.SyncFileService;
import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.apache.log4j.Level;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 服务授权代理
 * @author Lizw
 * 
 */
public class DataServiceDelegator implements InvocationHandler
{

	private Object				delegator	= null;
	private DataServerContext	context		= null;

	protected DataServiceDelegator(DataServerContext context, Object delegator)
	{
		this.context = context;
		this.delegator = delegator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		ServiceStateEnum serviceState = null;
		String connCredential = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null)
		{
			connCredential = (String) authentication.getDetails();
			serviceState = this.context.getServiceStateManager().getServiceState(connCredential);
		}
		else
		{
			serviceState = this.context.getServiceState();
			if (serviceState == ServiceStateEnum.SYNCHRONIZE)
			{
				serviceState = this.context.setServiceState(ServiceStateEnum.NORMAL);
			}
		}

		if ((serviceState == ServiceStateEnum.SYNCHRONIZE && !(this.delegator instanceof ModelService)) && !(this.delegator instanceof SyncFileService))
		{
			throw new DynaDataGenericException(new ServiceNotAvailableException(serviceState, "data service is not available, please try later"));
		}
		String methodName = method.getName();

		long start = System.currentTimeMillis();

		try
		{
			if (DynaLogger.isDebugEnabled())
			{
				DynaLogger.debug("invoke " + this.delegator.getClass() + "." + methodName);
			}

			Object ret = method.invoke(this.delegator, args);

			if (DynaLogger.isDebugEnabled())
			{
				DynaLogger.debug(this.delegator.getClass() + "." + methodName + " done");
			}

			return ret;
		}
		catch (InvocationTargetException e)
		{
			if (DynaLogger.isDebugEnabled())
			{
				DynaLogger.debug("error occurs during invoking " + this.delegator.getClass() + "." + methodName, e);
			}
			throw e.getTargetException();
		}
		finally
		{

			if (InvocationLogger.getInvocationLogger().getParent().getLevel() != null && Level.WARN.isGreaterOrEqual(InvocationLogger.getInvocationLogger().getParent().getLevel()))
			{
				long t = System.currentTimeMillis() - start;
				if (t > 10)
				{
					InvocationLogger.getInvocationLogger().warn("DATA--->credential:" + connCredential + "\t" + method.getName() + "\t" + "cost" + "\t" + t);
				}
			}

		}
	}

}
