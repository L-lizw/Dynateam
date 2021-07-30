/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Transition 工作流程活动变迁约束
 * Wanglei 2010-11-2
 */
package dyna.common.dto.wf;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.systemenum.WorkflowRouteModeType;
import dyna.common.systemenum.WorkflowRouteType;

/**
 * 工作流程活动变迁约束
 * 
 * @author Wanglei
 * 
 */
public class TransRestriction extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID	= 1L;

	public static final String	PROCRT_GUID			= "PROCRTGUID";
	public static final String	ACTRT_GUID			= "ACTRTGUID";
	// public static final String TRANS_GUID = "TRANSGUID";
	public static final String	INOUT_TYPE			= "INOUTTYPE";
	public static final String	CONN_TYPE			= "CONNTYPE";

	public WorkflowRouteModeType getConnectionType()
	{
		return WorkflowRouteModeType.valueOf((String) this.get(CONN_TYPE));
	}

	public void setConnectionType(WorkflowRouteModeType type)
	{
		this.put(CONN_TYPE, type.name());
	}

	public WorkflowRouteType getRestrictionType()
	{
		return WorkflowRouteType.valueOf((String) this.get(INOUT_TYPE));
	}

	public void setRestrictionType(WorkflowRouteType type)
	{
		this.put(INOUT_TYPE, type.name());
	}

	public String getProcessRuntimeGuid()
	{
		return (String) super.get(PROCRT_GUID);
	}

	public void setProcessRuntimeGuid(String guid)
	{
		super.put(PROCRT_GUID, guid);
	}

	public String getActRuntimeGuid()
	{
		return (String) super.get(ACTRT_GUID);
	}

	public void setActRuntimeGuid(String guid)
	{
		super.put(ACTRT_GUID, guid);
	}

	// public String getTransGuid()
	// {
	// return (String) super.get(TRANS_GUID);
	// }
	//
	// public void setTransGuid(String guid)
	// {
	// super.put(TRANS_GUID, guid);
	// }
}
