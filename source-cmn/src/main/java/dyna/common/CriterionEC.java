/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Criterion
 * xiasheng May 17, 2010
 */
package dyna.common;

import java.io.Serializable;

import dyna.common.systemenum.OperateSignEnum;

/**
 * 用于SearchCondition保存过滤条件
 * 
 * @author xiasheng
 */
public class CriterionEC implements Serializable
{
	private static final long	serialVersionUID	= -6754183374555979573L;
	public final static String	CON_AND				= "AND";
	public final static String	CON_OR				= "OR";
	public final static String	GROUP_START			= "(";
	public final static String	GROUP_END			= ")";

	private String				className			= null;
	private String				key					= null;
	private Object				value				= null;
	private String				conjunction			= null;
	private OperateSignEnum		operateSignEnum		= null;

	public CriterionEC(String className, String key, Object value, String conjunction)
	{
		this.className = className;
		this.key = key;
		if (value != null && value instanceof String)
		{
			value = ((String) value).trim();
		}

		if (value == null)
		{
			value = SearchCondition.VALUE_NULL;
		}

		this.value = value;
		this.conjunction = conjunction;
	}

	public CriterionEC(String className, String key, Object value, String conjunction, OperateSignEnum operateSignEnum)
	{
		this.className = className;
		this.key = key;
		if (value != null && value instanceof String)
		{
			value = ((String) value).trim();
		}

		if (value == null)
		{
			value = SearchCondition.VALUE_NULL;
		}

		this.value = value;
		this.conjunction = conjunction;
		this.operateSignEnum = operateSignEnum;
	}

	/**
	 * @return the conjunction
	 */
	public String getConjunction()
	{
		return this.conjunction;
	}

	/**
	 * @return the key
	 */
	public String getKey()
	{
		return this.key;
	}

	/**
	 * @return the operateSignEnum
	 */
	public OperateSignEnum getOperateSignEnum()
	{
		return this.operateSignEnum;
	}

	/**
	 * @return the value
	 */
	public Object getValue()
	{
		return this.value;
	}

	public Object getClassName()
	{
		return this.className;
	}

	public void setClassName(String className)
	{
		this.className = className;
	}

	public void setConjunction(String conjunction)
	{
		this.conjunction = conjunction;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	public void setOperateSignEnum(OperateSignEnum operateSignEnum)
	{
		this.operateSignEnum = operateSignEnum;
	}

	public void setValue(Object value)
	{
		this.value = value;
	}
}
