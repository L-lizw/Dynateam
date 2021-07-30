/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: CrossRequestModel
 * WangLHB Sep 9, 2011
 */
package dyna.common.bean.configure.cross;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * @author WangLHB
 * 
 */
@Root(name = "response")
public class CrossResponseModel extends CrossModel
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -5269035328928246673L;

	@Element(name = "srvver", required = false)
	private String				srvver				= null;

	@Element(name = "srvcode", required = false)
	private String				srvcode				= null;

	@Element(name = "payload", required = false, data = true)
	private String				payLoad				= null;

	@Element(name = "code", required = false, data = true)
	private String				code				= null;

	@Element(name = "message", required = false, data = true)
	private String				message				= null;

	@Element(name = "reqid", required = false, data = true)
	private String				reqid				= null;

	/**
	 * @return the code
	 */
	public String getCode()
	{
		return this.code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(String code)
	{
		this.code = code;
	}

	/**
	 * @return the message
	 */
	public String getMessage()
	{
		return this.message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message)
	{
		this.message = message;
	}

	/**
	 * @return the reqid
	 */
	public String getReqid()
	{
		return this.reqid;
	}

	/**
	 * @param reqid
	 *            the reqid to set
	 */
	public void setReqid(String reqid)
	{
		this.reqid = reqid;
	}

	/**
	 * @return the srvver
	 */
	public String getSrvver()
	{
		return this.srvver;
	}

	/**
	 * @param srvver
	 *            the srvver to set
	 */
	public void setSrvver(String srvver)
	{
		this.srvver = srvver;
	}

	/**
	 * @return the srvcode
	 */
	public String getSrvcode()
	{
		return this.srvcode;
	}

	/**
	 * @param srvcode
	 *            the srvcode to set
	 */
	public void setSrvcode(String srvcode)
	{
		this.srvcode = srvcode;
	}

	/**
	 * @return the playLoad
	 */
	public String getPayLoad()
	{
		return this.payLoad;
	}

	/**
	 * @param playLoad
	 *            the playLoad to set
	 */
	public void setPayLoad(String payLoad)
	{
		this.payLoad = payLoad;
	}
}
