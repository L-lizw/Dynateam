/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 工作流程附件关联
 * zhanghj 2011-3-30
 */
package dyna.common.dto.wf;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;

/**
 * 工作流程附件关联
 * 
 * @author zhanghj
 * 
 */
public class ProcTrackFile extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID	= 1L;

	public static final String	PROCRT_GUID			= "PROCRTGUID";
	public static final String	INSTANCE_GUID		= "INSTANCEGUID";
	public static final String	CLASS_GUID			= "CLASSGUID";

	public String getProcessRuntimeGuid()
	{
		return (String) super.get(PROCRT_GUID);
	}

	public void setProcessRuntimeGuid(String guid)
	{
		super.put(PROCRT_GUID, guid);
	}

	public String getInstatnceGuid()
	{
		return (String) super.get(INSTANCE_GUID);
	}

	public void setInstatnceGuid(String guid)
	{
		super.put(INSTANCE_GUID, guid);
	}

	public String getInstanceClassGuid()
	{
		return (String) super.get(CLASS_GUID);
	}

	public void setInstanceClassGuid(String guid)
	{
		super.put(CLASS_GUID, guid);
	}
}
