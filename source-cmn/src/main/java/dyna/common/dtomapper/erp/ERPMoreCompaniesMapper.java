/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ERPWFMoreCompanies
 * wangweixia 2012-4-5
 */
package dyna.common.dtomapper.erp;

import dyna.common.dto.erp.ERPMoreCompanies;

import java.util.List;
import java.util.Map;

/**
*
* @author   Lizw
* @date     2021/7/11 16:49
**/


public interface ERPMoreCompaniesMapper
{

	List<ERPMoreCompanies> selectForAll(Map<String,Object> param);

	List<ERPMoreCompanies> selectForAcl(Map<String,Object> param);

	void insertForAll(ERPMoreCompanies erpMoreCompanies);

	void insertForAcl(ERPMoreCompanies erpMoreCompanies);

	void deleteForAll(String erpTypeFlag);

	void deleteForAcl(String templateGuid);

}
