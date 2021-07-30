/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DESEndecrypt
 * wangweixia 2012-8-6
 */
package dyna.common.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * DES加密解密工具
 * 
 * @author wangweixia
 * 
 */
public class DESEndecryptUtils
{

	/**
	 * 3-DES加密
	 * 
	 * @param String
	 *            src 要进行3-DES加密的String
	 * @param String
	 *            spkey分配的SPKEY
	 * @return String 3-DES加密后的String
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchPaddingException
	 */

	public static String get3DESEncrypt(String src, String spkey) throws IllegalBlockSizeException,
			BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException,
			NoSuchPaddingException
	{

		byte[] encryptedData = null;
		String encryptkey = spkey;

		DESedeKeySpec keySpec = new DESedeKeySpec(encryptkey.getBytes());

		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
		SecretKey secureKey = keyFactory.generateSecret(keySpec);
		Cipher cipher = Cipher.getInstance("DESede");
		cipher.init(Cipher.ENCRYPT_MODE, secureKey);
		encryptedData = cipher.doFinal(src.getBytes());

		return new String(Base64.encodeBase64(encryptedData));

	}

	/**
	 * 3-DES解密
	 * 
	 * @param String
	 *            src 要进行3-DES解密的String
	 * @param String
	 *            spkey分配的SPKEY
	 * @return String 3-DES加密后的String
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws InvalidKeySpecException
	 */

	public static String get3DESDecrypt(String src, String spkey) throws IllegalBlockSizeException,
			BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidKeySpecException
	{

		String strDe = null;
		Cipher cipher = Cipher.getInstance("DESede");
		String decryptkey = spkey;
		DESedeKeySpec keySpec = new DESedeKeySpec(decryptkey.getBytes());
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
		SecretKey secureKey = keyFactory.generateSecret(keySpec);
		cipher.init(Cipher.DECRYPT_MODE, secureKey);
		byte ciphertext[] = cipher.doFinal(Base64.decodeBase64(src));
		strDe = new String(ciphertext);

		return strDe;
	}

}
