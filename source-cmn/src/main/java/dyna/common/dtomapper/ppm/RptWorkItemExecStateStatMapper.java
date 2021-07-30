package dyna.common.dtomapper.ppm;

import dyna.common.bean.data.ppms.RptWorkItemExecStateStat;

import java.util.List;
import java.util.Map;

/**
 * @author Lizw
 * @date 2021/7/17
 **/
public interface RptWorkItemExecStateStatMapper
{

	List<RptWorkItemExecStateStat> select(Map<String,Object> param);

	List<RptWorkItemExecStateStat> receiverSelect(Map<String,Object> param);
}
