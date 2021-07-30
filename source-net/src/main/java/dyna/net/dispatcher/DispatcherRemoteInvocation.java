/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DispatcherRemoteInvocation
 * Wanglei 2011-4-11
 */
package dyna.net.dispatcher;

import org.acegisecurity.Authentication;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.remoting.support.RemoteInvocation;

/**
 * @author Wanglei
 *
 */
public class DispatcherRemoteInvocation extends RemoteInvocation
{

	private static final long	serialVersionUID	= -593685314563302380L;

	public DispatcherRemoteInvocation(MethodInvocation methodInvocation, Authentication authentication)
	{
		super(methodInvocation);
		this.addAttribute("authentication", authentication);
	}
}
