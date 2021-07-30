/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Preference
 * Caogc 2010-10-08
 */
package dyna.common.dto;

import java.util.List;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;

/**
 * 
 * @author Caogc
 * 
 */
public class Preference extends SystemObjectImpl implements SystemObject
{

	private static final long		serialVersionUID		= -5466625404445108685L;

	public static final String		GUID					= "GUID";
	public static final String		USER_GUID				= "USERGUID";
	// 01:max tab count;02:max history;03:row count;04:common lib;05:common class;06:是否接收email
	public static final String		TYPE					= "CONFIGTYPE";
	public List<PreferenceDetail>	preferenceDetailList	= null;

	/**
	 * @return the GUID
	 */
	@Override
	public String getGuid()
	{
		return (String) this.get(GUID);
	}

	/**
	 * @return the PreferenceDetailList
	 */
	public List<PreferenceDetail> getPreferenceDetailList()
	{
		return this.preferenceDetailList;
	}

	/**
	 * @return the type
	 */
	public String getType()
	{
		return (String) this.get(TYPE);
	}

	/**
	 * @return the UserGuid
	 */
	public String getUserGuid()
	{
		return (String) this.get(USER_GUID);
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
	 * @param PreferenceDetailList
	 *            the PreferenceDetailList to set
	 */
	public void setPreferenceDetailList(List<PreferenceDetail> preferenceDetailList)
	{
		this.preferenceDetailList = preferenceDetailList;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type)
	{
		this.put(TYPE, type);
	}

	/**
	 * @param UserGuid
	 *            the UserGuid to set
	 */
	public void setUserGuid(String userGuid)
	{
		this.put(USER_GUID, userGuid);
	}
}
