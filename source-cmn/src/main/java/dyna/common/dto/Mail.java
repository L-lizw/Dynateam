/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Mail
 * caogc 2010-8-20
 */
package dyna.common.dto;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.ObjectGuid;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.MailMapper;
import dyna.common.systemenum.LanguageEnum;
import dyna.common.systemenum.MailCategoryEnum;
import dyna.common.systemenum.MailMessageType;
import dyna.common.util.BooleanUtils;
import dyna.common.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author caogc
 * 
 */
@EntryMapper(MailMapper.class)
public class Mail extends SystemObjectImpl implements SystemObject
{

	private static final long		serialVersionUID	= -5977652435532224138L;

	public static final String		GUID				= "GUID";
	public static final String		CATEGORY			= "CATEGORY";
	public static final String		TITLE				= "TITLE";
	public static final String		CONTENTS			= "CONTENTS";
	public static final String		IS_PROCESS			= "ISPROCESS";
	public static final String		SENDER_USER			= "SENDERUSER";
	public static final String		SENDER_USER_NAME	= "SENDERUSERNAME";
	public static final String		RECEIVE_USER		= "RECEIVEUSER";
	public static final String		RECEIVE_USER_NAME	= "RECEIVEUSERNAME";
	public static final String		RECEIVE_TIME		= "RECEIVETIME";
	public static final String		ORIGMAIL_GUID		= "ORIGMAILGUID";
	public static final String		HAS_ATTACHMENT		= "HASATTACHMENT";
	public static final String		IS_IN_TRASH			= "ISINTRASH";

	public static final String		USER_GUID			= "USERGUID";
	public static final String		IS_READ				= "ISREAD";
	public static final String		HAS_CATEGORY		= "HASCATEGORY";
	public static final String		MAIL_GUID			= "MAILGUID";

	public static final String		READ_TIME			= "READTIME";
	public static final String		PROCESS_TIME		= "PROCESSTIME";

	public static final String		PROCRT_GUID			= "PROCRTGUID";
	public static final String		PROCRT_TITLE		= "PROCRTTITLE";
	public static final String		PROCRT_DESC			= "PROCRTDESC";
	public static final String		ACTRT_GUID			= "ACTRTGUID";
	public static final String		ACTRT_TITLE			= "ACTRTTITLE";

	// public static final String IS_WF_NOTICE = "ISWFNOTICE";
	public static final String		WF_TEMPLATE_NAME	= "WFTEMPLATENAME";

	public static final String		NOT_READ_COUNT		= "NOTREADCOUNT";

	public static final String		RUMASTERGUID		= "RECEIVERMASTERGUID";

	public static final String		MODULE_TYPE			= "MODULETYPE";

	public static final String		START_NUMBER		= "STARTNUMBER";

	private List<MailAttachment>	mailAttachmentList	= null;

	public String getActivityRuntimeTitle(LanguageEnum lang)
	{
		return StringUtils.getMsrTitle((String) this.get(ACTRT_TITLE), lang.getType());
	}

	public String getActRuntimeGuid()
	{
		return (String) super.get(ACTRT_GUID);
	}

	/**
	 * @return the Category
	 */
	public String getCategory()
	{
		return (String) this.get(CATEGORY);
	}

	/**
	 * @return the Contents
	 */
	public String getContents(LanguageEnum lang)
	{
		if (!StringUtils.isNullString((String) this.get(CONTENTS)))
		{
			return (String) this.get(CONTENTS);
		}

		// if (StringUtils.isNullString(this.getProcessRuntimeGuid()) || this.isWFNotice())
		// {
		// return (String) this.get(CONTENTS);
		// }
		if (this.getModuleTypeEnum() != MailMessageType.WORKFLOWAPPROVED)
		{
			return (String) this.get(CONTENTS);
		}
		else
		{
			String pt = this.getActivityRuntimeTitle(lang);
			pt = pt == null ? "" : pt;
			return pt + "(" + (StringUtils.isNullString(this.getWFTemplateName()) ? this.getProcessRuntimeTitle(lang) : this.getWFTemplateName()) + ")";
		}
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
	 * @return the IsIntrash
	 */
	public boolean isIntrash()
	{
		Boolean ret = BooleanUtils.getBooleanByYN((String) this.get(IS_IN_TRASH));
		return ret == null ? false : ret.booleanValue();
	}

	/**
	 * @return the IsProcess
	 */
	public boolean isProcess()
	{
		Boolean ret = BooleanUtils.getBooleanByYN((String) this.get(IS_PROCESS));
		return ret == null ? false : ret.booleanValue();
	}

	/**
	 * @return the IS_READ
	 */
	public boolean isRead()
	{
		Boolean ret = BooleanUtils.getBooleanByYN((String) this.get(IS_READ));
		return ret == null ? false : ret.booleanValue();
	}

	/**
	 * @return the MailAttachmentList
	 */
	public List<MailAttachment> getMailAttachmentList()
	{
		return this.mailAttachmentList;
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

	/**
	 * @return the Title
	 */
	public String getTitle(LanguageEnum lang)
	{
		if (!StringUtils.isNullString((String) this.get(TITLE)))
		{
			return (String) this.get(TITLE);
		}

		if (StringUtils.isNullString(this.getProcessRuntimeGuid()))
		{
			return (String) this.get(TITLE);
		}
		else
		{
			String desc = this.getProcessRuntimeDesc();
			if (StringUtils.isNullString(desc))
			{
				desc = this.getProcessRuntimeTitle(lang);
			}
			return desc;
		}
	}

	public void setActRuntimeGuid(String guid)
	{
		super.put(ACTRT_GUID, guid);
	}

	/**
	 * @param Category
	 *            the Category to set
	 */
	public void setCategory(MailCategoryEnum category)
	{
		this.put(CATEGORY, category.toString());
	}

	/**
	 * @param Contents
	 *            the Contents to set
	 */
	public void setContents(String contents)
	{
		this.put(CONTENTS, contents);
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
	 * @param IsIntrash
	 *            the IsIntrash to set
	 */
	public void setIsIntrash(boolean isIntrash)
	{
		this.put(IS_IN_TRASH, BooleanUtils.getBooleanStringYN(isIntrash));
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
	 * @param IsRead
	 *            the IsRead to set
	 */
	public void setIsRead(boolean isRead)
	{
		this.put(IS_READ, BooleanUtils.getBooleanStringYN(isRead));
	}

	/**
	 * @param MailAttachmentList
	 *            the MailAttachmentList to set
	 */
	public void setMailAttachmentList(List<MailAttachment> mailAttachmentList)
	{
		this.mailAttachmentList = mailAttachmentList;
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

	/**
	 * @param Title
	 *            the Title to set
	 */
	public void setTitle(String title)
	{
		this.put(TITLE, title);
	}

	public Date getReadTime()
	{
		return (Date) this.get(READ_TIME);
	}

	public Date getProcessedTime()
	{
		return (Date) this.get(PROCESS_TIME);
	}

	/**
	 * @param isWFNotice
	 *            the isWFNotice to set
	 */
	/*
	 * public boolean isWFNotice()
	 * {
	 * Boolean value = BooleanUtils.getBooleanByYN((String) this.get(IS_WF_NOTICE));
	 * return value == null ? false : value.booleanValue();
	 * }
	 *//**
		 * @param isWFNotice
		 *            the isWFNotice to set
		 */
	/*
	 * public void setWFNotice(boolean isWFNotice)
	 * {
	 * this.put(IS_WF_NOTICE, BooleanUtils.getBooleanStringYN(isWFNotice));
	 * }
	 */

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

	public String getModuleType()
	{
		return (String) this.get(MODULE_TYPE);
	}

	public MailMessageType getModuleTypeEnum()
	{
		return MailMessageType.getMailMessageType((String) this.get(MODULE_TYPE));
	}

	public void setModuleType(String moduleType)
	{
		this.put(MODULE_TYPE, moduleType);
	}

	public void setStartNumber(int startNumber)
	{
		super.put(START_NUMBER, new BigDecimal(startNumber));
	}

	public Integer getStartNumber()
	{
		if (super.get(START_NUMBER) == null)
		{
			return 0;
		}
		return ((Number) super.get(START_NUMBER)).intValue();
	}
}
