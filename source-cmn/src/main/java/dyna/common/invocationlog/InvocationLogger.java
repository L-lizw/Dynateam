/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DynaLogger
 * Wanglei 2010-4-7
 */
package dyna.common.invocationlog;

import java.util.Properties;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import dyna.common.util.EnvUtils;

/**
 * 系统日志工具类, 提供包括info,warn,error,debug,fatal等5个级别的日志控制,
 * 以及控制台打印功能.
 * DEBUG < INFO < WARN < ERROR < FATAL
 * 
 * @author Wanglei
 * 
 */
public final class InvocationLogger
{

	private static Logger	invocationLogger	= null;

	public static Logger getInvocationLogger()
	{
		if (invocationLogger == null)
		{
			invocationLogger = Logger.getLogger(InvocationLogger.class);
			FileAppender appender = null;
			appender = (FileAppender) invocationLogger.getParent().getAppender("DYNAINVOCATION");

			if (appender != null)
			{
				appender.setFile(EnvUtils.getConfRootPath() + appender.getFile());
				appender.activateOptions();
			}
			config(null);
		}
		return invocationLogger;
	}

	public static void config(Properties prop)
	{
		if (prop == null)
		{
			return;
		}
		PropertyConfigurator.configure(prop);
	}
}
