package dyna.common.dtomapper.ppm;

import dyna.common.bean.data.ppms.RptTask;

import java.util.List;
import java.util.Map;

/**
 * @author Lizw
 * @date 2021/7/17
 **/
public interface RptTaskMapper
{

	RptTask selectCount(Map<String,Object> param);

	RptTask receiverSelectCount(Map<String,Object> param);

	List<RptTask> select(Map<String,Object> param);

	List<RptTask> receiverSelect(Map<String,Object> param);

}
