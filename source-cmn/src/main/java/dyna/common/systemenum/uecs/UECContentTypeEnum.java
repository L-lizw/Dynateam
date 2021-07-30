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
public enum UECContentTypeEnum
{

	ModifyAttribute("ID_ENUM_UEC_CONTENTTYPE_MODIFYATTRIBUTE"), // 修改属性
	ModifyRelation("ID_ENUM_UEC_CONTENTTYPE_MODIFYRELATION"), // 修改关联
	ModifyFile("ID_ENUM_UEC_CONTENTTYPE_MODIFYFILE"), // 修改文件
	ModifyBOM("ID_ENUM_UEC_CONTENTTYPE_MODIFYBOM"), // 修改BOM
	Others("ID_ENUM_UEC_CONTENTTYPE_OTHERS"); // 其他


	private String	msrID;
	
	private UECContentTypeEnum(String msrID)
	{
		this.msrID = msrID;
	}

	public String getMsrID()
	{
		return this.msrID;
	}
}
