/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Transition 工作流程活动变迁条件
 * Wanglei 2010-11-2
 */
package dyna.common.dto.wf;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.wf.TransConditionMapper;
import dyna.common.systemenum.WorkflowTransitionConditionType;

/**
 * 工作流程活动变迁条件
 * 
 * @author Wanglei
 * 
 */
@EntryMapper(TransConditionMapper.class)
public class TransCondition extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID	= 1L;

	public static final String	TRANS_GUID			= "TRANSGUID";
	public static final String	CONDITION_TYPE		= "CDTTYPE";
	public static final String	CONDITION			= "CDT";

	public String getTransGuid()
	{
		return (String) super.get(TRANS_GUID);
	}

	public void setTransGuid(String guid)
	{
		super.put(TRANS_GUID, guid);
	}

	public String getCondition()
	{
		return (String) super.get(CONDITION);
	}

	public void setCondition(String condition)
	{
		super.put(CONDITION, condition);
	}

	public WorkflowTransitionConditionType getConditionType()
	{
		return WorkflowTransitionConditionType.valueOf((String) this.get(CONDITION_TYPE));
	}

	public void setConditionType(WorkflowTransitionConditionType type)
	{
		this.put(CONDITION_TYPE, type.name());
	}

}
