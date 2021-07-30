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
public enum ECPLifecyclePhaseEnum
{
	Created("ID_ENUM_UEC_ECPLIFECYCLEPHASE_CREATED"), Released("ID_ENUM_UEC_ECPLIFECYCLEPHASE_RELEASED"), Canceled(
			"ID_ENUM_UEC_ECPLIFECYCLEPHASE_CANCEL");// 取消

	private String	msrID;

	private ECPLifecyclePhaseEnum(String msrID)
	{
		this.msrID = msrID;
	}

	public String getMsrID()
	{
		return this.msrID;
	}
}
