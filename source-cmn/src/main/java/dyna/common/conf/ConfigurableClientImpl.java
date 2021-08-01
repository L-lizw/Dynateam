/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ConfigurableClientImpl 读取客户端配置信息
 * Wanglei 2010-3-30
 */
package dyna.common.conf;

import dyna.common.systemenum.ConnectionModeEnum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 读取客户端配置信息
 * 
 * @author Wanglei
 * 
 */
public class ConfigurableClientImpl extends ConfigurableAdapter
{

	private ConnectionModeEnum clientMode = ConnectionModeEnum.ALL_IN_ONE;

	private int						defaultLookupServicePort	= 1299;
	private ConnToASConfig			defaultHost					= null;
	private ConnToASConfig			lookupServiceHost	= null;

	private List<ConnToASConfig>	hostList			= new ArrayList<ConnToASConfig>();

	/**
	 * @return the lookupServiceHost
	 */
	public ConnToASConfig getLookupServiceHost()
	{
		return this.lookupServiceHost;
	}

	/**
	 * @param lookupServiceHost
	 *            the lookupServiceHost to set
	 */
	public void setLookupServiceHost(ConnToASConfig lookupServiceHost)
	{
		this.lookupServiceHost = lookupServiceHost;
	}

	/**
	 * @return the lookupServicePort
	 */
	public int getDefaultLookupServicePort()
	{
		return this.defaultLookupServicePort;
	}

	/**
	 * @param lookupServicePort
	 *            the lookupServicePort to set
	 */
	public void setDefaultLookupServicePort(int lookupServicePort)
	{
		this.defaultLookupServicePort = lookupServicePort;
	}

	/**
	 * @return the defaultHost
	 */
	public ConnToASConfig getDefaultServiceHost()
	{
		return this.defaultHost;
	}

	/**
	 * @param defaultHost
	 *            the defaultHost to set
	 */
	public void setDefaultServiceHost(ConnToASConfig defaultHost)
	{
		this.defaultHost = defaultHost;
	}

	/**
	 * @return the hostList
	 */
	public List<ConnToASConfig> getServiceHostList()
	{
		return Collections.unmodifiableList(this.hostList);
	}

	public void addServiceHost(ConnToASConfig host)
	{
		this.hostList.add(host);
	}

	/**
	 * @return the clientMode
	 */
	public ConnectionModeEnum getClientMode()
	{
		return this.clientMode;
	}

	/**
	 * @param clientMode
	 *            the clientMode to set
	 */
	public void setClientMode(ConnectionModeEnum clientMode)
	{
		this.clientMode = clientMode;
	}

}
