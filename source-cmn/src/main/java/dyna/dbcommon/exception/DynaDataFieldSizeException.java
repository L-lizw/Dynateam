/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DynaDataNoAuthorityException
 * xiasheng Apr 22, 2010
 */
package dyna.dbcommon.exception;

import dyna.common.exception.DynaDataException;

/**
 * 传递了超长值
 * 
 * @author xiasheng
 */
public class DynaDataFieldSizeException extends DynaDataException
{
	static final long	serialVersionUID	= -3301523335307378815L;

	private String		fieldName			= null;
	private String		fieldSize			= null;
	/**
	 * @param message
	 * @param public
	 */
	public DynaDataFieldSizeException(String fieldName, String fieldSize)
	{
		super(fieldSize, null);
		this.fieldName = fieldName;
		this.fieldSize = fieldSize;
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

	/**
	 * 获取发生异常的字段的最大Size
	 * 
	 * @return field size
	 */
	public String getFieldSize()
	{
		return this.fieldSize;
	}

}
