/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DataServiceRemoteInvocationExecutorImpl
 * Wanglei 2010-12-10
 */
package dyna.data.dispatch;

import java.lang.reflect.InvocationTargetException;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationExecutor;
import org.springframework.util.Assert;

import dyna.common.util.StringUtils;
import dyna.data.DataServer;

/**
 * @author Wanglei
 * 
 */
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
		// if (!this.context.isActive())
		// {
		// throw new IllegalAccessException("data service is not available, please try later.");
		// }

		Assert.notNull(invocation, "RemoteInvocation must not be null");
		Assert.notNull(targetObject, "Target object must not be null");

		Authentication authentication = (Authentication) invocation.getAttribute("authentication");
		Assert.notNull(authentication, "Authentication must not be null");

		authentication.setAuthenticated(false);

		SecurityContextHolder.getContext().setAuthentication(authentication);

		// // check client type
		// try
		// {
		// String connCredential = (String) authentication.getDetails();
		// Signature signature = this.context.getCredentialManager().authenticate(connCredential);
		// if (!(signature instanceof ModuleSignature))
		// {
		// throw new AuthorizeException("invalid signature");
		// }
		//
		// String moduleId = ((ModuleSignature) signature).getModuleId();
		// if (!Signature.MODULE_APP_SERVER.equals(moduleId) && !Signature.MODULE_MODELER.equals(moduleId))
		// {
		// throw new AuthorizeException("invalid module, available value: " + Signature.MODULE_APP_SERVER + ", "
		// + Signature.MODULE_MODELER);
		// }
		//
		// String methodName = invocation.getMethodName();
		// if ((Signature.MODULE_MODELER.equals(moduleId) && //
		// !"loginModeler".equals(methodName) && //
		// !"logoutModeler".equals(methodName) && //
		// !"deploy".equals(methodName) && //
		// !"getDownloader".equals(methodName) && //
		// !"getUploader".equals(methodName))
		// || //
		// (Signature.MODULE_APP_SERVER.equals(moduleId) && //
		// "loginModeler".equals(methodName) && //
		// "deploy".equals(methodName)))
		// {
		// throw new AuthorizeException("Illegal Access: " + methodName + " by " + moduleId);
		// }
		// }
		// catch (AuthorizeException e)
		// {
		// throw new IllegalAccessException(e.getMessage());
		// }

		// do invoke
		String sessionId = (String) authentication.getCredentials();
		if (StringUtils.isNullString(sessionId))
		{
			sessionId = "";
		}
		DataServer.getTransactionService().resumeTransaction(sessionId);
		// do invoke
		// long start = System.currentTimeMillis();
		try
		{
			return invocation.invoke(targetObject);
		}
		finally
		{
			SecurityContextHolder.clearContext();
			// DynaLogger.println(targetObject.getClass().getSimpleName() + "." + invocation.getMethodName() + " cost  "
			// + (System.currentTimeMillis() - start));
		}
	}

}
