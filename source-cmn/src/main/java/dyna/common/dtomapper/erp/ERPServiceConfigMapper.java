/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ERP服务配置
 * caogc 2010-08-23
 */
package dyna.common.dtomapper.erp;

import dyna.common.dto.erp.ERPServiceConfig;
import dyna.common.dtomapper.DynaCommonMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
*
* @author   Lizw
* @date     2021/7/11 16:50
**/

public interface ERPServiceConfigMapper extends DynaCommonMapper<ERPServiceConfig>
{

	List<ERPServiceConfig> listERPConfigForNoClass(String groupGuid);

	List<ERPServiceConfig> selectBOConfig(@Param("TEMPLATEGUID")String templateGuid,@Param("BMGUID")String bmGuid);
}
