/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ActivityRuntime 工作流程活动
 * Wanglei 2010-11-2
 */
package dyna.common.dtomapper.wf;

import dyna.common.dto.wf.ActivityRuntime;
import dyna.common.dtomapper.DynaCommonMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
*
* @author   Lizw
* @date     2021/7/11 17:15
**/

public interface ActivityRuntimeMapper extends DynaCommonMapper<ActivityRuntime>
{

	List<ActivityRuntime> getNextActivityRuntime(Map<String,Object> param);

	List<ActivityRuntime> getPreviousActivityRuntime(Map<String,Object> param);

	int updateNextActrtStartTime(Map<String,Object> param);

	int updateActrtDeadline(Map<String,Object> param);

	int updateActivity(Map<String,Object> param);

	int finishActivityRuntime(Map<String,Object> param);

	int finishAllActivityRuntime(Map<String,Object> param);

	List<ActivityRuntime> selectPerformalActivityRuntime(Map<String,Object> param);

	List<ActivityRuntime> changephasestatus(Map<String,Object> param);

	List<ActivityRuntime> selectAdvActrt(Map<String,Object> param);

	String selectCloseTimeAdvActrt(Map<String,Object> param);

	List<ActivityRuntime> selectDefActrt(Map<String,Object> param);

	List<ActivityRuntime> selectCloseTimeDefActrt(Map<String,Object> parma);

	int updateActrtStartNumber(@Param("STARTNUMBER")String startNumber,@Param("GUID")String guid);

	List<ActivityRuntime> selectActrtProcess(Map<String,Object> param);

	List<ActivityRuntime> selectAllDeferredActrt(Map<String,Object> param);

}
