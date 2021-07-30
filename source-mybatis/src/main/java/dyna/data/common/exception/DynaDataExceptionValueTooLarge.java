/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: dataException
 * 2010-10-8 JiangHL
 */
package dyna.data.common.exception;

import dyna.common.systemenum.DataExceptionEnum;

import java.sql.SQLException;

/**
 * 捕获异常
 * 
 * @author Administrator
 * 
 */
public class DynaDataExceptionValueTooLarge extends DynaDataExceptionAll
{
	private static final long	serialVersionUID	= 252786333493412067L;

	/**
	 * 
	 * @param message
	 * @param e
	 * @param dee
	 * @param param
	 */
	public DynaDataExceptionValueTooLarge(String message, SQLException e, DataExceptionEnum dee, Object... param)
	{
		super(message, e, dee, param);
	}

}
