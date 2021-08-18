/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: MailTrashStub
 * Wanglei 2011-4-1
 */
package dyna.app.service.brs.sms;

import dyna.app.service.AbstractServiceStub;
import dyna.app.service.brs.pos.POSImpl;
import dyna.app.service.helper.ServiceRequestExceptionWrap;
import dyna.common.SearchCondition;
import dyna.common.SearchConditionFactory;
import dyna.common.bean.data.SystemObject;
import dyna.common.dto.Mail;
import dyna.common.dto.MailReceiveUser;
import dyna.common.dto.MailWorkFlow;
import dyna.common.dto.aas.User;
import dyna.common.exception.DynaDataException;
import dyna.common.exception.ServiceRequestException;
import dyna.common.systemenum.OperateSignEnum;
import dyna.common.util.SetUtils;
import dyna.data.DataServer;
import dyna.data.service.common.DSCommonService;
import dyna.data.service.sdm.FieldValueEqualsFilter;
import dyna.data.service.sdm.SystemDataService;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author Wanglei
 * 
 */
@Component
public class MailTrashStub extends AbstractServiceStub<SMSImpl>
{

	protected void clearTrash() throws ServiceRequestException
	{

		SystemDataService sds = DataServer.getSystemDataService();

		try
		{
			Map<String, Object> filter = new HashMap<String, Object>();
			String operatorGuid = this.stubService.getOperatorGuid();
			filter.put(Mail.USER_GUID, operatorGuid);

			sds.delete(Mail.class, filter, "deleteIsintrash");

		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

	}

	protected List<Mail> listTrash(SearchCondition searchCondition) throws ServiceRequestException
	{
		List<Mail> mailList = null;

		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			Map<String, Object> filter = this.stubService.getMailInboxStub().buildFilterBySearchCondition(searchCondition, null);
			String operatorGuid = this.stubService.getOperatorGuid();
			filter.put(Mail.USER_GUID, operatorGuid);

			mailList = sds.query(Mail.class, filter, "getTrash");
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}

		return mailList;
	}

	protected void restoreFromTrash(List<String> mailGuidList) throws ServiceRequestException
	{
		SystemDataService sds = DataServer.getSystemDataService();
		try
		{
			String operatorGuid = this.stubService.getOperatorGuid();

			for (String mailGuid : mailGuidList)
			{
				Mail mail = this.stubService.getMail(mailGuid);

				mail.setUpdateUserGuid(operatorGuid);
				mail.put(Mail.IS_IN_TRASH, "N");

				sds.update(Mail.class, mail, "update");
			}
		}
		catch (DynaDataException e)
		{
			throw ServiceRequestExceptionWrap.createByDynaDataException(this.stubService, e);
		}
	}

	/**
	 * @throws ServiceRequestException
	 * 
	 */
	protected void clearMailByConfig() throws ServiceRequestException
	{
		List<User> listUser = this.stubService.getAAS().listAllUser(null, false);
		if (!SetUtils.isNullList(listUser))
		{
			for (User user : listUser)
			{
				this.clearMessageMail(user);
				this.clearWorkFlowMail(user);
			}
		}

		SystemDataService sds = DataServer.getSystemDataService();
		List<User> userList = sds.listFromCache(User.class, new FieldValueEqualsFilter<User>(User.USERID, "SYSTEM.INTERNAL"));
		User user = SetUtils.isNullList(userList) ? null : userList.get(0);
		if (user != null)
		{
			DSCommonService ds = DataServer.getDSCommonService();
			ds.deleteMail(user.getGuid(), 45, false);
			ds.deleteMail(user.getGuid(), 45, true);
		}
	}

	/**
	 * 清除普通邮件
	 * 
	 * @param user
	 * @throws ServiceRequestException
	 */
	private void clearMessageMail(User user) throws ServiceRequestException
	{
		int messageDay = ((POSImpl) this.stubService.getPOS()).getMessageClearCycle(user.getGuid());
		if (messageDay > 0)
		{
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			int day = calendar.get(Calendar.DATE);
			calendar.set(Calendar.DATE, day - messageDay);
			Date date = calendar.getTime();

			// 此时间之前的普通邮件
			SearchCondition search = SearchConditionFactory.createSearchCondition4MailInbox(null, null);
			search.addFilter(SystemObject.CREATE_TIME, date, OperateSignEnum.NOTLATER);
			search.setPageNum(1);
			search.setPageSize(500);
			int size = 0;
			List<Mail> listMail = this.stubService.getMailInboxStub().listInbox(search, user.getGuid());
			if (!SetUtils.isNullList(listMail))
			{
				size = listMail.get(0).getRowCount();
				size = size / 500;
				for (int i = 1; i <= size + 1; i++)
				{
					if (i > 1)
					{
						search.setPageNum(i);
						listMail = this.stubService.getMailInboxStub().listInbox(search, user.getGuid());
					}
					if (!SetUtils.isNullList(listMail))
					{
						SystemDataService sds = DataServer.getSystemDataService();
						for (Mail mail : listMail)
						{
							deleteMail(user.getGuid(), mail.getRUMasterGuid(), mail.getGuid(), sds);
						}
					}
				}
			}
		}

	}

	/**
	 * 清除流程邮件
	 * 
	 * @param user
	 * @throws ServiceRequestException
	 */
	private void clearWorkFlowMail(User user) throws ServiceRequestException
	{
		int messageDay = ((POSImpl) this.stubService.getPOS()).getWorkFlowClearCycle(user.getGuid());
		if (messageDay > 0)
		{
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			int day = calendar.get(Calendar.DATE);
			calendar.set(Calendar.DATE, day - messageDay);
			Date date = calendar.getTime();

			// 此时间之前的普通邮件
			SearchCondition search = SearchConditionFactory.createSearchCondition4MailSent();
			search.addFilter(MailWorkFlow.RECEIVE_USER, user.getGuid(), OperateSignEnum.EQUALS);
			search.addFilter(MailWorkFlow.RECEIVE_TIME, date, OperateSignEnum.NOTLATER);
			search.addFilter("PROCRT_VALID", null, null);
			search.setPageNum(1);
			search.setPageSize(500);
			int size = 0;
			List<MailWorkFlow> listWorkFlowMail = this.stubService.listMailOfWorkFlow(search);
			if (!SetUtils.isNullList(listWorkFlowMail))
			{
				size = listWorkFlowMail.get(0).getRowCount();
				size = size / 500;
				for (int i = 1; i <= size + 1; i++)
				{
					if (i > 1)
					{
						search.setPageNum(i);
						listWorkFlowMail = this.stubService.listMailOfWorkFlow(search);
					}
					if (!SetUtils.isNullList(listWorkFlowMail))
					{
						SystemDataService sds = DataServer.getSystemDataService();
						for (MailWorkFlow mail : listWorkFlowMail)
						{
							deleteMail(user.getGuid(), mail.getRUMasterGuid(), mail.getGuid(), sds);
						}
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param userGuid
	 * @param ruMasterGuid
	 * @param mailGuid
	 * @param sds
	 */
	private void deleteMail(String userGuid, String ruMasterGuid, String mailGuid, SystemDataService sds)
	{
		// 先删掉关联表
		Map<String, Object> filter = new HashMap<String, Object>();
		filter.put(MailReceiveUser.RECEIVEUSER, userGuid);
		filter.put(MailReceiveUser.MASTERGUID, ruMasterGuid);
		sds.delete(MailReceiveUser.class, filter, "deleteIsintrash");

		// 删除邮件
		// Map<String, Object> mailFilter = new HashMap<String, Object>();
		// mailFilter.put(Mail.GUID, mailGuid);
		sds.delete(Mail.class, mailGuid);

	}
}
