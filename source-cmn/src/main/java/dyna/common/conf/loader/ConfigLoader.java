/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ConfigLoader
 * Wanglei 2010-3-30
 */
package dyna.common.conf.loader;

import org.xml.sax.InputSource;

import dyna.common.Configurable;


/**
*
 * 配置文件加载器接口
 * 读取并解析 XML 配置文件, 得到配置结果
 *
* @author   Lizw
* @date     2021/7/31 14:51
**/

public interface ConfigLoader<T extends Configurable>
{

	/**
	 * 加载配置
	 * 
	 * @return
	 */
	public void load();

	/**
	 * 加载指定文件的配置
	 * 
	 * @param xmlFilePath
	 * @return
	 */
	public void load(String xmlFilePath);
	
	/**
	 * 加载输入的文件流
	 * 
	 * @param inputSource
	 * @return
	 */
	public void load(InputSource inputSource);

	/**
	 * 获取配置
	 * @return
	 */
	public T getConfigurable();
}
