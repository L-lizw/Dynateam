/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: BOMTemplateEnd2
 * Caogc 2010-9-27
 */
package dyna.common.dtomapper.template.bom;

import dyna.common.dto.template.bom.BOMTemplateEnd2;
import dyna.common.dtomapper.DynaCacheMapper;

/**
*
* @author   Lizw
* @date     2021/7/11 17:10
**/

public interface BOMTemplateEnd2Mapper extends DynaCacheMapper<BOMTemplateEnd2>
{

	void deleteWithMasterfk(String masterfk);
}
