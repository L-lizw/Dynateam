/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: AccessConditionEnum
 * Wanglei 2010-7-30
 */
package dyna.common.systemenum.uecs;

/**
 * @author Wanglei
 * 
 */
public enum UECAnaysisSourceTypeEnum
{
	BOMCOMPOSITE("ID_ENUM_UEC_ANAYSISSOURCETYPE_BOMCOMPOSITE"), // BOM组成
	BOMUSED("ID_ENUM_UEC_ANAYSISSOURCETYPE_BOMUSED"), // BOM用在
	RELATION("ID_ENUM_UEC_ANAYSISSOURCETYPE_RELATION"); // 关联

	private String	msrID;
	
	private UECAnaysisSourceTypeEnum(String msrID)
	{
		this.msrID = msrID;
	}

	public String getMsrID()
	{
		return this.msrID;
	}
}
