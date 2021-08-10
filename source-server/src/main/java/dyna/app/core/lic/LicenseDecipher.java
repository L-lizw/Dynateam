/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: LicenseDecipher license解码
 * Wanglei 2011-4-20
 */
package dyna.app.core.lic;

import java.security.InvalidKeyException;
import java.util.Date;

import Rijndael.Rijndael_Algorithm;
import dyna.common.log.DynaLogger;

/**
 * license解码
 * 
 * @author Wanglei
 * 
 */
public class LicenseDecipher {

	private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	private static LicenseDecipher	instance	= null;

	public static LicenseDecipher getInstance()
	{
		if (instance == null)
		{
			return new LicenseDecipher();
		}
		return instance;
	}

	private LicenseDecipher()
	{
		// do nothing
	}

	public License decipher(String type, String mac, String product, String module, String version, String cipher)
	{

		License licenseTicket = null;
		int len=mac.length();
		for (int i=len;i<16;i++)
		{
			mac=mac+"0";
		}
		byte[] kb = new byte[16];
		String key = type + mac + product + module + version;
		this.HexStr2CharStr(key, kb, 15);

		long sum = 0;
		for(int j = 0; j < 15; j++) {
			sum += kb[j];
		}
		kb[15] = (byte)(sum % 256);

		Object sessionKey = null;
		try {
			DynaLogger.debug(" Create the Session Key.");
			sessionKey = Rijndael_Algorithm.makeKey(kb, 16);
		} catch (InvalidKeyException e) {
			DynaLogger.error(" Can't create the Session Key.");
		}

		byte[] pt = new byte[32];
		this.HexStr2CharStr(cipher, pt, 32);
		byte[] ct = Rijndael_Algorithm.blockDecrypt(pt, 0, sessionKey, 16);
		DynaLogger.debug(" Decrypt License Information.");

		if(toString(kb).equals(toString(ct))) {
			byte[] ct2 = Rijndael_Algorithm.blockDecrypt(pt, 16, sessionKey, 16);

			String _ct2 = toString(ct2);

			String _user = _ct2.substring(0, 4);
			int user = Integer.valueOf(_user, 16).intValue();

			String _stime = _ct2.substring(4, 12), _etime = _ct2.substring(12, 20);
			long stime = Long.valueOf(_stime, 16).longValue() * 1000, etime = Long.valueOf(_etime, 16).longValue() * 1000;
			Date CurrentTime = new Date();
			long ctime = System.currentTimeMillis();
			CurrentTime.setTime(ctime);

			boolean enableprint = true, enablesave = true;

			String _enableprint = _ct2.substring(20, 22);
			if(_enableprint.compareTo(new String("30")) == 0) {
				enableprint = false;
			}

			String _enablesave = _ct2.substring(22, 24);
			if(_enablesave.compareTo(new String("30")) == 0) {
				enablesave = false;
			}

			if(ctime >= stime && ctime <= etime) {
				licenseTicket = new License(product, module, version, user, stime, etime, enableprint, enablesave);
			} else {
				DynaLogger.info("License Expired.");
				DynaLogger.info("MAC [" + mac + "], Invalid License for " + product + "," + version + "," + module);
			}
		} else {
			DynaLogger.debug("License Incorrected.");
			DynaLogger.debug("MAC [" + mac + "], Invalid License for " + product + "," + version + "," + module);
		}

		return licenseTicket;
	}

	private static String toString(byte[] ba) {
		int length = ba.length;
		char[] buf = new char[length * 2];

		for (int i = 0 , j = 0 , k ; i < length ;) {
			k = ba[i++];
			buf[j++] = HEX_DIGITS[(k >>> 4) & 0x0F];
			buf[j++] = HEX_DIGITS[k & 0x0F];
		}
		return new String(buf);
	}

	private void HexStr2CharStr(String HexStr, byte[] CharStr, int iSize) {
		int i;
		byte ch = (byte)'\0';

		for (i = 0 ; i < iSize ; i++) {
			ch = this.Hex2Char(HexStr, i * 2);
			CharStr[i] = ch;
		}
	}

	private byte Hex2Char(String HexStr, int index) {
		byte rch = (byte)'\0';

		for (int i = 0 ; i < 2 ; i++) {
			if (HexStr.charAt(index + i) >= '0' && HexStr.charAt(index + i) <= '9') {
				rch = (byte)((rch << 4) + (HexStr.charAt(index + i) - '0'));
			}
			else if (HexStr.charAt(index + i) >= 'A' && HexStr.charAt(index + i) <= 'F') {
				rch = (byte)((rch << 4) + (HexStr.charAt(index + i) - 'A' + 10));
			}
			else {
				return rch;
			}
		}
		return rch;
	}
}
