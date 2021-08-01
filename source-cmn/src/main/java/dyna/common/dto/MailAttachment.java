/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: MailAttachment
 * caogc 2010-8-25
 */
package dyna.common.dto;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.MailAttachmentMapper;

/**
 * @author caogc
 * 
 */
@EntryMapper(MailAttachmentMapper.class)
public class MailAttachment extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID	= -5977652435532224138L;

	public static final String	GUID				= "GUID";
	public static final String	MAILGUID			= "MAILGUID";
	public static final String	INSTANCEGUID		= "INSTANCEGUID";
	public static final String	INSTANCETITLE		= "INSTANCETITLE";
	public static final String	INSTANCECLASS		= "INSTANCECLASSGUID";
	public static final String	INSTANCECLASSNAME	= "INSTANCECLASSNAME";
	public static final String	FILE_GUID			= "FILEGUID";
	public static final String	FILE_NAME			= "FILENAME";

	/**
	 * @return the GUID
	 */
	@Override
	public String getGuid()
	{
		return (String) this.get(GUID);
	}

	public String getInstanceClassName()
	{
		return (String) this.get(INSTANCECLASSNAME);
	}

	/**
	 * @return the InstanceClass
	 */
	public String getInstanceClass()
	{
		return (String) this.get(INSTANCECLASS);
	}

	/**
	 * @return the InstanceGuid
	 */
	public String getInstanceGuid()
	{
		return (String) this.get(INSTANCEGUID);
	}

	/**
	 * @return the InstanceTitle
	 */
	public String getInstanceTitle()
	{
		return (String) this.get(INSTANCETITLE);
	}

	/**
	 * @return the MailGuid
	 */
	public String getMailGuid()
	{
		return (String) this.get(MAILGUID);
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
	 * @param InstanceClass
	 *            the InstanceClass to set
	 */
	public void setInstanceClass(String instanceClass)
	{
		this.put(INSTANCECLASS, instanceClass);
	}

	/**
	 * @param InstanceGuid
	 *            the InstanceGuid to set
	 */
	public void setInstanceGuid(String instanceGuid)
	{
		this.put(INSTANCEGUID, instanceGuid);
	}

	/**
	 * @param InstanceTitle
	 *            the InstanceTitle to set
	 */
	public void setInstanceTitle(String instanceTitle)
	{
		this.put(INSTANCETITLE, instanceTitle);
	}

	/**
	 * @param MailGuid
	 *            the MailGuid to set
	 */
	public void setMailGuid(String mailGuid)
	{
		this.put(MAILGUID, mailGuid);
	}

	public void setFileGuid(String fileGuid)
	{
		this.put(FILE_GUID, fileGuid);
	}

	public String getFileGuid()
	{
		return (String) this.get(FILE_GUID);
	}

	public String getFileName()
	{
		return (String) this.get(FILE_NAME);
	}
}
