package dyna.common.dtomapper.ppm;

import dyna.common.bean.data.ppms.RptTaskExecStateStat;

import java.util.List;
import java.util.Map;

/**
 * @author Lizw
 * @date 2021/7/17
 **/
public interface RptTaskExecStateStatMapper
{

	List<RptTaskExecStateStat> select(Map<String,Object> param);

	List<RptTaskExecStateStat> receiverSelect(Map<String,Object> param);

}
