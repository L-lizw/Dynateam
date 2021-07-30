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
public enum UECActionTypeEnum
{

	Add("ID_ENUM_UEC_ACTIONTYPE_ADD"), // 新增 
	Mod("ID_ENUM_UEC_ACTIONTYPE_MOD"), // 修改
	Del("ID_ENUM_UEC_ACTIONTYPE_DEL"), // 删除
	Replace("ID_ENUM_UEC_ACTIONTYPE_REPLACE"); // 取代


	private String	msrID;
	
	private UECActionTypeEnum(String msrID)
	{
		this.msrID = msrID;
	}

	public String getMsrID()
	{
		return this.msrID;
	}
}
