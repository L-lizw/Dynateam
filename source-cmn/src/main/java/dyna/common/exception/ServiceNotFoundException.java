/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServiceNotFoundException
 * Wanglei 2010-4-2
 */
package dyna.common.exception;

/**
 * 搜索服务异常, 找不到指定服务
 * 
 * @author Wanglei
 * 
 */
public class ServiceNotFoundException extends Exception
{

	private static final long	serialVersionUID	= 1L;

	public ServiceNotFoundException(String message)
	{
		super(message);
	}

	public ServiceNotFoundException(Throwable t)
	{
		super(t);
	}

	public ServiceNotFoundException(String message, Throwable t)
	{
		super(message, t);
	}
}
