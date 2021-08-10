/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: AbstractMacAddressMatcher
 * Wanglei 2011-4-15
 */
package dyna.app.core.lic.mac;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dyna.app.core.lic.License;
import dyna.app.core.lic.LicenseDecipher;
import dyna.common.log.DynaLogger;
import dyna.common.util.StringUtils;

/**
 * @author Wanglei
 * 
 */
public abstract class AbstractMacAddressMatcher implements MacAddressMatcher
{

	protected String		macAddrCommand	= null;
	protected String		macPattern		= null;
	protected String		macSplitChar	= null;
	protected List<String>	addressArray	= new ArrayList<String>();

	protected AbstractMacAddressMatcher(String macAddrCommand, String macPattern, String macSplitChar)
	{
		this.macAddrCommand = macAddrCommand;
		this.macPattern = macPattern;
		this.macSplitChar = macSplitChar;
		if (!StringUtils.isNullString(macAddrCommand))
		{
			this.updateMacAddressInfo();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.lic.mac.MacAddressMatcher#getGrantMacAddress(java.util.List)
	 */
	@Override
	public String getGrantMacAddress(List<String> licenseRawData)
	{
		try
		{
			String type = licenseRawData.get(0);
			String product = licenseRawData.get(1);
			String module = licenseRawData.get(2);
			String version = licenseRawData.get(3);
			String cipher = licenseRawData.get(4);

			return this.getGrantMacAddress(type, product, module, version, cipher);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	/**
	 * 获取授权的mac地址
	 * 
	 * @param licenseType
	 * @param productCode
	 * @param moduleCode
	 * @param versionCode
	 * @param cipher
	 * @return
	 */
	protected String getGrantMacAddress(String licenseType, String productCode, String moduleCode, String versionCode, String cipher)
	{

		String grantMacAddress = null;
		DynaLogger.debug(" Searching the MAC Address");

		for (int index = 0; index < this.addressArray.size(); index++)
		{
			License license = null;
			String macAddr = this.addressArray.get(index);

			license = LicenseDecipher.getInstance().decipher(licenseType, macAddr, productCode, moduleCode, versionCode, cipher);
			if (license != null)
			{
				grantMacAddress = macAddr;
				DynaLogger.debug(" Detected the MAC Address : " + grantMacAddress);
				break;
			}
		}

		if (StringUtils.isNullString(grantMacAddress))
		{
			DynaLogger.error(" Can not find the MAC Address");
			grantMacAddress = "";
		}

		return grantMacAddress;

	}

	private void updateMacAddressInfo()
	{

		Matcher a = this.getMacAddressResult();

		while (a.find())
		{
			String mac = a.group();
			mac = this.toNormalString(mac);
			if (mac != null)
			{
				mac = mac.toUpperCase();
			}
			this.addressArray.add(mac);
		}
	}

	private String toNormalString(String mac)
	{

		String result = "";
		StringTokenizer token = new StringTokenizer(mac, this.macSplitChar);

		while (token.hasMoreElements())
		{
			result += token.nextElement().toString();
		}

		return result;
	}

	private Matcher getMacAddressResult()
	{

		Process p = null;

		try
		{
			p = Runtime.getRuntime().exec(this.macAddrCommand);
		}
		catch (java.io.IOException e)
		{
			e.printStackTrace();
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String output = "", currentLine = "";

		try
		{
			while ((currentLine = in.readLine()) != null)
			{
				currentLine = currentLine.trim();
				output += currentLine + "\n";
			}
		}
		catch (java.io.IOException e)
		{
			e.printStackTrace();
		}

		Pattern arpPattern = Pattern.compile(this.macPattern);
		Matcher arpM = arpPattern.matcher(output);

		return arpM;
	}
}
