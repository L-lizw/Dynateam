/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Transition 工作流程活动变迁
 * Wanglei 2010-11-2
 */
package dyna.common.dtomapper.wf;

import dyna.common.dto.wf.Performer;
import dyna.common.dtomapper.DynaCommonMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
*
* @author   Lizw
* @date     2021/7/11 17:17
**/

public interface PerformerMapper extends DynaCommonMapper<Performer>
{

	void insertActualPerformer(Performer performer);

	void deleteallperformer(String procrtGuid);

	void deleteperformeractual(String procrtGuid);

	void deleteperformeractualActrt(String actrtGuid);

	void deleteperformerActrt(String actrtGuid);

	int setPerformerFinished(String guid);

	List<Performer> selectActPerformerActual(@Param("")String ACTRTGUID,@Param("PERFGUID")String perfGuid);

	List<Performer> selectActmode(String perfGuid);

	List<Performer> selectNoticePer(Map<String,Object> param);

	List<Performer> selectLastNoticePer(Map<String,Object> param);

	List<Performer> selectNextNoticePer(Map<String,Object> param);

	List<Performer> selectAllNoticePer(String procrtGuid);

	List<Performer> selectProcessNoticePer(@Param("PROCRTGUID")String procrtGuid,@Param("NOTICETYPE")String noticeType);
	
}
