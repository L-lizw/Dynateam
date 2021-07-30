package dyna.common.dtomapper.ppm;

import dyna.common.bean.data.ppms.CheckpointConfig;
import dyna.common.dtomapper.DynaCommonMapper;

import java.util.List;
import java.util.Map;

/**
 * @author Lizw
 * @date 2021/7/17
 **/
public interface CheckpointConfigMapper extends DynaCommonMapper<CheckpointConfig>
{

	List<CheckpointConfig> selectClassOfMilestoneCheckpoint(Map<String,Object> param);

	List<CheckpointConfig> selectOnly(Map<String,Object> param);

	void deleteByTypeGuid(String typeGuid);

}
