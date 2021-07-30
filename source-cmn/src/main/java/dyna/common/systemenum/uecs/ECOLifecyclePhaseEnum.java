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
public enum ECOLifecyclePhaseEnum
{
	Created("ID_ENUM_UEC_ECOLIFECYCLEPHASE_CREATED"),
	Performing("ID_ENUM_UEC_ECOLIFECYCLEPHASE_PERFORMING"),
	Finished("ID_ENUM_UEC_ECOLIFECYCLEPHASE_FINISHED"),
	Canceled("ID_ENUM_UEC_ECOLIFECYCLEPHASE_CANCELED");

	private String	msrID;
	
	private ECOLifecyclePhaseEnum(String msrID)
	{
		this.msrID = msrID;
	}

	public String getMsrID()
	{
		return this.msrID;
	}
}
