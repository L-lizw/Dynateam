/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Event运行类型枚举
 * Jiagang 2010-7-13
 */
package dyna.common.systemenum;

/**
 * Event运行类型枚举
 * 
 * @author Jiagang
 * 
 */
public enum ScriptRunAtEnum
{
	SERVER("Server"), CLIENT("Client");

	private final String	type;

	@Override
	public String toString()
	{
		return this.type;
	}

	private ScriptRunAtEnum(String type)
	{
		this.type = type;
	}
}
