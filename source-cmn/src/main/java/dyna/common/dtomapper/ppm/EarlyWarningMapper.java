package dyna.common.dtomapper.ppm;

import dyna.common.bean.data.ppms.EarlyWarning;
import dyna.common.dtomapper.DynaCommonMapper;

import java.util.List;
import java.util.Map;

/**
 * @author Lizw
 * @date 2021/7/17
 **/
public interface EarlyWarningMapper extends DynaCommonMapper<EarlyWarning>
{

	List<EarlyWarning> selectWarningTask(Map<String,Object> param);

	void insertWarningTask(Map<String,Object> param);

	void deleteWarningTask(Map<String,Object> param);

}
