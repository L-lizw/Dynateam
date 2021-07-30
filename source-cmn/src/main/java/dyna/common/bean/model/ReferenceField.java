/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: RefField
 * WangLHB Jun 26, 2011
 */
package dyna.common.bean.model;

import org.simpleframework.xml.Attribute;

import java.io.Serializable;

/**
 * @author WangLHB
 *
 */

public class ReferenceField implements Cloneable, Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 3068996261210121742L;

	@Attribute(name = "itemname", required = false)
	private String				itemName			= null;

	@Attribute(name = "fieldname", required = false)
	private String				fieldName			= null;

	private String				codeItemGuid		= null;

	/**
	 * @return the itemName
	 */
	public String getItemName()
	{
		return this.itemName;
	}

	/**
	 * @param itemName
	 *            the itemName to set
	 */
	public void setItemName(String itemName)
	{
		this.itemName = itemName;
	}

	/**
	 * @return the fieldName
	 */
	public String getFieldName()
	{
		return this.fieldName;
	}

	/**
	 * @param fieldName
	 *            the fieldName to set
	 */
	public void setFieldName(String fieldName)
	{
		this.fieldName = fieldName;
	}

	public void setCodeItemGuid(String guid)
	{
		this.codeItemGuid = guid;
	}

	public String getCodeItemGuid()
	{
		return codeItemGuid;
	}

}
