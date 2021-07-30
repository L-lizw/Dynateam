/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ACLItem
 * Wanglei 2010-7-30
 */
package dyna.common.dtomapper.acl;

import dyna.common.dto.acl.ACLFunctionItem;
import dyna.common.dtomapper.DynaCommonMapper;

import java.util.List;

/**
*
* @author   Lizw
* @date     2021/7/11 16:41
**/

public interface ACLFunctionItemMapper extends DynaCommonMapper<ACLFunctionItem>
{

	List<ACLFunctionItem> selectItemByUser(ACLFunctionItem aclFunctionItem);

}
