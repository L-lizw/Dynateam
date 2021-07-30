package dyna.common.dtomapper.ppm;

import dyna.common.bean.data.ppms.RptDeptStat;

import java.util.List;
import java.util.Map;

/**
 * @author Lizw
 * @date 2021/7/17
 **/
public interface RptDeptStatMapper
{

	List<RptDeptStat> select(Map<String,Object> param);

	List<RptDeptStat> receiverSelect(Map<String,Object> param);

}
