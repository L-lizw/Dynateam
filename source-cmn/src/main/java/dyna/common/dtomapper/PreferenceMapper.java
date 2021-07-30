/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Preference
 * Caogc 2010-10-08
 */
package dyna.common.dtomapper;

import dyna.common.dto.Preference;
import org.apache.ibatis.annotations.Param;

/**
*
* @author   Lizw
* @date     2021/7/11 17:37
**/

public interface PreferenceMapper extends DynaCommonMapper<Preference>
{

	void deleteByType(@Param("USERGUID")String userGuid,@Param("CONFIGTYPE")String configType);
}
