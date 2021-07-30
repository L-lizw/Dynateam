/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SearchDetail 检索历史记录的明细   用以存储多类查询时的类名信息
 * caogc 2011-03-29
 */
package dyna.common.dto;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;

/**
 * SearchDetail 检索历史记录的明细   用以存储多类查询时的类名信息
 * 
 * @author caogc
 * 
 */
public class SearchDetail extends SystemObjectImpl implements SystemObject
{
	private static final long	serialVersionUID	= 3450462486254546456L;

	public static final String	SEARCHCONDITION_FK		= "SEARCHCONDITIONFK";

	public static final String	CLASS_GUID			= "CLASSGUID";

	/**
	 * @return the classGuid
	 */
	public String getClassGuid()
	{
		return (String) this.get(CLASS_GUID);
	}

	/**
	 * @return the searchConditionFK
	 */
	public String getSearchConditionFK()
	{
		return (String) this.get(SEARCHCONDITION_FK);
	}

	/**
	 * @param classGuid
	 *            the classGuid to set
	 */
	public void setClassGuid(String classGuid)
	{
		this.put(CLASS_GUID, classGuid);
	}

	/**
	 * @param searchConditionFK
	 *            the searchConditionFK to set
	 */
	public void setSearchConditionFK(String searchConditionFK)
	{
		this.put(SEARCHCONDITION_FK, searchConditionFK);
	}
}
