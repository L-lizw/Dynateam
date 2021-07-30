/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ConnToAS
 * Wanglei 2011-9-9
 */
package dyna.common.conf;

import java.io.Serializable;

/**
 * 连接到应用服务器的配置
 * @author Wanglei
 *
 */
public class ConnToASConfig implements Serializable
{
	private static final long	serialVersionUID	= -72146092742838365L;

	private boolean				isDefault			= false;
	private String				Name				= null;
	private String				ipAddress			= null;
	private int					port				= 1299;

	/**
	 * @return the isDefault
	 */
	public boolean isDefault()
	{
		return this.isDefault;
	}

	/**
	 * @param isDefault
	 *            the isDefault to set
	 */
	public void setDefault(boolean isDefault)
	{
		this.isDefault = isDefault;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return this.Name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name)
	{
		this.Name = name;
	}

	/**
	 * @return the ipAddress
	 */
	public String getIpAddress()
	{
		return this.ipAddress;
	}

	/**
	 * @param ipAddress
	 *            the ipAddress to set
	 */
	public void setIpAddress(String ipAddress)
	{
		this.ipAddress = ipAddress;
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
