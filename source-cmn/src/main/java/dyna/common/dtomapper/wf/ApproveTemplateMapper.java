/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ProcAttachTemplate
 * zhanghj 2011-4-16
 */
package dyna.common.dtomapper.wf;

import dyna.common.dto.wf.ApproveTemplate;
import dyna.common.dtomapper.DynaCommonMapper;

/**
*
* @author   Lizw
* @date     2021/7/11 17:15
**/

public interface ApproveTemplateMapper extends DynaCommonMapper<ApproveTemplate>
{

	void deleteAdvance(String perfTemplateName);


}
