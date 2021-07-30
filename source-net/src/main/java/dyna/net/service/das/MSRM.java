/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: MSRM 多语言服务
 * Wanglei 2010-7-1
 */
package dyna.net.service.das;

import java.util.Map;

import dyna.net.service.Service;

/**
 * 多语言服务
 * 
 * @author Wanglei
 * 
 */
public interface MSRM extends Service
{

	/**
	 * 依据地区获取多语言配置的缓存
	 * 
	 * @param locale
	 * @return
	 */
	public Map<String, String> getMSRMap(String locale);

	/**
	 * 依据地区和多语言id获取对应的内容信息
	 * 
	 * @param id
	 *            多语言配置id
	 * @param locale
	 *            地区
	 * @return
	 */
	public String getMSRString(String id, String locale);

	/**
	 * 依据地区和多语言id获取对应的内容信息
	 * 
	 * @param id
	 * @param locale
	 * @param arguments
	 * @return
	 */
	public String getMSRString(String id, String locale, Object... arguments);
}
