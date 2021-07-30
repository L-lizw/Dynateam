/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: NumberingModel
 * Jiagang 2010-8-9
 */

package dyna.data.service.sync.bean;

import java.io.Serializable;

import org.simpleframework.xml.Attribute;

public class TableIndexObject implements Cloneable, Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -1848637068221936853L;

	// 顺序号
	private String				sequence			= null;

	@Attribute(name = "name", required = false)
	private String				name				= null;

	private String				columnName			= null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.modeler.model.DynaModelObject#clone()
	 */
	@Override
	public TableIndexObject clone()
	{
		try
		{
			return (TableIndexObject) super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			return null;
		}
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getSequence()
	{
		return this.sequence;
	}

	public void setSequence(String sequence)
	{
		this.sequence = sequence;
	}

	public String getColumnName()
	{
		return columnName;
	}

	public void setColumnName(String columnName)
	{
		this.columnName = columnName;
	}
}
