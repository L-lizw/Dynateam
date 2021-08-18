/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServiceContextImpl
 * Wanglei 2010-3-30
 */
package dyna.app.server.context;

import dyna.app.core.Transactional;
import dyna.app.core.pool.ObjectPool;
import dyna.app.core.pool.ObjectPoolFactory;
import dyna.app.core.track.TrackerBuilder;
import dyna.app.core.track.annotation.Tracked;
import dyna.app.core.track.impl.DefaultTrackerBuilderImpl;
import dyna.app.net.ServiceFactoryDefaultImpl;
import dyna.app.net.impl.server.ServiceDelegatorRmiExecutorImpl;
import dyna.app.server.GenericServer;
import dyna.common.Poolable;
import dyna.common.conf.ConfigurableServiceImpl;
import dyna.common.conf.ServiceDefinition;
import dyna.common.conf.loader.ConfigLoaderFactory;
import dyna.common.conf.loader.ConfigLoaderServiceImpl;
import dyna.common.context.AbstractSvContext;
import dyna.common.exception.ServiceNotFoundException;
import dyna.common.log.DynaLogger;
import dyna.common.util.StringUtils;
import dyna.net.service.Service;
import org.springframework.remoting.rmi.RmiServiceExporter;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务的上下文实现
 * 
 * @author Wanglei
 * 
 */
@org.springframework.stereotype.Service
public class ServiceContextImpl extends AbstractSvContext implements ServiceContext
{
	/**
	 * 
	 */
	private static final long					serialVersionUID		= 1037614906399936105L;

	private ServiceContextObservable			scObservable			= new ServiceContextObservable();

	private ConfigurableServiceImpl				serviceConfig			= null;

	private ApplicationServerContext serverContext = null;

	private ServiceDelegatorRmiExecutorImpl		rmiExecutor				= null;

	private Map<String, Map<String, Poolable>>	serviceActiveRecordMap	= new ConcurrentHashMap<String, Map<String, Poolable>>();

	/**
	 * @param description
	 */
	public ServiceContextImpl()
	{
		super("DynaTeam Service Context");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.server.context.Context#init()
	 */
	@Override
	public void init() throws Exception
	{
		this.rmiExecutor = new ServiceDelegatorRmiExecutorImpl(this);

		ConfigLoaderServiceImpl configLoader = GenericServer.getServiceBean(ConfigLoaderFactory.class).getLoader4Service();
		this.serviceConfig = configLoader.getConfigurable();

		// initialize service into each service pool
		Integer dispPort = this.serverContext.getServerConfig().getRmiRegistryPort();
		DynaLogger.info("Loading Service[" + (dispPort == null ? "Listen Disable" : "Listen on " + dispPort) + "]...");
		this.loadServices();
	}

	private void loadServices()
	{
		ServiceDefinition serviceDefinition = null;
		Class<?> serviceInterface = null;
		Class<?> serviceImpl = null;

		for (Iterator<ServiceDefinition> iter = this.serviceConfig.getServiceDefinitions(); iter.hasNext();)
		{
			serviceDefinition = iter.next();
			// 传递RMI IP
			serviceDefinition.setDispatchIP(this.serverContext.getServerConfig().getServerIP());
			this.scObservable.setChanged();
			this.scObservable.notifyObservers(serviceDefinition);

			try
			{
				serviceInterface = serviceDefinition.getServiceInterface();
				serviceImpl = serviceDefinition.getServiceImplClass();

				DynaLogger.info("\t" + serviceDefinition.getServcieName() + "[" + serviceDefinition.getServcieID() + "]: " + serviceDefinition.getServcieDescription());

				this.buildTrackedMethod(serviceInterface, serviceImpl);

				ObjectPool dynaObjectPool = ObjectPoolFactory.createDynaObjectPool(serviceInterface, new ServiceFactoryDefaultImpl(serviceImpl, serviceDefinition));

				this.serverContext.getPoolManager().addPool(dynaObjectPool);

				serviceActiveRecordMap.put(serviceInterface.getSimpleName(), new ConcurrentHashMap<String, Poolable>());

				this.exportService(serviceDefinition);
			}
			catch (ClassNotFoundException e)
			{
				DynaLogger.info("Service class not found: " + serviceDefinition.getServiceImplName(), e.fillInStackTrace());
			}
			finally
			{
				serviceDefinition = null;
				serviceImpl = null;
			}
		}
	}

	private void buildTrackedMethod(Class<?> interfaceClass, Class<?> implClass)
	{

		Method[] methods = implClass.getMethods();
		if (methods == null || methods.length == 0)
		{
			return;
		}
		Tracked tracked = null;
		Method method = null;
		for (Method m : methods)
		{
			if (!m.isAnnotationPresent(Tracked.class))
			{
				continue;
			}
			tracked = m.getAnnotation(Tracked.class);

			try
			{
				method = interfaceClass.getMethod(m.getName(), m.getParameterTypes());
			}
			catch (Exception e)
			{
				continue;
			}

			TrackerBuilder builder = new DefaultTrackerBuilderImpl(this);
			builder.setTrackerRendererClass(tracked.renderer(), tracked.description());
			builder.setPersistenceClass(tracked.persistence());
			this.serverContext.getTrackerManager().bindTrackerBuilder(method, builder);
		}
	}

	private void exportService(final ServiceDefinition sd)
	{
		try
		{
			this.exportService(sd.getServcieID(), sd.getDispatchIP(), sd.getDispatchPort(), sd.getServiceInterface(), sd.getServiceImplClass().newInstance());
		}
		catch (Exception e)
		{
			DynaLogger.warn("service dispatch failed: " + sd.getServcieID(), e);
		}
	}

	@SuppressWarnings("rawtypes")
	protected void exportService(String serviceName, String serverIP, int port, Class interfaceClass, Object serviceObject) throws Exception
	{
		if (this.serverContext.getServerConfig().getRmiRegistryPort() == null)
		{
			return;
		}

		RmiServiceExporter exporter = new RmiServiceExporter();
		exporter.setAlwaysCreateRegistry(false);
		exporter.setServiceName(serviceName);
		if (!StringUtils.isNullString(serverIP))
		{
			System.setProperty("java.rmi.server.hostname", serverIP);
		}

		exporter.setRegistryPort(this.serverContext.getServerConfig().getRmiRegistryPort());
		exporter.setServiceInterface(interfaceClass);
		exporter.setService(serviceObject);
		exporter.setRemoteInvocationExecutor(this.rmiExecutor);
		if (port > 0)
		{
			exporter.setServicePort(port);
		}
		else
		{
			exporter.setServicePort(this.serverContext.getServerConfig().getServiceDispatchPort());
		}
		exporter.afterPropertiesSet();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Service> T allocatService(Class<T> serviceClass) throws ServiceNotFoundException
	{
		try
		{
			T delegator = (T) this.serverContext.getPoolManager().getObjectFromPool(serviceClass);
			Map<String, Poolable> recordMap = this.serviceActiveRecordMap.get(serviceClass.getSimpleName());
			if (recordMap.containsKey(((Poolable) delegator).getObjectUID()))
			{
				throw new IllegalArgumentException("Service is in Active");
			}
			recordMap.put(((Poolable) delegator).getObjectUID(), (Poolable) delegator);
			// TODO 事务初始化
			if (delegator instanceof Transactional)
			{
				((Transactional) delegator).newTransactionId();
			}
			return delegator;
		}
		catch (Exception e)
		{
			throw new ServiceNotFoundException(e.fillInStackTrace());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.server.context.ServiceContext#getServiceConfig()
	 */
	@Override
	public ConfigurableServiceImpl getServiceConfig()
	{
		return this.serviceConfig;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.server.context.ServiceContext#releaseService(dyna.net.service.Service)
	 */
	@Override
	public void releaseService(Service service)
	{
		Class<?>[] interfaces = service.getClass().getInterfaces();
		for (int i = 0; i < interfaces.length; i++)
		{
			if (Service.class.isAssignableFrom(interfaces[i]))
			{
				try
				{
					((Poolable) service).passivateObject();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				try
				{
					Map<String, Poolable> recordMap = this.serviceActiveRecordMap.get(interfaces[i].getSimpleName());
					if (!recordMap.containsKey(((Poolable) service).getObjectUID()))
					{
						throw new IllegalArgumentException("Service is not in Active");
					}
					recordMap.remove(((Poolable) service).getObjectUID());
					this.serverContext.getPoolManager().releaseObject(interfaces[i], (Poolable) service);
				}
				catch (Exception e)
				{
					DynaLogger.error(e, e);
				}
				break;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.server.context.ServiceContext#getServerContext()
	 */
	@Override
	public ApplicationServerContext getServerContext()
	{
		return this.serverContext;
	}

	public Observable getServiceContextObservable()
	{
		return this.scObservable;
	}

	class ServiceContextObservable extends Observable
	{

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observable#setChanged()
		 */
		@Override
		protected synchronized void setChanged()
		{
			super.setChanged();
		}

	}
}
