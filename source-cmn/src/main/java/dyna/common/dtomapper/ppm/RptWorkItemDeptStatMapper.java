package dyna.common.dtomapper.ppm;

import dyna.common.bean.data.ppms.RptWorkItemDeptStat;

import java.util.List;
import java.util.Map;

/**
 * @author Lizw
 * @date 2021/7/17
 **/
public interface RptWorkItemDeptStatMapper
{

	List<RptWorkItemDeptStat> receiverSelect(Map<String,Object> param);

	List<RptWorkItemDeptStat> select(Map<String,Object> param);
}
