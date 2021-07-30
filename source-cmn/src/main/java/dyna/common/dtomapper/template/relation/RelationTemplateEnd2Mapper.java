/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: RelationTemplateEnd2
 * Caogc 2010-8-31
 */
package dyna.common.dtomapper.template.relation;

import dyna.common.dto.template.relation.RelationTemplateEnd2;
import dyna.common.dtomapper.DynaCacheMapper;

/**
*
* @author   Lizw
* @date     2021/7/11 17:11
**/

public interface RelationTemplateEnd2Mapper extends DynaCacheMapper<RelationTemplateEnd2>
{

	void deleteWithMasterfk(String masterGuid);
}
