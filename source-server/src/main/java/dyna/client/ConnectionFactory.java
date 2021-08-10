/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ClientFactory
 * Wanglei 2011-1-12
 */
package dyna.client;

import dyna.common.conf.ConfigurableClientImpl;
import dyna.net.connection.Connection;
import dyna.net.connection.RMIClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 客户端工厂方法
 * 
 * @author Lizw
 * 
 */
@Component
public class ConnectionFactory
{
	@Autowired
	private ConfigurableClientImpl clientConfig;
	public  Connection createConnection() throws Exception
	{
		Connection client = null;
//		ConfigurableClientImpl clientConfig = ConfigLoaderFactory.getLoader4Client().load();

		switch (clientConfig.getClientMode())
		{
		case ALL_IN_ONE:
			client = new AllInOneClient(clientConfig);
			break;
		case BUILT_IN_SERVER:
			client = new BuiltInServerClient(clientConfig);
			break;
		case DISTRIBUTED:
			client = new RMIClient(clientConfig);
			break;
		default:
			throw new Exception("client mode invalid, please check client.xml");
		}

		return client;
	}
}
