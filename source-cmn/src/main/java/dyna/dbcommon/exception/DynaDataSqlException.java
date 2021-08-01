/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DynaDataSessionException - for db failed case
 * xiasheng Apr 22, 2010
 */
package dyna.dbcommon.exception;

import dyna.common.exception.DynaDataException;

/**
 *SQL语句执行异常
 * 
 * @author xiasheng
 */
public class DynaDataSqlException extends DynaDataException
{
	private static final long	serialVersionUID	= -4251749327998050317L;

	/**
	 * @param message
	 * @param cause
	 */
	public DynaDataSqlException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
