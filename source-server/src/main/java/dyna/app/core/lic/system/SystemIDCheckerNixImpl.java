/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SystemIDCheckerLinuxImpl
 * Wanglei 2011-4-15
 */
package dyna.app.core.lic.system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * @author Wanglei
 *
 */
public class SystemIDCheckerNixImpl extends AbstractSystemIDChecker
{
	private final int	OS_TYPE_OS		= 1;
	private final int	OS_TYPE_WINDOWS	= 2;
	private final int	OS_TYPE_AIX		= 3;
	private final int	OS_TYPE_SUNOS	= 4;
	private final int	OS_TYPE_LINUX	= 5;


	/**
	 * @param osName
	 */
	protected SystemIDCheckerNixImpl(String osName)
	{
		super(osName);
	}

	/* (non-Javadoc)
	 * @see dyna.app.core.lic.system.AbstractSystemIDCheckeer#lookupSystemIdentification(java.util.List)
	 */
	@Override
	protected String lookupSystemIdentification(List<String> licenseRawData)
	{
		String systemID = null;

		if (this.osName.toLowerCase().startsWith("sunos"))
		{
			systemID = this.system("hostid", this.OS_TYPE_AIX);
		}
		else if (this.osName.startsWith("HP-UX"))
		{
			systemID = this.system("lanscan -a", this.OS_TYPE_SUNOS);
		}
		else
		{
			systemID = this.system("uname -m", this.OS_TYPE_WINDOWS);
		}

		return systemID;
	}

	private synchronized String system(String command, int os)
	{

		String mac = "000000000000";
		Runtime r = Runtime.getRuntime();

		try
		{
			Process p = r.exec(command);
			try
			{
				p.waitFor();
				BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String readline = in.readLine();
				if (os == this.OS_TYPE_OS)
				{
					mac = readline.substring(12);
				}
				else if (os == this.OS_TYPE_WINDOWS)
				{
					mac = readline.substring(0, 12);
				}
				else if (os == this.OS_TYPE_AIX || os == this.OS_TYPE_LINUX)
				{
					mac = readline.toUpperCase();
				}
				else if (os == this.OS_TYPE_SUNOS)
				{
					mac = readline.substring(2, 14);
				}
				in.close();
				p = null;
			}
			catch (InterruptedException i)
			{
				i.printStackTrace();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return mac;
	}
}
