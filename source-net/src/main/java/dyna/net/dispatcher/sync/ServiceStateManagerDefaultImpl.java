/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServiceStateManagerDefaultImpl
 * Wanglei 2011-1-17
 */
package dyna.net.dispatcher.sync;

import dyna.common.systemenum.ServiceStateEnum;
import dyna.net.connection.ConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务状态管理器的默认实现类
 * 
 * @author Wanglei
 * 
 */
@Component
public class ServiceStateManagerDefaultImpl implements ServiceStateManager
{
	@Autowired
	private ConnectionManager					connectionManager	= null;

	/**
	 * 保存客户端对应的服务状态, 仅保存服务未能正常提供服务时的状态
	 */
	private final Map<String, ServiceStateEnum>	serviceStateMap		= Collections
	.synchronizedMap(new HashMap<String, ServiceStateEnum>());

	/* (non-Javadoc)
	 * @see dyna.net.security.ServiceStateManager#setServiceState(dyna.net.security.ServiceStateEnum)
	 */
	@Override
	public void setServiceState(ServiceStateEnum state)
	{
		if (state == null)
		{
			return;
		}

		List<String> connList = this.connectionManager.listConnectionCredential();
		for (String clientCredeantial : connList)
		{
			this.serviceStateMap.put(clientCredeantial, state);
		}
	}

	/* (non-Javadoc)
	 * @see dyna.net.security.ServiceStateManager#setServiceState(java.lang.String, dyna.net.security.ServiceStateEnum)
	 */
	@Override
	public void setServiceState(String clientCredeantial, ServiceStateEnum state)
	{
		if (state == null || state == ServiceStateEnum.NORMAL)
		{
			this.serviceStateMap.remove(clientCredeantial);
		}
		else {
			this.serviceStateMap.put(clientCredeantial, state);
		}

	}

	/* (non-Javadoc)
	 * @see dyna.net.security.ServiceStateManager#getServiceState(java.lang.String)
	 */
	@Override
	public ServiceStateEnum getServiceState(String clientCredential)
	{
		ServiceStateEnum state = this.serviceStateMap.get(clientCredential);
		return state == null ? ServiceStateEnum.NORMAL : state;
	}

}
