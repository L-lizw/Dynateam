package dyna.common.dtomapper.configparamter;

import dyna.common.bean.data.configparamter.TableOfParameter;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author Lizw
 * @date 2021/7/15
 **/
public interface TableOfParameterMapper
{

	List<TableOfParameter> select(Map<String,Object> param);

	List<TableOfParameter> haveRLSData(String masterGuid);

	void inserBatchList(Map<String,Object> param);

	int updateBatchList(Map<String,Object> param);

	int updateNextRevisionBatchList(Map<String,Object> param);

	int updateCustNextRevisionBatchList(Map<String,Object> param);

	void deleteLine(String guid);

	void deleteCustLine(@Param("MASTERGUID")String masterfk,@Param("ITEMMASTERGUID")String itemMasterGuid);

	void deleteColumnByTitle(@Param("MASTERGUID")String masterGuid,@Param("TITLELIST")List<String> titleGuidList);

	void deleteByMaster(String masterGuid);

	void deleteCustByMaster(String masterGuid);

	void deleteInfoByMaster(String masterGuid);

	int clearWIP(Map<String,Object> param);

	void deleteWIP(String masterGuid);

	int clearCustWIP(Map<String,Object> param);

	void deleteCustWIP(String masterGuid);

}
