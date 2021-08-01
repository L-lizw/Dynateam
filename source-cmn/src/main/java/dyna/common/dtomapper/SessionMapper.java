/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Session
 * sam Jun 8, 2010
 */
package dyna.common.dtomapper;

import dyna.common.dto.Session;

/**
*
* @author   Lizw
* @date     2021/7/11 17:40
**/
public interface SessionMapper extends DynaCacheMapper<Session>
{

	void deleteModelerSession();

	void clearSession();


}
