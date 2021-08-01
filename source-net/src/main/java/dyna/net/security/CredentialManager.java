/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: CredentialManager
 * Wanglei 2010-4-16
 */
package dyna.net.security;

import dyna.common.exception.AuthorizeException;
import dyna.net.security.signature.Signature;
import dyna.net.security.signature.SignatureFactory;

import java.util.UUID;

/**
 * 凭证管理器接口
 * 
 * @author Wanglei
 * 
 */
public interface CredentialManager
{
	public final String		INTERNAL_CREDENTIAL	= UUID.randomUUID().toString();
	public final Signature	INTERNAL_SIGNATURE	= SignatureFactory.createSignature(Signature.SYSTEM_INTERNAL);

	public String getModuleCredential(String moduleID);

	public void setModuleCredential(String moduleID, String credential);

	public void bind(String credential, Signature signature);

	public void unbind(String credential);

	public Signature authenticate(String credential) throws AuthorizeException;
}
