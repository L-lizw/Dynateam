/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ClassInfoDynaObject
 * WangLHB Feb 1, 2012
 */
package dyna.common.dto.aas;

import java.util.Date;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.util.BooleanUtils;

/**
 * @author WangLHB
 * 
 */
public class UserAgent extends SystemObjectImpl implements SystemObject
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	// 被代理人
	public static final String	PRINCIPALGUID		= "PRINCIPALGUID";
	// 被代理人名字
	private static final String	PRINCIPALNAME		= "PRINCIPALNAME";
	// 代理人
	public static final String	AGENTGUID			= "AGENTGUID";
	// 代理人名字
	private static final String	AGENTNAME			= "AGENTNAME";
	// 开始日期
	public static final String	START_DATE			= "STARTDATE";
	// 结束日期
	public static final String	FINISH_DATE			= "FINISHDATE";
	// 状态
	public static final String	VALID				= "VALID";

	public void setPrincipalGuid(String principalGuid)
	{
		this.put(PRINCIPALGUID, principalGuid);
	}

	public String getPrincipalGuid()
	{
		return (String) this.get(PRINCIPALGUID);
	}

	public String getAgentGuid()
	{
		return (String) this.get(AGENTGUID);
	}

	public void setAgentGuid(String agentGuid)
	{
		this.put(AGENTGUID, agentGuid);
	}

	public Date getStartDate()
	{
		return (Date) this.get(START_DATE);
	}

	public void setStartDate(Date startDate)
	{
		this.put(START_DATE, startDate);
	}

	public Date getFinishDate()
	{
		return (Date) this.get(FINISH_DATE);
	}

	public void setFinishDate(Date finishDate)
	{
		this.put(FINISH_DATE, finishDate);
	}

	public Boolean getValid()
	{
		return BooleanUtils.getBooleanByYN((String) this.get(VALID));
	}

	public void setValid(boolean valid)
	{
		this.put(VALID, BooleanUtils.getBooleanStringYN(valid));
	}

	public String getPrincipalName()
	{
		return (String) this.get(PRINCIPALNAME);
	}

	public void setPrincipalName(String principalName)
	{
		this.put(PRINCIPALNAME, principalName);
	}

	public String getAgentName()
	{
		return (String) this.get(AGENTNAME);
	}

	public void setAgentName(String agentName)
	{
		this.put(AGENTNAME, agentName);
	}
}
