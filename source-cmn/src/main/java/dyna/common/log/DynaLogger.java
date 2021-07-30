/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DynaLogger
 * Wanglei 2010-4-7
 */
package dyna.common.log;

import java.util.Properties;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import dyna.common.util.EnvUtils;

/**
 * 系统日志工具类, 提供包 括info,warn,error,debug,fatal等5个级别的日志控制,
 * 以及控制台打印功能.
 * DEBUG < INFO < WARN < ERROR < FATAL
 * 
 * @author Wanglei
 * 
 */
public final class DynaLogger
{
	enum LogType
	{
		DYNAAPP, DYNADATA, DYNAFTP, DYNA
	}

	private static Logger	logger			= null;			;
	private static LogType	currentLogType	= LogType.DYNA;

	public static Logger getLogger()
	{
		// TODO log configuration
		if (logger == null)
		{
			logger = Logger.getLogger(DynaLogger.class);
			FileAppender appender = null;
			if (currentLogType == LogType.DYNAAPP)
			{
				appender = (FileAppender) logger.getParent().getAppender(LogType.DYNAAPP.name());
				logger.getParent().removeAppender(LogType.DYNADATA.name());
				logger.getParent().removeAppender(LogType.DYNAFTP.name());
				logger.getParent().removeAppender(LogType.DYNA.name());
			}
			else if (currentLogType == LogType.DYNADATA)
			{
				appender = (FileAppender) logger.getParent().getAppender(LogType.DYNADATA.name());
				logger.getParent().removeAppender(LogType.DYNAAPP.name());
				logger.getParent().removeAppender(LogType.DYNAFTP.name());
				logger.getParent().removeAppender(LogType.DYNA.name());
			}
			else if (currentLogType == LogType.DYNAFTP)
			{
				appender = (FileAppender) logger.getParent().getAppender(LogType.DYNAFTP.name());
				logger.getParent().removeAppender(LogType.DYNAAPP.name());
				logger.getParent().removeAppender(LogType.DYNADATA.name());
				logger.getParent().removeAppender(LogType.DYNA.name());
			}
			else
			{
				appender = (FileAppender) logger.getParent().getAppender(LogType.DYNA.name());
				logger.getParent().removeAppender(LogType.DYNAAPP.name());
				logger.getParent().removeAppender(LogType.DYNADATA.name());
				logger.getParent().removeAppender(LogType.DYNAFTP.name());
			}

			if (appender != null)
			{
				appender.setFile(EnvUtils.getConfRootPath() + appender.getFile());
				// System.out.println("DYNA LOGFILE: " + (appender.getFile()));
				appender.activateOptions();
			}
			config(null);
		}
		return logger;
	}

	public static void setAppLog()
	{
		currentLogType = LogType.DYNAAPP;
	}

	public static void setDataLog()
	{
		currentLogType = LogType.DYNADATA;
	}

	public static void setFtpLog()
	{
		currentLogType = LogType.DYNAFTP;
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
		return getLogger().isDebugEnabled();
	}

	/**
	 * @see org.apache.log4j.Category#info(Object)
	 */
	public static void info(Object message)
	{
		getLogger().info(getNullObject(message));
	}

	/**
	 * @see org.apache.log4j.Category#info(Object,Throwable)
	 */
	public static void info(Object message, Throwable t)
	{
		getLogger().info(getNullObject(message), t);
	}

	/**
	 * @see org.apache.log4j.Category#warn(Object,Throwable)
	 */
	public static void warn(Object message)
	{
		getLogger().warn(getNullObject(message));
	}

	/**
	 * @see org.apache.log4j.Category#info(Object,Throwable)
	 */
	public static void warn(Object message, Throwable t)
	{
		getLogger().warn(getNullObject(message), t);
	}

	/**
	 * @see org.apache.log4j.Category#error(Object)
	 */
	public static void error(Object message)
	{
		getLogger().error(getNullObject(message));
	}

	/**
	 * @see org.apache.log4j.Category#error(Object,Throwable)
	 */
	public static void error(Object message, Throwable t)
	{
		getLogger().error(getNullObject(message), t);
	}

	/**
	 * @see org.apache.log4j.Category#debug(Object)
	 */
	public static void debug(Object message)
	{
		getLogger().debug(getNullObject(message));
	}

	/**
	 * @see org.apache.log4j.Category#debug(Object,Throwable)
	 */
	public static void debug(Object message, Throwable t)
	{
		getLogger().debug(getNullObject(message), t);
	}

	/**
	 * @see org.apache.log4j.Category#fatal(Object)
	 */
	public static void fatal(Object message)
	{
		getLogger().fatal(getNullObject(message));
	}

	/**
	 * @see org.apache.log4j.Category#fatal(Object,Throwable)
	 */
	public static void fatal(Object message, Throwable t)
	{
		getLogger().fatal(getNullObject(message), t);
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
