/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: MacAddressMatcher license 与 mac地址 适配
 * Wanglei 2011-4-15
 */
package dyna.app.core.lic.mac;

import java.util.List;

/**
 * license 与 mac地址 适配
 * 
 * @author Wanglei
 * 
 */
public interface MacAddressMatcher
{
	public static final String	MAC_SPLIT_CHAR_WIN32	= "-";
	public static final String	MAC_SPLIT_CHAR_LINUX	= ":";
	public static final String	MAC_PATTERN_WIN32	= "[\\da-zA-Z]{1,2}\\-[\\da-zA-Z]{1,2}\\-[\\da-zA-Z]{1,2}\\-[\\da-zA-Z]{1,2}\\-[\\da-zA-Z]{1,2}\\-[\\da-zA-Z]{1,2}";
	public static final String	MAC_PATTERN_LINUX	= "[\\da-zA-Z]{1,2}\\:[\\da-zA-Z]{1,2}\\:[\\da-zA-Z]{1,2}\\:[\\da-zA-Z]{1,2}\\:[\\da-zA-Z]{1,2}\\:[\\da-zA-Z]{1,2}";

	/**
	 * 获取授权的mac地址
	 * 
	 * @param licenseRawData
	 *            license数据
	 * @return
	 */
	public String getGrantMacAddress(List<String> licenseRawData);

}
