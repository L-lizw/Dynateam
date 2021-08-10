/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DynaDAService
 * Wanglei 2010-3-26
 */
package dyna.app.service;

import dyna.app.core.Signable;
import dyna.app.core.SignableAdapter;
import dyna.app.core.Transactional;
import dyna.app.core.sch.ScheduledTaskUdSessionImpl;
import dyna.app.server.Server;
import dyna.app.server.context.ServiceContext;
import dyna.common.Poolable;
import dyna.common.conf.ServiceDefinition;
import dyna.common.exception.AuthorizeException;
import dyna.common.exception.ServiceNotFoundException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.util.StringUtils;
import dyna.net.security.CredentialManager;
import dyna.net.security.signature.Signature;
import dyna.net.security.signature.SignatureFactory;
import dyna.net.security.signature.UserSignature;
import dyna.net.service.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * @author Wanglei
 *
 */
public abstract class DataAccessService extends SignableAdapter implements Poolable, Transactional, Service
{
	@Autowired
	protected ServiceContext						serviceContext		= null;
	@Autowired
	protected Server                                server              = null;
	protected ServiceDefinition						serviceDef			= null;

	private String									moduleID			= null;
	private String									serviceCredential	= null;

	private String									transactionId		= null;

	private Map<Class<? extends Service>, Service>	referenceServiceMap	= null;

	private boolean									isRefered			= false;

	private final Object							syncObject			= new Object();

	private long									activeSessionTime	= 0;

	private boolean									isActive			= false;

	private final String							serviceUID			= StringUtils.generateRandomUID(32);

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.pool.Poolable#activateObject()
	 */
	@Override
	public void activateObject() throws Exception
	{
		isActive = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.Signable#clearSignature()
	 */
	@Override
	public void clearSignature()
	{
		this.releaseRefService();
		super.clearSignature();
		this.cleartTransactionId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.pool.Poolable#destroyObject()
	 */
	@Override
	public void destroyObject() throws Exception
	{
		this.isActive = false;
		this.clearSignature();
	}

	public String getOperatorGuid() throws ServiceRequestException
	{
		return this.getUserSignature().getUserGuid();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.Signable#setSignature(dyna.net.security.signature.Signature)
	 */
	@Override
	public synchronized void setSignature(Signature signature) throws IllegalStateException
	{
		if (!this.isActive && signature != null)
		{
			DynaLogger.error("Service is Release , please check code", new IllegalStateException("Service is Release , please check code"));
		}
		super.setSignature(signature);
		if (this.isRefered == false && referenceServiceMap != null)
		{
			for (Iterator<Service> iter = this.referenceServiceMap.values().iterator(); iter.hasNext();)
			{
				Service service = iter.next();
				if (service == this)
				{
					continue;
				}

				if (service instanceof Signable)
				{
					((Signable) service).setSignature(signature);
				}
			}
		}

	}

	/**
	 * get other available service from current service
	 * 
	 * @param <T>
	 * @param serviceClass
	 * @return
	 * @throws ServiceNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public <T extends Service> T getRefService(Class<T> serviceClass) throws ServiceNotFoundException
	{
		Service service = null;
		synchronized (this.syncObject)
		{
			if (this.referenceServiceMap == null)
			{
				this.referenceServiceMap = Collections.synchronizedMap(new HashMap<Class<? extends Service>, Service>());
				Class<? extends DataAccessService> selfClass = this.getClass();
				Class<?>[] interfaces = selfClass.getInterfaces();
				if (interfaces != null && interfaces.length != 0)
				{
					for (Class<?> interfaceClass : interfaces)
					{
						if (Service.class.isAssignableFrom(interfaceClass))
						{
							this.referenceServiceMap.put((Class<? extends Service>) interfaceClass, this);
							break;
						}
					}
				}
			}

			service = this.referenceServiceMap.get(serviceClass);
			if (service == null)
			{
				service = this.serviceContext.allocatService(serviceClass);

				this.referenceServiceMap.put(serviceClass, service);

				((DataAccessService) service).isRefered = true;
				((DataAccessService) service).referenceServiceMap = this.referenceServiceMap;
			}
			if (service instanceof Signable)
			{
				((Signable) service).setSignature(this.signature);
				((Transactional) service).setFixedTransactionId(this.getFixedTransactionId());
			}
		}
		return (T) service;
	}

	/**
	 * simple call <code>getRefService(Class)</code>
	 * 
	 * @param <T>
	 * @param serviceClass
	 * @param sessionId
	 *            useless, just match form parameter
	 * @return
	 * @throws ServiceNotFoundException
	 */
	public <T extends Service> T getServiceInstance(Class<T> serviceClass, String sessionId) throws ServiceNotFoundException
	{
		return this.getRefService(serviceClass);
	}

	/**
	 * 获取服务
	 * 
	 * @param serviceClass
	 * @return
	 * @throws ServiceNotFoundException
	 */
	public <T extends Service> T getServiceInstance(Class<T> serviceClass) throws ServiceNotFoundException
	{
		return this.getServiceInstance(serviceClass, null);
	}

	public String getServiceCredential()
	{
		return this.serviceCredential;
	}

	public ServiceDefinition getServiceDefinition()
	{
		return this.serviceDef;
	}

	public String getServiceModuleID()
	{
		return this.moduleID;
	}

	public UserSignature getUserSignature() throws ServiceRequestException
	{
		if (this.signature instanceof UserSignature)
		{
			return ((UserSignature) this.signature);
		}
		throw ServiceRequestException.createByException(AuthorizeException.ID_PER_DENIED, new AuthorizeException("unauthorized signature"));
	}

	/**
	 * you should overwrite this method to initialize your service context.
	 */
	public void init()
	{
	}

	/**
	 * 
	 * @param serviceContext
	 * @param serviceDef
	 */
	public final void init(ServiceContext serviceContext, ServiceDefinition serviceDef)
	{
		this.serviceContext = serviceContext;
		this.serviceDef = serviceDef;

		this.moduleID = serviceDef.getServcieName();

		CredentialManager cm = serviceContext.getServerContext().getCredentialManager();
		this.serviceCredential = cm.getModuleCredential(this.moduleID);
		if (StringUtils.isNullString(this.serviceCredential))
		{
			this.serviceCredential = UUID.randomUUID().toString();
			cm.setModuleCredential(this.moduleID, this.serviceCredential);
			cm.bind(this.serviceCredential, SignatureFactory.createSignature(this.moduleID));
		}

		this.init();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.pool.Poolable#makeObject(java.lang.Object[])
	 */
	@Override
	public void initObject(Object... initArgs) throws Exception
	{
		this.init((ServiceContext) initArgs[0], (ServiceDefinition) initArgs[1]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.pool.Poolable#passivateObject()
	 */
	@Override
	public void passivateObject() throws Exception
	{
		this.isActive = false;
		this.clearSignature();
	}

	private void releaseRefService()
	{
		synchronized (this.syncObject)
		{
			if (this.isRefered || this.referenceServiceMap == null)
			{
				this.isRefered = false;
				this.referenceServiceMap = null;
				return;
			}

			Service service = null;
			for (Iterator<Service> iter = this.referenceServiceMap.values().iterator(); iter.hasNext();)
			{
				service = iter.next();
				if (service.equals(this))
				{
					continue;
				}
				if (service instanceof Signable)
				{
					((Signable) service).clearSignature();
				}
				this.serviceContext.releaseService(service);
			}
			this.referenceServiceMap.clear();
			this.referenceServiceMap = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.pool.Poolable#validateObject()
	 */
	@Override
	public boolean validateObject()
	{
		return true;
	}

	@Override
	public String getFixedTransactionId()
	{
		return this.transactionId;
	}

	@Override
	public synchronized void setFixedTransactionId(String transactionId)
	{
		// if (StringUtils.isGuid(this.transactionId))
		// {
		// return;
		// }
		this.transactionId = transactionId;
	}

	@Override
	public String newTransactionId()
	{
		String newTransacationId = StringUtils.generateRandomUID(32);
		this.setFixedTransactionId(newTransacationId);
		return newTransacationId;
	}

	private synchronized void cleartTransactionId()
	{
		this.transactionId = null;
	}

	public void activeSession(boolean isUserUpdate)
	{
		if (activeSessionTime == 0)
		{
			activeSessionTime = System.currentTimeMillis();
		}
		else if (System.currentTimeMillis() - activeSessionTime > 30000)
		{
			(new ScheduledTaskUdSessionImpl(this.getSignature().getCredential(), isUserUpdate)).run();
			activeSessionTime = System.currentTimeMillis();
		}
	}

	@Override
	public String getObjectUID()
	{
		return this.serviceUID;
	}

}
