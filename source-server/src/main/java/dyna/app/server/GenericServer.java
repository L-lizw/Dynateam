/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: GenericServer
 * Wanglei 2010-11-29
 */
package dyna.app.server;

import dyna.app.conf.ServerConfigaration;
import dyna.app.server.context.ApplicationServerContextImpl;
import dyna.common.log.DynaLogger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author Wanglei
 */
public class GenericServer
{
	private static Server        server        = null;
	public static  boolean       isDebug       = false;

	private static ApplicationContext applicationContext = null;

	public static void main(String[] args)
	{

		try
		{
			if (args.length > 0)
			{
				if ("debug".equalsIgnoreCase(args[0]))
				{
					isDebug = true;
				}
			}
			DynaLogger.setAppLog();
			if (server == null)
			{
				applicationContext = new AnnotationConfigApplicationContext(ServerConfigaration.class);
				applicationContext.getBean(ApplicationServerContextImpl.class).setDebugMode(isDebug);
				server = applicationContext.getBean(ServerImpl.class);
				((ServerImpl) server).initialize();
				server.start();
			}

		}
		catch (Exception e)
		{
			DynaLogger.info("Failed to start server: ", e.fillInStackTrace());
			System.exit(-1);
		}
	}
	public static <T> T getServiceBean(Class<T> clazz)
	{
		return applicationContext.getBean(clazz);
	}

	public static ApplicationContext getServerApplicationContext()
	{
		return applicationContext;
	}

}
