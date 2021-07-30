package dyna.common.dtomapper.ppm;

import dyna.common.bean.data.ppms.RptWorkItemExecutorStat;

import java.util.List;
import java.util.Map;

/**
 * @author Lizw
 * @date 2021/7/17
 **/
public interface RptWorkItemExecutorStatMapper
{

	List<RptWorkItemExecutorStat> select(Map<String,Object> param);

	List<RptWorkItemExecutorStat> receiverSelect(Map<String,Object> param);

}
