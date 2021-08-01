/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DynaDataNoAuthorityException
 * xiasheng Apr 22, 2010
 */
package dyna.data.common.exception;

import dyna.common.exception.DynaDataException;

/**
 * 传递了null给一个非空值异常
 * 
 * @author xiasheng
 */
public class DynaDataFieldNullException extends DynaDataException
{
	private static final long	serialVersionUID	= 252786333493412067L;
	private String				fieldName			= "";

	/**
	 * @param message
	 * @param public
	 */
	public DynaDataFieldNullException(String fieldName)
	{
		super(fieldName, null);
		this.fieldName = fieldName;
	}

	/**
	 * 获取发生异常的字段
	 * 
	 * @return field name
	 */
	public String getFieldName()
	{
		return this.fieldName;
	}

}
