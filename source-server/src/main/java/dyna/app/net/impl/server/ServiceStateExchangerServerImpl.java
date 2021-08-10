/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServiceStateExchangerServerImpl
 * Wanglei 2011-1-20
 */
package dyna.app.net.impl.server;

import dyna.app.server.context.ApplicationServerContext;
import dyna.common.systemenum.ServiceStateEnum;
import dyna.net.dispatcher.sync.ServiceStateChangeReactor;
import dyna.net.dispatcher.sync.ServiceStateExchanger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Wanglei
 *
 */
@Service(value = "serviceStateExchangerServerImpl")
public class ServiceStateExchangerServerImpl implements ServiceStateExchanger
{
	@Autowired
	private ApplicationServerContext serverContext = null;

	/* (non-Javadoc)
	 * @see dyna.net.dispatcher.sync.ServiceStateExchanger#exchangeState(dyna.net.dispatcher.sync.ServiceStateChangeReactor)
	 */
	@Override
	public void exchangeState(ServiceStateChangeReactor reactor)
	{
		ServiceStateEnum serviceState = this.serverContext.getServiceState();
		if (reactor != null)
		{
			serviceState = reactor.stateChanged(serviceState);
			if (serviceState == ServiceStateEnum.WAITING)
			{
				serviceState = ServiceStateEnum.INVALID;
			}

			this.serverContext.setServiceState(serviceState);
		}
	}

}
