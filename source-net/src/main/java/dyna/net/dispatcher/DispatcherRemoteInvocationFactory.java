/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DispatcherRemoteInvocationFactory
 * Wanglei 2010-12-8
 */
package dyna.net.dispatcher;

import org.acegisecurity.Authentication;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationFactory;

import dyna.net.connection.GenericClient;

/**
 * @author Wanglei
 *
 */
public class DispatcherRemoteInvocationFactory implements RemoteInvocationFactory
{

	private GenericClient	client			= null;
	private Authentication	authentication	= new UsernamePasswordAuthenticationToken(null, null);

	public DispatcherRemoteInvocationFactory(GenericClient client)
	{
		this.client = client;
	}

	/* (non-Javadoc)
	 * @see org.springframework.remoting.support.RemoteInvocationFactory#createRemoteInvocation(org.aopalliance.intercept.MethodInvocation)
	 */
	@Override
	public RemoteInvocation createRemoteInvocation(MethodInvocation methodInvocation)
	{
		this.setupAuthentication();
		// return new ContextPropagatingRemoteInvocation(methodInvocation);
		return new DispatcherRemoteInvocation(methodInvocation, this.authentication);

	}

	private void setupAuthentication()
	{
		((UsernamePasswordAuthenticationToken) this.authentication).setDetails(this.client.getClientIdentifier());
		// SecurityContextHolder.getContext().setAuthentication(this.authentication);
	}

}
