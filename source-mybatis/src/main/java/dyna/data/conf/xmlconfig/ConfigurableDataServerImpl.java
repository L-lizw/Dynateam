/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DynaServiceConfig
 * Wanglei 2010-3-30
 */
package dyna.data.conf.xmlconfig;

import dyna.common.conf.ConfigurableAdapter;
import dyna.common.conf.ServiceDefinition;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

/**
 * 读取数据服务配置信息, 并缓存, 供服务调用查询
 * 
 * @author Wanglei
 * 
 */
@Repository
public class ConfigurableDataServerImpl extends ConfigurableAdapter
{
	private String                               serverIP            = null;
	private Integer                              serviceDispatchPort = null;
	private Integer                              rmiRegistryPort     = null;
	private Hashtable<String, ServiceDefinition> serviceTable        = new Hashtable<String, ServiceDefinition>();
	private List<ServiceDefinition>              serviceList         = new ArrayList<ServiceDefinition>();

	/**
	 * @return the serviceDispatchPort
	 */
	public Integer getServiceDispatchPort()
	{
		return this.serviceDispatchPort;
	}

	/**
	 * @param serviceDispatchPort
	 *            the serviceDispatchPort to set
	 */
	public void setServiceDispatchPort(Integer serviceDispatchPort)
	{
		this.serviceDispatchPort = serviceDispatchPort;
	}

	/**
	 * 枚举配置文件中的所有服务定义
	 * 
	 * @return 返回枚举
	 */
	public Iterator<ServiceDefinition> getServiceDefinitions()
	{
		return this.serviceList.iterator();
	}

	/**
	 * 获取指定的服务定义
	 * 
	 * @param serviceID
	 * @return
	 */
	public ServiceDefinition getServiceDefinition(String serviceID)
	{
		return this.serviceTable.get(serviceID);
	}

	public synchronized void addServiceDefinition(ServiceDefinition sd)
	{
		if (this.isConfigured())
		{
			return;
		}
		this.serviceList.add(sd);
		this.serviceTable.put(sd.getServcieID(), sd);
	}

	/**
	 * @return the serverIP
	 */
	public String getServerIP()
	{
		return this.serverIP;
	}

	/**
	 * @param serverIP
	 *            the serverIP to set
	 */
	public void setServerIP(String serverIP)
	{
		this.serverIP = serverIP;
	}

	public Integer getRmiRegistryPort()
	{
		return rmiRegistryPort;
	}

	public void setRmiRegistryPort(Integer rmiRegistryPort)
	{
		this.rmiRegistryPort = rmiRegistryPort;
	}

}
