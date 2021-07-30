package dyna.common.dtomapper;

import dyna.common.dto.ModelSync;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
*
* @author   Lizw
* @date     2021/7/11 17:37
**/
@Mapper
public interface ModelSyncMapper extends DynaCommonMapper<ModelSync>
{

	List<ModelSync> getSync(Map<String,Object> param);

}
