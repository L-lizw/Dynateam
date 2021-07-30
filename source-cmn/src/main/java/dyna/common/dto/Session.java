/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Session
 * sam Jun 8, 2010
 */
package dyna.common.dto;

import java.util.Date;

import dyna.common.annotation.Cache;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.systemenum.ApplicationTypeEnum;
import dyna.common.systemenum.LanguageEnum;

/**
 * @author sam
 *
 */
@Cache
public class Session extends SystemObjectImpl implements SystemObject
{
	private static final long serialVersionUID = 7421915174906171354L;

	public Session()
	{
		super();
	}

	public Session(String guid)
	{
		this.setGuid(guid);
	}

	public String getBizModelGuid()
	{
		return (String) this.get("BMGUID");
	}

	public String getBizModelName()
	{
		return (String) this.get("BMNAME");
	}

	public String getClientType()
	{
		return (String) this.get("CLIENTTYPE");
	}

	/**
	 * @return the ipAddress
	 */
	public String getIpAddress()
	{
		return (String) this.get("IPADDRESS");
	}

	/**
	 * @return the hostName
	 */
	public String getHostName()
	{
		return (String) this.get("HOSTNAME");
	}

	/**
	 * @return the loginGroupGuid
	 */
	public String getLoginGroupGuid()
	{
		return (String) this.get("LOGINGROUPGUID");
	}

	public String getLoginGroupId()
	{
		return (String) this.get("LOGINGROUPID");
	}

	public String getLoginGroupName()
	{
		return (String) this.get("LOGINGROUPNAME");
	}

	/**
	 * @return the loginRoleGuid
	 */
	public String getLoginRoleGuid()
	{
		return (String) this.get("LOGINROLEGUID");
	}

	public String getLoginRoleId()
	{
		return (String) this.get("LOGINROLEID");
	}

	public String getLoginRoleName()
	{
		return (String) this.get("LOGINROLENAME");
	}

	/**
	 * @return the loginTime
	 */
	public Date getLoginTime()
	{
		return (Date) this.get("LOGINTIME");
	}

	public String getSiteId()
	{
		return (String) this.get("SITEID");
	}

	/**
	 * @return the updateTime
	 */
	@Override
	public Date getUpdateTime()
	{
		return (Date) this.get("UPDATETIME");
	}

	public String getUserGuid()
	{
		return (String) this.get("USERGUID");
	}

	public String getUserId()
	{
		return (String) this.get("USERID");
	}

	public String getUserName()
	{
		return (String) this.get("USERNAME");
	}

	public void setBizModelGuid(String bmGuid)
	{
		this.put("BMGUID", bmGuid);
	}

	public void setClientType(String clientType)
	{
		this.put("CLIENTTYPE", clientType);
	}

	/**
	 * @param ipAddress
	 *            the ipAddress to set
	 */
	public void setIpAddress(String ipAddress)
	{
		this.put("IPADDRESS", ipAddress);
	}

	/**
	 * @param hostName
	 *            the hostName to set
	 */
	public void setHostName(String hostName)
	{
		this.put("HOSTNAME", hostName);
	}

	/**
	 * @param loginGroupGuid
	 *            the loginGroupGuid to set
	 */
	public void setLoginGroupGuid(String loginGroupGuid)
	{
		this.put("LOGINGROUPGUID", loginGroupGuid);
	}

	/**
	 * @param loginRoleGuid
	 *            the loginRoleGuid to set
	 */
	public void setLoginRoleGuid(String loginRoleGuid)
	{
		this.put("LOGINROLEGUID", loginRoleGuid);
	}

	/**
	 * @param loginTime
	 *            the loginTime to set
	 */
	public void setLoginTime(Date loginTime)
	{
		this.put("LOGINTIME", loginTime);
	}

	public void setSiteId(String siteId)
	{
		this.put("SITEID", siteId);
	}

	/**
	 * @param updateTime
	 *            the updateTime to set
	 */
	public void setUpdateTime(Date updateTime)
	{
		this.put("UPDATETIME", updateTime);
	}

	public void setUserGuid(String userGuid)
	{
		this.put("USERGUID", userGuid);
	}

	public ApplicationTypeEnum getAppType()
	{
		String type = (String) this.get("APPTYPE");
		return type == null ? null : ApplicationTypeEnum.valueOf(type);
	}

	public void setAppType(ApplicationTypeEnum appType)
	{
		this.put("APPTYPE", appType.name());
	}

	public LanguageEnum getLanguageEnum()
	{
		return LanguageEnum.getById((String) this.get("LOCALE"));
	}

	public void setLanguageEnum(LanguageEnum lang)
	{
		this.put("LOCALE", lang.getId());
	}

	public Date getLastAccesseTime()
	{
		return (Date) this.get("LASTACCESSTIME");
	}

	public void setLastAccesseTime(Date time)
	{
		this.put("LASTACCESSTIME", time);
	}
}
