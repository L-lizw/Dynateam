/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: UpdateWorkItem
 * wangweixia 2013-12-5
 */
package dyna.common.bean.data.ppms;

import java.util.List;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dto.aas.User;
import dyna.common.systemenum.ppms.WorkItemStateEnum;

/**
 * @author wangweixia
 * 
 */
public class UpdateWorkItem extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID	= -8372766083934841454L;

	// 工作项guid
	public static final String	WORKITEMGUID		= "WORKITEMGUID";

	// 更新内容
	public static final String	UPDATECONTENT		= "UPDATECONTENT";

	// 状态
	public static final String	WORKITEMSTATE		= "WORKITEMSTATE";

	// 转发人guidlist
	public List<User>			forwardUserList		= null;

	/**
	 * @return the forwardUserList
	 */
	public List<User> getForwardUserList()
	{
		return forwardUserList;
	}

	/**
	 * @param forwardUserList
	 *            the forwardUserList to set
	 */
	public void setForwardUserList(List<User> forwardUserList)
	{
		this.forwardUserList = forwardUserList;
	}

	/**
	 * @return the workitemguid
	 */
	public String getWorkitemGuid()
	{
		return (String) super.get(WORKITEMGUID);
	}

	public void setWorkitemGuid(String workitemguid)
	{
		super.put(WORKITEMGUID, workitemguid);
	}

	/**
	 * @return the updatecontent
	 */
	public String getUpdateContent()
	{
		return (String) super.get(UPDATECONTENT);
	}

	public void setUpdateContent(String updatecontent)
	{
		super.put(UPDATECONTENT, updatecontent);
	}

	/**
	 * @return the workitemstate
	 */
	public WorkItemStateEnum getWorkitemState()
	{
		return WorkItemStateEnum.valueOf((String) super.get(WORKITEMSTATE));
	}

	public void setWorkitemState(WorkItemStateEnum workitemstate)
	{
		super.put(WORKITEMSTATE, workitemstate.name());
	}
}
