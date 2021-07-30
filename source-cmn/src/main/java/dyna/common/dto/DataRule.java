/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: BOMRule
 * xiasheng 2010-7-23
 */
package dyna.common.dto;

import java.io.Serializable;
import java.util.Date;

import dyna.common.systemenum.SystemStatusEnum;

/**
 * BOM和关系结构查询时的规则
 * 
 * @author Administrator
 * 
 */
public class DataRule implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 7187897819624778083L;

	// 数据状态：wip 最新版；RLS :发布版
	private SystemStatusEnum	systemStatus		= SystemStatusEnum.WIP;

	// 和发布时间进行比较
	private Date				locateTime			= null;

	/**
	 * @return the systemStatus
	 */
	public SystemStatusEnum getSystemStatus()
	{
		return this.systemStatus;
	}

	/**
	 * @param systemStatus
	 *            the systemStatus to set
	 */
	public void setSystemStatus(SystemStatusEnum systemStatus)
	{
		this.systemStatus = systemStatus;
	}

	/**
	 * @return the locateTime
	 */
	public Date getLocateTime()
	{
		return this.locateTime;
	}

	/**
	 * @param locateTime
	 *            如果使用当前时间则不需设置或者设置为null。
	 */
	public void setLocateTime(Date locateTime)
	{
		this.locateTime = locateTime;
	}
}
