package dyna.common.dtomapper.itf;

import dyna.common.dto.model.itf.InterfaceData;
import dyna.common.dtomapper.DynaCacheMapper;

/**
 * @author Lizw
 * @date 2021/7/17
 **/
public interface InterfaceDataMapper extends DynaCacheMapper<InterfaceData>
{

	int deleteAll();
}
