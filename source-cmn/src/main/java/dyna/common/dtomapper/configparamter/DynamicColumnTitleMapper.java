package dyna.common.dtomapper.configparamter;

import dyna.common.bean.data.configparamter.DynamicColumnTitle;

import java.util.List;
import java.util.Map;

/**
 * @author Lizw
 * @date 2021/7/16
 **/
public interface DynamicColumnTitleMapper
{

	List<DynamicColumnTitle> selectCustTtileOfTable(Map<String,Object> param);

	List<DynamicColumnTitle> haveRLSData(String masterGuid);

	void inserBatchList(Map<String,Object> param);

	int updateBatchList(Map<String,Object> param);

	int updateNextRevisionBatchList(Map<String,Object> param);

	int release(Map<String,Object> param);

	int obsolete(Map<String,Object> param);

	void delete(List<String> titleGuidList);

	int obsoleteOnly(Map<String,Object> param);

	void deleteByMaster(String masterGuid);

	int clearWIP(Map<String,Object> param);

	void deleteWIP(String masterGuid);

}
