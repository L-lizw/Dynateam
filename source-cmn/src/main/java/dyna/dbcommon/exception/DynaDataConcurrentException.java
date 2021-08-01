/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DynaDataConcurrentException - for Concurrent data
 * jianghl 2010-9-8
 */
package dyna.dbcommon.exception;

import dyna.common.exception.DynaDataException;

/**
 * 数据并发不可以更新
 * 
 * @author jianghl
 */
public class DynaDataConcurrentException extends DynaDataException
{
	private static final long	serialVersionUID	= 9181066035589770594L;

	/**
	 * @param message
	 * @param cause
	 */
	public DynaDataConcurrentException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
