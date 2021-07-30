package dyna.common.dtomapper.ppm;

import dyna.common.bean.data.ppms.UpdateRemark;
import dyna.common.dtomapper.DynaCommonMapper;

import java.util.Map;

/**
 * @author Lizw
 * @date 2021/7/17
 **/
public interface UpdateRemarkMapper extends DynaCommonMapper<UpdateRemark>
{

	void delete(Map<String,Object> param);
}
