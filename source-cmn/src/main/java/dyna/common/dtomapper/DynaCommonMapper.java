package dyna.common.dtomapper;

import dyna.common.bean.data.DynaObject;

import java.util.List;

public interface DynaCommonMapper<T extends DynaObject>
{

	void delete(String guid);

	void insert(T sourceData);

	List<T> select(T param);

	int update(T data);
}
