/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DynaDataSessionException - for session failed case
 * xiasheng Apr 22, 2010
 */
package dyna.data.common.exception;

import dyna.common.exception.DynaDataException;

/**
 * Session异常
 * 
 * @author xiasheng
 */
public class DynaDataSessionException extends DynaDataException
{
	private static final long	serialVersionUID	= 7256628155699915488L;

	/**
	 * @param message
	 * @param cause
	 */
	public DynaDataSessionException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
