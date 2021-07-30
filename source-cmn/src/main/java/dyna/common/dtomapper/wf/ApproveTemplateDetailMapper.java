/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ProcAttachTemplate
 * zhanghj 2011-4-16
 */
package dyna.common.dtomapper.wf;

import dyna.common.dto.wf.ApproveTemplateDetail;
import dyna.common.dtomapper.DynaCommonMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
*
* @author   Lizw
* @date     2021/7/11 17:16
**/

public interface ApproveTemplateDetailMapper extends DynaCommonMapper<ApproveTemplateDetail>
{

	List<ApproveTemplateDetail> selectDetailWithMaster(@Param("GUID")String guid,@Param("PROCRTNAME")String procrtName);

	void deleteAdvance(Map<String,Object> param);

}
