/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Mail_WorkFlow
 * wangweixia 2014-7-21
 */
package dyna.common.dtomapper;

import dyna.common.dto.MailWorkFlow;

import java.util.List;
import java.util.Map;

/**
*
* @author   Lizw
* @date     2021/7/11 17:37
**/

public interface MailWorkFlowMapper
{
	Integer selectMailCountOfActrtDataType1(Map<String,Object> param);

	Integer selectMailCountOfActrtDataType2(Map<String,Object> param);

	Integer selectMailCountOfActrtDataType3(Map<String,Object> param);

	List<MailWorkFlow> selectMailOfActrtDataType1(Map<String,Object> param);

	List<MailWorkFlow> selectMailOfActrtDataType2(Map<String,Object> param);

	List<MailWorkFlow> selectMailOfActrtDataType3(Map<String,Object> param);

}
