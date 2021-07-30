/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ClassInfoDynaObject
 * WangLHB Feb 1, 2012
 */
package dyna.common.dtomapper.aas;

import dyna.common.dto.aas.UserAgent;

import java.util.List;
import java.util.Map;

/**
*
* @author   Lizw
* @date     2021/7/11 16:45
**/

public interface UserAgentMapper
{
	void insert(UserAgent userAgent);

	List<UserAgent> select(Map<String,Object> param);

	int update(UserAgent userAgent);
}
