/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: LifecycleGateType
 * Jiagang 2010-9-20
 */
package dyna.common.systemenum;

/**
 * 生命周期的Gate的类型
 * @author Jiagang
 *
 */
public enum LifecycleGateType
{
	WORKFLOW("WorkFlow"), ACTION("Action"), EVENT("Event");

	private String type = null;

	private LifecycleGateType(String type)
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
}
