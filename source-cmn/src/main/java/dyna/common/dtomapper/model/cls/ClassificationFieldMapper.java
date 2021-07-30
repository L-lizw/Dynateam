/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ClassificationField
 * duanll 2014-1-14
 */
package dyna.common.dtomapper.model.cls;

import dyna.common.dto.model.cls.ClassificationField;
import dyna.common.dtomapper.DynaCacheMapper;

import java.util.List;

/**
*
* @author   Lizw
* @date     2021/7/11 16:53
**/

public interface ClassificationFieldMapper extends DynaCacheMapper<ClassificationField>
{

	void deleteByItem(String classificationfk);

	void deleteByMaster(String masterGuid);

	List<ClassificationField> select(ClassificationField classificationField);

}
