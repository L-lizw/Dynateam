/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ConfigurableAdapter
 * Wanglei 2010-11-26
 */
package dyna.common.conf;

import dyna.common.Configurable;

/**
 * adapter for configurable
 * 
 * @author Wanglei
 * 
 */
public class ConfigurableAdapter implements Configurable
{

	private boolean	isCofigured	= false;

	/* (non-Javadoc)
	 * @see dyna.common.Configurable#configured()
	 */
	@Override
	public void configured()
	{
		this.isCofigured = true;
	}

	/* (non-Javadoc)
	 * @see dyna.common.Configurable#isConfigured()
	 */
	@Override
	public boolean isConfigured()
	{
		return this.isCofigured;
	}

}
