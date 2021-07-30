/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ParameterizedException
 * Caogc 2011-01-13
 */
package dyna.common.bean.extra;

import java.io.Serializable;
import java.text.MessageFormat;

/**
 * @author Caogc
 *
 */
public class PromptMessage implements Serializable
{

	private static final long	serialVersionUID	= -9004288806099391831L;

	protected String			msrId				= null;
	protected Object[]			args				= null;

	public PromptMessage(String msrId,Object... args)
	{
		this.setMsrId(msrId);
		this.setArgs(args);
	}

	public String format(String message)
	{
		if (message == null)
		{
			return null;
		}
		return MessageFormat.format(message, this.args);
	}

	/**
	 * @return the args
	 */
	public Object[] getArgs()
	{
		return this.args;
	}

	/**
	 * @return the msrId
	 */
	public String getMsrId()
	{
		return this.msrId;
	}

	/**
	 * @param args
	 *            the args to set
	 */
	public void setArgs(Object... args)
	{
		this.args = args;
	}

	/**
	 * @param msrId
	 *            the msrId to set
	 */
	public void setMsrId(String msrId)
	{
		this.msrId = msrId;
	}

}
