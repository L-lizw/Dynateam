/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServiceModel
 * WangLHB Sep 9, 2011
 */
package dyna.common.bean.configure.cross;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * @author WangLHB
 * 
 */
@Root(name = "service")
public class ServiceModel extends CrossModel
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 5222186661143942023L;

	@Attribute(name = "prod", required = false)
	private String				prod				= null;

	@Attribute(name = "name", required = false)
	private String				name				= null;

	@Attribute(name = "prodver", required = false)
	private String				prodver				= null;

	@Attribute(name = "srvver", required = false)
	private String				srvver				= null;

	@Attribute(name = "ip", required = false)
	private String				ip					= null;

	@Attribute(name = "id", required = false)
	private String				id					= null;

	@Attribute(name = "acct", required = false)
	private String				acct				= null;

	private String				uid					= null;

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
	 * @return the name
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return the prodver
	 */
	public String getProdver()
	{
		return this.prodver;
	}

	/**
	 * @param prodver
	 *            the prodver to set
	 */
	public void setProdver(String prodver)
	{
		this.prodver = prodver;
	}

	/**
	 * @return the srvVer
	 */
	public String getSrvver()
	{
		return this.srvver;
	}

	/**
	 * @param srvVer
	 *            the srvVer to set
	 */
	public void setSrvver(String srvver)
	{
		this.srvver = srvver;
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

	public String getUid()
	{
		return uid;
	}

	public void setUid(String uid)
	{
		this.uid = uid;
	}
}