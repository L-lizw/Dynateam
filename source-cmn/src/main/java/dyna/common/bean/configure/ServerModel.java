/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ProjectModel
 * WangLHB 2011-4-12
 */

package dyna.common.bean.configure;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 同步文件项目模型
 * 
 * @author WangLHB
 * 
 */
@XStreamAlias("service-lookup")
public class ServerModel implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -1121422443671536003L;

	@XStreamAlias("name")
	private String	name		= null;

	@XStreamAlias("description")
	private String				description			= null;

	@XStreamAlias("host")
	private String				host				= null;

	@XStreamAlias("port")
	private String				port				= null;


	/**
	 * @return the name
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return this.description;
	}

	/**
	 * @param host
	 *            the host to set
	 */
	public void setHost(String host)
	{
		this.host = host;
	}

	/**
	 * @return the host
	 */
	public String getHost()
	{
		return this.host;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(String port)
	{
		this.port = port;
	}

	/**
	 * @return the port
	 */
	public String getPort()
	{
		return this.port;
	}


}
