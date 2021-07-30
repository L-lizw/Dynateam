/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Queue 队列
 * zhanghw 2012-04-24
 */
package dyna.common.dtomapper;

import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
*
* @author   Lizw
* @date     2021/7/11 17:39
**/

public interface SaLicenseOccupiedMapper
{

	int addOccupation(Map<String,Object> param);

	void deleteBy(@Param("GUID")String guid,@Param("SESSIONTYPE")String sessionType,@Param("LOGTIME")String loginTime);
}
