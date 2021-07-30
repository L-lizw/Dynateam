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
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * 同步文件模型
 * 
 * @author WangLHB
 * 
 */
@XStreamAlias("sync")
public class SyncModel implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -8329445553113678067L;
	@XStreamImplicit(itemFieldName = "project")
	private List<ProjectModel>	project				= null;

	/**
	 * @param project
	 *            the project to set
	 */
	public void setProject(List<ProjectModel> project)
	{
		this.project = project;
	}

	/**
	 * @return the project
	 */
	public List<ProjectModel> getProject()
	{
		if (this.project == null)
		{
			this.project = new ArrayList<ProjectModel>();
		}
		return this.project;
	}
}
