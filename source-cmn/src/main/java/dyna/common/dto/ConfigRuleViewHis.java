/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ConfigRuleViewHis
 * 			 该配置用于记录"最近打开"的最大记录数
 * caogc 2011-2-15
 */
package dyna.common.dto;

import java.util.Date;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;

/**
 * ConfigRuleViewHis
 * 该配置用于记录"最近打开"的最大记录数
 * 
 * @author caogc
 * 
 */
public class ConfigRuleViewHis extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID	= -5168560756324214395L;
	public static final String	GUID				= "GUID";
	public static final String	RULE				= "RULE";
	public static final String	NAME				= "NAME";
	public static final String	CREATE_TIME			= "CREATETIME";
	public static final String	UPDATE_TIME			= "UPDATETIME";

	/**
	 * @return the CreateTime
	 */
	@Override
	public Date getCreateTime()
	{
		return (Date) this.get(CREATE_TIME);
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
	 * @return the Name
	 */
	@Override
	public String getName()
	{
		return (String) this.get(NAME);
	}

	/**
	 * @return the rule
	 */
	public String getRule()
	{
		return (String) this.get(RULE);
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
	 * @param CreateTime
	 *            the CreateTime to set
	 */
	public void setCreateTime(Date createTime)
	{
		this.put(CREATE_TIME, createTime);
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
	 * @param Name
	 *            the Name to set
	 */
	@Override
	public void setName(String name)
	{
		this.put(NAME, name);
	}

	/**
	 * @param rule
	 *            the rule to set
	 */
	public void setRule(String rule)
	{
		this.put(RULE, rule);
	}

	/**
	 * @param UpdateTime
	 *            the UpdateTime to set
	 */
	public void setUpdateTime(Date updateTime)
	{
		this.put(UPDATE_TIME, updateTime);
	}

}
