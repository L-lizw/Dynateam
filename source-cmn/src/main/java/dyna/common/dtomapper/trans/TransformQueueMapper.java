package dyna.common.dtomapper.trans;

import dyna.common.bean.data.trans.TransformQueue;
import dyna.common.dtomapper.DynaCommonMapper;

import java.util.List;
import java.util.Map;

/**
 * @author Lizw
 * @date 2021/7/17
 **/
public interface TransformQueueMapper extends DynaCommonMapper<TransformQueue>
{

	List<TransformQueue> selectFuzzyCount(Map<String,Object> param);

	List<TransformQueue> selectFuzzy(Map<String,Object> param);

	int updateList(Map<String,Object> param);

	List<TransformQueue> selectNotComplete(String procrtGuid);

	List<TransformQueue> selectHasTrans(String procrtGuid);

}
