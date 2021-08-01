/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServiceDispatcher
 * Wanglei 2010-11-30
 */
package dyna.data.dispatch;

import dyna.common.log.DynaLogger;
import dyna.common.util.StringUtils;
import dyna.data.conf.xmlconfig.ConfigurableDataServerImpl;
import dyna.net.dispatcher.DispatcherRemoteInvocationExecutor;
import dyna.net.dispatcher.ServiceDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.remoting.rmi.RmiServiceExporter;
import org.springframework.stereotype.Repository;

import java.rmi.RemoteException;

/**
 * 数据服务分发器
 * 
 * @author Wanglei
 * 
 */
@Repository
public class DataServiceExporter
{
	@Autowired
	private ConfigurableDataServerImpl config            = null;
	@Autowired
	private ServiceDispatcher          serviceDispatcher = null;

	public void initialize()
	{
		try
		{

			RmiServiceExporter exporter = new RmiServiceExporter();
			exporter.setAlwaysCreateRegistry(true);
			exporter.setServiceInterface(ServiceDispatcher.class);
			exporter.setServiceName("dataServiceDispatcher");
			exporter.setService(this.serviceDispatcher);
			String serverIP = this.config.getServerIP();
			if (!StringUtils.isNullString(serverIP))
			{
				// exporter.setRegistryHost(serverIP);
				System.setProperty("java.rmi.server.hostname", serverIP);
			}
			exporter.setRegistryPort(this.config.getRmiRegistryPort());
			exporter.setServicePort(this.config.getServiceDispatchPort());
			exporter.setRemoteInvocationExecutor(new DispatcherRemoteInvocationExecutor());

			exporter.afterPropertiesSet();
		}
		catch (RemoteException e)
		{
			DynaLogger.warn("failed to dispatch service, turn into pojo mode.", e);
		}
	}
}
