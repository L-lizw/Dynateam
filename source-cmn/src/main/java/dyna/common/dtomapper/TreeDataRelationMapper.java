package dyna.common.dtomapper;

import dyna.common.dto.TreeDataRelation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
*
* @author   Lizw
* @date     2021/7/11 17:42
**/

public interface TreeDataRelationMapper
{

	void insert(TreeDataRelation treeDataRelation);

	List<TreeDataRelation> selectAllSubData(@Param("DATAGUID")String dataGuid,@Param("DATATYPE") String dataType);

	List<TreeDataRelation> selectAllParentData(@Param("SUBDATAGUID")String subDataGuid,@Param("DATATYPE") String dataType);

	void deleteBy(@Param("SUBDATAGUID")String subDataGuid,@Param("DATAGUID")String dataGuid,@Param("DATATYPE")String dataType);

	void deleteByType(String dataType);
}
