package dyna.common.dtomapper.ppm;

import dyna.common.bean.data.ppms.RptManagerStat;

import java.util.List;
import java.util.Map;

/**
 * @author Lizw
 * @date 2021/7/17
 **/
public interface RptManagerStatMapper
{

	List<RptManagerStat> select(Map<String,Object> param);

	List<RptManagerStat> receiverSelect(Map<String,Object> param);
}
