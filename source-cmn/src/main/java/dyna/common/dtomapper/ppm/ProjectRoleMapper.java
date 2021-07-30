package dyna.common.dtomapper.ppm;

import dyna.common.bean.data.ppms.ProjectRole;
import dyna.common.dtomapper.DynaCommonMapper;

import java.util.List;
import java.util.Map;

/**
 * @author Lizw
 * @date 2021/7/17
 **/
public interface ProjectRoleMapper extends DynaCommonMapper<ProjectRole>
{

	List<ProjectRole> selectRoleByUser(Map<String,Object> param);
}
