/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServiceNotAvailableException 服务无效异常
 * Wanglei 2010-12-23
 */
package dyna.common.exception;

import dyna.common.systemenum.ServiceStateEnum;

/**
 * 服务无效异常
 * 
 * @author Wanglei
 * 
 */
public class ServiceNotAvailableException extends Exception
{

	private static final long	serialVersionUID	= 1202583154665113415L;

	private ServiceStateEnum	state				= null;

	public ServiceNotAvailableException(ServiceStateEnum state, String message)
	{
		super(message);
		this.state = state;
	}

	public ServiceStateEnum getServiceState()
	{
		return this.state;
	}
}
