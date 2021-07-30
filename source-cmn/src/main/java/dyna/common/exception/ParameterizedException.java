/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ParameterizedException
 * Wanglei 2010-7-13
 */
package dyna.common.exception;

import dyna.common.log.DynaLogger;

/**
 * @author Wanglei
 *
 */
public class ParameterizedException extends Exception
{

	private static final long	serialVersionUID	= 6832563041638458975L;

	protected String			msrId				= null;
	protected Object[]			args				= null;

	public ParameterizedException(String msrId, String message, Throwable t, Object... args)
	{
		super(message, t);
		this.setMsrId(msrId);
		this.setArgs(args);

		StringBuffer msg = new StringBuffer(t == null ? "" : t.getClass().getName());
		msg.append("[");
		msg.append(msrId);
		msg.append("]");
		msg.append(message);

		DynaLogger.error(msg.toString());
	}

	/**
	 * @return the msrId
	 */
	public String getMsrId()
	{
		return this.msrId;
	}

	/**
	 * @param msrId
	 *            the msrId to set
	 */
	public void setMsrId(String msrId)
	{
		this.msrId = msrId;
	}

	/**
	 * @return the args
	 */
	public Object[] getArgs()
	{
		return this.args;
	}

	/**
	 * @param args
	 *            the args to set
	 */
	public void setArgs(Object... args)
	{
		this.args = args;
	}

}
