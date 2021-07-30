package dyna.common.cache;

import dyna.common.bean.data.DynaObject;

/**
 * @Description:
 * @author: duanll
 * @date: 2020年3月26日
 */
public interface DynaCacheModelService<T extends DynaObject>
{
	T update(T dynaObject);

	T delete(T dynaObject);
}
