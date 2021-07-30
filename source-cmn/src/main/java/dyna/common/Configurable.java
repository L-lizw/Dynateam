/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Configurable
 * Wanglei 2010-3-26
 */
package dyna.common;

/**
 * 提供可配置化接口, 没有具体方法, 仅做接口标识
 * 系统中, 从XML文件中读取到的配置均需实现此接口
 * 
 * @see dyna.common.conf.loader.ConfigLoader
 * @author Wanglei
 * 
 */
public interface Configurable
{

	/**
	 * config finish successfully
	 */
	public void configured();

	/**
	 * check whether is configured.
	 * 
	 * @return
	 */
	public boolean isConfigured();
}
