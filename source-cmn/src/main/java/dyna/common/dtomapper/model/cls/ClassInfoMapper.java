/**
 *    Copyright(C) DCIS 版权所有。
 *    功能描述：data common object definitions
 *    创建标识：Xiasheng , 2010-3-30
 **/

package dyna.common.dtomapper.model.cls;

import dyna.common.dto.model.cls.ClassInfo;
import dyna.common.dtomapper.DynaCacheMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
*
* @author   Lizw
* @date     2021/7/11 16:53
**/

public interface ClassInfoMapper extends DynaCacheMapper<ClassInfo>
{
	List<ClassInfo> select(Map<String,Object> param);

	List<ClassInfo> selectFirstClassWithCreateTable();

	List<ClassInfo> selectAllRealbaseTableName();

	int updateIcon(@Param("GUID")String guid,@Param("ICONPATH")String iconPath,@Param("ICONPATH32")String iconPath32);
}
