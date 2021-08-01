package dyna.common.dtomapper.model.itf;

import dyna.common.dto.model.itf.InterfaceRefData;
import dyna.common.dtomapper.DynaCacheMapper;

/**
 * @author Lizw
 * @date 2021/7/17
 **/
public interface InterfaceRefDataMapper extends DynaCacheMapper<InterfaceRefData>
{

	void deleteAll();
}
