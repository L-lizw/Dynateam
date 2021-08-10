/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServiceDelegatorRmiExecutorImpl
 * Wanglei 2010-11-26
 */
package dyna.app.net.impl.server;

import dyna.app.server.context.ServiceContext;
import dyna.common.log.DynaLogger;
import dyna.net.service.Service;
import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationExecutor;
import org.springframework.util.Assert;

import java.lang.reflect.InvocationTargetException;
import java.rmi.server.RemoteServer;

/**
 * @author Wanglei
 * 
 */
public class ServiceDelegatorRmiExecutorImpl extends ServiceDelegatorServerImpl implements RemoteInvocationExecutor
{
	/**
	 * @param sc
	 * @param serviceID
	 * @param credential
	 */
	public ServiceDelegatorRmiExecutorImpl(ServiceContext sc)
	{
		super(sc, null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.remoting.support.RemoteInvocationExecutor#invoke(org.springframework.remoting.support.
	 * RemoteInvocation, java.lang.Object)
	 */
	@Override
	public Object invoke(RemoteInvocation invocation, Object targetObject) throws NoSuchMethodException,
			IllegalAccessException, InvocationTargetException
	{

		Assert.notNull(invocation, "RemoteInvocation must not be null");
		Assert.notNull(targetObject, "Target object must not be null");

		Authentication authentication = (Authentication) invocation.getAttribute("authentication");
		Assert.notNull(authentication, "Authentication must not be null");

		authentication.setAuthenticated(false);

		SecurityContextHolder.getContext().setAuthentication(authentication);

		@SuppressWarnings("unchecked")
		Class<? extends Service> serviceClass = (Class<? extends Service>) authentication.getPrincipal();
		Assert.notNull(serviceClass, "serviceClass must not be null");

		String credential = (String) authentication.getCredentials();
		Assert.notNull(credential, "credential must not be null");
		long start = System.currentTimeMillis();
		String clientip = null;
		try
		{
			// System.out.println("class:" + serviceClass + "    method:    " + invocation.getMethodName());
			if (this.serviceContext.getServerContext().isDebugMode())
			{
				this.serviceContext.getServerContext().startRMIRemoteInvocation(invocation,RemoteServer.getClientHost());
			}
			clientip=RemoteServer.getClientHost();
			Object invoke = super.invoke(serviceClass, credential, targetObject, null, invocation.getMethodName(),
					invocation.getParameterTypes(), invocation.getArguments());
			//TODO
//			int countOfNotCommitTranscation = DataServer.getTransactionManager().getCountOfNotCommitTranscation();
//			if (countOfNotCommitTranscation > 0)
//			{
//				DynaLogger.info("APP Transation Not Commit");
//				DynaLogger.info("APP Start:" + DateFormat.format(new Date(start), "HH:mm:ss,SSS") + "\tcredential:" + authentication.getCredentials() + "\tRMI clientIP:" + clientip
//						+ "\tmethod:" + invocation.getMethodName());
//				if (invocation.getArguments() != null)
//				{
//					for (Object obj : invocation.getArguments())
//					{
//						DynaLogger.info("Argument:" + obj);
//					}
//				}
//				for (int i=0;i<countOfNotCommitTranscation;i++)
//				{
//					DataServer.getTransactionManager().commitTransaction();
//				}
//			}
			return invoke;
		}
		catch (Throwable e)
		{
			DynaLogger.error(e.getMessage(),e);
			throw new InvocationTargetException(e);
		}
		finally
		{
			//TODO
//			int countOfNotCommitTranscation = DataServer.getTransactionManager().getCountOfNotCommitTranscation();
//			if (countOfNotCommitTranscation > 0)
//			{
//				DynaLogger.info("APP Transation Not Commit");
//				DynaLogger.info("APP Start:" + DateFormat.format(new Date(start), "HH:mm:ss,SSS") + "\tcredential:" + authentication.getCredentials() + "\tRMI clientIP:" + clientip
//						+ "\tmethod:" + invocation.getMethodName());
//				if (invocation.getArguments() != null)
//				{
//					for (Object obj : invocation.getArguments())
//					{
//						DynaLogger.info("Argument:" + obj);
//					}
//				}
//				for (int i=0;i<countOfNotCommitTranscation;i++)
//				{
//					DataServer.getTransactionManager().rollbackTransaction();
//				}
//			}
			if (this.serviceContext.getServerContext().isDebugMode())
		    {
				this.serviceContext.getServerContext().finishRMIRemoteInvocation(invocation);
			}
			SecurityContextHolder.clearContext();

		}
	}

}
