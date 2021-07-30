/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Transition 工作流程活动变迁
 * Wanglei 2010-11-2
 */
package dyna.common.dto.wf;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;

/**
 * 工作流程活动变迁
 * 
 * @author Wanglei
 * 
 */
public class Transition extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID	= 1L;

	public static final String	PROCRT_GUID			= "PROCRTGUID";
	public static final String	FROM_ACT_GUID		= "FROMACTGUID";
	public static final String	TO_ACT_GUID			= "TOACTGUID";
	public static final String	NAME				= "TRANSITIONNAME";

	public String getProcessRuntimeGuid()
	{
		return (String) super.get(PROCRT_GUID);
	}

	public void setProcessRuntimeGuid(String guid)
	{
		super.put(PROCRT_GUID, guid);
	}

	public String getFromActGuid()
	{
		return (String) super.get(FROM_ACT_GUID);
	}

	public void setFromActGuid(String guid)
	{
		super.put(FROM_ACT_GUID, guid);
	}

	public String getToActGuid()
	{
		return (String) super.get(TO_ACT_GUID);
	}

	public void setToActGuid(String guid)
	{
		super.put(TO_ACT_GUID, guid);
	}

	@Override
	public String getName()
	{
		return (String) super.get(NAME);
	}
	
	@Override
	public void setName(String name)
	{
		super.put(NAME, name);
	}
}
