/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ChangePhaseActivetyType
 * WangLHB 2011-4-20
 */
package dyna.common.systemenum;

/**
 * 项目管理中检索类型枚举
 * 
 * @author WangLHB
 * 
 */
public enum PMSearchTypeEnum
{
	MANAGE("1"), PARTICIPATE("2"), TRACK("3"),RPT_PROJECT("4"),RPT_TASK("5"),RPT_DELIVERABLE("6"),PRT_WORKITEM("7");

	private final String	value;

	private PMSearchTypeEnum(String value)
	{
		this.value = value;
	}

	public String getValue()
	{
		return this.value;
	}

	public static PMSearchTypeEnum getEnum(String value)
	{
		for (PMSearchTypeEnum type : PMSearchTypeEnum.values())
		{
			if (type.getValue().equalsIgnoreCase(value))
			{
				return type;
			}
		}
		return null;
	}
}
