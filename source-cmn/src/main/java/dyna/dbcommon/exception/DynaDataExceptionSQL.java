/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 捕获SQL异常
 * JiangHL 2011-5-10
 */
package dyna.dbcommon.exception;

import dyna.common.systemenum.DataExceptionEnum;

/**
 * 捕获SQL异常
 * 
 * @author Administrator
 * 
 */
public class DynaDataExceptionSQL extends DynaDataExceptionAll
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -3972616358554530532L;

	/**
	 * 
	 * @param message
	 * @param t
	 * @param dee
	 * @param param
	 */

	public DynaDataExceptionSQL(String message, Exception t, DataExceptionEnum dee, Object... param)
	{
		super(message, t, dee, param);
	}

}
