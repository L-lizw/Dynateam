package dyna.common.dtomapper.ppm;

import dyna.common.bean.data.ppms.RptProject;

import java.util.List;
import java.util.Map;

/**
 * @author Lizw
 * @date 2021/7/17
 **/
public interface RptProjectMapper
{

	RptProject selectCount(Map<String,Object> param);

	RptProject receiverSelectCount(Map<String,Object> param);

	List<RptProject> select(Map<String,Object> param);

	List<RptProject> receiverSelect(Map<String,Object> param);

	List<RptProject> selectClass(Map<String,Object> param);

}
