/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: KVConfigLoaderImpl
 * Wanglei 2010-4-2
 */
package dyna.common.conf.loader;

import dyna.common.conf.ConfigurableKVElementImpl;
import org.xml.sax.InputSource;

import java.io.File;

/**
 * 通用的XML解析器
 * 
 * @author Lizw
 * 
 */
public class ConfigLoaderDefaultImpl extends AbstractConfigLoader<ConfigurableKVElementImpl>
{

	@Override
	public void load()
	{
		 super.loadDefault();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.conf.loader.ConfigLoader#load(java.lang.String)
	 */
	@Override
	public void load(String xmlFilePath)
	{
		this.setConfigFile(new File(xmlFilePath));
		this.load();
	}

	@Override
	public void load(InputSource inputSource)
	{
		this.setConfigInputSource(inputSource);
		this.load();
	}

	@Override public ConfigurableKVElementImpl getConfigurable()
	{
		return kvElement;
	}
}
