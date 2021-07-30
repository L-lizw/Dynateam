package dyna.common.dtomapper.erp.tmptab;

import dyna.common.dto.erp.tmptab.ERPTempTableInfo;

import java.util.List;
import java.util.Map;

public interface ERPTempTableInfoMapper
{

	List<ERPTempTableInfo> selectBaseTable(Map<String,Object> param);

	void insert(ERPTempTableInfo erpTempTableInfo);

	int createTable(Map<String,Object> param);

	int createTableIndex(Map<String,Object> param);

}
