/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: ACLObject
 * Wanglei 2010-7-30
 */
package dyna.common.dtomapper.acl;

import dyna.common.dto.acl.ACLFunctionObject;
import dyna.common.dtomapper.DynaCommonMapper;

import java.util.List;

/**
*
* @author   Lizw
* @date     2021/7/11 16:46
**/

public interface ACLFunctionObjectMapper extends DynaCommonMapper<ACLFunctionObject>
{

	List<ACLFunctionObject> getRootACLSubjectByLIB();

	List<ACLFunctionObject> getSubACLSubject(String parentGuid);


}
