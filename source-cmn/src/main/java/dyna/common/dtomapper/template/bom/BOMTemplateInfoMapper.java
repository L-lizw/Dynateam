/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: BOMTemplate
 * Caogc 2010-9-27
 */
package dyna.common.dtomapper.template.bom;

import dyna.common.dto.template.bom.BOMTemplateInfo;
import dyna.common.dtomapper.DynaCacheMapper;

import java.util.List;
import java.util.Map;

/**
*
* @author   Lizw
* @date     2021/7/11 17:11
**/

public interface BOMTemplateInfoMapper extends DynaCacheMapper<BOMTemplateInfo>
{

	List<BOMTemplateInfo> selectName();

	List<BOMTemplateInfo> getBOMTemplateByEND2(Map<String,Object> param);

	int obsleteByName(Map<String,Object> param);

}
