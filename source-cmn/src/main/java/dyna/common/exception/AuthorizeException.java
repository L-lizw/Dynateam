/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: AuthorizeException
 * 系统接入访问认证异常
 * Wanglei 2010-3-24
 */
package dyna.common.exception;

/**
 * 系统接入访问认证异常
 * 
 * @author Wanglei
 * 
 */
public class AuthorizeException extends ParameterizedException
{
	private static final long	serialVersionUID	= 1L;

	public static final String	ID_PER_DENIED		= "ID_PERMISSION_DENIED";

	/**
	 * @param string
	 */
	public AuthorizeException(String message)
	{
		this(null, message, null);
	}

	public AuthorizeException(String message, Throwable t)
	{
		this(null, message, null);
	}

	public AuthorizeException(String msrId, String message, Throwable t)
	{
		this(msrId, message, t, (Object[]) null);
	}

	/**
	 * @param msrId
	 * @param message
	 * @param t
	 * @param args
	 */
	public AuthorizeException(String msrId, String message, Throwable t, Object... args)
	{
		super(msrId, message, t, args);
	}

}
