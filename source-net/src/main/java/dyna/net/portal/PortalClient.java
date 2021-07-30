/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: PortalClient
 * wangweixia 2012-8-7
 */
package dyna.net.portal;

import java.io.IOException;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.swing.JOptionPane;

import org.apache.axis.encoding.Base64;

import dyna.common.util.DateFormat;
import dyna.common.util.EncryptUtils;
import dyna.common.util.StringUtils;
import dyna.net.portal.integrateService.InvalidSOKException;
import dyna.net.portal.integrateService.IssueandValidateTicketWS;
import dyna.net.portal.integrateService.PortService;
import dyna.net.portal.integrateService.PubKeyInfo;

/**
 * portal产品促发PLM入口
 * 
 * @author wangweixia
 * 
 */
public class PortalClient
{

	/**
	 * portal传入的参数
	 * args[0] Portal
	 * args[1] 用户ID
	 * args[2] 多语言
	 * args[3] PLM Service名称
	 * args[4] IssueandValidateTicketWS接口URL
	 */
	public static void main(String[] args) throws Exception
	{
		String plmClientName = "DigiWin PLM.exe";
		String userId = null;
		String password = null;
		String language = null;
		String portalString = "portal";
		// 判断portal是否传过来参数，传过来的参数是不是5个。
		if (args != null && args.length == 5)
		{

			// 获取IssueandValidateTicketWS接口
			IssueandValidateTicketWS iws = null;
			iws = new PortService(args[4]).portGateway();
			// PDM將此SOK送至SSO Server驗證，用以获取用户ID的key值
			String signOnKey = args[1];

			// 由SSO WebService取得RSA加密所需要的公鑰
			PubKeyInfo tPubKeyInfo = null;
			try
			{
				tPubKeyInfo = iws.getPubKeyInfo();
			}
			catch (RemoteException e)
			{
				JOptionPane.showMessageDialog(null, e.getMessage());
			}
			// 使用RSAPublicKey可參考的物件實例 間接取得RSA加密所需要的公鑰值(PublicExponent及Modulus)
			RSAPublicKey tPublicKey = new RSAPubKey(tPubKeyInfo);

			// 将signOnKey加密
			Cipher c = null;
			try
			{
				c = Cipher.getInstance("RSA");
			}
			catch (NoSuchAlgorithmException e)
			{
				JOptionPane.showMessageDialog(null, e.getMessage());
			}
			catch (NoSuchPaddingException e)
			{
				JOptionPane.showMessageDialog(null, e.getMessage());
			}
			try
			{
				c.init(Cipher.ENCRYPT_MODE, tPublicKey);
			}
			catch (InvalidKeyException e)
			{
				JOptionPane.showMessageDialog(null, e.getMessage());
			}
			// 加密后的signOnKey
			String signOnKeyString = null;
			try
			{
				signOnKeyString = Base64.encode(c.doFinal(signOnKey.getBytes()));
			}
			catch (IllegalBlockSizeException e)
			{
				JOptionPane.showMessageDialog(null, e.getMessage());
			}
			catch (BadPaddingException e)
			{
				JOptionPane.showMessageDialog(null, e.getMessage());
			}

			// 生成随机数
			java.util.Random r = new java.util.Random();
			// 随机生成私钥
			String encrypteedSessionKey = String.valueOf(r.nextLong()) + String.valueOf(r.nextLong());
			// 将随机数加密
			// 加密后的encrypteedSessionKey
			String encrypteedSessionKeyString = null;
			try
			{
				encrypteedSessionKeyString = Base64.encode(c.doFinal(encrypteedSessionKey.getBytes()));
			}
			catch (IllegalBlockSizeException e)
			{
				JOptionPane.showMessageDialog(null, e.getMessage());
			}
			catch (BadPaddingException e)
			{
				JOptionPane.showMessageDialog(null, e.getMessage());
			}

			// 获取解码前的用户id
			try
			{
				userId = iws.getValidatedUserInBase64(signOnKeyString, args[3], encrypteedSessionKeyString);
			}
			catch (InvalidSOKException e)
			{
				JOptionPane.showMessageDialog(null, e.getMessage());
			}
			catch (RemoteException e)
			{
				JOptionPane.showMessageDialog(null, e.getMessage());
			}

			// 將結果以Base64解碼
			byte[] tEncryptUserIdXml = Base64.decode(userId);

			// 建立一個以金鑰SessionKey為主的TripleDES解密物件
			DESedeKeySpec tKeySpec = null;
			try
			{
				tKeySpec = new DESedeKeySpec(encrypteedSessionKey.getBytes());
			}
			catch (InvalidKeyException e)
			{
				JOptionPane.showMessageDialog(null, e.getMessage());
			}
			SecretKeyFactory tKeyFactory = null;
			try
			{
				tKeyFactory = SecretKeyFactory.getInstance("DESede");
			}
			catch (NoSuchAlgorithmException e)
			{
				JOptionPane.showMessageDialog(null, e.getMessage());
			}
			SecretKey tSecureKey = null;
			try
			{
				tSecureKey = tKeyFactory.generateSecret(tKeySpec);
			}
			catch (InvalidKeySpecException e)
			{
				JOptionPane.showMessageDialog(null, e.getMessage());
			}
			SecureRandom tSr = new SecureRandom();
			Cipher cipher = null;
			try
			{
				cipher = Cipher.getInstance("DESede");
			}
			catch (NoSuchAlgorithmException e)
			{
				JOptionPane.showMessageDialog(null, e.getMessage());
			}
			catch (NoSuchPaddingException e)
			{
				JOptionPane.showMessageDialog(null, e.getMessage());
			}
			try
			{
				cipher.init(Cipher.DECRYPT_MODE, tSecureKey, tSr);
			}
			catch (InvalidKeyException e)
			{
				JOptionPane.showMessageDialog(null, e.getMessage());
			}

			// 將結果以TripleDES解密
			String tUserIdXml = null;
			try
			{
				tUserIdXml = new String(cipher.doFinal(tEncryptUserIdXml));
			}
			catch (IllegalBlockSizeException e)
			{
				JOptionPane.showMessageDialog(null, e.getMessage());
			}
			catch (BadPaddingException e)
			{
				JOptionPane.showMessageDialog(null, e.getMessage());
			}

			// SSO驗證是否成功
			boolean tAuthSuccess = true;

			// 解析結果的內容
			int tStartIndex = 0;
			int tEndIndex = 0;
			if (tUserIdXml.indexOf("cas:authenticationFail") != -1)
			{
				tAuthSuccess = false;
			}
			if (tAuthSuccess)
			{
				tStartIndex = tUserIdXml.indexOf("<cas:user>") + "<cas:user>".length();
				tEndIndex = tUserIdXml.indexOf("</cas:user>");
			}
			else
			{
				tStartIndex = tUserIdXml.indexOf("<cas:exception>") + "<cas:exception>".length();
				tEndIndex = tUserIdXml.indexOf("</cas:exception>");
			}

			userId = tUserIdXml.substring(tStartIndex, tEndIndex);

			// 解密并解码后的userId
			if (userId != null && tAuthSuccess)
			{

				password = portalString + userId + DateFormat.format(new Date(), "yyyyMMdd");
				password = EncryptUtils.encryptMD5(password);
				if (!StringUtils.isNullString(args[2]) && args[2].equals("zh_tw"))
				{
					language = "2";
				}
				else if (!StringUtils.isNullString(args[2]) && args[2].equals("zh_cn"))
				{
					language = "1";
				}
				else
				{
					language = "0";
				}

				String[] cmd = new String[] { plmClientName, userId, password, language };
				try
				{

					Runtime.getRuntime().exec(cmd);

				}
				catch (IOException e)
				{

					JOptionPane.showMessageDialog(null, e.getMessage());
				}

			}
			else
			{
				JOptionPane.showMessageDialog(null, userId);
			}
		}

	}
}
