/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SecureRemoteInvocation
 * Wanglei 2010-11-26
 */
package dyna.net.impl.rmi;

import org.acegisecurity.Authentication;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.remoting.support.RemoteInvocation;

/**
 * @author Wanglei
 *
 */
public class SecureRemoteInvocation extends RemoteInvocation/* ContextPropagatingRemoteInvocation */
{

	private static final long	serialVersionUID	= 7382973382451969402L;

	/**
	 * @param methodInvocation
	 */
	public SecureRemoteInvocation(MethodInvocation methodInvocation, Authentication authentication)
	{
		super(methodInvocation);
		this.addAttribute("authentication", authentication);
	}

}
