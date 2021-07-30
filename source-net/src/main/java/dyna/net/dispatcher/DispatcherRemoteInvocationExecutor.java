/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DispatcherRemoteInvocationExecutor
 * Wanglei 2011-4-11
 */
package dyna.net.dispatcher;

import java.lang.reflect.InvocationTargetException;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationExecutor;
import org.springframework.util.Assert;

/**
 * @author Wanglei
 *
 */
public class DispatcherRemoteInvocationExecutor implements RemoteInvocationExecutor
{

	/* (non-Javadoc)
	 * @see org.springframework.remoting.support.RemoteInvocationExecutor#invoke(org.springframework.remoting.support.RemoteInvocation, java.lang.Object)
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

		try
		{
			return invocation.invoke(targetObject);
		}
		catch (Throwable e)
		{
			throw new InvocationTargetException(e);
		}
		finally
		{
			SecurityContextHolder.clearContext();
		}
	}

}
