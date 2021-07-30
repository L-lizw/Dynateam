/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: Mail
 * caogc 2010-8-20
 */
package dyna.common.dtomapper;

import dyna.common.dto.Mail;

import java.util.List;
import java.util.Map;

/**
*
* @author   Lizw
* @date     2021/7/11 17:36
**/

public interface MailMapper extends DynaCommonMapper<Mail>
{

	List<Mail> getInboxCount(Map<String,Object> param);

	List<Mail> getInbox(Map<String,Object> param);

	List<Mail> getSent(Map<String,Object> param);

	List<Mail> getTrash(Map<String,Object> param);

	Integer getCountForNotRead(String receiverUser);

	void deleteAdvanced(Map<String,Object> param);

	void deleteIsintrash(String userGuid);

	void selectReceiverByMasterGuid(String masterGuid);

	void deleteMailOutOfDate(Map<String,Object> param);

	void deleteMailReceiverOutOfDate(Map<String,Object> param);

	void deleteWorkflowOutOfDate(Map<String,Object> param);

	void deleteWorkflowReceiverOutOfDate(Map<String,Object> param);

}
