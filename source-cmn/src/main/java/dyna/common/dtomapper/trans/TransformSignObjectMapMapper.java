package dyna.common.dtomapper.trans;

import dyna.common.bean.data.trans.TransformSignObjectMap;
import dyna.common.dtomapper.DynaCommonMapper;

/**
 * @author Lizw
 * @date 2021/7/17
 **/
public interface TransformSignObjectMapMapper extends DynaCommonMapper<TransformSignObjectMap>
{

	void deleteAll(String singParamGuid);
}
