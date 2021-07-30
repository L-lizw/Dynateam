package dyna.common.dtomapper.ppm;

import dyna.common.bean.data.ppms.RptTaskDeptStat;

import java.util.List;
import java.util.Map;

/**
 * @author Lizw
 * @date 2021/7/17
 **/
public interface RptTaskDeptStatMapper
{

	List<RptTaskDeptStat> receiverSelect(Map<String,Object> param);


}
