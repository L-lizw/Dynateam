/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SystemIDCheckerLinuxImpl
 * Wanglei 2011-4-15
 */
package dyna.app.core.lic.system;

import java.util.List;

import dyna.app.core.lic.mac.MacAddressMatcher;
import dyna.app.core.lic.mac.MacAddressMatcherLinuxImpl;

/**
 * @author Wanglei
 *
 */
public class SystemIDCheckerLinuxImpl extends AbstractSystemIDChecker
{
	private MacAddressMatcher	macAddressMatcher	= new MacAddressMatcherLinuxImpl();

	/**
	 * @param osName
	 */
	protected SystemIDCheckerLinuxImpl(String osName)
	{
		super(osName);
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
