/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DynaDataSessionException - for checkout failed case
 * xiasheng Apr 22, 2010
 */
package dyna.dbcommon.exception;

import dyna.common.exception.DynaDataException;

/**
 * Check In异常
 * 
 * @author xiasheng
 */
public class DynaDataCheckinException extends DynaDataException
{
	private static final long	serialVersionUID	= -3468597342172791106L;

	/**
	 * @param message
	 * @param cause
	 */
	public DynaDataCheckinException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
