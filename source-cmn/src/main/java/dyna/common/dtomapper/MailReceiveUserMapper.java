/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Mail
 * caogc 2010-8-20
 */
package dyna.common.dtomapper;

import dyna.common.dto.MailReceiveUser;
import org.apache.ibatis.annotations.Param;

/**
*
* @author   Lizw
* @date     2021/7/11 17:37
**/

public interface MailReceiveUserMapper
{

	void insert(MailReceiveUser mailReceiveUser);

	void deleteIsintrash(@Param("MASTERGUID")String masterGuid,@Param("RECEIVEUSER")String receiverUser);

}
