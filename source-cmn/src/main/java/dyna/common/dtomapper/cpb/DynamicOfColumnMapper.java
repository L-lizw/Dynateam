package dyna.common.dtomapper.cpb;

import dyna.common.bean.data.configparamter.DynamicOfColumn;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
*
* @author   Lizw
* @date     2021/7/15 21:13
**/

public interface DynamicOfColumnMapper
{

	List<DynamicOfColumn> listAllVirableOfList(@Param("STATUS")String status,@Param("RELEASETIME") Date releaseTime);

	List<DynamicOfColumn> listAllVirableOfLNumber(@Param("LNUMBERLIST")List<String> lnumberList,@Param("STATUS")String status,@Param("RELEASETIME") Date releaseTime);

	List<DynamicOfColumn> listAllVirableOfRegion(@Param("RNUMBERLIST")List<String> rnumberList,@Param("STATUS")String status,@Param("RELEASETIME") Date releaseTime);

	List<DynamicOfColumn> listAllVirableOfG(@Param("GNUMBERLIST")List<String> gnumberList,@Param("STATUS")String status,@Param("RELEASETIME") Date releaseTime);

	List<DynamicOfColumn> listAllVirableOfP(@Param("PNUMBERLIST")List<String> pnumberList,@Param("STATUS")String status,@Param("RELEASETIME") Date releaseTime);

	void insertCustColumn(Map<String,Object> param);

	int updateCustColumn(Map<String,Object> param);

	int updateCustColumnHasNextRevision(Map<String,Object> param);

	void deleteCustColumn(Map<String,Object> param);

	int releaseCustColumn(Map<String,Object> param);

	int obsoleteCustColumn(Map<String,Object> param);
}
