/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Version  版本
 * Wanglei 2011-7-18
 */
package dyna.common;

import dyna.common.util.EncryptUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 版本信息
 * 
 * @author Wanglei
 * 
 */
public class Version
{

	private static final String	PN	= " DigiWin PLM";
	private static final String	CR	= "Copyright (C)DigiWin";

	private static final String	PC	= "V2.0";

	private static final String	PV	= "6";
	private static final String	SV	= "0";
	private static final String	RV	= "0";

	private static final String	PH	= " *****";

	public static String getProductCode()
	{
		return PC;
	}

	public static String getPhase()
	{
		return PH;
	}

	public static String getPrimaryNumber()
	{
		return PV;
	}

	public static String getSecondaryNumber()
	{
		return SV;
	}

	public static String getRevisionNumber()
	{
		return RV;
	}

	public static String getProductName()
	{
		return PN;
	}

	public static String getCopyRight()
	{
		return CR;
	}

	public static String getVersionInfo()
	{
		return PC + "." + SV + "." + RV + PH;
	}

	public static String getInternalkey()
	{
		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] buffer = (PN + "." + CR + "." + PC + "." + PV + "." + SV + "." + SV + "." + PH).getBytes();
			md.update(buffer, 0, buffer.length);
			String retString = EncryptUtils.toHexString(md.digest());
			return retString;
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		return "";
	}
}
