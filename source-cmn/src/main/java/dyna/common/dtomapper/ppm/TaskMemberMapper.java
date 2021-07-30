package dyna.common.dtomapper.ppm;

import dyna.common.bean.data.ppms.TaskMember;
import dyna.common.dtomapper.DynaCommonMapper;

import java.util.Map;

/**
 * @author Lizw
 * @date 2021/7/17
 **/
public interface TaskMemberMapper extends DynaCommonMapper<TaskMember>
{

	int updateNotCharge(Map<String,Object> param);

	int updateCharge(Map<String,Object> param);
}
