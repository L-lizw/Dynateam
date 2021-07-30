/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ConnectionManagerDefaultImpl
 * Wanglei 2011-1-17
 */
package dyna.net.connection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import dyna.common.exception.AuthorizeException;
import dyna.net.security.CredentialManager;
import dyna.net.security.signature.ModuleSignature;
import dyna.net.security.signature.Signature;
import dyna.net.security.signature.SignatureFactory;

/**
 * 连接管理器默认实现
 * 
 * @author Wanglei
 * 
 */
public class ConnectionManagerDefaultImpl implements ConnectionManager
{

	private CredentialManager	credentialManager	= null;

	private final Map<String, String>	moduleCredentialMap	= Collections
	.synchronizedMap(new HashMap<String, String>());

	public ConnectionManagerDefaultImpl(CredentialManager cm)
	{
		this.credentialManager = cm;
	}

	/* (non-Javadoc)
	 * @see dyna.net.connection.ConnectionManager#listConnectionCredential()
	 */
	@Override
	public List<String> listConnectionCredential()
	{
		return Collections.unmodifiableList(new ArrayList<String>(this.moduleCredentialMap.keySet()));
	}

	/* (non-Javadoc)
	 * @see dyna.net.connection.ConnectionManager#getConnectionSignature(java.lang.String)
	 */
	@Override
	public ModuleSignature getConnectionSignature(String clientCredential)
	{
		try
		{
			Signature signature = this.credentialManager.authenticate(clientCredential);
			if (signature instanceof ModuleSignature)
			{
				return (ModuleSignature) signature;
			}
		}
		catch (AuthorizeException e)
		{
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see dyna.net.connection.ConnectionManager#removeConnection(java.lang.String)
	 */
	@Override
	public void removeConnection(String clientCredential)
	{
		this.credentialManager.unbind(clientCredential);
		this.moduleCredentialMap.remove(clientCredential);
	}

	/* (non-Javadoc)
	 * @see dyna.net.connection.ConnectionManager#clearConnection()
	 */
	@Override
	public void clearConnection()
	{
		synchronized (this.moduleCredentialMap)
		{
			List<String> list = this.listConnectionCredential();
			for (String clientCredential : list)
			{
				this.removeConnection(clientCredential);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.net.connection.ConnectionManager#newConnection(java.lang.String)
	 */
	@Override
	public String newConnection(String moduleName)
	{
		String credential = UUID.randomUUID().toString();
		this.credentialManager.bind(credential, SignatureFactory.createSignature(moduleName));
		this.moduleCredentialMap.put(credential, moduleName);
		return credential;
	}

}
