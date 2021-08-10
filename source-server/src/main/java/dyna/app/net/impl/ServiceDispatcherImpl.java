/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServiceLookupImpl
 * Wanglei 2010-11-26
 */
package dyna.app.net.impl;

import dyna.app.server.GenericServer;
import dyna.app.server.context.ApplicationServerContext;
import dyna.common.bean.serv.ServiceBean;
import dyna.common.conf.ConfigurableServiceImpl;
import dyna.common.conf.ServiceDefinition;
import dyna.common.conf.loader.ConfigLoaderFactory;
import dyna.common.conf.loader.ConfigLoaderServiceImpl;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.ServiceStateEnum;
import dyna.common.util.StringUtils;
import dyna.net.dispatcher.ServiceDispatcher;
import dyna.net.security.signature.ModuleSignature;
import dyna.net.security.signature.Signature;
import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.rmi.server.RemoteServer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 提供发布服务
 * 
 * @author Wanglei
 * 
 */
@Service
public class ServiceDispatcherImpl implements ServiceDispatcher
{

	private ApplicationServerContext serverContext = null;

	public ServiceDispatcherImpl(ApplicationServerContext serverContext)
	{
		this.serverContext = serverContext;
	}

	/* (non-Javadoc)
	 * @see dyna.net.lookup.ServiceLookup#listService()
	 */
	@Override
	public List<ServiceBean> listService() throws ServiceRequestException
	{
		List<ServiceBean> retList = new ArrayList<ServiceBean>();

		ConfigLoaderServiceImpl loader = GenericServer.getServiceBean(ConfigLoaderFactory.class).getLoader4Service();
		ConfigurableServiceImpl serviceConfig = loader.getConfigurable();

		Iterator<ServiceDefinition> iterator = serviceConfig.getServiceDefinitions();
		for (; iterator.hasNext();)
		{
			ServiceBean sb = new ServiceBean(iterator.next());
			sb.setDispatchPort(this.serverContext.getServerConfig().getRmiRegistryPort());
			retList.add(sb);
		}

		return retList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.dispatcher.ServiceDispatcher#getInternalCredential()
	 */
	@Override
	public String getConnectionCredential(String moduleName) throws ServiceRequestException
	{
		try
		{
			String clientHost = RemoteServer.getClientHost();
			DynaLogger.info(clientHost + " try to connect appserver...");

			if (Signature.MODULE_CLIENT.equals(moduleName) || // client
					Signature.MODULE_DSS.equals(moduleName) || // dss
					Signature.MODULE_MODELER.equals(moduleName) || // modeler
					Signature.MODULE_DATA_SERVER.equals(moduleName)/* || ModelCADTypeEnum.ACAD.name().equals(moduleName)
					|| ModelCADTypeEnum.CAT5.name().equals(moduleName) || ModelCADTypeEnum.CS.name().equals(moduleName)
					|| ModelCADTypeEnum.INVE.name().equals(moduleName)
					|| ModelCADTypeEnum.PROE.name().equals(moduleName)
					|| ModelCADTypeEnum.SOLE.name().equals(moduleName)
					|| ModelCADTypeEnum.SOLW.name().equals(moduleName)
					|| ModelCADTypeEnum.UGNX.name().equals(moduleName)*/// ds
			)
			{
				SecurityContext context = SecurityContextHolder.getContext();
				Authentication authentication = context.getAuthentication();
				if (authentication == null)
				{
					throw new ServiceRequestException("reject any unsigned call");
				}

				String clientIdentifier = (String) authentication.getDetails();
				if (!StringUtils.isNullString(clientIdentifier))
				{
					ModuleSignature signature = this.serverContext.getConnectionManager().getConnectionSignature(
							clientIdentifier);
					if (signature == null || moduleName.equals(signature.getModuleId()))
					{
						DynaLogger.info("duplicate connection: " + clientIdentifier);
						return clientIdentifier;
					}
				}

				String credential = this.serverContext.getConnectionManager().newConnection(moduleName);

				DynaLogger.info(clientHost + " connected successfully");

				return credential;
			}
			else
			{
				throw new ServiceRequestException("invalid module name");
			}
		}
		catch (Exception e)
		{
			throw ServiceRequestException.createByException("", e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.dispatcher.ServiceDispatcher#testServiceAvailable()
	 */
	@Override
	public ServiceStateEnum getServiceState() throws ServiceRequestException
	{
		SecurityContext context = SecurityContextHolder.getContext();
		Authentication authentication = context.getAuthentication();
		if (authentication != null)
		{
			String clientIdentifier = (String) authentication.getDetails();
			if (!StringUtils.isNullString(clientIdentifier))
			{
				return this.serverContext.getServiceStateManager().getServiceState(clientIdentifier);
			}
		}
		else
		{
			return this.serverContext.getServiceState();
		}

		return ServiceStateEnum.NORMAL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.dispatcher.ServiceDispatcher#synchronizedService()
	 */
	@Override
	public void synchronizedService() throws ServiceRequestException
	{
		SecurityContext context = SecurityContextHolder.getContext();
		Authentication authentication = context.getAuthentication();
		if (authentication != null)
		{
			String clientIdentifier = (String) authentication.getDetails();
			if (!StringUtils.isNullString(clientIdentifier))
			{
				this.serverContext.getServiceStateManager().setServiceState(clientIdentifier, ServiceStateEnum.NORMAL);
			}
		}
		else
		{
			this.serverContext.setServiceState(ServiceStateEnum.NORMAL);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.dispatcher.ServiceDispatcher#disconnect(java.lang.String)
	 */
	@Override
	public void disconnect(String moduleName) throws ServiceRequestException
	{
		SecurityContext context = SecurityContextHolder.getContext();
		Authentication authentication = context.getAuthentication();
		if (authentication == null)
		{
			throw new ServiceRequestException("reject any unsigned call");
		}

		String clientIdentifier = (String) authentication.getDetails();
		if (!StringUtils.isNullString(clientIdentifier))
		{
			ModuleSignature signature = this.serverContext.getConnectionManager().getConnectionSignature(
					clientIdentifier);
			if (signature != null && moduleName.equals(signature.getModuleId()))
			{
				this.serverContext.getConnectionManager().removeConnection(clientIdentifier);
				return;
			}
		}

		throw new ServiceRequestException("unmatch moduleName " + moduleName + "and clientIdentifier "
				+ clientIdentifier);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.dispatcher.ServiceDispatcher#isConnected()
	 */
	@Override
	public boolean isConnected()
	{
		return true;
	}

}
