/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: LocationTypeEnum
 * caogc 2010-12-08
 */
package dyna.common.systemenum;

/**
 * Location类型枚举
 * 
 * @author caogc
 * 
 */
public enum ObsoleteRuleEnum
{
	// 1，仅能废弃流程废弃 2，用户指定废弃时间 3，需要废弃流程审批通过
	WORKFLOWONLY("1"), OBSOLETETIME("2"), WORKFLOWNEED("3");

	public static ObsoleteRuleEnum typeValueOf(String type)
	{
		for (ObsoleteRuleEnum locationTypeEnum : ObsoleteRuleEnum.values())
		{
			if (type.equals(locationTypeEnum.toString()))
			{
				return locationTypeEnum;
			}
		}

		return null;
	}

	private String	type	= null;

	private ObsoleteRuleEnum(String type)
	{
		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString()
	{
		return this.type;
	}

	public int toValue()
	{
		return Integer.parseInt(this.type);
	}
}
