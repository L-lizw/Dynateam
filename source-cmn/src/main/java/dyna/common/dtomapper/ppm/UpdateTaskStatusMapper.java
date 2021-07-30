package dyna.common.dtomapper.ppm;

import dyna.common.bean.data.ppms.UpdateTaskStatus;
import dyna.common.dtomapper.DynaCommonMapper;

import java.util.List;
import java.util.Map;

/**
 * @author Lizw
 * @date 2021/7/17
 **/
public interface UpdateTaskStatusMapper extends DynaCommonMapper<UpdateTaskStatus>
{

	List<UpdateTaskStatus> selectForwardUser(Map<String,Object> param);

	void insertForwardUser(Map<String,Object> param);

	void deleteForwardUser(Map<String,Object> param);
}
