/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DynaDataSearchConditionException - for search condition error case
 * xiasheng Apr 22, 2010
 */
package dyna.dbcommon.exception;

import dyna.common.exception.DynaDataException;

/**
 * Search Condition异常
 * 
 * @author xiasheng
 */
public class DynaDataSearchConditionException extends DynaDataException
{
	private static final long	serialVersionUID	= 6626978028215481047L;

	/**
	 * @param message
	 * @param cause
	 */
	public DynaDataSearchConditionException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
