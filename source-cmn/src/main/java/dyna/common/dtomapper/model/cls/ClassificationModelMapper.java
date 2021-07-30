package dyna.common.dtomapper.model.cls;

import dyna.common.bean.data.FoundationObject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ClassificationModelMapper
{
	List<FoundationObject> selectItemData(Map<String,Object> param);

	void deleteClassificationData(@Param("TABLE")String table,@Param("WHERE")String where);

	void deleteOverflowCFIterationData(Map<String,Object> param);

	void insertCFIterationData(Map<String,Object> param);
}
