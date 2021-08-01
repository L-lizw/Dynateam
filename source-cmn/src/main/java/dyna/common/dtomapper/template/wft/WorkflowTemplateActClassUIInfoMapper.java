/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: WorkflowTemplateActClassRelation
 * WangLHB Jan 6, 2012
 */
package dyna.common.dtomapper.template.wft;

import dyna.common.dto.template.wft.WorkflowTemplateActClassUIInfo;
import dyna.common.dtomapper.DynaCacheMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
*
* @author   Lizw
* @date     2021/7/11 17:13
**/
@Mapper
public interface WorkflowTemplateActClassUIInfoMapper extends DynaCacheMapper<WorkflowTemplateActClassUIInfo>
{
	List<WorkflowTemplateActClassUIInfo> selectForLoad();

}
