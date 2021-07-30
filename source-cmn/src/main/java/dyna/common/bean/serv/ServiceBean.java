/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServiceBean
 * Wanglei 2010-3-31
 */
package dyna.common.bean.serv;

import java.io.Serializable;

import dyna.common.conf.ServiceDefinition;

/**
 * 服务定义bean
 * 
 * @author Wanglei
 * 
 */
public class ServiceBean implements Serializable
{
	private static final long	serialVersionUID		= -1430115025141173352L;

	private String	servcieID				= null;

	private String	serviceName				= null;

	private String	servcieDescription		= null;

	private String	serviceInterfaceName	= null;

	private int		dispatchPort			= 0;

	public ServiceBean(String id, String name, String desc, String interfaceName)
	{
		this.servcieID = id;
		this.serviceName = name;
		this.servcieDescription = desc;
		this.serviceInterfaceName = interfaceName;
	}

	public ServiceBean(ServiceDefinition sd)
	{
		this.servcieID = sd.getServcieID();
		this.serviceName = sd.getServcieName();
		this.dispatchPort = sd.getDispatchPort();
		this.servcieDescription = sd.getServcieDescription();
		this.serviceInterfaceName = sd.getServiceInterfaceName();
	}

	/**
	 * @return the servcieID
	 */
	public String getServcieId()
	{
		return this.servcieID;
	}

	/**
	 * @param servcieID
	 *            the servcieID to set
	 */
	public void setServcieID(String servcieID)
	{
		this.servcieID = servcieID;
	}

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
	 * @return the servcieDescription
	 */
	public String getServcieDescription()
	{
		return this.servcieDescription;
	}

	/**
	 * @param servcieDescription
	 *            the servcieDescription to set
	 */
	public void setServcieDescription(String servcieDescription)
	{
		this.servcieDescription = servcieDescription;
	}

	/**
	 * @return the serviceInterfaceName
	 */
	public String getServiceInterfaceName()
	{
		return this.serviceInterfaceName;
	}

	/**
	 * @param serviceInterfaceName
	 *            the serviceInterfaceName to set
	 */
	public void setServiceInterfaceName(String serviceInterfaceName)
	{
		this.serviceInterfaceName = serviceInterfaceName;
	}

	/**
	 * @return the dispatchPort
	 */
	public int getDispatchPort()
	{
		return this.dispatchPort;
	}

	/**
	 * @param dispatchPort
	 *            the dispatchPort to set
	 */
	public void setDispatchPort(int dispatchPort)
	{
		this.dispatchPort = dispatchPort;
	}

}
