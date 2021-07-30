package dyna.common.dtomapper;

import dyna.common.bean.data.foundation.BOMView;
import dyna.common.bean.data.structure.BOMStructure;
import dyna.common.bean.xml.UpperKeyMap;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author Lizw
 * @date 2021/7/18
 **/
@Mapper
public interface BomObjectMapper
{

	List<BOMStructure> getBOMStructureObject(Map<String,Object> param);

	String isRepeatForBomstructure(Map<String,Object> param);

	String isEND1HaveBOM(Map<String,Object> param);

	int setFixedRevisionEnd2(Map<String,Object> param);

	int setLatestEnd2(@Param("STRUCTABLE")String tableName,@Param("VIEWGUID")String viewGuid);

	UpperKeyMap checkBOMViewMaster(@Param("VIEWTABLENAME")String viewTableName,@Param("VIEWGUID")String viewGuid);

	int updateUHasBOM(Map<String,Object> param);

	BOMView getBOMView(@Param("BOMVIEWGUID")String viewGuid,@Param("VIEWTABLE")String tableName);

	String isPreciseByStruc(@Param("GUID")String guid,@Param("STRUCTABLE")String structureTable,@Param("VIEWTABLE")String viewTable);

	String getFoundationGuidByMaster(@Param("TABLENAME")String tableName,@Param("MASTER")String masterGuid);

	String getStrucClassGuidByViewName(@Param("VIEWCLASS")String viewClass,@Param("VIEWNAME")String viewName);
}
