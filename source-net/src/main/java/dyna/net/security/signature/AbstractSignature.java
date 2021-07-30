/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: AbstractSignature
 * Wanglei 2010-4-15
 */
package dyna.net.security.signature;

import java.util.Date;

/**
 * @author Wanglei
 *
 */
public abstract class AbstractSignature implements Signature
{
	private String		identifier	= null;
	protected String	ipAddress	= null;
	protected String	credential	= null;
	protected Date		createdTime	= null;

	protected AbstractSignature()
	{
		this.generateIdentifier();
		this.createdTime = new Date();
	}

	protected void generateIdentifier()
	{
		this.identifier = java.util.UUID.randomUUID().toString();
	}

	@Override
	public String getIdentifier()
	{
		return this.identifier;
	}

	/* (non-Javadoc)
	 * @see dyna.net.security.Signature#getIPAddress()
	 */
	@Override
	public String getIPAddress()
	{
		return this.ipAddress;
	}

	/* (non-Javadoc)
	 * @see dyna.net.security.Signature#setIPAddress(java.lang.String)
	 */
	@Override
	public void setIPAddress(String ip)
	{
		this.ipAddress = ip;
	}

	public Date getTimeStamp()
	{
		return this.createdTime;
	}

	public String getCredential()
	{
		return this.credential;
	}

	public void setCredential(String credential)
	{
		if (this.credential == null)
		{
			this.credential = credential;
		}
	}

	@Override
	public String toString()
	{
		return " credential: [" + this.credential + "]" + " ip: [" + this.ipAddress + "]" + " created at: ["
				+ this.createdTime + "]";
	}
}
