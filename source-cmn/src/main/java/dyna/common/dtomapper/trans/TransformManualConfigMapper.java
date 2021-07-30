package dyna.common.dtomapper.trans;

import dyna.common.bean.data.trans.TransformManualConfig;
import dyna.common.dtomapper.DynaCommonMapper;

/**
 * @author Lizw
 * @date 2021/7/17
 **/
public interface TransformManualConfigMapper extends DynaCommonMapper<TransformManualConfig>
{

	void deleteConfig(String confGuid);
}
