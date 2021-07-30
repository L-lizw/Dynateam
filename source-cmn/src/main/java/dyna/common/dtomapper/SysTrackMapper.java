/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: SysTrack
 * Wanglei 2011-11-14
 */
package dyna.common.dtomapper;

import dyna.common.dto.SysTrack;

import java.util.Map;

/**
*
* @author   Lizw
* @date     2021/7/11 17:42
**/

public interface SysTrackMapper extends DynaCommonMapper<SysTrack>
{

	void deleteAdvanced(Map<String,Object> param);

	void deleteALL(Map<String,Object> param);

	void deleteByGuids(Map<String,Object> param);

	void deleteBySID(Map<String,Object> param);

	void deleteByUserGuid(Map<String,Object> param);

	void deleteByAll(Map<String,Object> param);

}
