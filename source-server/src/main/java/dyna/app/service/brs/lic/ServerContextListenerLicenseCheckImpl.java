/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServerContextListenerLicenseCheckImpl
 * Wanglei 2011-9-26
 */
package dyna.app.service.brs.lic;

import dyna.app.core.sch.SessionCheck;
import dyna.app.server.context.ApplicationServerContext;
import dyna.app.server.context.ServerContextListener;
import dyna.app.server.context.ServiceContext;
import dyna.common.conf.ConfigurableServerImpl;

/**
 * @author Wanglei
 *
 */
public class ServerContextListenerLicenseCheckImpl implements ServerContextListener
{

	/* (non-Javadoc)
	 * @see dyna.app.server.context.ServerContextListener#contextInitialized(dyna.app.server.context.ServerContext, dyna.app.server.context.ServiceContext)
	 */
	@Override
	public void contextInitialized(ApplicationServerContext serverContext, ServiceContext serviceContext)
	{

		ConfigurableServerImpl svConfig = serverContext.getServerConfig();
		int timeout = svConfig.getSessionTimeout() == null ? 0 : svConfig.getSessionTimeout();

		serverContext.getSchedulerManager().getScheduledTaskScheduler()
		.scheduleAtFixedRate(new SessionCheck(serviceContext, timeout), 1000, 300 * 1000);
	}

}
