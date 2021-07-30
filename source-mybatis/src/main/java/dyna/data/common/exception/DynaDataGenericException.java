/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DynaDataGenericException 数据通用异常 DynaDataException 包装
 * Wanglei 2010-12-23
 */
package dyna.data.common.exception;

import dyna.common.exception.DynaDataException;

/**
 * 数据通用异常 DynaDataException 包装
 * 
 * @author Wanglei
 * 
 */
public class DynaDataGenericException extends DynaDataException
{

	private static final long	serialVersionUID	= -4956853473986819924L;

	/**
	 * @param message
	 * @param cause
	 */
	public DynaDataGenericException(Throwable cause)
	{
		super(null, cause);
	}

}
