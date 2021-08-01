/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Mail
 * caogc 2010-8-20
 */
package dyna.common.dto;

import dyna.common.annotation.EntryMapper;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.dtomapper.MailReceiveUserMapper;

/**
 * @author caogc
 * 
 */
@EntryMapper(MailReceiveUserMapper.class)
public class MailReceiveUser extends SystemObjectImpl implements SystemObject
{

	private static final long	serialVersionUID	= -5977652435532224138L;

	public static final String	MASTERGUID			= "MASTERGUID";
	public static final String	RECEIVEUSER			= "RECEIVEUSER";

	public String getReceiveUser()
	{
		return (String) this.get(RECEIVEUSER);
	}

	public void setReceiveUser(String receiveUser)
	{
		this.put(RECEIVEUSER, receiveUser);
	}

	public void setMasterGuid(String masterGuid)
	{
		this.put(MASTERGUID, masterGuid);
	}

}
