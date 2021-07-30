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
public enum UECTypeEnum
{

	NORMALECTYPE("ID_ENUM_UEC_ECTYPE_NORMALEC"), // 一般变更
	BATCHECTYPE("ID_ENUM_UEC_ECTYPE_BATCHEC"); // 批量变更

	private String	msrID;
	
	private UECTypeEnum(String msrID)
	{
		this.msrID = msrID;
	}

	public String getMsrID()
	{
		return this.msrID;
	}
}
