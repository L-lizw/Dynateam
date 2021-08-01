/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: MySchedule 我的日程
 * caogc 2010-8-20
 */
package dyna.common.dto;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.MyScheduleMapper;
import dyna.common.util.DateFormat;

import java.util.Date;

/**
 * @author caogc
 * 
 */
@EntryMapper(MyScheduleMapper.class)
public class MySchedule extends SystemObjectImpl implements SystemObject
{
	private static final long	serialVersionUID	= -7367964919474962375L;
	public static final String	GUID				= "GUID";
	public static final String	OWNER_USER_GUID		= "OWNERUSERGUID";
	public static final String	OWNER_USER_NAME		= "OWNERUSERNAME";
	public static final String	SCHEDULE_TITLE		= "SCHEDULETITLE";
	public static final String	SCHEDULE_CONTENT	= "SCHEDULECONTENT";
	public static final String	SCHEDULE_DATE		= "SCHEDULEDATE";
	public static final String	CREATE_USER			= "CREATEUSER";
	public static final String	UPDATE_USER			= "UPDATEUSER";

	/**
	 * @return the createUserGuid
	 */
	@Override
	public String getCreateUserGuid()
	{
		return (String) this.get(CREATE_USER);
	}

	/**
	 * @return the createUserName
	 */
	public String getCreateUserName()
	{
		return (String) this.get(CREATE_USER_NAME);
	}

	/**
	 * @return the GUID
	 */
	@Override
	public String getGuid()
	{
		return (String) this.get(GUID);
	}

	/**
	 * @return the ownerUserGuid
	 */
	public String getOwnerUserGuid()
	{
		return (String) this.get(OWNER_USER_GUID);
	}

	/**
	 * @return the ownerUserName
	 */
	public String getOwnerUserName()
	{
		return (String) this.get(OWNER_USER_NAME);
	}

	/**
	 * @return the scheduleContent
	 */
	public String getScheduleContent()
	{
		return (String) this.get(SCHEDULE_CONTENT);
	}

	/**
	 * @return the scheduleDate
	 */
	public Date getScheduleDate()
	{
		return DateFormat.parse((String) this.get(SCHEDULE_DATE));
	}

	/**
	 * @return the scheduleTitle
	 */
	public String getScheduleTitle()
	{
		return (String) this.get(SCHEDULE_TITLE);
	}

	/**
	 * @return the updateUserGuid
	 */
	@Override
	public String getUpdateUserGuid()
	{
		return (String) this.get(UPDATE_USER);
	}

	/**
	 * @return the updateUserName
	 */
	public String getUpdateUserName()
	{
		return (String) this.get(UPDATE_USER_NAME);
	}

	/**
	 * @param createUserGuid
	 *            the createUserGuid to set
	 */
	@Override
	public void setCreateUserGuid(String createUserGuid)
	{
		this.put(CREATE_USER, createUserGuid);
	}

	/**
	 * @param createUserName
	 *            the createUserName to set
	 */
	public void setCreateUserName(String createUserName)
	{
		this.put(CREATE_USER_NAME, createUserName);
	}

	/**
	 * @param GUID
	 *            the GUID to set
	 */
	@Override
	public void setGuid(String guid)
	{
		this.put(GUID, guid);
	}

	/**
	 * @param ownerUserGuid
	 *            the ownerUserGuid to set
	 */
	public void setOwnerUserGuid(String ownerUserGuid)
	{
		this.put(OWNER_USER_GUID, ownerUserGuid);
	}

	/**
	 * @param ownerUserName
	 *            the ownerUserName to set
	 */
	public void setOwnerUserName(String ownerUserName)
	{
		this.put(OWNER_USER_NAME, ownerUserName);
	}

	/**
	 * @param scheduleContent
	 *            the scheduleContent to set
	 */
	public void setScheduleContent(String scheduleContent)
	{
		this.put(SCHEDULE_CONTENT, scheduleContent);
	}


	/**
	 * @param scheduleDate
	 *            the scheduleDate to set
	 */
	public void setScheduleDate(Date scheduleDate)
	{
		this.put(SCHEDULE_DATE, DateFormat.formatYMD(scheduleDate));
	}

	/**
	 * @param scheduleTitle
	 *            the scheduleTitle to set
	 */
	public void setScheduleTitle(String scheduleTitle)
	{
		this.put(SCHEDULE_TITLE, scheduleTitle);
	}

	/**
	 * @param updateUserGuid
	 *            the updateUserGuid to set
	 */
	@Override
	public void setUpdateUserGuid(String updateUserGuid)
	{
		this.put(UPDATE_USER, updateUserGuid);
	}

	/**
	 * @param updateUserName
	 *            the updateUserName to set
	 */
	public void setUpdateUserName(String updateUserName)
	{
		this.put(UPDATE_USER_NAME, updateUserName);
	}
}
