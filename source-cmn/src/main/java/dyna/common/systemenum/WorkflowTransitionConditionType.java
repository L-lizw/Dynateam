/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkflowTransitionConditionType
 * Jiagang 2010-10-19
 */
package dyna.common.systemenum;


/**
 * @author Jiagang
 *
 */
public enum WorkflowTransitionConditionType
{
	REGULAR("Regular"), ACCEPT("Accept"), REJECT("Reject");

	private final String	type;

	@Override
	public String toString()
	{
		return this.type;
	}

	private WorkflowTransitionConditionType(String type)
	{
		this.type = type;
	}
}
