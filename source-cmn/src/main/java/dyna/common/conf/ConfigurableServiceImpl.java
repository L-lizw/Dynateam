/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DynaServiceConfig
 * Wanglei 2010-3-30
 */
package dyna.common.conf;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

/**
 * 读取服务配置信息, 并缓存, 供服务调用查询
 * 
 * @author Wanglei
 * 
 */
public class ConfigurableServiceImpl extends ConfigurableAdapter
{

	private Hashtable<String, ServiceDefinition>	serviceTable	= new Hashtable<String, ServiceDefinition>();
	private List<ServiceDefinition>					serviceList		= new ArrayList<ServiceDefinition>();

	/**
	 * 服务的数量
	 * 
	 * @return
	 */
	public int getServiceDefinitionSize()
	{
		return this.serviceList.size();
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

}
