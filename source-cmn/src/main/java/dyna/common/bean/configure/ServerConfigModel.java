/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SyncModel
 * WangLHB 2011-4-12
 */

package dyna.common.bean.configure;

import java.io.Serializable;
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
@XStreamAlias("service")
public class ServerConfigModel implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -8329445553113678067L;
	@XStreamImplicit(itemFieldName = "service-lookup")
	private List<ServerModel>	serverModel			= null;

	@XStreamAlias("activity")
	@XStreamAsAttribute
	private   String	activity = null;



	/**
	 * @param serverModel
	 *            the serverModel to set
	 */
	public void setServerModel(List<ServerModel> serverModel)
	{
		this.serverModel = serverModel;
	}

	/**
	 * @return the serverModel
	 */
	public List<ServerModel> getServerModel()
	{
		return this.serverModel;
	}

	/**
	 * @param activity the activity to set
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
