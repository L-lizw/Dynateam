/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ConfigurableMSRImpl
 * Wanglei 2010-7-1
 */
package dyna.common.conf;

import java.util.HashMap;
import java.util.Map;

import dyna.common.log.DynaLogger;

/**
 * @author Wanglei
 *
 */
public class ConfigurableMSRImpl extends ConfigurableAdapter
{

	private Map<String, Map<String, String>>	map	= new HashMap<String, Map<String, String>>();

	public void clearConfig()
	{
		this.map.clear();
	}

	public Map<String, Map<String, String>> getConfig()
	{
		return this.map;
	}

	public void putValue(String id, String value, String loc)
	{
		String previous = this.getLocMap(loc).put(id, value);
		if (previous != null)
		{
			DynaLogger.println("duplicated msr: " + loc + "," + id);
		}
	}

	private Map<String, String> getLocMap(String loc)
	{
		Map<String, String> locMap = this.map.get(loc);
		if (locMap == null)
		{
			locMap = new HashMap<String, String>();
			this.map.put(loc, locMap);
		}
		return locMap;
	}
}
