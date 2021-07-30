/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Base64Util
 * WangLHB Oct 13, 2011
 */
package dyna.common.util;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;

/**
 * @author WangLHB
 * 
 */
public class Base64Util
{

	public static String encryptBase64(String source)
	{
		if (source == null)
		{
			return null;
		}

		Base64 base64 = new Base64();
		String encode1 = null;
		try
		{
			byte[] bytes = source.getBytes("UTF-8");
			byte[] encode = base64.encode(bytes);
			encode1 = new String(encode, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		return encode1;
	}

	public static String decodeBase64(String source)
	{
		if (source == null)
		{
			return null;
		}

		Base64 base64 = new Base64();
		String encode1 = null;
		try
		{
			byte[] bytes = source.getBytes("UTF-8");
			byte[] encode = base64.decode(bytes);
			encode1 = new String(encode, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		return encode1;
	}

}
