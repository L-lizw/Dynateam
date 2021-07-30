/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: CodeItemInfo
 * caogc 2010-9-19
 */
package dyna.common.dtomapper.model.code;

import dyna.common.dto.model.code.CodeItemInfo;
import dyna.common.dtomapper.DynaCacheMapper;

import java.util.List;

/**
*
* @author   Lizw
* @date     2021/7/11 16:55
**/

public interface CodeItemInfoMapper extends DynaCacheMapper<CodeItemInfo>
{
	List<CodeItemInfo> select(CodeItemInfo codeItemInfo);

	void deleteBy(String masterGuid);
}
