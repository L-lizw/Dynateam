/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Mail_WorkFlow
 * wangweixia 2014-7-21
 */
package dyna.common.dto;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.MailWorkFlowMapper;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.systemenum.ProcessStatusEnum;
import dyna.common.util.BooleanUtils;
import dyna.common.util.StringUtils;

import java.util.Date;

/**
 * @author wangweixia
 * 
 */
@EntryMapper(MailWorkFlowMapper.class)
public class MailWorkFlow extends SystemObjectImpl implements SystemObject
{
	private static final long	serialVersionUID	= 6076824363035753361L;

	public static final String	GUID				= "GUID";				// 邮件Guid
	public static final String	PROCRT_TITLE		= "PROCRTTITLE";		// 流程的标题--主题
	public static final String	PROCRT_DESC			= "PROCRTDESC";			// 流程的描述
	public static final String	IS_PROCESS			= "ISPROCESS";			// 是否处理
	public static final String	SENDER_USER			= "SENDERUSER";			// 发送人
	public static final String	SENDER_USER_NAME	= "SENDERUSERNAME";
	public static final String	RECEIVE_USER		= "RECEIVEUSER";		// 接收人
	public static final String	RECEIVE_USER_NAME	= "RECEIVEUSERNAME";
	public static final String	RECEIVE_TIME		= "RECEIVETIME";
	public static final String	ORIGMAIL_GUID		= "ORIGMAILGUID";
	public static final String	HAS_ATTACHMENT		= "HASATTACHMENT";

	public static final String	USER_GUID			= "USERGUID";
	public static final String	HAS_CATEGORY		= "HASCATEGORY";

	public static final String	PROCESS_TIME		= "PROCESSTIME";

	public static final String	PROCRT_GUID			= "PROCRTGUID";
	public static final String	ACTRT_GUID			= "ACTRTGUID";			// 活动节点
	public static final String	ACTRT_TITLE			= "ACTRTTITLE";			// 活动节点title

	public static final String	PROCRT_STATUS		= "PROCRTSTATUS";		// 流程状态 ProcessStatusEnum
	public static final String	APPROVAL_STATUS		= "APPROVALSTATUS";		// 审批状态 :0 处理中 1已通过 2 已拒绝
	public static final String	PLANFINISH_TIME		= "PLANFINISHTIME";		// 计划完成时间

	public static final String	MODULE_TYPE			= "MODULETYPE";

	public static final String	WF_TEMPLATE_NAME	= "WFTEMPLATENAME";

	public static final String	RUMASTERGUID		= "RECEIVERMASTERGUID";

	public static final String	AGENTUSERNAME		= "AGENTUSERNAME";

	public static final String	AGENTUSERGUID		= "AGENTUSERGUID";

	public static final String	STARTNUMBER			= "STARTNUMBER";

	/**
	 * 流程处理状态
	 * 
	 * @return
	 */
	public ProcessStatusEnum getProcrtStatus()
	{
		return ProcessStatusEnum.valueOf((String) super.get(PROCRT_STATUS));
	}

	/**
	 * 审批状态
	 * 
	 * @return
	 */
	public String getApprovalStatus()
	{
		return (String) super.get(APPROVAL_STATUS);
	}

	public String getActivityRuntimeTitle(LanguageEnum lang)
	{
		return StringUtils.getMsrTitle((String) this.get(ACTRT_TITLE), lang.getType());
	}

	public String getActRuntimeGuid()
	{
		return (String) super.get(ACTRT_GUID);
	}

	/**
	 * @return the GUID
	 */
	@Override
	public String getGuid()
	{
		return (String) this.get(GUID);
	}

	/**
	 * @return the HasAttachment
	 */
	public boolean hasAttachment()
	{
		Boolean value = BooleanUtils.getBooleanByYN((String) this.get(HAS_ATTACHMENT));
		return value == null ? false : value.booleanValue();
	}

	/**
	 * @return the IsProcess
	 */
	public boolean isProcess()
	{
		Boolean ret = BooleanUtils.getBooleanByYN((String) this.get(IS_PROCESS));
		return ret == null ? false : ret.booleanValue();
	}

	@Override
	public ObjectGuid getObjectGuid()
	{
		if (this.objectGuid == null)
		{
			this.objectGuid = new ObjectGuid(null, null, this.getGuid(), null);
		}
		return this.objectGuid;
	}

	/**
	 * @return the OrigMailGuid
	 */
	public String getOrigMailGuid()
	{
		return (String) this.get(ORIGMAIL_GUID);
	}

	public String getProcessRuntimeDesc()
	{
		return (String) this.get(PROCRT_DESC);
	}

	public String getProcessRuntimeGuid()
	{
		return (String) super.get(PROCRT_GUID);
	}

	public String getProcessRuntimeTitle(LanguageEnum lang)
	{
		return StringUtils.getMsrTitle((String) this.get(PROCRT_TITLE), lang.getType());
	}

	/**
	 * @return the ReceiveTime
	 */
	public Date getReceiveTime()
	{
		return (Date) this.get(RECEIVE_TIME);
	}

	/**
	 * @return the ReceiveUser
	 */
	public String getReceiveUser()
	{
		return (String) this.get(RECEIVE_USER);
	}

	/**
	 * @return the ReceiveUserName
	 */
	public String getReceiveUserName()
	{
		return (String) this.get(RECEIVE_USER_NAME);
	}

	/**
	 * @return the SenderUser
	 */
	public String getSenderUser()
	{
		return (String) this.get(SENDER_USER);
	}

	/**
	 * @return the SenderUserName
	 */
	public String getSenderUserName()
	{
		return (String) this.get(SENDER_USER_NAME);
	}

	public void setActRuntimeGuid(String guid)
	{
		super.put(ACTRT_GUID, guid);
	}

	/**
	 * @param GUID
	 *            the GUID to set
	 */
	@Override
	public void setGuid(String guid)
	{
		this.put(GUID, guid);
	}

	/**
	 * @param HasAttachment
	 *            the HasAttachment to set
	 */
	public void setHasAttachment(boolean hasAttachment)
	{
		this.put(HAS_ATTACHMENT, BooleanUtils.getBooleanStringYN(hasAttachment));
	}

	/**
	 * @param IsProcess
	 *            the IsProcess to set
	 */
	public void setIsProcess(boolean isProcess)
	{
		this.put(IS_PROCESS, BooleanUtils.getBooleanStringYN(isProcess));
	}

	/**
	 * @param OrigMailGuid
	 *            the OrigMailGuid to set
	 */
	public void setOrigMailGuid(String origMailGuid)
	{
		this.put(ORIGMAIL_GUID, origMailGuid);
	}

	public void setProcessRuntimeGuid(String guid)
	{
		super.put(PROCRT_GUID, guid);
	}

	/**
	 * @param ReceiveTime
	 *            the ReceiveTime to set
	 */
	public void setReceiveTime(Date receiveTime)
	{
		this.put(RECEIVE_TIME, receiveTime);
	}

	/**
	 * @param ReceiveUser
	 *            the ReceiveUser to set
	 */
	public void setReceiveUser(String receiveUser)
	{
		this.put(RECEIVE_USER, receiveUser);
	}

	/**
	 * @param ReceiveUserName
	 *            the ReceiveUserName to set
	 */
	public void setReceiveUserName(String receiveUserName)
	{
		this.put(RECEIVE_USER_NAME, receiveUserName);
	}

	/**
	 * @param SenderUser
	 *            the SenderUser to set
	 */
	public void setSenderUser(String senderUser)
	{
		this.put(SENDER_USER, senderUser);
	}

	/**
	 * @param SenderUserName
	 *            the SenderUserName to set
	 */
	public void setSenderUserName(String senderUserName)
	{
		this.put(SENDER_USER_NAME, senderUserName);
	}

	public Date getProcessedTime()
	{
		return (Date) this.get(PROCESS_TIME);
	}

	public String getWFTemplateName()
	{
		return (String) this.get(WF_TEMPLATE_NAME);
	}

	public String getRUMasterGuid()
	{
		return (String) this.get(RUMASTERGUID);
	}

	public void setRUMasterGuid(String ruMasterGuid)
	{
		this.put(RUMASTERGUID, ruMasterGuid);
	}

	public Date getPlanFinishTime()
	{
		return (Date) this.get(PLANFINISH_TIME);
	}

	public String getAgentUserName()
	{
		return (String) this.get(AGENTUSERNAME);
	}

	public String getAgentUserGuid()
	{
		return (String) this.get(AGENTUSERGUID);
	}

	public void setAgentUserName(String agentUserName)
	{
		this.put(AGENTUSERNAME, agentUserName);
	}

	public void setAgentUserGuid(String agentUserGuid)
	{
		this.put(AGENTUSERGUID, agentUserGuid);
	}

	public int getStartNumber()
	{
		if (super.get(STARTNUMBER) == null)
		{
			return 0;
		}
		return ((Number) super.get(STARTNUMBER)).intValue();
	}
}
