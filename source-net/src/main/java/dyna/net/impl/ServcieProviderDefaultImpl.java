/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DefaultServcieProviderImpl
 * Wanglei 2010-4-6
 */
package dyna.net.impl;

import java.util.List;

import dyna.common.exception.ServiceNotFoundException;
import dyna.common.log.DynaLogger;
import dyna.common.util.StringUtils;
import dyna.net.service.Service;
import dyna.net.service.brs.LIC;
import dyna.net.spi.ServiceLocator;
import dyna.net.spi.ServiceProvider;

/**
 * 服务提供者的默认实现,
 * 所有需要自行实现的服务提供者继承此实现可简化某些操作
 * 
 * @author Wanglei
 * 
 */
public class ServcieProviderDefaultImpl implements ServiceProvider
{
	private String				sessionMgrCredential	= null;

	protected ServiceLocator	serviceLocator			= null;

	protected ServcieProviderDefaultImpl(ServiceLocator serviceLocator)
	{
		super();
		this.serviceLocator = serviceLocator;
		this.serviceLocator.getServiceStateSync().start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.spi.ServiceProvider#clearServiceInstance(java.lang.String)
	 */
	@Override
	public void clearServiceInstance(String credential)
	{
		try
		{
			if (!StringUtils.isNullString(this.getSessionMgrCredential()))
			{
				LIC lic = ServiceProviderFactory.getServiceProvider().getServiceInstance(LIC.class,
						this.getSessionMgrCredential());
				lic.deleteSession(credential);
			}
		}
		catch (Exception e)
		{
			DynaLogger.error("deleteSession failed", e);
		}

		this.serviceLocator.clear(credential);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.spi.ServiceProvider#clear()
	 */
	@Override
	public void destroy()
	{
		this.serviceLocator.getServiceStateSync().shutdown();
		List<String> credentialList = this.serviceLocator.listCredential();
		for (String credential : credentialList)
		{
			if (StringUtils.isNullString(credential) || credential.equals(this.getSessionMgrCredential()))
			{
				continue;
			}
			this.clearServiceInstance(credential);
		}
		this.clearServiceInstance(this.getSessionMgrCredential());
		this.serviceLocator.shutdown();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.spi.ServiceProvider#getServiceInstance(java.lang.Class)
	 */
	@Override
	public synchronized <T extends Service> T getServiceInstance(Class<T> serviceClass) throws ServiceNotFoundException
	{
		return this.serviceLocator.lookup(serviceClass, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.spi.ServiceProvider#getServiceInstance(java.lang.Class, java.lang.String)
	 */
	@Override
	public synchronized <T extends Service> T getServiceInstance(Class<T> serviceClass, String credential)
	throws ServiceNotFoundException
	{
		return this.serviceLocator.lookup(serviceClass, credential);
	}

	public String getSessionMgrCredential()
	{
		return this.sessionMgrCredential;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.spi.ServiceProvider#releaseServiceInstance(java.lang.Class)
	 */
	@Override
	public void releaseServiceInstance(Class<? extends Service> serviceClass) throws ServiceNotFoundException
	{
		this.releaseServiceInstance(serviceClass, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.spi.ServiceProvider#releaseServiceInstance(java.lang.Class, java.lang.String)
	 */
	@Override
	public void releaseServiceInstance(Class<? extends Service> serviceClass, String credential)
	throws ServiceNotFoundException
	{
		this.serviceLocator.release(serviceClass, credential);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.spi.ServiceProvider#setSessionMgrCredential(java.lang.String)
	 */
	@Override
	public void setSessionMgrCredential(String sessionMgrCredential)
	{
		if (this.sessionMgrCredential != null)
		{
			this.sessionMgrCredential = sessionMgrCredential;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.spi.ServiceProvider#syncServiceState()
	 */
	@Override
	public void syncServiceState()
	{
		this.serviceLocator.getServiceStateSync().run();
	}
}
