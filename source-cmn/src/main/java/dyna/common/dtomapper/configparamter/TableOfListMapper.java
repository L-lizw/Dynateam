package dyna.common.dtomapper.configparamter;

import dyna.common.bean.data.configparamter.TableOfList;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author Lizw
 * @date 2021/7/15
 **/
public interface TableOfListMapper
{

	List<TableOfList> selectAllList(Map<String,Object> pram);

	List<TableOfList> listMoreThanOneGroupOfLNumber(Map<String,Object> param);

	List<TableOfList> listDuplicateLNumber(@Param("MASTERGUID")String masterGuid,@Param("TABLETYPE")String tableType);

	List<TableOfList> haveRLSData(String masterGuid);

	List<TableOfList> isDuplicateSN(Map<String,Object> param);

	void inserBatchList(Map<String,Object> param);

	int updateBatchList(Map<String,Object> param);

	int updateNextRevisionBatchList(Map<String,Object> param);

	int updateCustNextRevisionBatchList(Map<String,Object> param);

	void deleteLine(String guid);

	void deleteCustLine(@Param("MASTERGUID")String masterfk,@Param("ITEMMASTERGUID")String itemMasterGuid);

	int obsoleteColumnByTitle(Map<String,Object> param);

	void deleteColumnByTitle(Map<String,Object> param);

	void deleteByMaster(String masterGuid);

	void deleteCustByMaster(String masterGuid);

	void deleteInfoByMaster(String masterGuid);

	int clearWIP(Map<String,Object> param);

	void deleteWIP(String masterGuid);

	int clearCustWIP(Map<String,Object> param);

	void deleteCustWIP(String masterGuid);
}
