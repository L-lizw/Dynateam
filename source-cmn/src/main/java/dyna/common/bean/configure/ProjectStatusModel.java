/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SyncModel
 * WangLHB 2011-4-12
 */

package dyna.common.bean.configure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * 同步文件模型
 * 
 * @author WangLHB
 * 
 */
@XStreamAlias("project-path")
public class ProjectStatusModel implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -8329445553113678067L;
	@XStreamImplicit(itemFieldName = "path")
	private List<String>		projectPathList		= null;

	@XStreamAlias("activity")
	@XStreamAsAttribute
	private String				activity			= null;

	/**
	 * @return the projectStatusList
	 */
	public List<String> getProjectStatusList()
	{
		if (this.projectPathList == null)
		{
			this.projectPathList = new ArrayList<String>();
		}
		return this.projectPathList;
	}

	/**
	 * @param activity
	 *            the activity to set
	 */
	public void setActivity(String activity)
	{
		this.activity = activity;
	}

	/**
	 * @return the activity
	 */
	public String getActivity()
	{
		return this.activity;
	}
}
