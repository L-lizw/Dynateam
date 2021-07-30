/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServiceStateExchangerRMIImpl
 * Wanglei 2011-1-19
 */
package dyna.net.dispatcher.sync;

import dyna.common.log.DynaLogger;
import dyna.common.systemenum.ServiceStateEnum;
import dyna.net.dispatcher.ServiceDispatcher;

/**
 * 服务状态交换器(同步)
 * 
 * @author Wanglei
 * 
 */
public class ServiceStateExchangerRMIImpl implements ServiceStateExchanger
{

	private ServiceDispatcher	dispatcher	= null;

	public ServiceStateExchangerRMIImpl(ServiceDispatcher dispatcher)
	{
		this.dispatcher = dispatcher;
	}

	/* (non-Javadoc)
	 * @see dyna.net.dispatcher.sync.ServiceStateExchanger#exchangeState(dyna.net.dispatcher.sync.ServiceStateChangeReactor)
	 */
	@Override
	public void exchangeState(ServiceStateChangeReactor reactor)
	{
		try
		{
			ServiceStateEnum serviceState = this.dispatcher.getServiceState();
			if (reactor != null)
			{
				serviceState = reactor.stateChanged(serviceState);
			}

			if (serviceState == ServiceStateEnum.NORMAL)
			{
				this.dispatcher.synchronizedService();
			}
		}
		catch (Exception e)
		{
			DynaLogger.error("service state sync error: " + e.getMessage());
		}
	}

}
