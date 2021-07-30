/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DynaSysLogger
 * wangweixia 2012-12-3
 */
package dyna.common.syslog;

import java.net.InetAddress;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.net.SyslogAppender;

/**
 * @author wangweixia
 *         将Monitor中日志写入远程服务器
 * 
 */
public final class DynaSysLogger
{

	private static Logger	logger	= Logger.getLogger(DynaSysLogger.class);

	static
	{
		SyslogAppender appender = (SyslogAppender) logger.getParent().getAppender("SYSLOG");
		if (appender != null)
		{

		}
		else
		{
			logger = null;
		}
		if (logger != null)
		{
			config(null);
		}
	}

	public static boolean isReachable(String address)
	{
		boolean result = false;
		try
		{
			InetAddress.getAllByName(address);
			result = true;
		}
		catch (Exception e)
		{
			result = false;
		}

		return result;
	}

	public static void config(Properties prop)
	{
		if (prop == null)
		{
			return;
		}
		PropertyConfigurator.configure(prop);
	}

	private static Object getNullObject(Object msg)
	{
		return msg == null ? "<null object>" : msg;
	}

	/**
	 * @see org.apache.log4j.Category#isDebugEnabled()
	 */
	public static boolean isDebugEnabled()
	{
		if (logger != null)
		{
			return logger.isDebugEnabled();
		}
		return false;

	}

	/**
	 * @see org.apache.log4j.Category#info(Object)
	 */
	public static void info(Object message)
	{
		if (logger != null)
		{
			logger.info(message);
		}
	}

	/**
	 * @see org.apache.log4j.Category#info(Object,Throwable)
	 */
	public static void info(Object message, Throwable t)
	{
		if (logger != null)
		{
			logger.info(getNullObject(message), t);
		}

	}

	/**
	 * @see org.apache.log4j.Category#warn(Object,Throwable)
	 */
	public static void warn(Object message)
	{
		if (logger != null)
		{
			logger.warn(getNullObject(message));
		}

	}

	/**
	 * @see org.apache.log4j.Category#info(Object,Throwable)
	 */
	public static void warn(Object message, Throwable t)
	{
		if (logger != null)
		{
			logger.warn(getNullObject(message), t);
		}
	}

	/**
	 * @see org.apache.log4j.Category#error(Object)
	 */
	public static void error(Object message)
	{
		if (logger != null)
		{
			logger.error(getNullObject(message));
		}
	}

	/**
	 * @see org.apache.log4j.Category#error(Object,Throwable)
	 */
	public static void error(Object message, Throwable t)
	{
		if (logger != null)
		{
			logger.error(getNullObject(message), t);
		}
	}

	/**
	 * @see org.apache.log4j.Category#debug(Object)
	 */
	public static void debug(Object message)
	{
		if (logger != null)
		{
			logger.debug(getNullObject(message));
		}
	}

	/**
	 * @see org.apache.log4j.Category#debug(Object,Throwable)
	 */
	public static void debug(Object message, Throwable t)
	{
		if (logger != null)
		{
			logger.debug(getNullObject(message), t);
		}
	}

	/**
	 * @see org.apache.log4j.Category#fatal(Object)
	 */
	public static void fatal(Object message)
	{
		if (logger != null)
		{
			logger.fatal(getNullObject(message));
		}
	}

	/**
	 * @see org.apache.log4j.Category#fatal(Object,Throwable)
	 */
	public static void fatal(Object message, Throwable t)
	{
		if (logger != null)
		{
			logger.fatal(getNullObject(message), t);
		}
	}

	/**
	 * 在控制台打印消息, 不记录到日志文件中
	 * 
	 * @param message
	 */
	public static void print(Object message)
	{
		System.out.print(message);
	}

	/**
	 * 在控制台打印消息(自动换行), 不记录到日志文件中
	 * 
	 * @param message
	 */
	public static void println(Object message)
	{
		System.out.println(message);
	}

}
