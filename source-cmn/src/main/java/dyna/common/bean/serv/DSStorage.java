/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DSStorage 文件存储bean
 * Wanglei 2010-9-28
 */
package dyna.common.bean.serv;

import java.io.Serializable;

/**
 * 文件存储bean
 * 
 * @author Wanglei
 * 
 */
public class DSStorage implements Serializable
{

	private static final long	serialVersionUID	= -3500487957392681556L;

	private String				id					= null;
	private String				name				= null;
	private String				dsserverId			= null;
	private String				path				= null;

	/**
	 * @return the dsserverId
	 */
	public String getDsserverId()
	{
		return this.dsserverId;
	}

	/**
	 * @param dsserverId
	 *            the dsserverId to set
	 */
	public void setDsserverId(String dsserverId)
	{
		this.dsserverId = dsserverId;
	}

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
	 * @return the path
	 */
	public String getPath()
	{
		return this.path;
	}

	/**
	 * @param path
	 *            the path to set
	 */
	public void setPath(String path)
	{
		this.path = path;
	}


}
