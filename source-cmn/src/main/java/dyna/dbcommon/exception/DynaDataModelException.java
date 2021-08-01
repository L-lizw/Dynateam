/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DynaDataSearchConditionException - for no search condition case
 * xiasheng Apr 22, 2010
 */
package dyna.dbcommon.exception;

import dyna.common.exception.DynaDataException;

/**
 * 模型服务异常
 * 
 * @author xiasheng
 */
public class DynaDataModelException extends DynaDataException
{
	private static final long	serialVersionUID	= 3721217196534904606L;

	/**
	 * @param message
	 * @param cause
	 */
	public DynaDataModelException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
