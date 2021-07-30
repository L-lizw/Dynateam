/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: GenericClient
 * Wanglei 2010-12-8
 */
package dyna.net.connection;

import dyna.common.util.StringUtils;

/**
 * @author Wanglei
 * 
 */
public abstract class GenericClient implements Connection
{

	private String	clientIdentifier	= null;

	public String getClientIdentifier()
	{
		return this.clientIdentifier;
	}

	public synchronized void setClientIdentifier(String identifier)
	{
		if (StringUtils.isNullString(this.clientIdentifier))
		{
			this.clientIdentifier = identifier;
		}
		else
		{
			throw new IllegalArgumentException("clientIdentifier has been set");
		}
	}

	public String getRequestDetail()
	{
		return null;
	}

}
