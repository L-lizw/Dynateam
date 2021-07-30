/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ProcLockObject 工作流程锁定对象
 * Wanglei 2010-11-2
 */
package dyna.common.dtomapper.wf;

import dyna.common.dto.wf.ProcLockObject;
import dyna.common.dtomapper.DynaCommonMapper;

import java.util.Map;

/**
*
* @author   Lizw
* @date     2021/7/11 17:18
**/

public interface ProcLockObjectMapper extends DynaCommonMapper<ProcLockObject>
{

	void lock(Map<String,Object> param);

}
