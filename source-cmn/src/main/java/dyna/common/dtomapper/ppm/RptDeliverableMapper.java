package dyna.common.dtomapper.ppm;

import dyna.common.bean.data.ppms.RptDeliverable;

import java.util.List;
import java.util.Map;

/**
 * @author Lizw
 * @date 2021/7/17
 **/
public interface RptDeliverableMapper
{

	RptDeliverable selectCount(Map<String,Object> param);

	RptDeliverable receiverSelectCount(Map<String,Object> param);

	List<RptDeliverable> select(Map<String,Object> param);

	List<RptDeliverable> receiverSelect(Map<String,Object> param);

	List<RptDeliverable> selectClass(Map<String,Object> param);

}
