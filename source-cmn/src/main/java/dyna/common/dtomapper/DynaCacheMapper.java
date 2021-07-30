package dyna.common.dtomapper;

import dyna.common.bean.data.DynaObject;

import java.util.List;

/**
*
* @author   Lizw
* @date     2021/7/14 21:26
**/

public interface DynaCacheMapper<T extends DynaObject>
{

	void delete(String guid);

	T get(String guid);

	void insert(T folderACLItem);

	List<T> selectForLoad();

	int update(T folderACLItem);
}
