/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: 工作流程人为活动变迁
 * zhanghj 2010-12-23
 */
package dyna.common.dto.wf;

import java.io.Serializable;

/**
 * @author zhanghj
 *         工作流程人为活动变迁
 */
public class GraphRuntimeTransition implements Serializable
{
	private static final long	serialVersionUID	= -101686639203233939L;
	private String	procGuid			= null;
	private String	fromActivityGuid	= null;
	private String	toActivityGuid		= null;

	/**
	 * @return the procGuid
	 */
	public String getProcGuid()
	{
		return this.procGuid;
	}

	/**
	 * @param procGuid
	 *            the procGuid to set
	 */
	public void setProcGuid(String procGuid)
	{
		this.procGuid = procGuid;
	}

	/**
	 * @return the fromActivityGuid
	 */
	public String getFromActivityGuid()
	{
		return this.fromActivityGuid;
	}

	/**
	 * @param fromActivityGuid
	 *            the fromActivityGuid to set
	 */
	public void setFromActivityGuid(String fromActivityGuid)
	{
		this.fromActivityGuid = fromActivityGuid;
	}

	/**
	 * @return the toActivityGuid
	 */
	public String getToActivityGuid()
	{
		return this.toActivityGuid;
	}

	/**
	 * @param toActivityGuid
	 *            the toActivityGuid to set
	 */
	public void setToActivityGuid(String toActivityGuid)
	{
		this.toActivityGuid = toActivityGuid;
	}

}
