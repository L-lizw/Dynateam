package dyna.common.dtomapper.trans;

import dyna.common.bean.data.trans.TransformFieldMapping;
import dyna.common.dtomapper.DynaCommonMapper;

/**
 * @author Lizw
 * @date 2021/7/17
 **/
public interface TransformFieldMappingMapper extends DynaCommonMapper<TransformFieldMapping>
{

	void deleteConfig(String confGuid);
}
