/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: NLSManager
 * Wanglei 2011-11-11
 */
package dyna.app.core.i18n;

import dyna.app.server.GenericServer;
import dyna.common.conf.ConfigurableMSRImpl;
import dyna.common.conf.loader.ConfigLoaderFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 多语言管理器
 * 
 * @author Wanglei
 * 
 */
@Component
public class NLSManager
{
	private static final Map<String, Map<String, String>>	msrCache	= new HashMap<String, Map<String, String>>();

	public void loadStringRepository()
	{
		ConfigurableMSRImpl config = GenericServer.getServiceBean(ConfigLoaderFactory.class).getLoader4MSR().getConfigurable();
		msrCache.putAll(config.getConfig());
		config.clearConfig();
	}

	public Map<String, String> getMSRMap(String locale)
	{
		return msrCache.get(locale);
	}

	public String getMSRString(String id, String locale)
	{
		Map<String, String> msrMap = this.getMSRMap(locale);
		if (msrMap == null)
		{
			return null;
		}
		return msrMap.get(id);
	}
}
