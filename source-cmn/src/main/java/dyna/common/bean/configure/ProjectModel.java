/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ProjectModel
 * WangLHB 2011-4-12
 */

package dyna.common.bean.configure;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import dyna.common.dto.ModelSync;

/**
 * 同步文件项目模型
 * 
 * @author WangLHB
 * 
 */
@XStreamAlias("project")
public class ProjectModel implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -1121422443671536003L;

	@XStreamAlias("syncguid")
	private String				guid				= null;

	@XStreamAlias("name")
	private String				name				= null;

	@XStreamAlias("description")
	private String				description			= null;

	@XStreamAlias("synctime")
	private String				synctime			= null;

	@XStreamAlias("version")
	private String				version				= null;

	@XStreamAlias("remarks")
	private String				remarks				= null;

	@XStreamAlias("omguid")
	private String				omGuid				= null;

	@XStreamAlias("syncip")
	private String				syncIP				= null;

	@XStreamAlias("omname")
	private String				omName				= null;

	@XStreamAlias("syncuser")
	private String				syncUser			= null;

	@XStreamOmitField
	private String				path				= null;

	public ProjectModel()
	{
	}

	public ProjectModel(ModelSync sync)
	{
		this.setDescription(sync.getDescription());
		this.setGuid(sync.getGuid());
		this.setName(sync.getName());
		this.setOmGuid(sync.getOmGuid());
		this.setOmName(sync.getOmName());
		this.setRemarks(sync.getRemarks());
		this.setSyncIP(sync.getSyncIP());
		this.setSyncUser(sync.getSyncName());// TODO duanll
		this.setVersion(sync.getVersion());
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
	 * @return the synctime
	 */
	public String getSynctime()
	{
		return this.synctime;
	}

	/**
	 * @param synctime
	 *            the synctime to set
	 */
	public void setSynctime(String synctime)
	{
		this.synctime = synctime;
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
	 * @param version
	 *            the version to set
	 */
	public void setVersion(String version)
	{
		this.version = version;
	}

	/**
	 * @return the version
	 */
	public String getVersion()
	{
		return this.version;
	}

	/**
	 * @param remarks
	 *            the remarks to set
	 */
	public void setRemarks(String remarks)
	{
		this.remarks = remarks;
	}

	/**
	 * @return the remarks
	 */
	public String getRemarks()
	{
		return this.remarks;
	}

	/**
	 * @param guid
	 *            the guid to set
	 */
	public void setGuid(String guid)
	{
		this.guid = guid;
	}

	/**
	 * @return the guid
	 */
	public String getGuid()
	{
		return this.guid;
	}

	/**
	 * @param path
	 *            the path to set
	 */
	public void setPath(String path)
	{
		this.path = path;
	}

	/**
	 * @return the path
	 */
	public String getPath()
	{
		return this.path;
	}

	/**
	 * @param omGuid
	 *            the omGuid to set
	 */
	public void setOmGuid(String omGuid)
	{
		this.omGuid = omGuid;
	}

	/**
	 * @return the omGuid
	 */
	public String getOmGuid()
	{
		return this.omGuid;
	}

	/**
	 * @param syncIP
	 *            the syncIP to set
	 */
	public void setSyncIP(String syncIP)
	{
		this.syncIP = syncIP;
	}

	/**
	 * @return the syncIP
	 */
	public String getSyncIP()
	{
		return this.syncIP;
	}

	/**
	 * @param omName
	 *            the omName to set
	 */
	public void setOmName(String omName)
	{
		this.omName = omName;
	}

	/**
	 * @return the omName
	 */
	public String getOmName()
	{
		return this.omName;
	}

	/**
	 * @param syncUser
	 *            the syncUser to set
	 */
	public void setSyncUser(String syncUser)
	{
		this.syncUser = syncUser;
	}

	/**
	 * @return the syncUser
	 */
	public String getSyncUser()
	{
		return this.syncUser;
	}

}
