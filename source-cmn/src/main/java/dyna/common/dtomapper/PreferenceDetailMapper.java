/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: PreferenceDetail
 * Caogc 2010-10-08
 */
package dyna.common.dtomapper;

import dyna.common.dto.PreferenceDetail;
import org.apache.ibatis.annotations.Param;

/**
*
* @author   Lizw
* @date     2021/7/11 17:38
**/

public interface PreferenceDetailMapper extends DynaCommonMapper<PreferenceDetail>
{

	void deleteWithMasterfk(String masterGuid);

	void deleteWithBmguid(@Param("MASTERFK")String masterfk,@Param("BMGUID")String bmGuid);
}
