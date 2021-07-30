package dyna.common.dtomapper.configparamter;

import dyna.common.bean.data.configparamter.DynamicOfMultiVariable;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface DynamicOfMultiVariableMapper
{
	List<DynamicOfMultiVariable> listMultiVariable(@Param("MASTERGUID")String masterGuid,@Param("RELEASETIME") Date releaseTime,@Param("STATUS")String status);

	void insert(Map<String,Object> param);

	int updateBatchList(Map<String,Object> param);

	int updateNextRevisionBatchList(Map<String,Object> param);

	int releaseCustColumn(Map<String,Object> param);

	int obsoleteCustColumn(Map<String,Object> param);

	void deleteLine(String guid);

	void deleteByMaster(String maseterGuid);

	int clearWIP(Map<String,Object> poaram);

	void deleteWIP(String masterGuid);
}
