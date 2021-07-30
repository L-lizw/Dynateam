/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ERPOutModel
 * WangLHB Oct 13, 2011
 */
package dyna.common.bean.configure.cross;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * @author WangLHB
 * 
 */
@Root(name = "STD_OUT")
public class ERPOutModel
{

	public static String	yifeiXML	= "<?xml version=\"1.0\" encoding=\"GB2312\"?>";
	public static String	yifeiDOC	= "<!DOCTYPE SC[<!ENTITY stdd \"&amp;stdd;\">]>";

	@Attribute(name = "Origin", required = false)
	private String			origin		= null;

	@Element(name = "Service", required = false)
	private ERPOutService	outService	= null;

	public String getStatus()
	{
		if (this.outService != null)
		{
			return this.outService.getStatus();
		}
		else
		{
			return null;
		}
	}

	public String getServiceName()
	{
		if (this.outService != null)
		{
			return this.outService.getServiceName();
		}
		else
		{
			return null;
		}
	}

	/**
	 * @return the origin
	 */
	public String getOrigin()
	{
		return this.origin;
	}

	/**
	 * @param origin
	 *            the origin to set
	 */
	public void setOrigin(String origin)
	{
		this.origin = origin;
	}

	/**
	 * @return the outService
	 */
	public ERPOutService getOutService()
	{
		return this.outService;
	}

	/**
	 * @param outService
	 *            the outService to set
	 */
	public void setOutService(ERPOutService outService)
	{
		this.outService = outService;
	}

	public String getError()
	{
		if (this.outService != null)
		{
			return this.outService.getError();
		}
		else
		{
			return null;
		}
	}

	public String getJobID()
	{
		if (this.outService != null)
		{
			return this.outService.getJobID();
		}
		else
		{
			return null;
		}
	}

}

@Root(name = "Service")
class ERPOutService
{

	@Attribute(name = "Name", required = false)
	private String	serviceName	= null;

	@Element(name = "Status", required = false)
	private String	status		= null;

	@Element(name = "JobID", required = false)
	private String	jobID		= null;

	@Element(name = "Error", required = false)
	private String	error		= null;

	/**
	 * @return the serviceName
	 */
	public String getServiceName()
	{
		return this.serviceName;
	}

	/**
	 * @param serviceName
	 *            the serviceName to set
	 */
	public void setServiceName(String serviceName)
	{
		this.serviceName = serviceName;
	}

	/**
	 * @return the status
	 */
	public String getStatus()
	{
		return this.status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status)
	{
		this.status = status;
	}

	/**
	 * @return the jobID
	 */
	public String getJobID()
	{
		return this.jobID;
	}

	/**
	 * @param jobID
	 *            the jobID to set
	 */
	public void setJobID(String jobID)
	{
		this.jobID = jobID;
	}

	/**
	 * @return the error
	 */
	public String getError()
	{
		return this.error;
	}

	/**
	 * @param error
	 *            the error to set
	 */
	public void setError(String error)
	{
		this.error = error;
	}
}
