/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: CredentialManagerDefaultImpl
 * Wanglei 2010-4-16
 */
package dyna.net.security;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import dyna.net.security.signature.Signature;

/**
 * 凭证管理器默认实现
 * 
 * @author Wanglei
 * 
 */
public class CredentialManagerDefaultImpl implements CredentialManager
{
	private Map<String, Signature>	signatureCache	= Collections.synchronizedMap(new HashMap<String, Signature>());
	private Map<String, String>		moduleCreCache	= Collections.synchronizedMap(new HashMap<String, String>());

	public CredentialManagerDefaultImpl()
	{
		this.bind(INTERNAL_CREDENTIAL, INTERNAL_SIGNATURE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.security.CredentialManager#authenticate(java.lang.String)
	 */
	@Override
	public Signature authenticate(String credential)
	{
		return this.signatureCache.get(credential);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.security.CredentialManager#bind(java.lang.String, dyna.net.security.signature.Signature)
	 */
	@Override
	public synchronized void bind(String credential, Signature signature)
	{
		if (this.signatureCache.containsKey(credential) || this.signatureCache.containsValue(signature))
		{
			return;
		}

		signature.setCredential(credential);
		this.signatureCache.put(credential, signature);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.security.CredentialManager#unbind(java.lang.String)
	 */
	@Override
	public synchronized void unbind(String credential)
	{
		this.signatureCache.remove(credential);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.security.CredentialManager#getModuleCredential(java.lang.String)
	 */
	@Override
	public synchronized String getModuleCredential(String moduleID)
	{
		return this.moduleCreCache.get(moduleID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.app.core.security.CredentialManager#setModuleCredential(java.lang.String, java.lang.String)
	 */
	@Override
	public synchronized void setModuleCredential(String moduleID, String credential)
	{
		if (this.moduleCreCache.containsKey(moduleID) || this.moduleCreCache.containsValue(credential))
		{
			return;
		}
		this.moduleCreCache.put(moduleID, credential);
	}

}
