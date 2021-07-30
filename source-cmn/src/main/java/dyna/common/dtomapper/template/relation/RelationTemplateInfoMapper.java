/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: RelationTemplate
 * Caogc 2010-8-18
 */
package dyna.common.dtomapper.template.relation;

import dyna.common.dto.template.relation.RelationTemplateInfo;
import dyna.common.dtomapper.DynaCacheMapper;

import java.util.List;
import java.util.Map;

/**
*
* @author   Lizw
* @date     2021/7/11 17:11
**/

public interface RelationTemplateInfoMapper extends DynaCacheMapper<RelationTemplateInfo>
{

	List<RelationTemplateInfo> selectTemplate(Map<String,Object> param);

	List<RelationTemplateInfo> selectName(Map<String,Object> param);

	List<RelationTemplateInfo> selectNameForBuiltIn(Map<String,Object> param);

	RelationTemplateInfo end2Count(Map<String,Object> param);

	int obsleteByName(Map<String,Object> param);

	void deleteRelationTemplateByName(String templateName);

	List<RelationTemplateInfo> getRelationTemplateByEND2(Map<String,Object> param);


}
