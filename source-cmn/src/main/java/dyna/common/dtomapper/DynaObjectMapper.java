package dyna.common.dtomapper;

import dyna.common.bean.data.FoundationObject;
import dyna.common.bean.data.FoundationObjectImpl;
import dyna.common.bean.data.StructureObjectImpl;
import dyna.common.bean.xml.UpperKeyMap;
import dyna.common.dto.FileType;
import dyna.common.sqlbuilder.plmdynamic.insert.DynamicInsertParamData;
import dyna.common.sqlbuilder.plmdynamic.update.DynamicUpdateParamData;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author Lizw
 * @date 2021/7/18
 **/
public interface DynaObjectMapper
{

	void insert(Map<String,Object> param);

	void insertSelectOne(Map<String,Object> param);

	void createBiFile(Map<String,Object> param);

	int updatePrimaryFileToFoundation(Map<String,Object> param);

	UpperKeyMap selectFoundationFile(@Param("TABLENAME")String tableName,@Param("GUID")String guid);

	int updatePrimaryFileToIteration(Map<String,Object> param);

	void insertAll(String sql);

	void insertSelect(Map<String,Object> param);

	int update(Map<String,Object> param);

	int save(Map<String,Object> param);

	int updateDynamic(DynamicUpdateParamData dynamicUpdateParamData);

	String insertDynamic(DynamicInsertParamData dynamicInsertParamData);

	int updateAll(String sql);

	int checkin(Map<String,Object> param);

	int transferCheckout(Map<String,Object> param);

	int obsolete(Map<String,Object> param);

	int release(Map<String,Object> param);

	int saveFile(Map<String,Object> param);

	int unlockAllByECO(Map<String,Object> param);

	int changeViewBomLocation(Map<String,Object> param);

	int saveOwner(Map<String,Object> param);

	int saveSystemSpecialFiled(Map<String,Object> param);

	int resume(Map<String,Object> param);

	int changeStatus(Map<String,Object> param);

	List<UpperKeyMap> select(Map<String,Object> param);

	String pingQuery();

	List<FileType> listFileType();

	List<Map<String, String>> selectAutoHalf(Map<String,Object> param);

	List<String> selectAuto(String sql);

	String getEnd2masterCount(Map<String,Object> param);

	List<StructureObjectImpl> getStructureByGuid(Map<String,Object> param);

	UpperKeyMap iswfdata(String end1Guid);

	FoundationObjectImpl getMasterByGuid(Map<String,Object> param);

	Integer checkLibName(Map<String,Object> param);

	String SelectLibUser(Map<String,Object> param);

	String getMasterGuidByFoundationGuid(Map<String,Object> param);

	String getCheckoutUser(Map<String,Object> param);

	int delete(Map<String,Object> param);

	void deleteAllStruc(@Param("table")String tableName,@Param("VIEWGUID")String viewGuid);

	int deleteMaster(Map<String,Object> param);

	int deleteMasterByMasterGuid(Map<String,Object> param);

	void deleteRevisionByMaster(@Param("tablename")String tableName,@Param("MASTERFK")String masterfk);

	int deleteRevision(Map<String,Object> param);

	void deleteAllStructure(@Param("STRUCTABLE")String tableName,@Param("VIEWFK")String viewfk);

	void deleteAutoHalf(@Param("DELETE")String delete,@Param("WHERE")String where);

	List<UpperKeyMap> getAllRevisionByMaster(Map<String,Object> param);

	List<String> selectEnd2ClassOfStruc(@Param("BASETABLENAME")String tableName,@Param("ORIGVIEWGUID")String viewGuid);

	String insertAllStructureInfo(Map<String,Object> param);

	void copyStructure(Map<String,Object> param);

	int updateEnd2ToNull(Map<String,Object> param);

	Integer getRevisionIdSequence(@Param("BASETABLENAME")String tableName,@Param("MASTERFK")String masterfk);

	void deleteAll(String tableName);

	FoundationObject lockForCheckout(@Param("GUID")String guid,@Param("$TABLENAME$")String tableName);

	Integer selectMaxIterationId(Map<String,Object> param);

	String isLockInWF(String instanceGuid);

	UpperKeyMap getConfigRuleRevise();

	Integer selectDataCountOfLibrary(@Param("TABLENAME")String tableName,@Param("LOCATIONLIB")String locationLib,@Param("ISMASTER")String isMaster);
 }
