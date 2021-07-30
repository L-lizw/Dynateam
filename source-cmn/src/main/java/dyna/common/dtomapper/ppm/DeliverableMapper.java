package dyna.common.dtomapper.ppm;

import dyna.common.bean.data.ppms.Deliverable;
import dyna.common.dtomapper.DynaCommonMapper;

import java.util.List;
import java.util.Map;

/**
 * @author Lizw
 * @date 2021/7/17
 **/
public interface DeliverableMapper extends DynaCommonMapper<Deliverable>
{

	List<Deliverable> selectClassOfTaskDeliverableWithSub(Map<String,Object> param);

	List<Deliverable> selectTaskDeliverableWithSub(Map<String,Object> param);

	List<Deliverable> selectClassOfDeliverable(Map<String,Object> param);


}
