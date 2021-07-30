/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SecureRemoteInvocationFactory
 * Wanglei 2010-11-26
 */
package dyna.net.impl.rmipool;

import org.acegisecurity.Authentication;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationFactory;

import dyna.net.connection.GenericClient;
import dyna.net.impl.rmi.SecureRemoteInvocation;
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
	private StatelessRMIClient	client	= null;
	public SecureRemoteInvocationFactory(StatelessRMIClient client, Class<? extends Service> serviceClass)
	{
		this.client=client;
		this.serviceClass = serviceClass;
	}

	/* (non-Javadoc)
	 * @see org.springframework.remoting.support.RemoteInvocationFactory#createRemoteInvocation(org.aopalliance.intercept.MethodInvocation)
	 */
	@Override
	public RemoteInvocation createRemoteInvocation(MethodInvocation methodInvocation)
	{
		Authentication authentication = new UsernamePasswordAuthenticationToken(this.serviceClass, this.client.getSeesionId()==null?client.getClientIdentifier():this.client.getSeesionId());
		((UsernamePasswordAuthenticationToken) authentication).setDetails(client.getClientIdentifier());
		return new SecureRemoteInvocation(methodInvocation, authentication);
	}

}
