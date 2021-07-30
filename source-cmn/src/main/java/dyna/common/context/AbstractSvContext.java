/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: AbstractSvContext
 * Wanglei 2010-3-26
 */
package dyna.common.context;

import dyna.common.systemenum.ServiceStateEnum;

/**
 * 服务上下文环境的实现
 * 
 * @author Wanglei
 * 
 */
public abstract class AbstractSvContext extends BaseContext implements SvContext
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -5200866514785528655L;

	protected String			description		= null;

	protected ServiceStateEnum	serviceState	= ServiceStateEnum.NORMAL;

	public AbstractSvContext(String description)
	{
		this.description = description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.context.Context#getServiceState()
	 */
	@Override
	public ServiceStateEnum getServiceState()
	{
		return this.serviceState;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.context.Context#setServiceState(dyna.common.systemenum.ServiceStateEnum)
	 */
	@Override
	public ServiceStateEnum setServiceState(ServiceStateEnum serviceState)
	{
		if (serviceState == ServiceStateEnum.NORMAL && this.serviceState == ServiceStateEnum.SYNCHRONIZE
				|| serviceState == ServiceStateEnum.WAITING
				&& (this.serviceState == ServiceStateEnum.NORMAL || serviceState == ServiceStateEnum.INVALID)
				|| serviceState == ServiceStateEnum.SYNCHRONIZE && this.serviceState == ServiceStateEnum.WAITING
		)
		{
			this.serviceState = serviceState;
		}
		return this.serviceState;
	}

	/* (non-Javadoc)
	 * @see dyna.app.core.DynaContext#getContextDescription()
	 */
	@Override
	public String getContextDescription()
	{
		return this.description;
	}

}
