/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: MailMessageType
 * wangweixia 2014-7-17
 */
package dyna.common.systemenum;

import java.util.ArrayList;
import java.util.List;

import dyna.common.util.StringUtils;

/**
 * @author wangweixia
 * 
 */
public enum MailMessageType
{
	PROJECTNOTIFY("ID_SYS_MAILMESSAGETYPE_PROJECTNOTIFY", "1"), // 项目通知
	TASKNOTIFY("ID_SYS_MAILMESSAGETYPE_TASKNOTIFY", "2"), // 任务通知
	WORKFLOWNOTIFY("ID_SYS_MAILMESSAGETYPE_WORKFLOWNOTIFY", "3"), // 流程通知
	TRANSFERCHECKOUT("ID_SYS_MAILMESSAGETYPE_TRANSFERCHECKOUT", "4"), // 移交检出通知
	OWNERUPDATE("ID_SYS_MAILMESSAGETYPE_OWNERUPDATE", "5"), // 所有者修改通知
	ERPNOTIFY("ID_SYS_MAILMESSAGETYPE_ERPNOTIFY", "6"), // ERP集成通知
	REPORTNOTIFY("ID_SYS_MAILMESSAGETYPE_REPORTNOTIFY", "7"), // 报表通知
	WORKFLOWAPPROVED("ID_SYS_MAILMESSAGETYPE_WORKFLOWAPPROVED", "8"), // 流程可审批通知
	ECNOTIFY("ID_SYS_MAILMESSAGETYPE_ECNOTIFY", "9"), // 工程变更通知
	JOBNOTIFY("ID_SYS_MAILMESSAGETYPE_JOBNOTIFY", "10"), // JOB通知
	SYSNOTIFY("ID_SYS_MAILMESSAGETYPE_SYSNOTIFY", "11"), // SYS通知
	AGENTNOTIFY("ID_SYS_MAILMESSAGETYPE_AGENTNOTIFY", "12"); // 代理通知
	private final String	msrId;
	private final String	value;

	private MailMessageType(String msrId, String value)
	{
		this.msrId = msrId;
		this.value = value;
	}

	/**
	 * @return the msrId
	 */
	public String getMsrId()
	{
		return this.msrId;
	}

	/**
	 * @return the value
	 */
	public String getValue()
	{
		return this.value;
	}

	public static MailMessageType getMailMessageType(String value)
	{
		if (!StringUtils.isGuid(value))
		{
			MailMessageType[] messageTypes = MailMessageType.values();
			if (messageTypes != null)
			{
				for (MailMessageType type : messageTypes)
				{
					if (type.getValue().equals(value))
					{
						return type;
					}
				}
			}
		}
		return null;
	}

	/**
	 * 取得所有普通邮件的类型
	 * 
	 * @return
	 */
	public static List<MailMessageType> listMailMessageType()
	{
		List<MailMessageType> list = new ArrayList<MailMessageType>();
		list.add(MailMessageType.PROJECTNOTIFY);
		list.add(MailMessageType.TASKNOTIFY);
		list.add(MailMessageType.WORKFLOWNOTIFY);
		list.add(MailMessageType.TRANSFERCHECKOUT);
		list.add(MailMessageType.OWNERUPDATE);
		list.add(MailMessageType.ERPNOTIFY);
		list.add(MailMessageType.REPORTNOTIFY);
		list.add(MailMessageType.ECNOTIFY);
		list.add(MailMessageType.SYSNOTIFY);
		list.add(MailMessageType.AGENTNOTIFY);
		list.add(MailMessageType.JOBNOTIFY);
		return list;
	}
}
