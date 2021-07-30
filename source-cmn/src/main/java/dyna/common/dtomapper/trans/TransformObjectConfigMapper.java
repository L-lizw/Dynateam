package dyna.common.dtomapper.trans;

import dyna.common.bean.data.trans.TransformObjectConfig;
import dyna.common.dtomapper.DynaCommonMapper;

/**
 * @author Lizw
 * @date 2021/7/17
 **/
public interface TransformObjectConfigMapper extends DynaCommonMapper<TransformObjectConfig>
{

	void deleteConfig(String confGuid);
}
