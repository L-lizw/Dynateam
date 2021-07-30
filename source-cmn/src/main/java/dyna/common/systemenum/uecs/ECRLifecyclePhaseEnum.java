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
public enum ECRLifecyclePhaseEnum
{
	Created("ID_ENUM_UEC_ECRLIFECYCLEPHASE_CREATED"),
	Reviewing("ID_ENUM_UEC_ECRLIFECYCLEPHASE_REVIEWING"),
	Released("ID_ENUM_UEC_ECRLIFECYCLEPHASE_RELEASED"),
	Canceled("ID_ENUM_UEC_ECRLIFECYCLEPHASE_CANCELED");

	private String	msrID;
	
	private ECRLifecyclePhaseEnum(String msrID)
	{
		this.msrID = msrID;
	}

	public String getMsrID()
	{
		return this.msrID;
	}
}
