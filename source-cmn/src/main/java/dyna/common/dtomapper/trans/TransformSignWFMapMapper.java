package dyna.common.dtomapper.trans;

import dyna.common.bean.data.trans.TransformSignWFMap;
import dyna.common.dtomapper.DynaCommonMapper;

/**
 * @author Lizw
 * @date 2021/7/17
 **/
public interface TransformSignWFMapMapper extends DynaCommonMapper<TransformSignWFMap>
{

	void deleteAll(String singParamGuid);
}
