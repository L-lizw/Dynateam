/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ERPWFTableNameEnum
 * wangweixia 2012-4-11
 */
package dyna.common.systemenum;

/**
 * 用于WF中各个表名枚举
 * 
 * @author wangweixia
 * 
 */
public enum ERPWFTableNameEnum
{
	ITEM("INVMB"), // WF中Item表名
	IBHEAD("BOMMC"), // WF中Item/BOM的Head表名
	IBBODY("BOMMD"), // WF中Item/BOM的Body表名
	IBEHEAD("BOMTP"), // WF中Item/BOM/ECN的Head表名
	IBEBODY("BOMTQ"), // WF中Item/BOM/ECN的Body表名
	IBESUBBODY("BOMTR");// WF中Item/BOM/ECN的SubBody表名

	private String	value;

	private ERPWFTableNameEnum(String value)
	{
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value)
	{
		this.value = value;
	}

}
