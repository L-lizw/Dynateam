/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ProcessRuntime 工作流程
 * Wanglei 2010-11-2
 */
package dyna.common.dtomapper.wf;

import dyna.common.dto.wf.ProcessRuntime;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
*
* @author   Lizw
* @date     2021/7/11 17:18
**/

public interface ProcessRuntimeMapper
{
	void insert(ProcessRuntime processRuntime);

	List<ProcessRuntime> select(Map<String,Object> param);

	List<ProcessRuntime> selectcntsons(@Param("PARENTGUID")String parentGuid,@Param("ACTRTGUID")String actrtGuid);

	int updateProcessRuntime(Map<String,Object> param);

	List<ProcessRuntime> selectByObject(Map<String,Object> param);

	List<ProcessRuntime> selectFinishBy(String orderField);

	List<ProcessRuntime> selectCurrentByObject(String procrtGuid);

	List<ProcessRuntime> selectCurrentByObject4WF(String procrtGuid);

	List<ProcessRuntime> selectFirstPassApproval();

	List<ProcessRuntime> selectAllNotFinishProc(String guidList);

	void removeLock(String procrtGuid);

	void deletePerformer(String procrtGuid);

	void deletePerformerActual(String procrtGuid);

	void deleteProcrt(String procrtGuid);

	void deleteActrt(String procrtGuid);

	void deleteTransition(String procrtGuid);

	void deleteTransrst(String procrtGuid);

	void deleteAttach(String procrtGuid);

	void deleteTrack(String procrtGuid);

	void deleteTrackAttach(String procrtGuid);

}
