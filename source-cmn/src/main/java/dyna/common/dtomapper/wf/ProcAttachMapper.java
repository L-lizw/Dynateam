/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ProcAttach 工作流程附件
 * Wanglei 2010-11-2
 */
package dyna.common.dtomapper.wf;

import dyna.common.dto.wf.ProcAttach;
import dyna.common.dtomapper.DynaCommonMapper;

import java.util.List;
import java.util.Map;

/**
*
* @author   Lizw
* @date     2021/7/11 17:17
**/

public interface ProcAttachMapper extends DynaCommonMapper<ProcAttach>
{

	List<ProcAttach> selectDirectly(Map<String,Object> param);

	List<ProcAttach> selectClassOfInstance(String procrtGuid);

	List<ProcAttach> selectWithClass(Map<String,Object> param);

	void deleteUnexistsAttach(Map<String,Object> param);

	List<ProcAttach> selectRevisionGuidInWFByMaster(Map<String,Object> param);
}
