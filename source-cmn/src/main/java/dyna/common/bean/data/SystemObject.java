/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: System Object bean
 * xiasheng Apr 22, 2010
 */
package dyna.common.bean.data;

import java.util.Date;

/**
 * @author xiasheng
 *
 */
public interface SystemObject extends DynaObject // extends Serializable
{
	public static final String	GUID				= "GUID";
	public static final String	NAME				= "NAME";
	public static final String	CREATE_USER_GUID	= "CREATEUSERGUID";
	public static final String	CREATE_USER_NAME	= "CREATEUSERNAME";
	public static final String	UPDATE_USER_GUID	= "UPDATEUSERGUID";
	public static final String	UPDATE_USER_NAME	= "UPDATEUSERNAME";
	public static final String	CREATE_TIME			= "CREATETIME";
	public static final String	UPDATE_TIME			= "UPDATETIME";

	public String getGuid();

	public void setGuid(String guid);

	public String getName();

	public void setName(String name);

	public String getCreateUserGuid();

	public String getUpdateUserGuid();

	public void setCreateUserGuid(String createUserGuid);

	public void setUpdateUserGuid(String updateUserGuid);

	public Date getCreateTime();

	public Date getUpdateTime();

	public void removeNoChanged();
}
