/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: BIViewHis
 * Caogc 2011-01-11
 */
package dyna.common.dtomapper;

import dyna.common.dto.BIViewHis;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
*
* @author   Lizw
* @date     2021/7/11 17:20
**/

public interface BIViewHisMapper extends DynaCommonMapper<BIViewHis>
{

	List<BIViewHis> selectClassOfHistory(Map<String,Object> param);

	List<BIViewHis> selectByUserIns(@Param("INSTANCEGUID")String instanceGuid,@Param("CREATEUSER")String createUserGuid);

	List<BIViewHis> selectByUser(String createUser);

	void deleteUser(String createUser);

	void deleteBy(@Param("INSTANCEGUID")String instanceGuid,@Param("CREATEUSER")String createUser);
}
