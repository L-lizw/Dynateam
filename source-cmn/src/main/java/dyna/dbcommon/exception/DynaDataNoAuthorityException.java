/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DynaDataNoAuthorityException
 * xiasheng Apr 22, 2010
 */
package dyna.dbcommon.exception;

import dyna.common.exception.DynaDataException;
import dyna.common.systemenum.AuthorityEnum;

/**
 * 无权限异常，可以获取权限的类别
 * 
 * @author xiasheng
 */
public class DynaDataNoAuthorityException extends DynaDataException
{
	private static final long	serialVersionUID	= 257001609049657930L;
	private final AuthorityEnum	authorityType;

	/**
	 * @param message
	 * @param authorityType
	 *            - AuthorityEnum
	 */
	public DynaDataNoAuthorityException(String message, AuthorityEnum authorityType)
	{
		super(message, null);
		this.authorityType = authorityType;
	}

	/**
	 * 获取发生异常的权限类别
	 * 
	 * @return AuthorityEnum
	 */
	public AuthorityEnum getAuthorityType()
	{
		return this.authorityType;
	}

}
