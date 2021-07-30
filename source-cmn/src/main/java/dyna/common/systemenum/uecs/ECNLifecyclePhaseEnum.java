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
public enum ECNLifecyclePhaseEnum
{
	Created("ID_ENUM_UEC_ECNLIFECYCLEPHASE_CREATED"),
	Reviewing("ID_ENUM_UEC_ECNLIFECYCLEPHASE_REVIEWING"),
	Performing("ID_ENUM_UEC_ECNLIFECYCLEPHASE_PERFORMING"),
	Released("ID_ENUM_UEC_ECNLIFECYCLEPHASE_RELEASED"),
	Closed("ID_ENUM_UEC_ECNLIFECYCLEPHASE_CLOSED"),
	Canceled("ID_ENUM_UEC_ECNLIFECYCLEPHASE_CANCELED");

	private String	msrID;
	
	private ECNLifecyclePhaseEnum(String msrID)
	{
		this.msrID = msrID;
	}

	public String getMsrID()
	{
		return this.msrID;
	}
}
