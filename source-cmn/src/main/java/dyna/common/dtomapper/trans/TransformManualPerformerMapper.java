package dyna.common.dtomapper.trans;

import dyna.common.bean.data.trans.TransformManualPerformer;
import dyna.common.dtomapper.DynaCommonMapper;

/**
 * @author Lizw
 * @date 2021/7/17
 **/
public interface TransformManualPerformerMapper extends DynaCommonMapper<TransformManualPerformer>
{

	void deleteAll(String fongGuid);
}
