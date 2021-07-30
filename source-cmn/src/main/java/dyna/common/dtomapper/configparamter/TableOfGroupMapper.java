package dyna.common.dtomapper.configparamter;

import dyna.common.bean.data.configparamter.TableOfGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author Lizw
 * @date 2021/7/15
 **/
public interface TableOfGroupMapper
{

	List<TableOfGroup> selectGNumberList(Map<String,Object> parm);

	List<TableOfGroup> haveRLSData(String masterGuid);

	List<TableOfGroup> isDuplicateSN(Map<String,Object> param);

	void inserBatchList(Map<String,Object> param);

	int updateBatchList(Map<String,Object> param);

	int updateNextRevisionBatchList(Map<String,Object> param);

	int updateCustNextRevisionBatchList(Map<String,Object> param);

	void deleteLine(String guid);

	void deleteCustLine(@Param("MASTERGUID")String masterf,@Param("ITEMMASTERGUID")String itemMasterGuid);

	int obsoleteColumnByTitle(Map<String,Object> param);

	void deleteColumnByTitle(@Param("MASTERGUID")String masterGuid,@Param("TITLELIST")List<String> titleList);

	void deleteByMaster(String masterGuid);

	void deleteCustByMaster(String masterGuid);

	void deleteInfoByMaster(String masterGuid);

	int clearWIP(Map<String,Object> param);

	void deleteWIP(String masterGuid);

	int clearCustWIP(Map<String,Object> param);

	void deleteCustWIP(String masterGuid);
}
