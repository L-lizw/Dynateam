/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ERPYFPLMCanUseClass
 * wangweixia 2012-3-13
 */
package dyna.common.dtomapper.erp;

import dyna.common.dto.erp.ERPBOConfig;
import dyna.common.dtomapper.DynaCommonMapper;

/**
*
* @author   Lizw
* @date     2021/7/11 16:49
**/

public interface ERPBOConfigMapper  extends DynaCommonMapper<ERPBOConfig>
{

	void deleteWithMasterfk(String templateGuid);
}
