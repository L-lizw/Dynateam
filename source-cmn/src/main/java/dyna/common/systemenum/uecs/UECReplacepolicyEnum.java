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
public enum UECReplacepolicyEnum
{

	MandatoryReplace("ID_ENUM_UEC_REPLACEPOLICY_MANDATORYREPLACE"), // 强制取代
	NaturalReplace("ID_ENUM_UEC_REPLACEPOLICY_NATURALREPLACE"); // 自然取代


	private String	msrID;
	
	private UECReplacepolicyEnum(String msrID)
	{
		this.msrID = msrID;
	}

	public String getMsrID()
	{
		return this.msrID;
	}
}
