package dyna.common.dtomapper.configparamter;

import dyna.common.bean.data.iopconfigparamter.IOPColumnValue;

import java.util.List;
import java.util.Map;

/**
 * @author Lizw
 * @date 2021/7/16
 **/
public interface IOPColumnValueMapper
{

	List<IOPColumnValue> listAllVirableOfValue(Map<String,Object> param);

	List<IOPColumnValue> listColumnValue(Map<String,Object> param);

	void inserBatchList(Map<String,Object> param);

	int updateBatchList(Map<String,Object> param);

	int updateNextRevisionBatchList(Map<String,Object> param);

	void delete(String guid);

	int releaseCustColumn(Map<String,Object> param);

	int obsoleteCustColumn(Map<String,Object> param);

	void deleteByMaster(String masterGuid);

	int clearWIP(Map<String,Object> param);

	void deleteWIP(String masterGuid);

}
