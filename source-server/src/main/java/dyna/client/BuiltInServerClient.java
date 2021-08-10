/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: BuiltInServerClient
 * Wanglei 2010-11-30
 */
package dyna.client;

import dyna.app.server.Server;
import dyna.app.server.ServerImpl;
import dyna.common.conf.ConfigurableClientImpl;
import dyna.common.log.DynaLogger;
import dyna.net.connection.AbstractClient;

/**
 * client integrated with application server
 * 
 * @author Wanglei
 * 
 */
public class BuiltInServerClient extends AbstractClient
{
	protected static Server			server			= null;
	private boolean					isDebugMode		= false;

//	public BuiltInServerClient(boolean isDebugMode) throws Exception
//	{
//		super(ConfigLoaderFactory.getLoader4Client().load());
//		this.isDebugMode=isDebugMode;
//	}

	/**
	 * @throws Exception
	 */
	public BuiltInServerClient(ConfigurableClientImpl clientConfig ) throws Exception
	{
		super(clientConfig);
	}

	/**
	 * @throws Exception
	 */
	public BuiltInServerClient(ConfigurableClientImpl clientConfig,boolean isDebugMode) throws Exception
	{
		super(clientConfig);
		this.isDebugMode = isDebugMode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.connection.AbstractClient#initServiceProvider()
	 */
	@Override
	protected void initServiceProvider() throws Exception
	{

		try
		{
			server = new ServerImpl();
			//TODO
			//this.getSscReactor()
			server.start();

		}
		catch (Exception e)
		{
			DynaLogger.info("Failed to start server: ", e.fillInStackTrace());
			throw e;
		}
	}

	@Override
	public void close()
	{
		super.close();
		if (server != null && server.isAlive())
		{
			try
			{
				server.shutdown();
			}
			catch (Exception e)
			{
				// do nothing.
			}
			finally
			{
				server = null;
			}
		}
	}
}
