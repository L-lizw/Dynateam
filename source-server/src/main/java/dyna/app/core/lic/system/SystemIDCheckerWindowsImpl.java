/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SystemIDCheckerWindowsImpl
 * Wanglei 2011-4-15
 */
package dyna.app.core.lic.system;

import java.util.List;

import dyna.app.core.lic.mac.MacAddressMatcher;
import dyna.app.core.lic.mac.MacAddressMatcherWindowsImpl;

/**
 * @author Wanglei
 *
 */
public class SystemIDCheckerWindowsImpl extends AbstractSystemIDChecker
{
	private MacAddressMatcher	macAddressMatcher	= new MacAddressMatcherWindowsImpl();

	/**
	 * @param osName
	 */
	protected SystemIDCheckerWindowsImpl(String osName)
	{
		super(osName);
		this.vMCheckCommand="VirtualMachine.exe";
	}

	/* (non-Javadoc)
	 * @see dyna.app.core.lic.system.AbstractSystemIDCheckeer#lookupSystemIdentification(java.util.List)
	 */
	@Override
	protected String lookupSystemIdentification(List<String> licenseRawData)
	{
		return this.macAddressMatcher.getGrantMacAddress(licenseRawData);
	}

}
