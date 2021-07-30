/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ERPYTParameter
 * wangweixia 2012-5-2
 */
package dyna.common.bean.erp;

import java.util.Map;
import java.util.HashMap;

/**
 * @author wangweixia
 *         ERP配置文件里的目录category和表的参数配置
 *         目前只有YT的xml格式有这样的需求
 *         <br/>
 *         YT中表的参数配置改到字段映射中配置。因此原先的<b>tableParams</b>被注释，防止以后扩展
 */
public class ERPParameter
{
	/**
	 * 目录的参数Map
	 */
	private Map<String, String> paramMap = new HashMap<String, String>();

	/**
	 * 表的参数配置
	 */
	// private Map<String, Map<String, String>> tableParams = new HashMap<String, Map<String, String>>();

	public Map<String, String> getParamMap()
	{
		return paramMap;
	}

	public void setParamMap(Map<String, String> paramMap)
	{
		this.paramMap = paramMap;
	}

	// public Map<String, Map<String, String>> getTableParams()
	// {
	// return tableParams;
	// }
	//
	// public void setTableParams(Map<String, Map<String, String>> tableParams)
	// {
	// this.tableParams = tableParams;
	// }
}
