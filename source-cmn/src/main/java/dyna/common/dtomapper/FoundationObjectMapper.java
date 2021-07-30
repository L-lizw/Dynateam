package dyna.common.dtomapper;

import dyna.common.bean.data.FoundationObject;
import dyna.common.sqlbuilder.plmdynamic.select.DynamicSelectParamData;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
*
* @author   Lizw
* @date     2021/7/11 23:06
**/

public interface FoundationObjectMapper
{

	public Object selectDynamic(DynamicSelectParamData paramData);

	List<FoundationObject> selectHistoryView(Map<String,Object> param);

	List<FoundationObject> getWIPFOCount(Map<String,Object> param);

	List<FoundationObject> selectIteration(Map<String,Object> param);

	List<FoundationObject> listFoundationByGuid(Map<String,Object> param);

	FoundationObject getFoundationByGuid(Map<String,Object> param);

	List<FoundationObject> selectViewbyEnd1(@Param("tablename")String tablename,@Param("end1")String end1);

	Integer hasRevision(@Param("tablename")String tablename,@Param("masterfk")String masterfk);

	void deleteRevisionByGuid(@Param("tablename")String tablename,@Param("GUID")String guid);

	void deleteStrucByViewGuid(@Param("tablename")String tablename,@Param("GUID")String guid);

	void deleteStrucByEnd2MASTER(@Param("tablename")String tablename,@Param("END2MASTER")String end2Master);

	void deleteStrucByEnd2GUID(@Param("tablename")String tablename,@Param("END2")String end2);

	void deleteByGuid(@Param("tablename")String tableName,@Param("GUID") String guid);

	Integer getMaxRevisionIdSequence(Map<String,Object> param);

	List<FoundationObject> getFoundationByMaster(Map<String,Object> param);

	List<FoundationObject> getFoundationByTime(Map<String,Object> param);

	List<FoundationObject> selectSubTask(Map<String,Object> param);

	int updateFoundationPhase(Map<String,Object> param);

	int updateFoundationQuick(Map<String,Object> param);

	List<FoundationObject> selectCFGuid(Map<String,Object> param);

	List<FoundationObject> getUniqueFoundation(Map<String,Object> param);

	List<FoundationObject> listEnd2ClassOfStruc(Map<String,Object> param);

	int updateEND2OfEND1StructureInProc(Map<String,Object> param);

	List<FoundationObject> listSameEnd2MasterInBOM(Map<String,Object> param);

	List<FoundationObject> selectCFRollbackRevisionData(Map<String,Object> param);

	int updateToCFRollbackRevision(Map<String,Object> param);

	void deleteCFOverIteration(Map<String,Object> param);

	void deleteOverIteration(Map<String,Object> param);

	void deleteOverflowIteration(Map<String,Object> param);

	int updateMainToRollbackRevision(Map<String,Object> param);

	int updateToRollbackRevision(Map<String,Object> param);

	int updateValOfMaster(Map<String,Object> param);

	int updateShort(Map<String,Object> param);

	Integer selectCount(Map<String,Object> param);

	List<FoundationObject> selectDynamic(Map<String,Object> param);

	List<FoundationObject> selectShort(Map<String,Object> param);

	List<FoundationObject> selectOneShort(Map<String,Object> param);

	List<FoundationObject> selectOneShortHalf(Map<String,Object> param);

	List<FoundationObject> selectMultiClassQuickSearch(Map<String,Object> param);

	Integer selectDataCountForMultiClassQuickSearch(Map<String,Object> param);

	void deleteAllProjectRole(Map<String,Object> param);

	void deleteAllTaskDI(Map<String,Object> param);

	void deleteAllTask_I(Map<String,Object> param);

	void deleteAllTask_M(Map<String,Object> param);

	void deleteAllTask(Map<String,Object> param);

	int clearRelationProject(Map<String,Object> param);

	void clearErrDataInMast(Map<String,Object> param);

	int setInstanceCheckout(Map<String,Object> param);

	List<FoundationObject> selectAllRevisionShort(Map<String,Object> param);

	int updateLatestRev(Map<String,Object> param);

	int updateNextRlsTime(Map<String,Object> param);

	int updateStatusByCustomize(Map<String,Object> param);

 }
