package dyna.common.dtomapper.erp.tmptab;

import dyna.common.dto.erp.tmptab.ERPtempData;

import java.util.List;
import java.util.Map;

/**
*
* @author   Lizw
* @date     2021/7/11 16:51
**/

public interface ERPtempDataMapper
{
	List<ERPtempData> select(Map<String,Object> param);

	void insert(ERPtempData erPtempData);

	int update(ERPtempData erPtempData);

}
