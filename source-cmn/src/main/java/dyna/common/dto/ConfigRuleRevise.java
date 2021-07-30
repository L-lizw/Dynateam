/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ConfigRuleRevise 固定版序规则
 * caogc 2010-11-14
 */
package dyna.common.dto;

import java.util.Date;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.systemenum.ReviseSeriesRuleEnum;

/**
 * ConfigRuleRevise 固定版序规则
 * 
 * @author caogc
 * 
 */
public class ConfigRuleRevise extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID	= -5168560756324214395L;
	public static final String	GUID				= "GUID";
	// 类别：参照枚举ReviseSeriesRuleEnum
	public static final String	TYPE				= "REVISETYPE";
	// 当类别为枚举时有值 存的是用户枚举出的版序的值 用,号分隔
	public static final String	VALUE				= "REVISEVALUE";

	/**
	 * @return the GUID
	 */
	@Override
	public String getGuid()
	{
		return (String) this.get(GUID);
	}

	/**
	 * @return the type
	 */
	public ReviseSeriesRuleEnum getType()
	{
		return ReviseSeriesRuleEnum.typeValueOf((String) this.get(TYPE));
	}

	/**
	 * @return the UpdateTime
	 */
	@Override
	public Date getUpdateTime()
	{
		return (Date) this.get(UPDATE_TIME);
	}

	/**
	 * @return the value
	 */
	public String getValue()
	{
		return (String) this.get(VALUE);
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
	 * @param type
	 *            the type to set
	 */
	public void setType(ReviseSeriesRuleEnum type)
	{
		this.put(TYPE, type.toString());
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value)
	{
		this.put(VALUE, value);
	}

}
