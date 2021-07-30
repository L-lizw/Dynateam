package dyna.common.dtomapper.ppm;

import dyna.common.bean.data.ppms.RptMilestone;

import java.util.List;
import java.util.Map;

/**
 * @author Lizw
 * @date 2021/7/17
 **/
public interface RptMilestoneMapper
{

	List<RptMilestone> select(Map<String,Object> param);

	List<RptMilestone> receiverSelect(Map<String,Object> param);

}
