/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServiceLocatorDataServerImpl
 * Wanglei 2010-12-7
 */
package dyna.data.dispatch;

import dyna.common.bean.serv.ServiceBean;
import dyna.common.conf.ConfigurableDataServerImpl;
import dyna.common.conf.ServiceDefinition;
import dyna.common.exception.ServiceNotFoundException;
import dyna.common.log.DynaLogger;
import dyna.common.util.StringUtils;
import dyna.data.context.DataServerContext;
import dyna.net.dispatcher.sync.ServiceStateExchanger;
import dyna.net.impl.AbstractServiceLocator;
import dyna.net.service.Service;
import dyna.net.spi.DataServiceLocator;
import org.springframework.remoting.rmi.RmiServiceExporter;
import org.springframework.remoting.support.RemoteInvocationExecutor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Proxy;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * data server service locator pojo implementation
 * 
 * @author Wanglei
 * 
 */
public class ServiceLocatorDataServerImpl extends AbstractServiceLocator implements DataServiceLocator
{
	private DataServerContext			context		= null;
	private ConfigurableDataServerImpl	dsConfig	= null;
	private DataServiceExporter			exporter	= null;
	private RemoteInvocationExecutor	executor	= null;
	private ServiceStateExchanger		ssExchanger	= null;

	public ServiceLocatorDataServerImpl(DataServerContext context, ConfigurableDataServerImpl dsConfig)
	{
		super();
		this.context = context;
		this.dsConfig = dsConfig;

		this.ssExchanger = new ServiceStateExchangerDataServerImpl(this.context);
		this.getServiceStateSync().setExchanger(this.ssExchanger);

		this.exporter = new DataServiceExporter(context, this.dsConfig);
		this.executor = new DataServiceRemoteInvocationExecutorImpl();
	}

	@SuppressWarnings("unchecked")
	public void init()
	{
		this.serviceList = new ArrayList<ServiceBean>();

		boolean exportable = true;
		Integer dispPort = this.dsConfig.getRmiRegistryPort();
		if (dispPort == null)
		{
			exportable = false;
		}
		
		if (exportable)
		{
			this.exporter.initialize();
		}

		DynaLogger.info("Loading Service[" + (!exportable ? "Listen Disable" : "Listen on " + dispPort) + "]...");
		boolean suc = true;
		Iterator<ServiceDefinition> iterator = this.dsConfig.getServiceDefinitions();
		for (; iterator.hasNext();)
		{
			ServiceDefinition sd = iterator.next();
			sd.setDispatchIP(dsConfig.getServerIP());
			try
			{
				DynaLogger.info("\t" + "[" + sd.getServcieID() + "]: " + sd.getServcieName());
				ServiceBean serviceBean = new ServiceBean(sd);
				this.serviceList.add(serviceBean);
				this.serviceBeanMap.put(sd.getServcieID(), serviceBean);
				this.serviceBeanMap.put(sd.getServiceInterfaceName(), serviceBean);

				Object service = null;
				Object delegator = null;

				Class<?> interfaceClass = sd.getServiceInterface();
				Class<?> implClass = sd.getServiceImplClass();
				Constructor<?> constructor = null;
				try
				{
					constructor = implClass.getConstructor(DataServerContext.class, ServiceDefinition.class);
					delegator = constructor.newInstance(this.context, sd);
				}
				catch (Exception e)
				{
					if (!(e instanceof NoSuchMethodException))
					{
						throw e;
					}

					constructor = implClass.getConstructor(ServiceDefinition.class);
					delegator = constructor.newInstance(sd);

				}

				this.context.setInternalService(interfaceClass, delegator);

				service = Proxy.newProxyInstance(sd.getServiceInterface().getClassLoader(), new Class[] { interfaceClass },
						new DataServiceDelegator(this.context, delegator));

				this.addService((Class<? extends Service>) sd.getServiceInterface(), null, (Service) service);

				if (exportable)
				{
					this.exportService(sd.getServcieID(), sd.getServiceInterface(), service, sd.getDispatchIP(), sd.getDispatchPort());
				}

			}
			catch (Exception e)
			{
				DynaLogger.warn("failed to load service: " + sd.getServiceInterfaceName(), e);
				suc = false;
			}
		}


		DynaLogger.info(suc ? "Data Server is ready." : "Data Server is not available, see error log.");
	}

	private void exportService(String serviceName, Class<?> serviceInterface, Object serviceImpl, String serverIP, int port)
			throws RemoteException
	{
		RmiServiceExporter exporter = new RmiServiceExporter();
		exporter.setAlwaysCreateRegistry(false);
		exporter.setServiceInterface(serviceInterface);
		exporter.setServiceName(serviceName);
		exporter.setService(serviceImpl);
		exporter.setRemoteInvocationExecutor(this.executor);
		if (!StringUtils.isNullString(serverIP))
		{
			System.setProperty("java.rmi.server.hostname", serverIP);
		}
		exporter.setRegistryPort(this.dsConfig.getRmiRegistryPort());
		if (port>0)
		{
			exporter.setServicePort(port);
		}
		else
		{
			exporter.setServicePort(this.dsConfig.getServiceDispatchPort());
		}

		exporter.afterPropertiesSet();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.spi.DataServiceLocator#lookup(java.lang.Class)
	 */
	@Override
	public <T extends Service> T lookup(Class<T> serviceClass) throws ServiceNotFoundException
	{
		return super.lookup(serviceClass, null);
	}

}
