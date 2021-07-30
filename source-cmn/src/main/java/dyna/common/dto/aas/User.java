/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: FUser
 * xiasheng Apr 22, 2010
 */
package dyna.common.dto.aas;

import java.util.Date;

import dyna.common.annotation.Cache;
import dyna.common.bean.data.SystemObject;
import dyna.common.bean.data.SystemObjectImpl;
import dyna.common.util.BooleanUtils;
import dyna.common.util.StringUtils;

/**
 * @author xiasheng
 * 
 */
@Cache
public class User extends SystemObjectImpl implements SystemObject
{
	private static final long	serialVersionUID	= -7267860794534000689L;

	public static final String	IS_ACTIVE			= "ISACTIVE";

	public static final String	ISSHIELD			= "ISSHIELD";

	public static final String	MASK_CHAR			= "XXXXXXXXX";

	public static final String	userIdWX			= "USERIDWX";

	public static final String	ADDRESS				= "ADDRESS";

	public static final String	CITY				= "CITY";

	public static final String	COUNTRY				= "COUNTRY";

	public static final String	DEFAULTGROUPGUID	= "DEFAULTGROUPGUID";

	public static final String	DEFAULTROLEGUID		= "DEFAULTROLEGUID";

	public static final String	EMAIL				= "EMAIL";

	public static final String	FAX					= "FAX";

	public static final String	USERID				= "USERID";

	public static final String	USERNAME			= "USERNAME";

	public static final String	LATESTLOGINTIME		= "LATESTLOGINTIME";

	public static final String	MOBILE				= "MOBILE";

	public static final String	PASSWORD			= "PASSWORD";

	public static final String	STATE				= "STATE";

	public static final String	ZIPCODE				= "ZIPCODE";

	public static final String	TEL					= "TEL";

	public String getAddress()
	{
		return (String) this.get(ADDRESS);
	}

	public String getCity()
	{
		return (String) this.get(CITY);
	}

	public String getCountry()
	{
		return (String) this.get(COUNTRY);
	}

	public String getDefaultGroupGuid()
	{
		return (String) this.get(DEFAULTGROUPGUID);
	}

	public String getDefaultRoleGuid()
	{
		return (String) this.get(DEFAULTROLEGUID);
	}

	public String getEmail()
	{
		return (String) this.get(EMAIL);
	}

	public String getFax()
	{
		return (String) this.get(FAX);
	}

	public String getFullname()
	{
		return this.getUserId() + "-" + this.getUserName();
	}

	public String getOriginalFullname()
	{
		return this.getOriginalValue(USERID) + "-" + this.getOriginalValue(USERNAME);
	}

	public Date getLatestLoginTime()
	{
		return (Date) this.get(LATESTLOGINTIME);
	}

	public String getMobile()
	{
		return (String) this.get(MOBILE);
	}

	public String getPassword()
	{
		return (String) this.get(PASSWORD);
	}

	public String getState()
	{
		return (String) this.get(STATE);
	}

	public String getTel()
	{
		return (String) this.get(TEL);
	}

	public String getUserId()
	{
		return (String) this.get(USERID);
	}

	public String getName()
	{
		return (String) this.get(USERNAME);
	}

	public String getUserName()
	{
		return this.getName();
	}

	public String getZipCode()
	{
		return (String) this.get(ZIPCODE);
	}

	public boolean isActive()
	{
		return this.get(IS_ACTIVE) == null || "Y".equals(this.get(IS_ACTIVE)) ? true : false;
	}

	public void setActive(boolean isActive)
	{
		this.put(IS_ACTIVE, isActive ? "Y" : "N");
	}

	public void setAddress(String address)
	{
		this.put(ADDRESS, address);
	}

	public void setCity(String city)
	{
		this.put(CITY, city);
	}

	public void setCountry(String country)
	{
		this.put(COUNTRY, country);
	}

	public void setDefaultGroupGuid(String defaultGroupGuid)
	{
		this.put(DEFAULTGROUPGUID, defaultGroupGuid);
	}

	public void setDefaultRoleGuid(String defaultRoleGuid)
	{
		this.put(DEFAULTROLEGUID, defaultRoleGuid);
	}

	public void setEmail(String email)
	{
		this.put(EMAIL, email);
	}

	public void setFax(String fax)
	{
		this.put(FAX, fax);
	}

	public void setLatestLoginTime(Date latestLoginTime)
	{
		this.put(LATESTLOGINTIME, latestLoginTime);
	}

	public void setMobile(String mobile)
	{
		this.put(MOBILE, mobile);
	}

	public void setPassword(String password)
	{
		this.put(PASSWORD, password);
	}

	public void setState(String country)
	{
		this.put(STATE, country);
	}

	public void setTel(String tel)
	{
		this.put(TEL, tel);
	}

	public void setUserId(String id)
	{
		this.put(USERID, id);
	}

	public void setName(String name)
	{
		this.put(USERNAME, name);
	}

	public void setUserName(String name)
	{
		this.setName(name);
	}

	public void setZipCode(String zipCode)
	{
		this.put(ZIPCODE, zipCode);
	}

	public boolean isShield()
	{
		if (StringUtils.isNullString((String) this.get(ISSHIELD)))
		{
			return false;
		}

		return BooleanUtils.getBooleanByYN((String) this.get(ISSHIELD));
	}

	public void setShield(boolean isShield)
	{
		this.put(ISSHIELD, BooleanUtils.getBooleanStringYN(isShield));
	}

	public String getUseridwx()
	{
		return (String) this.get(userIdWX);
	}

	public void setUseridwx(String userid)
	{
		this.put(userIdWX, userid);
	}
}