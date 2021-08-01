/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DataServiceRemoteInvocationExecutorImpl
 * Wanglei 2010-12-10
 */
package dyna.data.dispatch;

import dyna.common.util.StringUtils;
import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Wanglei
 * 
 */
@Repository
public class DataServiceRemoteInvocationExecutorImpl implements RemoteInvocationExecutor
{

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


		// do invoke
		String sessionId = (String) authentication.getCredentials();
		if (StringUtils.isNullString(sessionId))
		{
			sessionId = "";
		}
//		DataServer.getTransactionService().resumeTransaction(sessionId);
		// do invoke
		try
		{
			return invocation.invoke(targetObject);
		}
		finally
		{
			SecurityContextHolder.clearContext();
		}
	}

}
