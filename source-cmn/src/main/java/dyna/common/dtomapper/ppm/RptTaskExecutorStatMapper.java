package dyna.common.dtomapper.ppm;

import dyna.common.bean.data.ppms.RptTaskExecutorStat;

import java.util.List;
import java.util.Map;

/**
 * @author Lizw
 * @date 2021/7/17
 **/
public interface RptTaskExecutorStatMapper
{

	List<RptTaskExecutorStat> select(Map<String,Object> param);

	List<RptTaskExecutorStat> receiverSelect(Map<String,Object> param);
}
