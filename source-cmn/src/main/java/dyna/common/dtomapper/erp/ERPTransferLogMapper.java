package dyna.common.dtomapper.erp;

import dyna.common.dto.erp.ERPTransferLog;

import java.util.List;
import java.util.Map;

/**
*
* @author   Lizw
* @date     2021/7/11 16:50
**/

public interface ERPTransferLogMapper
{

	List<ERPTransferLog> selectClassByItem(Map<String,Object> param);

	List<ERPTransferLog> selectByItem(Map<String,Object> param);

	List<ERPTransferLog> selectClassByBom(Map<String,Object> param);

	List<ERPTransferLog> selectByBom(Map<String,Object> param);

	void insert(ERPTransferLog erpTransferLog);

}
