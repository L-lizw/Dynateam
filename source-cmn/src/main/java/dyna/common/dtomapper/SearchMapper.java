/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Search  检索条件BEAN
 * caogc 2010-08-26
 */
package dyna.common.dtomapper;

import dyna.common.dto.Search;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
*
* @author   Lizw
* @date     2021/7/11 17:40
**/

public interface SearchMapper extends DynaCommonMapper<Search>
{

	List<Search> selectByGuid(Map<String,Object> param);

	List<Search> selectBOSearch(Map<String,Object> param);

	Integer checkNameUniqueForPublic(String conditionName);

	Integer checkNameUniqueForSystem(@Param("CONDITIONNAME")String conditionName,@Param("USERGUID")String userGuid);

	Integer checkNameUniqueForPM(@Param("CONDITIONNAME")String conditionName,@Param("PMTYPE")String pmType);
}
