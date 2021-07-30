/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServiceDelegator
 * Wanglei 2010-4-13
 */
package dyna.net.spi;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import dyna.net.service.Service;

/**
 * @author Wanglei
 *
 */
public interface ServiceDelegator extends InvocationHandler
{

	public Object invoke(Class<? extends Service> serviceClass, String credential, Object delegate, Method method,
			String methodName, Class<?>[] parameterTypes, Object[] args)
	throws Throwable;
}
