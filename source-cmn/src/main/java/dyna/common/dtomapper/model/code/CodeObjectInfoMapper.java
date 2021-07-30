/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: CodeInfo
 * caogc 2010-9-19
 */
package dyna.common.dtomapper.model.code;

import dyna.common.dto.model.code.CodeObjectInfo;
import dyna.common.dtomapper.DynaCacheMapper;

import java.util.List;

/**
*
* @author   Lizw
* @date     2021/7/11 16:55
**/

public interface CodeObjectInfoMapper extends DynaCacheMapper<CodeObjectInfo>
{
	List<CodeObjectInfo> select(CodeObjectInfo codeObjectInfo);
}

