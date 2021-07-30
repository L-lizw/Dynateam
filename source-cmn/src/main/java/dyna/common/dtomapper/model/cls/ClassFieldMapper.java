/**
 *    Copyright(C) DCIS 版权所有。
 *    功能描述：data common object definitions
 *    创建标识：Xiasheng , 2010-05-07
 **/

package dyna.common.dtomapper.model.cls;

import dyna.common.dto.model.cls.ClassField;
import dyna.common.dtomapper.DynaCacheMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
*
* @author   Lizw
* @date     2021/7/11 16:52
**/

public interface ClassFieldMapper extends DynaCacheMapper<ClassField>
{
	List<ClassField> select(ClassField classField);

	int updateTableIndex(@Param("TABLEINDEX")String tableIndex,@Param("REABBASETABLENAME")String realBaseTableName,@Param("COLUMNNAME")String columnName);

	int clear(@Param("CLASSGUID")String classGuid,@Param("FIELDNAME")String fieldName);

	void deleteBy(String classGuid);
}
