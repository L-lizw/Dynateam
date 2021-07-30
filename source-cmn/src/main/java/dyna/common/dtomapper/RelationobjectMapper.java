package dyna.common.dtomapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * @author Lizw
 * @date 2021/7/18
 **/
@Mapper
public interface RelationobjectMapper
{

	int updateOwnerContract(Map<String,Object> param);

	int clearItemOfContent(Map<String,Object> param);

}
