package dyna.common.dtomapper.configparamter;

import dyna.common.bean.data.configparamter.TableOfRegion;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author Lizw
 * @date 2021/7/16
 **/
public interface TableOfRegionMapper
{

	List<TableOfRegion> select(Map<String,Object> param);

	List<TableOfRegion> haveRLSData(String materGuid);

	void inserBatchList(Map<String,Object> param);

	int updateBatchList(Map<String,Object> param);

	int updateNextRevisionBatchList(Map<String,Object> param);

	int updateCustNextRevisionBatchList(Map<String,Object> param);

	void deleteLine(String guid);

	void deleteCustLine(@Param("MASTERGUID")String masterfk,@Param("ITEMMASTERGUID")String itemMasterGuid);

	int obsoleteColumnByTitle(Map<String,Object> param);

	void deleteColumnByTitle(@Param("")String guid,@Param("TITLELIST")List<String> titleList);

	void deleteByMaster(@Param("")String masterGUid,@Param("TABLETYPELIST")List<String> tableTypeList);

	void deleteCustByMaster(@Param("MASTERGUID")String masterGuid,@Param("TABLETYPELIST")String tableTypeList);

	void deleteInfoByMaster(@Param("")String masterGuid,@Param("TABLETYPELIST")String tableTypeList);

	int clearWIP(Map<String,Object> param);

	void deleteWIP(String masterGuid);

	int clearCustWIP(Map<String,Object> param);

	void deleteCustWIP(String masterGuid);

}
