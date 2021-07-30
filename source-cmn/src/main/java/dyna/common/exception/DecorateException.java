/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DecorateException
 * Wanglei 2010-7-21
 */
package dyna.common.exception;

/**
 * @author Wanglei
 *
 */
public class DecorateException extends ParameterizedException
{

	private static final long	serialVersionUID	= 6773659110252647145L;

	/**
	 * @param msrId
	 * @param message
	 * @param t
	 * @param args
	 */
	public DecorateException(String message, Throwable t)
	{
		super(null, message, t, (Object[]) null);
	}

}
