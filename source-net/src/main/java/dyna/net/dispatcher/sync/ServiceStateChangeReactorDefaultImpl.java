/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServiceStateChangeReactorDefaultImpl
 * Wanglei 2011-7-6
 */
package dyna.net.dispatcher.sync;

import dyna.common.systemenum.ServiceStateEnum;
import org.springframework.stereotype.Component;

/**
 * @author Wanglei
 *
 */
@Component
public class ServiceStateChangeReactorDefaultImpl implements ServiceStateChangeReactor
{

	/* (non-Javadoc)
	 * @see dyna.net.dispatcher.sync.ServiceStateChangeReactor#stateChanged(dyna.common.systemenum.ServiceStateEnum)
	 */
	@Override
	public ServiceStateEnum stateChanged(ServiceStateEnum serviceState)
	{
		if (serviceState == ServiceStateEnum.SYNCHRONIZE)
		{
			return ServiceStateEnum.NORMAL;
		}
		return serviceState;
	}

}
