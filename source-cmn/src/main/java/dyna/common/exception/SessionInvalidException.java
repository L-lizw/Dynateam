/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SessionInvalidException
 * Wanglei 2010-7-13
 */
package dyna.common.exception;

/**
 * @author Wanglei
 *
 */
public class SessionInvalidException extends ParameterizedException
{
	private static final long	serialVersionUID	= -641177590972336605L;

	/**
	 * @param msrId
	 * @param message
	 * @param t
	 * @param args
	 */
	public SessionInvalidException(String msrId, String message, Throwable t)
	{
		super(msrId, message, t, (Object[]) null);
	}

}
