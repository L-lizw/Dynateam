package dyna.common.dtomapper;

import dyna.common.bean.data.ppms.RptWorkItem;

import java.util.List;
import java.util.Map;

/**
 * @author Lizw
 * @date 2021/7/17
 **/
public interface RptWorkItemMapper
{

	RptWorkItem selectCount(Map<String,Object> param);

	RptWorkItem receiverSelectCount(Map<String,Object> param);

	List<RptWorkItem> select(Map<String,Object> param);

	List<RptWorkItem> receiverSelect(Map<String,Object> param);

}
