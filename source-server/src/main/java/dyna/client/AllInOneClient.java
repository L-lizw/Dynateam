/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Client
 * Wanglei 2010-11-29
 */
package dyna.client;

import dyna.common.conf.ConfigurableClientImpl;
//import dyna.dsserver.server.DSServer;

/**
 * all server in one client
 * 
 * @author Wanglei
 * 
 */
public class AllInOneClient extends BuiltInServerClient
{
//	private static DSServer			dsServer	 			= null;

	//TODO
//	public AllInOneClient(ConfigurableClientImpl clientConfig) throws Exception
//	{
//		super(ConfigLoaderFactory.getLoader4Client().load());
//	}

	public AllInOneClient(ConfigurableClientImpl clientConfig) throws Exception
	{
		super(clientConfig);
	}

	@Override
	public void initServiceProvider() throws Exception
	{
		super.initServiceProvider();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.connection.AbstractClient#open()
	 */
	@Override
	public void open() throws Exception
	{
		super.open();
//		dsServer = DSServer.createDSServer(this);
	}

	@Override
	public void close()
	{
		super.close();
		try
		{
//			dsServer.stop();
		}
		catch (Exception e)
		{
			// do nothing.
		}
		finally
		{
//			dsServer = null;
		}
	}
}
