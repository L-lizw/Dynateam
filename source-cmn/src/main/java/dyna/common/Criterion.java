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
public class Criterion implements Serializable
{
	private static final long	serialVersionUID	= -6754183374555979573L;
	public final static String	CON_AND				= "AND";
	public final static String	CON_OR				= "OR";
	public final static String	GROUP_START			= "(";
	public final static String	GROUP_END			= ")";

	private String				key					= null;
	private Object				value				= null;
	private String				conjunction			= null;
	private OperateSignEnum		operateSignEnum		= null;
	private FieldOrignTypeEnum	fieldOrignType		= null;
	private String				fieldtype			= null;
	private String				joinFieldName		= null;
	private String				joinIndex			= null;

	public String getFieldtype()
	{
		return fieldtype;
	}

	public void setFieldtype(String fieldtype)
	{
		this.fieldtype = fieldtype;
	}

	public Criterion(String key, Object value, String conjunction)
	{
		this.settestValue(key, value, null, conjunction, null, null);
	}

	public Criterion(String key, Object value, String conjunction, OperateSignEnum operateSignEnum)
	{
		this.settestValue(key, value, null, conjunction, operateSignEnum, null);
	}

	public Criterion(String key, Object value, FieldOrignTypeEnum fieldOrignType, String conjunction, OperateSignEnum operateSignEnum)
	{
		this.settestValue(key, value, fieldOrignType, conjunction, operateSignEnum, null);
	}

	public Criterion(String key, Object value, String conjunction, OperateSignEnum operateSignEnum, String joinFieldName, String joinIndex)
	{
		this.settestValue(key, value, conjunction, operateSignEnum, null, joinFieldName, joinIndex);
	}

	public Criterion(String key, Object value, FieldOrignTypeEnum fieldOrignType, String conjunction, OperateSignEnum operateSignEnum, String fieldtype)
	{
		this.settestValue(key, value, fieldOrignType, conjunction, operateSignEnum, fieldtype);
	}

	private void settestValue(String key, Object value, FieldOrignTypeEnum fieldOrignType, String conjunction, OperateSignEnum operateSignEnum, String fieldtype)
	{
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
		this.fieldOrignType = fieldOrignType;
		this.fieldtype = fieldtype;
	}

	private void settestValue(String key, Object value, String conjunction, OperateSignEnum operateSignEnum, String fieldtype, String joinFieldName, String joinIndex)
	{
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
		this.fieldOrignType = FieldOrignTypeEnum.CLASS;
		this.fieldtype = fieldtype;
		this.joinFieldName = joinFieldName;
		this.joinIndex = joinIndex;
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

	public void setConjunction(String conjunction)
	{
		this.conjunction = conjunction;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	/**
	 * @return the joinFieldName
	 */
	public String getJoinFieldName()
	{
		return this.joinFieldName;
	}

	public void setJoinFieldName(String joinFieldName)
	{
		this.joinFieldName = joinFieldName;
	}

	/**
	 * @return the joinIndex
	 */
	public String getJoinIndex()
	{
		return this.joinIndex;
	}

	public void setJoinIndex(String joinIndex)
	{
		this.joinIndex = joinIndex;
	}

	public void setOperateSignEnum(OperateSignEnum operateSignEnum)
	{
		this.operateSignEnum = operateSignEnum;
	}

	public void setValue(Object value)
	{
		this.value = value;
	}

	public FieldOrignTypeEnum getFieldOrignType()
	{
		if (this.fieldOrignType == null)
		{
			return FieldOrignTypeEnum.CLASS;
		}
		return fieldOrignType;
	}

	public void setFieldOrignType(FieldOrignTypeEnum fieldOrignType)
	{
		this.fieldOrignType = fieldOrignType;
	}
}
