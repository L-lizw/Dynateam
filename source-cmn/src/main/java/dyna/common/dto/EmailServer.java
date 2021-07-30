/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: EmailServer
 * WangLHB Feb 22, 2012
 */
package dyna.common.dto;

import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.util.Base64Util;
import dyna.common.util.BooleanUtils;
import dyna.common.util.StringUtils;

/**
 * @author WangLHB
 * 
 */
public class EmailServer extends SystemObjectImpl implements SystemObject
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public static final String  FROMADDRESS         = "FROMADDRESS";
	public static final String	USERNAME			= "USERNAME";
	public static final String	PASSWORD			= "PASSWORD";
	public static final String	SMTP				= "SMTP";
	public static final String	ISSHOWWARN			= "ISSHOWWARN";

	/**
	 * @param fromAddress
	 *            the fromAddress to set
	 */
	public void setFromAddress(String fromAddress)
	{
		this.put(FROMADDRESS, fromAddress);
	}

	/**
	 * 
	 * @return fromAddress
	 */
	public String getFromAddress()
	{
		return (String) this.get(FROMADDRESS);
	}
	
	/**
	 * @param userName
	 *            the userName to set
	 */
	public void setUserName(String userName)
	{
		this.put(USERNAME, userName);
	}

	/**
	 * 
	 * @return userName
	 */
	public String getUserName()
	{
		return (String) this.get(USERNAME);
	}

	/**
	 * @param Password
	 *            the Password to set
	 */
	public void setPassword(String password)
	{
		this.put(PASSWORD, Base64Util.encryptBase64(password));
	}

	/**
	 * 
	 * @return Password
	 */
	public String getPassword()
	{
		if (!StringUtils.isNullString((String) this.get(PASSWORD)))
		{
			return Base64Util.decodeBase64((String) this.get(PASSWORD));
		}
		else
		{
			return null;
		}
	}

	/**
	 * @param SMTP
	 *            the SMTP to set
	 */
	public void setSMTP(String smtp)
	{
		this.put(SMTP, smtp);
	}

	/**
	 * 
	 * @return SMTP
	 */
	public String getSMTP()
	{
		return (String) this.get(SMTP);
	}

	/**
	 * @param isShowWarn
	 *            the isShowWarn to set
	 */
	public void setShowWarn(boolean isShowWarn)
	{
		this.put(ISSHOWWARN, BooleanUtils.getBooleanStringYN(isShowWarn));
	}

	/**
	 * 
	 * @return isShowWarn
	 */
	public boolean isShowWarn()
	{
		if (this.get(ISSHOWWARN) == null)
		{
			return true;
		}
		return BooleanUtils.getBooleanByYN((String) this.get(ISSHOWWARN));
	}
}
