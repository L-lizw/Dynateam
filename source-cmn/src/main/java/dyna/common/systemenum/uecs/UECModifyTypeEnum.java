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
public enum UECModifyTypeEnum
{

	BatchAdd("ID_ENUM_UEC_MODIFYTYPE_BATCHADD"), // 批量新增
	BatchMod("ID_ENUM_UEC_MODIFYTYPE_BATCHMOD"), // 批量修改
	BatchDel("ID_ENUM_UEC_MODIFYTYPE_BATCHDEL"), // 批量删除
	BatchReplace("ID_ENUM_UEC_MODIFYTYPE_BATCHREPLACE"); // 批量取代


	private String	msrID;
	
	private UECModifyTypeEnum(String msrID)
	{
		this.msrID = msrID;
	}

	public String getMsrID()
	{
		return this.msrID;
	}
}
