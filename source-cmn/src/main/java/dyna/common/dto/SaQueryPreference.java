/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SaQueryPreferenceMaster
 * duanll 2013-1-17
 */
package dyna.common.dto;

import java.util.List;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;

/**
 * @author duanll
 *         高级搜索自定义栏位
 */
public class SaQueryPreference extends SystemObjectImpl implements SystemObject
{
	private static final long				serialVersionUID		= -5823574809902566778L;

	/**
	 * 用户唯一标识
	 */
	public static final String				USERGUID				= "USERGUID";

	/**
	 * 类型标识
	 */
	public static final String				CLASSFK					= "CLASSFK";

	/**
	 * 高级搜索自定义栏位明细字段
	 */
	private List<SaQueryPreferenceDetail>	preferenceDetailList	= null;

	/**
	 * @return the userGuid
	 */
	public String getUserGuid()
	{
		return (String) super.get(USERGUID);
	}

	/**
	 * @param userGuid
	 *            the userGuid to set
	 */
	public void setUserGuid(String userGuid)
	{
		super.put(USERGUID, userGuid);
	}
	
	/**
	 * @return the classfk
	 */
	public String getClassfk()
	{
		return (String) super.get(CLASSFK);
	}

	/**
	 * @param classfk
	 *            the classfk to set
	 */
	public void setClassfk(String classfk)
	{
		super.put(CLASSFK, classfk);
	}

	/**
	 * @return the preferenceDetailList
	 */
	public List<SaQueryPreferenceDetail> getPreferenceDetailList()
	{
		return preferenceDetailList;
	}

	/**
	 * @param preferenceDetailList
	 *            the preferenceDetailList to set
	 */
	public void setPreferenceDetailList(List<SaQueryPreferenceDetail> preferenceDetailList)
	{
		this.preferenceDetailList = preferenceDetailList;
	}
}
