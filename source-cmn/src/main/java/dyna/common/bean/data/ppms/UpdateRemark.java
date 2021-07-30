/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: UpdateRemark
 * wangweixia 2013-12-5
 */
package dyna.common.bean.data.ppms;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;

/**
 * @author wangweixia
 * 
 */
public class UpdateRemark extends SystemObjectImpl implements SystemObject
{
	private static final long	serialVersionUID		= -3602980090638191414L;

	// 更新工作项或者任务Guid
	public static final String	UPDATEWORKITEMTASKGUID	= "UPDATEWORKITEMTASKGUID";

	// 回复内容
	public static final String	DESCRIPTION				= "DESCRIPTION";

	/**
	 * @return the updateworkitemguid
	 */
	public String getUpdateWorkitemGuid()
	{
		return (String) super.get(UPDATEWORKITEMTASKGUID);
	}

	public void setUpdateWorkitemGuid(String updateworkitemguid)
	{
		super.put(UPDATEWORKITEMTASKGUID, updateworkitemguid);
	}

	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return (String) super.get(DESCRIPTION);
	}

	public void setDescription(String description)
	{
		super.put(DESCRIPTION, description);
	}
}
