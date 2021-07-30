/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ProcTrackComm
 * zhanghj 2011-3-30
 */
package dyna.common.dto.wf;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;

/**
 * 流程常用意见
 * 
 * @author zhanghj
 * 
 */
public class ProcTrackComm extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID	= 1L;

	public static final String	COMMENTS			= "COMMENTS";
	public static final String	USER_GUID			= "USERGUID";
	public static final String	CREATE_USER_NAME	= "CREATEUSERNAME";
	public static final String	UPDATE_USER_NAME	= "UPDATEUSERNAME";

	public String getComments()
	{
		return (String) super.get(COMMENTS);
	}

	public void setComments(String comments)
	{
		super.put(COMMENTS, comments);
	}

	public String getCreateUserName()
	{
		return (String) super.get(CREATE_USER_NAME);
	}

	public void setCreateUserName(String createUserName)
	{
		super.put(CREATE_USER_NAME, createUserName);
	}

	public String getUpdateUserName()
	{
		return (String) super.get(UPDATE_USER_NAME);
	}

	public void setUpdateUserName(String updateUserName)
	{
		super.put(UPDATE_USER_NAME, updateUserName);
	}

	public String getUserGuid()
	{
		return (String) super.get(USER_GUID);
	}

	public void setUserGuid(String userGuid)
	{
		super.put(USER_GUID, userGuid);
	}
}
