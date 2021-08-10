/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Signable
 * Wanglei 2010-4-6
 */
package dyna.app.core;

import dyna.net.security.signature.Signature;

/**
 * 给服务对象签名的接口
 * 
 * @author Wanglei
 * 
 */
public interface Signable
{
	/**
	 * 清除签名
	 */
	public void clearSignature();

	/**
	 * 获取签名信息
	 * 
	 * @return
	 */
	public Signature getSignature();

	/**
	 * 判断是否已经签名
	 * 
	 * @return
	 */
	public boolean isSigned();

	/**
	 * 添加签名
	 * 
	 * @param signature
	 *            签名信息
	 * @throws IllegalStateException
	 */
	public void setSignature(Signature signature) throws IllegalStateException;
}
