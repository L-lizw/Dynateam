/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: AbstractServiceLocator
 * Wanglei 2010-4-16
 */
package dyna.net.impl;

import dyna.common.bean.serv.ServiceBean;
import dyna.common.exception.ServiceNotFoundException;
import dyna.net.dispatcher.sync.ServiceStateSync;
import dyna.net.service.Service;
import dyna.net.spi.ServiceLocator;

import java.util.*;

/**
 * @author Wanglei
 */
public abstract class AbstractServiceLocator implements ServiceLocator
{

	protected Map<String, ServiceBean> serviceBeanMap = new HashMap<String, ServiceBean>();

	protected List<ServiceBean> serviceList = null;

	private ServiceStateSync serviceStateSync = null;

	private final Map<String, Map<Class<? extends Service>, Service>> serviceCache = Collections.synchronizedMap(new HashMap<String, Map<Class<? extends Service>, Service>>());

	private final Map<String, Class<? extends Service>> serviceClassCache = Collections.synchronizedMap(new HashMap<String, Class<? extends Service>>());

	public AbstractServiceLocator()
	{
		this.serviceStateSync = new ServiceStateSync();
	}

	protected void addService(Class<? extends Service> serviceClass, String credential, Service service)
	{
		synchronized (this.serviceCache)
		{
			this.getServiceMap(credential).put(serviceClass, service);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.net.spi.ServiceLocator#clear(java.lang.String)
	 */
	@Override public void clear(String credential)
	{
		synchronized (this.serviceCache)
		{
			this.getServiceMap(credential).clear();
			this.serviceCache.remove(credential);
		}
	}

	@Override public void clearAll()
	{
		synchronized (this.serviceCache)
		{
			this.serviceCache.clear();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.net.spi.ServiceLocator#getServiceBean(java.lang.Class)
	 */
	@Override public ServiceBean getServiceBean(Class<? extends Service> serviceClass)
	{
		return this.serviceBeanMap.get(serviceClass.getName());
	}

	public ServiceBean getServiceBean(String serviceID)
	{
		return this.serviceBeanMap.get(serviceID);
	}

	@SuppressWarnings("unchecked") protected <T extends Service> Class<T> getServiceClassById(String serviceId) throws ServiceNotFoundException
	{
		Class<T> serviceClass = (Class<T>) this.serviceClassCache.get(serviceId);
		if (serviceClass != null)
		{
			return serviceClass;
		}

		ServiceBean serviceBean = this.getServiceBean(serviceId);
		if (serviceBean == null)
		{
			throw new ServiceNotFoundException("not found service: " + serviceId);
		}
		try
		{
			serviceClass = (Class<T>) Class.forName(serviceBean.getServiceInterfaceName());
			this.serviceClassCache.put(serviceId, serviceClass);
			return serviceClass;
		}
		catch (ClassNotFoundException e)
		{
			throw new ServiceNotFoundException("not found service: " + serviceBean.getServiceInterfaceName(), e.fillInStackTrace());
		}
	}

	private Map<Class<? extends Service>, Service> getServiceMap(String credential)
	{
		if (!this.serviceCache.containsKey(credential))
		{
			this.serviceCache.put(credential, Collections.synchronizedMap(new HashMap<Class<? extends Service>, Service>()));
		}
		return this.serviceCache.get(credential);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.net.spi.ServiceLocator#setExchanger(dyna.net.dispatcher.sync.ServiceStateExchanger)
	 */
	@Override public ServiceStateSync getServiceStateSync()
	{
		return this.serviceStateSync;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.net.spi.ServiceLocator#listCredential()
	 */
	@Override public List<String> listCredential()
	{
		return Collections.unmodifiableList(new ArrayList<String>(this.serviceCache.keySet()));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.net.spi.ServiceLocator#listServiceBean()
	 */
	@Override public List<ServiceBean> listServiceBean()
	{
		return this.serviceList;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.net.spi.ServiceLocator#lookup(java.lang.Class, java.lang.String)
	 */
	@SuppressWarnings("unchecked") @Override public <T extends Service> T lookup(Class<T> serviceClass, String credential) throws ServiceNotFoundException
	{
		return (T) this.getServiceMap(credential).get(serviceClass);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.net.spi.ServiceLocator#release(java.lang.Class, java.lang.String)
	 */
	@Override public void release(Class<? extends Service> serviceClass, String credential)
	{
		synchronized (this.serviceCache)
		{
			this.getServiceMap(credential).remove(serviceClass);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.net.spi.ServiceLocator#shutdown()
	 */
	@Override public void shutdown()
	{
		this.clearAll();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see dyna.net.spi.ServiceLocator#isAvailable()
	 */
	@Override public boolean isAvailable()
	{
		return true;
	}

}
