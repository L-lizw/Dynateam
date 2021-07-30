/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ConfigurableClientImpl 读取客户端配置信息
 * Wanglei 2010-3-30
 */
package dyna.common.conf;

import java.io.Serializable;


/**
 * 读取GuardServer配置信息
 * 
 * @author Wanglei
 * 
 */
public class ConfigurableConnToGuardServerImpl extends ConfigurableAdapter implements Serializable
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -3268234766688150594L;
	/**
	 * 
	 */


	private String			lookupServiceHost	= "127.0.0.1";
	private int				lookupServicePort	= 1299;

	/**
	 * @return the lookupServiceHost
	 */
	public String getLookupServiceHost()
	{
		return this.lookupServiceHost;
	}

	/**
	 * @param lookupServiceHost
	 *            the lookupServiceHost to set
	 */
	public void setLookupServiceHost(String lookupServiceHost)
	{
		this.lookupServiceHost = lookupServiceHost;
	}

	/**
	 * @return the lookupServicePort
	 */
	public int getLookupServicePort()
	{
		return this.lookupServicePort;
	}

	/**
	 * @param lookupServicePort
	 *            the lookupServicePort to set
	 */
	public void setLookupServicePort(int lookupServicePort)
	{
		this.lookupServicePort = lookupServicePort;
	}


}
