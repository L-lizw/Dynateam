package dyna.common.dtomapper.model.itf;

import dyna.common.dto.model.itf.InterfaceClassData;
import dyna.common.dtomapper.DynaCacheMapper;

import java.util.Map;

/**
 * @author Lizw
 * @date 2021/7/17
 **/
public interface InterfaceClassDataMapper extends DynaCacheMapper<InterfaceClassData>
{

	void deleteBy(Map<String,Object> param);

	void deleteAll();
}
