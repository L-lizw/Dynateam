/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: KVConfigLoaderImpl
 * Wanglei 2010-4-2
 */
package dyna.common.conf.loader;

import java.io.File;

import org.xml.sax.InputSource;

import dyna.common.conf.ConfigurableKVElementImpl;

/**
 * 通用的XML解析器
 * 
 * @author Wanglei
 * 
 */
public class ConfigLoaderDefaultImpl extends AbstractConfigLoader<ConfigurableKVElementImpl>
{

	@Override
	public ConfigurableKVElementImpl load()
	{
		return super.loadDefault();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dyna.common.conf.loader.ConfigLoader#load(java.lang.String)
	 */
	@Override
	public ConfigurableKVElementImpl load(String xmlFilePath)
	{
		this.setConfigFile(new File(xmlFilePath));
		return this.load();
	}

	@Override
	public ConfigurableKVElementImpl load(InputSource inputSource)
	{
		this.setConfigInputSource(inputSource);
		return this.load();
	}
}
