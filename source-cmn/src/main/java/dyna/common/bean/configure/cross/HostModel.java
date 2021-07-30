/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: HostModel
 * WangLHB Sep 9, 2011
 */
package dyna.common.bean.configure.cross;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * @author WangLHB
 * 
 */
@Root(name = "host")
public class HostModel extends CrossModel
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -4568876766302582279L;

	@Attribute(name = "prod", required = false)
	private String				prod				= null;

	@Attribute(name = "ver", required = false)
	private String				ver					= null;

	@Attribute(name = "ip", required = false)
	private String				ip					= null;

	@Attribute(name = "id", required = false)
	private String				id					= null;

	@Attribute(name = "lang", required = false)
	private String				lang				= null;

	@Attribute(name = "timezone", required = false)
	private String				timeZone			= null;

	@Attribute(name = "acct", required = false)
	private String				acct				= null;

	@Attribute(name = "timestamp", required = false)
	private String				timestamp			= null;

	/**
	 * @return the prod
	 */
	public String getProd()
	{
		return this.prod;
	}

	/**
	 * @param prod
	 *            the prod to set
	 */
	public void setProd(String prod)
	{
		this.prod = prod;
	}

	/**
	 * @return the ver
	 */
	public String getVer()
	{
		return this.ver;
	}

	/**
	 * @param ver
	 *            the ver to set
	 */
	public void setVer(String ver)
	{
		this.ver = ver;
	}

	/**
	 * @return the ip
	 */
	public String getIp()
	{
		return this.ip;
	}

	/**
	 * @param ip
	 *            the ip to set
	 */
	public void setIp(String ip)
	{
		this.ip = ip;
	}

	/**
	 * @return the id
	 */
	public String getId()
	{
		return this.id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 * @return the lang
	 */
	public String getLang()
	{
		return this.lang;
	}

	/**
	 * @param lang
	 *            the lang to set
	 */
	public void setLang(String lang)
	{
		this.lang = lang;
	}

	/**
	 * @return the timeZone
	 */
	public String getTimeZone()
	{
		return this.timeZone;
	}

	/**
	 * @param timeZone
	 *            the timeZone to set
	 */
	public void setTimeZone(String timeZone)
	{
		this.timeZone = timeZone;
	}

	/**
	 * @return the acct
	 */
	public String getAcct()
	{
		return this.acct;
	}

	/**
	 * @param acct
	 *            the acct to set
	 */
	public void setAcct(String acct)
	{
		this.acct = acct;
	}
	
	/**
	 * @return the timeZone
	 */
	public String getTimestamp()
	{
		return this.timestamp;
	}

	/**
	 * @param timestamp
	 *            the timestamp to set
	 */
	public void setTimestamp(String timestamp)
	{
		this.timestamp = timestamp;
	}
}