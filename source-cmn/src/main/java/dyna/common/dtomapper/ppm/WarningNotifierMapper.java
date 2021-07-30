package dyna.common.dtomapper.ppm;

import dyna.common.bean.data.ppms.WarningNotifier;
import dyna.common.dtomapper.DynaCommonMapper;

import java.util.List;
import java.util.Map;

/**
 * @author Lizw
 * @date 2021/7/17
 **/
public interface WarningNotifierMapper extends DynaCommonMapper<WarningNotifier>
{

	List<WarningNotifier> selectOrganization(Map<String,Object> param);

	List<WarningNotifier> selectOrganizationOfWarning(Map<String,Object> param);


}
