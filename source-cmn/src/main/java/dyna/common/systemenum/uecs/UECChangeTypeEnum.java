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
public enum UECChangeTypeEnum
{

	Attribute("ID_ENUM_UEC_CHANGETYPE_ATTRIBUTE"), // 属性
	Classification("ID_ENUM_UEC_CHANGETYPE_ATTRIBUTE"), // 分类
	ClassificationAttribute("ID_ENUM_UEC_CHANGETYPE_ATTRIBUTE"), // 分类属性
	Relation("ID_ENUM_UEC_CHANGETYPE_RELATION"), // 关联
	BOM("ID_ENUM_UEC_CHANGETYPE_BOM"), // BOM
	File("ID_ENUM_UEC_CHANGETYPE_FILE"), // 文件
	Others("ID_ENUM_UEC_CHANGETYPE_OTHERS"); // 其他

	private String	msrID;
	private UECChangeTypeEnum(String msrID)
	{
		this.msrID = msrID;
	}

	public String getMsrID()
	{
		return this.msrID;
	}
}
