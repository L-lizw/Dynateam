/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ServiceConfigBean
 * Wanglei 2010-3-30
 */
package dyna.common.conf;

/**
 * 服务配置定义
 * 
 * @author Wanglei
 * 
 */
public class ServiceDefinition extends InitParameter
{

	private String		servcieID				= null;

	private String		servcieName				= null;

	private String		servcieDescription		= null;

	private String		serviceImplName			= null;

	private String		serviceInterfaceName	= null;

	private Class<?>	serviceImplClass		= null;

	private Class<?>	serviceInterface		= null;

	private int			dispatchPort			= 0;

	private String		dispatchIP				= null;

	/**
	 * @return the servcieID
	 */
	public String getServcieID()
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
	 * @return the servcieName
	 */
	public String getServcieName()
	{
		return this.servcieName;
	}

	/**
	 * @param servcieName
	 *            the servcieName to set
	 */
	public void setServcieName(String servcieName)
	{
		this.servcieName = servcieName;
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
	 * @return the serviceImplStr
	 */
	public String getServiceImplName()
	{
		return this.serviceImplName;
	}

	/**
	 * @param serviceImplStr
	 *            the serviceImplStr to set
	 */
	public void setServiceImplName(String serviceImplStr)
	{
		this.serviceImplName = serviceImplStr;
	}

	/**
	 * @return the serviceInterfaceStr
	 */
	public String getServiceInterfaceName()
	{
		return this.serviceInterfaceName;
	}

	/**
	 * @param serviceInterfaceStr
	 *            the serviceInterfaceStr to set
	 */
	public void setServiceInterfaceName(String serviceInterfaceStr)
	{
		this.serviceInterfaceName = serviceInterfaceStr;
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

	/**
	 * @return the serviceImplClass
	 * @throws ClassNotFoundException
	 */
	public Class<?> getServiceImplClass() throws ClassNotFoundException
	{
		if (this.serviceImplClass == null)
		{
			this.serviceImplClass = Class.forName(this.serviceImplName);
		}
		return this.serviceImplClass;
	}

	/**
	 * @return the serviceInterface
	 * @throws ClassNotFoundException
	 */
	public Class<?> getServiceInterface() throws ClassNotFoundException
	{
		if (this.serviceInterface == null)
		{
			this.serviceInterface = Class.forName(this.serviceInterfaceName);
		}
		return this.serviceInterface;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(this.servcieName);
		sb.append("[" + this.servcieID + "]");
		sb.append("interface: " + this.serviceInterfaceName + " && ");
		sb.append("class: " + this.serviceImplName);
		return sb.toString();
	}

	/**
	 * @return the dispatchIP
	 */
	public String getDispatchIP()
	{
		return dispatchIP;
	}

	/**
	 * @param dispatchIP the dispatchIP to set
	 */
	public void setDispatchIP(String dispatchIP)
	{
		this.dispatchIP = dispatchIP;
	}

}
