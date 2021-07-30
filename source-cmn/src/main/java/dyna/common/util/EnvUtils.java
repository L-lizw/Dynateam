/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: EnvUtils 工具类, 与程序运行相关的环境参数
 * Wanglei 2010-4-15
 */
package dyna.common.util;

/**
 * 工具类, 与程序运行相关的环境参数
 * 
 * @author Wanglei
 * 
 */
public class EnvUtils
{

	public static final int	UNKNOWN		= 0;
	public static final int	WINDOWS		= 1;
	public static final int	LINUX		= 2;
	public static final int	MACOSX		= 4;

	public static final int	X86			= 1;
	public static final int	X86_64		= 2;

	private static String	rootPath	= FileUtils.decodeURLString(EnvUtils.class.getResource("/").getPath(), "UTF-8");
	private static String	confPath	= rootPath;

	/**
	 * 获取程序运行的根目录<br>
	 * 例如, tomcat中, 该返回值为web工程所在目录的全路径
	 * 
	 * @return
	 */
	public static String getRootPath()
	{
		return rootPath;
	}

	public static void setRootPath(String path)
	{
		if (path == null)
		{
			return;
		}
		rootPath = path;
		confPath = path;
	}

	/**
	 * 获取系统中配置文件目录conf的所处位置
	 * 例如, tomcat中, 系统的配置文件目录的所处位置为{webproject}/WEB-INF/
	 * 
	 * @return
	 */
	public static String getConfRootPath()
	{
		return confPath;
	}

	public static void appendConfRootPath(String suffix)
	{
		confPath += suffix;
	}

	/**
	 * 返回操作系统名称, 例如windows NT, linux...
	 * 
	 * @return
	 */
	public static String getOSName()
	{
		return System.getProperty("os.name");
	}

	/**
	 * 返回操作系统的架构, 32或者64, 用于判断是否32/64位操作系统
	 * 
	 * @return
	 */
	public static String getOSArch()
	{
		return System.getProperty("os.arch");
	}

	/**
	 * 返回操作系统类型
	 * 
	 * @return one of {@link #WINDOWS}, {@link #LINUX}, {@link #MACOSX}
	 */
	public static int getOSType()
	{
		String osName = getOSName();
		if (osName.indexOf("Windows") != -1)
		{
			return WINDOWS;
		}
		else if (osName.indexOf("Linux") != -1)
		{
			return LINUX;
		}
		else if (osName.indexOf("Mac") != -1)
		{
			return MACOSX;
		}
		return UNKNOWN;
	}

	/**
	 * 获取操作系统结构
	 * 
	 * @return one of {@link #X86}, {@link #X86_64}
	 */
	public static int getOSArchType()
	{
		String osArch = getOSArch().trim();
		if (osArch.equals("x86"))
		{
			return X86;
		}
		else if (osArch.equals("x86_64"))
		{
			return X86_64;
		}
		return UNKNOWN;
	}
}
