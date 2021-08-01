/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DynaDataNormalException
 * Wanglei 2011-7-6
 */
package dyna.dbcommon.exception;

import dyna.common.exception.DynaDataException;

/**
 * @author Wanglei
 *
 */
public class DynaDataNormalException extends DynaDataException
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1511487952825344289L;

	/**
	 * @param message
	 * @param cause
	 */
	public DynaDataNormalException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
