/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SaQueryPreferenceMaster
 * duanll 2013-1-17
 */
package dyna.common.dto;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.SaCustomColumnsPreferenceMapper;

import java.util.List;

/**
 * @author niumr
 *         表格自定义列
 */
@EntryMapper(SaCustomColumnsPreferenceMapper.class)
public class SaCustomColumnsPreference extends SystemObjectImpl implements SystemObject
{
	private static final long						serialVersionUID		= -5823574809902566778L;

	/**
	 * 用户唯一标识
	 */
	public static final String						USERGUID				= "USERGUID";

	/**
	 * 表格的标识（是哪一个表格）
	 */
	public static final String						TABLETYPE				= "TABLETYPE";

	public static final String						CLASSIFICATIONFK		= "CLASSIFICATIONFK";

	public static final String						CLASSFK					= "CLASSFK";

	/**
	 * 表格自定义列明细字段
	 */
	private List<SaCustomColumnsPreferenceDetail>	preferenceDetailList	= null;

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
	 * @return the preferenceDetailList
	 */
	public List<SaCustomColumnsPreferenceDetail> getPreferenceDetailList()
	{
		return preferenceDetailList;
	}

	/**
	 * @param preferenceDetailList
	 *            the preferenceDetailList to set
	 */
	public void setPreferenceDetailList(List<SaCustomColumnsPreferenceDetail> preferenceDetailList)
	{
		this.preferenceDetailList = preferenceDetailList;
	}

	/**
	 * @return the tableType
	 */
	public String getTableType()
	{
		return (String) super.get(TABLETYPE);
	}

	/**
	 * @param tableType
	 *            the tableType to set
	 */
	public void setTableType(String tableType)
	{
		super.put(TABLETYPE, tableType);
	}

	/**
	 * @return the classificationfk
	 */
	public String getClassificationFK()
	{
		return (String) super.get(CLASSIFICATIONFK);
	}

	/**
	 * @param classificationfk
	 *            the classificationfk to set
	 */
	public void setClassificationFK(String classificationfk)
	{
		super.put(CLASSIFICATIONFK, classificationfk);
	}

	/**
	 * @return the classfk
	 */
	public String getClassFK()
	{
		return (String) super.get(CLASSFK);
	}

	/**
	 * @param classfk
	 *            the classfk to set
	 */
	public void setClassFK(String classfk)
	{
		super.put(CLASSFK, classfk);
	}

}
