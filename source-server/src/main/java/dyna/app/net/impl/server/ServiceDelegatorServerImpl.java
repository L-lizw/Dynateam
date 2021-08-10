/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServiceDelegatorLocalImpl
 * Wanglei 2010-4-14
 */
package dyna.app.net.impl.server;

import dyna.app.core.Authorizable;
import dyna.app.core.SessionActiveTime;
import dyna.app.core.Signable;
import dyna.app.core.Transactional;
import dyna.app.core.sch.ScheduledTaskUdSessionImpl;
import dyna.app.core.sch.Scheduler;
import dyna.app.core.track.ScheduledTaskTrackImpl;
import dyna.app.core.track.TrackerBuilder;
import dyna.app.core.track.TrackerManager;
import dyna.app.server.context.ServiceContext;
import dyna.common.exception.*;
import dyna.common.invocationlog.InvocationLogger;
import dyna.common.systemenum.DataExceptionEnum;
import dyna.common.systemenum.ServiceStateEnum;
import dyna.common.util.StringUtils;
import dyna.data.common.exception.DynaDataGenericException;
import dyna.net.impl.DataServiceProviderFactory;
import dyna.net.impl.ServiceProviderFactory;
import dyna.net.security.signature.ModuleSignature;
import dyna.net.security.signature.Signature;
import dyna.net.service.Service;
import dyna.net.spi.ServiceDelegator;
import dyna.net.syncfile.SyncFileService;
import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 服务代表, 在服务端的实现.
 * 用于执行服务调用
 *
 * @author Wanglei
 */
public class ServiceDelegatorServerImpl implements ServiceDelegator
{

	protected ServiceContext			serviceContext		= null;
	protected Class<? extends Service>	serviceClass		= null;
	protected String					credential			= null;
	protected Scheduler					sessionUpdater		= null;
	protected Scheduler					trackerScheduler	= null;
	private TrackerManager				trackerManager		= null;

	public ServiceDelegatorServerImpl(ServiceContext sc, Class<? extends Service> serviceClass, String credential)
	{
		this.serviceContext = sc;
		this.serviceClass = serviceClass;
		this.credential = credential;
		this.trackerManager = this.serviceContext.getServerContext().getTrackerManager();
		this.sessionUpdater = this.serviceContext.getServerContext().getSchedulerManager().getMultiThreadQueuedTaskScheduler();
		this.trackerScheduler = this.serviceContext.getServerContext().getSchedulerManager().getMultiThreadQueuedTaskScheduler();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.net.spi.ServiceDelegator#invoke(java.lang.String, java.lang.String, java.lang.Object,
	 * java.lang.reflect.Method, java.lang.Object[])
	 */
	@Override
	public Object invoke(Class<? extends Service> serviceClass, String credential, Object delegate, Method method, String methodName, Class<?>[] parameterTypes, Object[] args)
			throws Throwable
	{

		Object retObject = null;
		Object delegator = null;
		Signature signature = null;
		ServiceStateEnum serviceState = null;
		Authentication authentication = null;
		long time = 0;
		try
		{
			time = System.currentTimeMillis();
			InvocationLogger.getInvocationLogger().debug(credential + "  key:" + time + "  delegator start method:" + methodName);
			// check remote invocation
			authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication != null)
			{
				String clientCredential = (String) authentication.getDetails();
				serviceState = this.serviceContext.getServerContext().getServiceStateManager().getServiceState(clientCredential);
			}
			else
			{
				serviceState = this.serviceContext.getServerContext().getServiceState();
				if (serviceState == ServiceStateEnum.SYNCHRONIZE)
				{
					serviceState = this.serviceContext.getServerContext().setServiceState(ServiceStateEnum.NORMAL);
				}
			}

			if (serviceState != ServiceStateEnum.NORMAL && !SyncFileService.class.equals(serviceClass))
			{
				throw new ServiceRequestException("ID_APP_SERVICE_NOT_AVAILABLE", "Service not available, please try later",
						new ServiceNotAvailableException(serviceState, "Service not available, please try later"));
			}

			if (method == null)
			{
				method = serviceClass.getMethod(methodName, parameterTypes);
			}

			signature = this.serviceContext.getServerContext().getCredentialManager().authenticate(credential);

			this.modelSignatureCheck(methodName, signature);

			delegator = this.serviceContext.allocatService(serviceClass);
			// TODO 更新session updatetime
			if (delegator == null)
			{
				throw new ServiceRequestException("Service not available.");
			}

			Method method_ = delegator.getClass().getMethod(methodName, parameterTypes);
			if (method_.isAnnotationPresent(SessionActiveTime.class))
			{
				this.updateSession(signature, method_.getAnnotation(SessionActiveTime.class).isUpdate());
			}
			else
			{
				this.updateSession(signature, true);
			}

			// TODO 签名
			if (delegator instanceof Signable)
			{
				((Signable) delegator).setSignature(signature);
			}

			// TODO 事务初始化
			if (delegator instanceof Transactional)
			{
				((Transactional) delegator).getFixedTransactionId();
			}

			// TODO 认证授权
			if (delegator instanceof Authorizable)
			{
				((Authorizable) delegator).authorize(method, args);
			}

			// TODO 执行service调用
			retObject = method.invoke(delegator, args);
			InvocationLogger.getInvocationLogger().debug(credential + "  key:" + time + "  delegator end method:" + methodName);
			InvocationLogger.getInvocationLogger()
					.info(credential + "  key:" + time + "  delegator method:" + methodName + "\t cost:" + String.valueOf(System.currentTimeMillis() - time));
			return retObject;
		}
		catch (InvocationTargetException e)
		{
			Throwable targetException = e.getTargetException();
			Throwable cause = targetException.getCause();
			if (cause instanceof DynaDataException)
			{
				cause = cause.getCause();
			}

			if (cause instanceof ServiceNotAvailableException)
			{
				serviceState = ((ServiceNotAvailableException) cause).getServiceState();
				if (serviceState == ServiceStateEnum.SYNCHRONIZE)
				{
					DataServiceProviderFactory.getServiceProvider().syncServiceState();
				}
				else if (serviceState == ServiceStateEnum.WAITING)
				{
					this.serviceContext.getServerContext().setServiceState(ServiceStateEnum.INVALID);
				}
				else
				{
					this.serviceContext.getServerContext().setServiceState(serviceState);
				}

				Throwable throwable = new ServiceRequestException("ID_APP_SERVICE_NOT_AVAILABLE", "Service not available, please try later",
						new ServiceNotAvailableException(serviceState, "Service not available, please try later"));
				retObject = throwable;
				throw throwable;
			}

			if (targetException instanceof ServiceRequestException)
			{
				if (DataExceptionEnum.DS_NOT_FOUND_SESSION.getMsrId().equals(((ServiceRequestException) targetException).getMsrId()))
				{
					SessionInvalidException se = new SessionInvalidException("ID_APP_SESSION_EXPIRED", "Session Expired", null);
					Throwable throwable = this.convertSessionInvalidException(se);
					retObject = throwable;
					throw throwable;
				}
			}
			retObject = targetException;
			throw targetException;
		}
		catch (SessionInvalidException e)
		{
			Throwable throwable = this.convertSessionInvalidException(e);
			retObject = throwable;
			throw throwable;
		}
		catch (AuthorizeException e)
		{
			if (AuthorizeException.ID_PER_DENIED.equals(e.getMsrId()) && !StringUtils.isNullString(credential))
			{
				SessionInvalidException se = new SessionInvalidException("ID_APP_SESSION_EXPIRED", "Session Expired", null);
				Throwable throwable = this.convertSessionInvalidException(se);
				retObject = throwable;
				throw throwable;
			}
			else
			{
				Throwable throwable = new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
				retObject = throwable;
				throw throwable;
			}
		}
		catch (Exception e)
		{
			if (e instanceof ServiceRequestException)
			{
				retObject = e;
				throw e;
			}
			Throwable throwable = new ServiceRequestException(null, e.getMessage(), e.fillInStackTrace());
			retObject = throwable;
			throw throwable;
		}
		finally
		{
			if (delegator != null)
			{
				this.serviceContext.releaseService((Service) delegator);
			}

			TrackerBuilder trackerBuilder = this.trackerManager.getTrackerBuilder(method);
			if (trackerBuilder != null)
			{
				this.trackerScheduler.addTask(new ScheduledTaskTrackImpl(trackerBuilder, signature, method, args, retObject));
			}

			delegator = null;
			signature = null;
			// DynaLogger.println(serviceClass.getSimpleName() + "." + methodName + " cost " +
			// (System.currentTimeMillis() - start));
		}
	}

	private void modelSignatureCheck(String methodName, Signature signature)
	{
		if ("deploy".equals(methodName) || "getDownloader".equals(methodName) || "getUploader".equals(methodName))
		{
			// check client type
			try
			{
				if (!(signature instanceof ModuleSignature))
				{
					throw new AuthorizeException("invalid signature");
				}

				String moduleId = ((ModuleSignature) signature).getModuleId();
				if (!Signature.MODULE_APP_SERVER.equals(moduleId) && !Signature.MODULE_MODELER.equals(moduleId) && !Signature.MODULE_CLIENT.equals(moduleId))
				{
					throw new AuthorizeException("invalid module, available value: " + Signature.MODULE_APP_SERVER + ", " + Signature.MODULE_MODELER);
				}

				if (Signature.MODULE_APP_SERVER.equals(moduleId) && "deploy".equals(methodName))
				{
					throw new AuthorizeException("Illegal Access: " + methodName + " by " + moduleId);
				}
			}
			catch (AuthorizeException e)
			{
				throw new DynaDataGenericException(e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		String methodName = null;
		Class<?>[] parameterTypes = null;
		if (method != null)
		{
			methodName = method.getName();
			parameterTypes = method.getParameterTypes();

		}
		return this.invoke(this.serviceClass, this.credential, proxy, method, methodName, parameterTypes, args);
	}

	private ServiceRequestException convertSessionInvalidException(SessionInvalidException e)
	{
		this.serviceContext.getServerContext().getCredentialManager().unbind(this.credential);
		ServiceProviderFactory.getServiceProvider().clearServiceInstance(this.credential);
		this.serviceContext.getServerContext().removeSessionUpdateTime(this.credential);
		ServiceRequestException sre = new ServiceRequestException(e.getMsrId(), e.getMessage(), e.fillInStackTrace());
		return sre;
	}

	private void updateSession(final Signature signature, boolean isUserAccess) throws SessionInvalidException, InvocationTargetException
	{
		if (signature == null)
		{
			throw new SessionInvalidException("ID_APP_SESSION_EXPIRED", "Session Expired", null);
		}

		if (signature instanceof ModuleSignature)
		{
			return;
		}

		String sessionId = signature.getCredential();
		if (isUserAccess)
		{
			long nowTime = System.currentTimeMillis();
			boolean update = this.serviceContext.getServerContext().shouldUpdateSessionTime(sessionId, nowTime);
			if (!update)
			{
				return;
			}
		}
		// this.sessionUpdater.submitTask(new ScheduledTaskUdSessionImpl(sessionId,isUserAccess));
		(new ScheduledTaskUdSessionImpl(sessionId, isUserAccess)).run();
	}

}
