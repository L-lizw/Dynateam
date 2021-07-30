/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ACLItem
 * Wanglei 2010-7-30
 */
package dyna.common.dto;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;

/**
 * @author duanll
 * 
 */
public class DBVersion extends SystemObjectImpl implements SystemObject
{
	private static final long	serialVersionUID			= -5256245302334123561L;

	public static final String	VER							= "VER";
	public static final String	GUARDSERVICEID				= "GUARDSERVICEID";
	public static final String	TEMPLICENSEEFFECTIVETIME	= "TEMPLICENSEEFFECTIVETIME";

	public String getVersion()
	{
		return (String) this.get(VER);
	}

	public String getGuardServiceID()
	{
		return (String) this.get(GUARDSERVICEID);
	}

	public void setGuardServiceID(String guardServiceID)
	{
		this.put(GUARDSERVICEID, guardServiceID);
	}

	public String getTempLicenseEffectiveTime()
	{
		return (String) this.get(TEMPLICENSEEFFECTIVETIME);
	}

	public void setTempLicenseEffectiveTime(String tempLicenseEffectiveTime)
	{
		this.put(TEMPLICENSEEFFECTIVETIME, tempLicenseEffectiveTime);
	}
}
