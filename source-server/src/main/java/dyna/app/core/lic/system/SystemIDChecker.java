/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SystemIDChecker
 * Wanglei 2011-4-15
 */
package dyna.app.core.lic.system;

import java.util.List;

import dyna.common.log.DynaLogger;
import dyna.common.util.EnvUtils;

/**
 * @author Wanglei
 *
 */
public interface SystemIDChecker
{
	/**
	 * 通过licence获取系统标识符
	 * 
	 * @param licenseRawData
	 * @return
	 */
	public String getSystemIdentification(List<String> licenseRawData);
	
	public boolean isVM();

	public static class Utils
	{
		public static SystemIDChecker getSystemIDChecker()
		{
			SystemIDChecker checker = null;
			String osName = EnvUtils.getOSName();
			DynaLogger.debug("[LIC] OS Type is : " + osName);

			if (osName.toLowerCase().startsWith("window"))
			{
				checker = new SystemIDCheckerWindowsImpl(osName);
			}
			else if (osName.toLowerCase().startsWith("linux"))
			{
				checker = new SystemIDCheckerLinuxImpl(osName);
			}
			else if (osName.toLowerCase().startsWith("mac os x"))
			{
				checker = new SystemIDCheckerLinuxImpl(osName);
			}
			else
			{
				checker = new SystemIDCheckerNixImpl(osName);
			}
			return checker;
		}

	}
}
