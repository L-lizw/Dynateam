/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: MacAddressMatcherWindowsImpl
 * Wanglei 2011-4-15
 */
package dyna.app.core.lic.mac;


/**
 * @author Wanglei
 *
 */
public class MacAddressMatcherWindowsImpl extends AbstractMacAddressMatcher
{

	/**
	 * @param macAddrCommand
	 */
	public MacAddressMatcherWindowsImpl()
	{
		super("ipconfig /all", MAC_PATTERN_WIN32, MAC_SPLIT_CHAR_WIN32);
	}

}
