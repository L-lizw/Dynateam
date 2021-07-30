/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ProcessStatusEnum
 * Wanglei 2010-11-2
 */
package dyna.common.systemenum;

/**
 * @author Wanglei
 * 
 */
public enum ProcessStatusEnum
{
	/**
	 * 已创建
	 */
	CREATED("ID_WF_STA_CRT"),

	/**
	 * 运行中
	 */
	RUNNING("ID_WF_STA_RUN"),

	/**
	 * 等待中
	 */
	ONHOLD("ID_WF_STA_HLD"),

	/**
	 * 已关闭
	 */
	CLOSED("ID_WF_STA_CLS"),

	/**
	 * 已撤销
	 */
	CANCEL("ID_WF_STA_CAL"),

	/**
	 * 已废弃
	 */
	OBSOLETE("ID_WF_STA_OBE");

	private String	msrId	= null;

	private ProcessStatusEnum(String msrId)
	{
		this.msrId = msrId;
	}

	public String getMsrId()
	{
		return this.msrId;
	}
}
