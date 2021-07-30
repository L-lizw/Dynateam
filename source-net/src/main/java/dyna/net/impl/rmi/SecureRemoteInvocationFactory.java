/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SecureRemoteInvocationFactory
 * Wanglei 2010-11-26
 */
package dyna.net.impl.rmi;

import org.acegisecurity.Authentication;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationFactory;

import dyna.net.connection.GenericClient;
import dyna.net.service.Service;

/**
 * remote invocation factory for rmi, call by spring framework automatically.
 * 
 * @author Wanglei
 * 
 */
public class SecureRemoteInvocationFactory implements RemoteInvocationFactory
{
	private Class<? extends Service>	serviceClass	= null;
	private String	credential	= null;
	private Authentication	authentication	= null;

	public SecureRemoteInvocationFactory(GenericClient client, Class<? extends Service> serviceClass, String credential)
	{
		this.serviceClass = serviceClass;
		this.credential = credential;
		this.authentication = new UsernamePasswordAuthenticationToken(this.serviceClass, this.credential);
		((UsernamePasswordAuthenticationToken) this.authentication).setDetails(client.getClientIdentifier());
	}

	/* (non-Javadoc)
	 * @see org.springframework.remoting.support.RemoteInvocationFactory#createRemoteInvocation(org.aopalliance.intercept.MethodInvocation)
	 */
	@Override
	public RemoteInvocation createRemoteInvocation(MethodInvocation methodInvocation)
	{
		return new SecureRemoteInvocation(methodInvocation, this.authentication);
	}

}
