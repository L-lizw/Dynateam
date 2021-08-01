/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DataServiceDispatcherImpl
 * Wanglei 2010-11-26
 */
package dyna.data.dispatch;

import dyna.common.bean.serv.ServiceBean;
import dyna.common.conf.ServiceDefinition;
import dyna.common.exception.ServiceRequestException;
import dyna.common.log.DynaLogger;
import dyna.common.systemenum.ServiceStateEnum;
import dyna.common.util.StringUtils;
import dyna.data.DataServer;
import dyna.data.conf.XmlConfigLoaderFactory;
import dyna.data.conf.xmlconfig.ConfigurableDataServerImpl;
import dyna.data.context.DataServerContext;
import dyna.net.dispatcher.ServiceDispatcher;
import dyna.net.security.signature.ModuleSignature;
import dyna.net.security.signature.Signature;
import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
@Repository
public class DataServiceDispatcherImpl implements ServiceDispatcher
{
	@Autowired
	private DataServerContext	context				= null;

	/* (non-Javadoc)
	 * @see dyna.net.lookup.ServiceLookup#listService()
	 */
	@Override
	public List<ServiceBean> listService() throws ServiceRequestException
	{
		List<ServiceBean> retList = new ArrayList<ServiceBean>();

		ConfigurableDataServerImpl serviceConfig = DataServer.getRepositoryBean(XmlConfigLoaderFactory.class).getLoader4DataServer().getConfigurable();
		Iterator<ServiceDefinition> iterator = serviceConfig.getServiceDefinitions();
		for (; iterator.hasNext();)
		{
			ServiceBean sb = new ServiceBean(iterator.next());
			sb.setDispatchPort(serviceConfig.getRmiRegistryPort());
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
			DynaLogger.info(clientHost + " try to connect dataserver...");

			if (Signature.MODULE_APP_SERVER.equals(moduleName)// as
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
					ModuleSignature signature = this.context.getConnectionManager().getConnectionSignature(
							clientIdentifier);
					if (signature == null || moduleName.equals(signature.getModuleId()))
					{
						DynaLogger.info("duplicate connection: " + clientIdentifier);
						return clientIdentifier;
					}
				}

				String credential = this.context.getConnectionManager().newConnection(moduleName);

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
				return this.context.getServiceStateManager().getServiceState(clientIdentifier);
			}
		}
		else
		{
			return this.context.getServiceState();
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
				this.context.getServiceStateManager().setServiceState(clientIdentifier, ServiceStateEnum.NORMAL);
			}
		}
		else
		{
			this.context.setServiceState(ServiceStateEnum.NORMAL);
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
			ModuleSignature signature = this.context.getConnectionManager().getConnectionSignature(
					clientIdentifier);
			if (signature != null && moduleName.equals(signature.getModuleId()))
			{
				this.context.getConnectionManager().removeConnection(clientIdentifier);
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
