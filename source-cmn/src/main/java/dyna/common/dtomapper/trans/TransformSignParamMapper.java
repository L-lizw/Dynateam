package dyna.common.dtomapper.trans;

import dyna.common.bean.data.trans.TransformSignParam;
import dyna.common.dtomapper.DynaCommonMapper;

/**
 * @author Lizw
 * @date 2021/7/17
 **/
public interface TransformSignParamMapper extends DynaCommonMapper<TransformSignParam>
{

	void deleteAll(String singGuid);
}
