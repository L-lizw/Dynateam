/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: EncryptUtils 加密相关的工具
 * Wanglei 2010-4-15
 */
package dyna.common.util;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 加密相关的工具
 * 
 * @author Wanglei
 * 
 */
public class EncryptUtils
{

	/**
	 * Encrypt byte array.
	 */
	public final static byte[] encrypt(byte[] source, String algorithm) throws NoSuchAlgorithmException
	{
		MessageDigest md = MessageDigest.getInstance(algorithm);
		md.reset();
		md.update(source);
		return md.digest();
	}

	/**
	 * Encrypt string
	 */
	public final static String encrypt(String source, String algorithm) throws NoSuchAlgorithmException
	{
		byte[] resByteArray = encrypt(source.getBytes(), algorithm);
		return toHexString(resByteArray);
	}

	/**
	 * Encrypt string using MD5 algorithm
	 */
	public final static String encryptMD5(String source)
	{
		if (source == null)
		{
			source = "";
		}

		String result = "";
		try
		{
			result = encrypt(source, "MD5");
		}
		catch (NoSuchAlgorithmException ex)
		{
			// this should never happen
			throw new RuntimeException(ex);
		}
		return result;
	}

	/**
	 * Encrypt string using SHA algorithm
	 */
	public final static String encryptSHA(String source)
	{
		if (source == null)
		{
			source = "";
		}

		String result = "";
		try
		{
			result = encrypt(source, "SHA");
		}
		catch (NoSuchAlgorithmException ex)
		{
			// this should never happen
			throw new RuntimeException(ex);
		}
		return result;
	}

	public final static String toHexString(byte[] res)
	{
		return toHexString(res,0,res.length);
	}
	
	public final static String toHexString(byte[] res,int start,int length)
	{
		StringBuffer sb = new StringBuffer(length << 1);
		for (int i = start; i < start+length; i++)
		{
			String digit = Integer.toHexString(0xFF & res[i]);
			if (digit.length() == 1)
			{
				digit = '0' + digit;
			}
			sb.append(digit);
		}
		return sb.toString().toUpperCase();
	}
	
	public static byte[] hexStringToByte(String hex)
	{
		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		for (int i = 0; i < len; i++)
		{
			int pos = i * 2;
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
		}
		return result;
	}

	private static byte toByte(char c)
	{
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		return b;
	}

	public static byte[] LongToBytes(long v) throws IOException
	{
		byte[] writeBuffer = new byte[8];
		writeBuffer[0] = (byte) (v >>> 56);
		writeBuffer[1] = (byte) (v >>> 48);
		writeBuffer[2] = (byte) (v >>> 40);
		writeBuffer[3] = (byte) (v >>> 32);
		writeBuffer[4] = (byte) (v >>> 24);
		writeBuffer[5] = (byte) (v >>> 16);
		writeBuffer[6] = (byte) (v >>> 8);
		writeBuffer[7] = (byte) (v >>> 0);
		return writeBuffer;
	}
	
	public static long BytesToLong(byte[] writeBuffer ) throws IOException
	{
		long returnvalue=0;
		for (int i=0;i<writeBuffer.length;i++)
		{
			int pos=i*8;
			if (pos>0)
			{
				returnvalue=returnvalue<<pos;
			}
			returnvalue=returnvalue+writeBuffer[i];
		}
		return returnvalue;
	}

}
