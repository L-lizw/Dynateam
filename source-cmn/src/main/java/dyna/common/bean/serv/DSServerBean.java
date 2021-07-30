/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: FTPBean ftp定义bean
 * Wanglei 2010-10-20
 */
package dyna.common.bean.serv;

import java.io.Serializable;

/**
 * ftp定义bean
 * 
 * @author Wanglei
 * 
 */
public class DSServerBean implements Serializable
{

	private static final long	serialVersionUID	= -2790282367379331528L;
	private String	id		= null;
	private String	ip		= null;
	private int		port	= 21;

	/**
	 * @return the id
	 */
	public String getId()
	{
		return this.id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 * @return the ip
	 */
	public String getIp()
	{
		return this.ip;
	}

	/**
	 * @param ip
	 *            the ip to set
	 */
	public void setIp(String ip)
	{
		this.ip = ip;
	}

	/**
	 * @return the port
	 */
	public int getPort()
	{
		return this.port;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(int port)
	{
		this.port = port;
	}

}
