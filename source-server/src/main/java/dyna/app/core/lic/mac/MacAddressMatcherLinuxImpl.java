/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: MacAddressMatcherLinuxImpl
 * Wanglei 2011-4-15
 */
package dyna.app.core.lic.mac;


/**
 * @author Wanglei
 *
 */
public class MacAddressMatcherLinuxImpl extends AbstractMacAddressMatcher
{

	/**
	 * @param macAddrCommand
	 */
	public MacAddressMatcherLinuxImpl()
	{
		super("/sbin/ifconfig -a", MAC_PATTERN_LINUX, MAC_SPLIT_CHAR_LINUX);
	}

}
