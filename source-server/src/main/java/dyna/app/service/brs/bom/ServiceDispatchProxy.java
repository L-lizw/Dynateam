package dyna.app.service.brs.bom;

import dyna.app.core.sch.Scheduler;
import dyna.app.core.track.ScheduledTaskTrackImpl;
import dyna.app.core.track.TrackerBuilder;
import dyna.app.core.track.TrackerManager;
import dyna.app.server.context.ServiceContext;
import dyna.app.service.DataAccessService;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.exception.SessionInvalidException;
import dyna.common.systemenum.DataExceptionEnum;
import dyna.net.service.Service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ServiceDispatchProxy<T extends Service> implements InvocationHandler
{
	private DataAccessService	service				= null;
	private Class<T>			serviceClass		= null;
	private Scheduler			trackerScheduler	= null;
	private TrackerManager		trackerManager		= null;

	public ServiceDispatchProxy(DataAccessService service, Class<T> serviceClass)
	{
		this.service = service;
		this.serviceClass = serviceClass;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		proxy = this.service.getServiceInstance(this.serviceClass);
		Object retObject = null;
		try
		{
			retObject = method.invoke(proxy, args);
			return retObject;
		}
		catch (InvocationTargetException e)
		{
			Throwable targetException = e.getTargetException();
			Throwable cause = targetException.getCause();
			if (cause != null && cause instanceof DynaDataException)
			{
				// cause = cause.getCause();
				Throwable throwable = ServiceRequestException.createByDynaDataException((DynaDataException) cause);
				retObject = throwable;
				throw throwable;
			}

			if (targetException instanceof ServiceRequestException)
			{
				if (DataExceptionEnum.DS_NOT_FOUND_SESSION.getMsrId().equals(
						((ServiceRequestException) targetException).getMsrId()))
				{
					SessionInvalidException se = new SessionInvalidException("ID_APP_SESSION_EXPIRED",
							"Session Expired", null);
					Throwable throwable = new ServiceRequestException(null, se.getMessage(), se.fillInStackTrace());
					retObject = throwable;
					throw throwable;
				}
			}
			retObject = targetException;
			throw targetException;
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
			TrackerBuilder trackerBuilder = this.trackerManager.getTrackerBuilder(method);
			if (trackerBuilder != null)
			{
				this.trackerScheduler.addTask(new ScheduledTaskTrackImpl(trackerBuilder, this.service.getSignature(),
						method, args, retObject));
			}

		}
	}

	public void init(ServiceContext serviceContext)
	{
		this.trackerManager = serviceContext.getServerContext().getTrackerManager();
		this.trackerScheduler = serviceContext.getServerContext().getSchedulerManager()
				.getMultiThreadQueuedTaskScheduler();
	}

}
