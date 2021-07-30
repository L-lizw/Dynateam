/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ConfigurableServerImpl
 * Wanglei 2010-3-30
 */
package dyna.common.conf;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import dyna.common.bean.serv.DSServerBean;
import dyna.common.bean.serv.DSStorage;
import dyna.common.systemenum.LanguageEnum;

/**
 * 读取服务器配置信息, 并保存, 供服务器获取初始化参数使用.
 * 
 * @author Wanglei
 * 
 */
public class ConfigurableServerImpl extends ConfigurableAdapter
{

	private static final int				DEFAULT_TIMEOUT				= 240;
	private String							serverId					= null;
	private String							serverDescription			= null;

	private String							serverIP					= null;
	private Integer							serviceDispatchPort			= null;
	private Integer							rmiRegistryPort				= null;
	private Integer							sessionTimeout				= DEFAULT_TIMEOUT;
	private Integer							sessionPromptTime			= null;
	private Integer							threadPoolCount				= null;
	private Integer							threadPoolDelay				= null;
	private Integer							scheduledThreadPoolCount	= null;

	private final Map<String, DSServerBean>	dsserverBeanMap				= new HashMap<String, DSServerBean>();
	private final Map<String, DSStorage>	storageMap					= new HashMap<String, DSStorage>();
	private final List<DSStorage>			storageList					= new LinkedList<DSStorage>();
	private LanguageEnum					lang						= LanguageEnum.EN;

	public void addDSServerBean(DSServerBean dsserverBean)
	{
		this.dsserverBeanMap.put(dsserverBean.getId(), dsserverBean);
	}

	public void addDSStorage(DSStorage storage)
	{
		if (this.storageMap.containsKey(storage.getId()))
		{
			return;
		}
		this.storageMap.put(storage.getId(), storage);
		this.storageList.add(storage);
	}

	/**
	 * @return the storageList
	 */
	public List<DSStorage> getStorageList()
	{
		return Collections.unmodifiableList(this.storageList);
	}

	public DSServerBean getDSServerBean(String ftpId)
	{
		return this.dsserverBeanMap.get(ftpId);
	}

	public DSStorage getDSStorage(String storageId)
	{
		return this.storageMap.get(storageId);
	}

	/**
	 * @return the serverId
	 */
	public String getServerId()
	{
		return this.serverId;
	}

	/**
	 * @param serverId
	 *            the serverId to set
	 */
	public void setServerId(String serverId)
	{
		this.serverId = serverId;
	}

	/**
	 * @return the serverDescription
	 */
	public String getServerDescription()
	{
		return this.serverDescription;
	}

	/**
	 * @param serverDescription
	 *            the serverDescription to set
	 */
	public void setServerDescription(String serverDescription)
	{
		this.serverDescription = serverDescription;
	}

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
	 * @return the sessionTimeout
	 */
	public Integer getSessionTimeout()
	{
		return this.sessionTimeout;
	}

	/**
	 * @param sessionTimeout
	 *            the sessionTimeout to set
	 */
	public void setSessionTimeout(Integer sessionTimeout)
	{
		if (sessionTimeout != null && sessionTimeout.intValue() > 0)
		{
			this.sessionTimeout = sessionTimeout;
		}
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

	/**
	 * @return the threadPoolCount
	 */
	public Integer getThreadPoolCount()
	{
		return threadPoolCount;
	}

	/**
	 * @param threadPoolCount
	 *            the threadPoolCount to set
	 */
	public void setThreadPoolCount(Integer threadPoolCount)
	{
		this.threadPoolCount = threadPoolCount;
	}

	/**
	 * @return the threadPoolDelay
	 */
	public Integer getThreadPoolDelay()
	{
		return threadPoolDelay;
	}

	/**
	 * @param threadPoolDelay
	 *            the threadPoolDelay to set
	 */
	public void setThreadPoolDelay(Integer threadPoolDelay)
	{
		this.threadPoolDelay = threadPoolDelay;
	}

	/**
	 * @return the threadPoolDelay
	 */
	public Integer getScheduledThreadPoolCount()
	{
		return scheduledThreadPoolCount;
	}

	/**
	 * @param threadPoolDelay
	 *            the threadPoolDelay to set
	 */
	public void setScheduledThreadPoolCount(Integer scheduledThreadPoolCount)
	{
		this.scheduledThreadPoolCount = scheduledThreadPoolCount;
	}

	/**
	 * @param lang
	 */
	public void setLanguage(LanguageEnum lang)
	{
		this.lang = lang;
	}

	/**
	 * @return the lang
	 */
	public LanguageEnum getLanguage()
	{
		return lang;
	}

	public Integer getRmiRegistryPort()
	{
		return rmiRegistryPort;
	}

	public void setRmiRegistryPort(Integer rmiRegistryPort)
	{
		this.rmiRegistryPort = rmiRegistryPort;
	}

	public Integer getSessionPromptTime()
	{
		return sessionPromptTime;
	}

	public void setSessionPromptTime(Integer sessionPromptTime)
	{
		this.sessionPromptTime = sessionPromptTime;
	}

}
