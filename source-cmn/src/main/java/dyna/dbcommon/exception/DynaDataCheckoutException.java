/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DynaDataSessionException - for checkout failed case
 * xiasheng Apr 22, 2010
 */
package dyna.dbcommon.exception;

import dyna.common.exception.DynaDataException;

/**
 * Check Out异常
 * 
 * @author xiasheng
 */
public class DynaDataCheckoutException extends DynaDataException
{
	private static final long	serialVersionUID	= 3608413416752421035L;

	/**
	 * @param message
	 * @param cause
	 */
	public DynaDataCheckoutException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
