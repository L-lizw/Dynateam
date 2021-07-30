/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Queue 队列
 * zhanghw 2012-04-24
 */
package dyna.common.dtomapper;

import dyna.common.dto.Queue;

import java.util.List;
import java.util.Map;

/**
*
* @author   Lizw
* @date     2021/7/11 17:38
**/

public interface QueueMapper extends DynaCommonMapper<Queue>
{

	Integer selectForCount(Map<String,Object> param);

	List<Queue> selectFuzzy(Map<String,Object> param);

	List<Queue> selectvip(Map<String,Object> param);

	void deleteTimeOutJobByType(Queue queue);

}
