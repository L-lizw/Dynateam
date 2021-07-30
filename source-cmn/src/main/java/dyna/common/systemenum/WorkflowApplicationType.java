/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkflowApplicationType
 * Jiagang 2010-10-8
 */
package dyna.common.systemenum;

/**
 * @author Jiagang
 * 
 */
public enum WorkflowApplicationType
{
	CHANGE_PHASE("20"), CHANGE_STATUS("21"), LOCK("7"), UNLOCK("8"), NOTIFY("9"), ACTION("15");

	private final String	value;

	private WorkflowApplicationType(String value)
	{
		this.value = value;
	}

	public String getValue()
	{
		return this.value;
	}

	public static WorkflowApplicationType getEnum(String value)
	{
		for (WorkflowApplicationType workflowApplicationType : WorkflowApplicationType.values())
		{
			if (workflowApplicationType.getValue().equalsIgnoreCase(value))
			{
				return workflowApplicationType;
			}
		}
		return null;
	}
}
