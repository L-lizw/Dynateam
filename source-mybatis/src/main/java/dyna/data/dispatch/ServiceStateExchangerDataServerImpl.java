/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServiceStateExchangerDataServerImpl
 * Wanglei 2011-1-20
 */
package dyna.data.dispatch;

import dyna.common.systemenum.ServiceStateEnum;
import dyna.data.context.DataServerContext;
import dyna.net.dispatcher.sync.ServiceStateChangeReactor;
import dyna.net.dispatcher.sync.ServiceStateExchanger;
import org.springframework.stereotype.Repository;

/**
 * @author Wanglei
 *
 */
@Repository(value = "serviceStateExchangerDataServerImpl")
public class ServiceStateExchangerDataServerImpl implements ServiceStateExchanger
{

	private DataServerContext	serverContext	= null;

	public ServiceStateExchangerDataServerImpl(DataServerContext serverContext)
	{
		this.serverContext = serverContext;
	}

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
