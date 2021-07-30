/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkflowActivityType
 * Jiagang 2010-10-8
 */
package dyna.common.systemenum;

/**
 * 工作流活动类型
 * 
 * @author Jiagang
 * 
 */
public enum WorkflowActivityType
{
	BEGIN("1"), END("2"), MANUAL("3"), APPLICATION("30"), ROUTE("11"), NOTIFY("9"), SUB_PROCESS("10"); // EFFECTIVE("12");
	private final String	value;

	private WorkflowActivityType(String value)
	{
		this.value = value;
	}

	public String getValue()
	{
		return this.value;
	}

	public static WorkflowActivityType getEnum(String value)
	{
		for (WorkflowActivityType workflowActivityType : WorkflowActivityType.values())
		{
			if (workflowActivityType.getValue().equalsIgnoreCase(value))
			{
				return workflowActivityType;
			}
		}
		return null;
	}
}
