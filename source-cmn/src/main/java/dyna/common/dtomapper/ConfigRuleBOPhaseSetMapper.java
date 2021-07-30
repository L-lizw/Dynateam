/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ConfigRuleBOEctemplate  处于当前生命周期的数据能否修订
 * jianghl 2012-2-13
 */
package dyna.common.dtomapper;

import dyna.common.dto.ConfigRuleBOPhaseSet;

/**
*
* @author   Lizw
* @date     2021/7/11 17:21
**/

public interface ConfigRuleBOPhaseSetMapper extends DynaCommonMapper<ConfigRuleBOPhaseSet>
{

	void deleteWithBolmguid(String bolmGuid);
}
