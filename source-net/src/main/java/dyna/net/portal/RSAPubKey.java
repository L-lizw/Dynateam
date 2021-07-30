/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: RSAPubKey
 * wangweixia 2012-8-7
 */
package dyna.net.portal;

import java.math.BigInteger;
import java.security.interfaces.RSAPublicKey;

import dyna.net.portal.integrateService.PubKeyInfo;

/**
 * @author wangweixia
 * 
 */

public class RSAPubKey implements RSAPublicKey
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -1442765315261500958L;
	PubKeyInfo					pubKeyInfo			= null;

	public RSAPubKey(PubKeyInfo pPubKeyInfo)
	{
		pubKeyInfo = pPubKeyInfo;
	}

	public BigInteger getPublicExponent()
	{
		return pubKeyInfo.getPublicExponent();
	}

	public String getAlgorithm()
	{
		return "RSA";
	}

	public byte[] getEncoded()
	{
		return null;
	}

	public String getFormat()
	{
		return "X.509";
	}

	public BigInteger getModulus()
	{
		return pubKeyInfo.getPublicModulus();
	}

}
